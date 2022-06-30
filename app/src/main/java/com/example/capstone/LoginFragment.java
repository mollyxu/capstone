package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginFragment";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    public LoginFragment() {}

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etEmail = getView().findViewById(R.id.et_email);
        etPassword = getView().findViewById(R.id.et_password);
        btnLogin = getView().findViewById(R.id.btn_login);
        btnSignup = getActivity().findViewById(R.id.btn_signup);

        initializeFacebookLogin();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.matches("") || password.matches("")) {
                    Toast.makeText(getActivity(), "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(email, password);
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                // swap to signup fragment
                ((AuthenticationActivity)getActivity()).replaceFragment(R.id.authentication, SignupFragment.class);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
        updateUI(currentUser);
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {}

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            //TODO: comeback to cleanup updateUI
                            Intent home = new Intent(getActivity(), HomescreenActivity.class);
                            startActivity(home);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void initializeFacebookLogin() {
        LoginButton loginButton = getActivity().findViewById(R.id.btn_facebook_login);
        CallbackManager mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // update parse db
                            User registeredUser = new User();
                            String firebase_uid = user.getUid();

                            // check if user already exists
                            ParseQuery<User> query = ParseQuery.getQuery(User.class);
                            query.include(User.KEY_FIREBASE_UID);
                            query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
                            try {
                                int account = query.count();
                                if (account == 0) {
                                    registeredUser.setFirebaseUid(user.getUid());
                                    registeredUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null){
                                                Log.e(TAG, "Error while saving", e);
                                                Toast.makeText(getActivity(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                            }
                                            Log.i(TAG, "Registration was successful!");
                                        }
                                    });
                                    ((AuthenticationActivity)getActivity()).replaceFragment(R.id.authentication, ProfileCreationFragment.class);
                                } else {
                                    // ADD if else
                                    // TODO: comeback later to cleanup updateUI
                                    Intent home = new Intent(getActivity(), HomescreenActivity.class);
                                    startActivity(home);
                                    updateUI(user);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}