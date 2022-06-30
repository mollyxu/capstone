package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ProfileCreationFragment extends Fragment {
    private static final String TAG = "ProfileCreationFragment";
    private FirebaseAuth mAuth;

    private EditText etFullName;
    private EditText etSchool;
    private ImageView ivProfilePicture;
    private Button btnCompleteProfile;

    public ProfileCreationFragment() {}

    public static ProfileCreationFragment newInstance(String param1, String param2) {
        ProfileCreationFragment fragment = new ProfileCreationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_creation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etFullName = getActivity().findViewById(R.id.et_full_name);
        etSchool = getActivity().findViewById(R.id.et_school);
        ivProfilePicture = getActivity().findViewById(R.id.iv_profile_picture);

        btnCompleteProfile = getActivity().findViewById(R.id.btn_complete_profile);

        // add more logic here later
        Glide.with(getActivity()).load(R.drawable.placeholder_image).into(ivProfilePicture);

        btnCompleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick complete profile button");
                String fullName = etFullName.getText().toString();
                String school = etSchool.getText().toString();

                if (fullName.matches("")) {
                    Toast.makeText(getActivity(), "Please enter your full name.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String firebase_uid = user.getUid();
                    ParseQuery<User> query = ParseQuery.getQuery(User.class);
                    query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);

                    query.getFirstInBackground(new GetCallback<User>() {
                        public void done(User user, ParseException e) {
                            if (e == null) {
                                user.put("full_name",fullName);
                                user.put("school", school);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                            Log.d(getClass().getSimpleName(), "User update error: " + e);
                                        }
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                    Intent home = new Intent(getActivity(), HomescreenActivity.class);
                    startActivity(home);
                }
            }
        });
    }
}