package com.example.capstone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileCreationFragment extends Fragment {
    public final static int PICK_PHOTO_CODE = 1046;
    private static final String TAG = "ProfileCreationFragment";
    private FirebaseAuth mAuth;

    private EditText etFullName;
    private EditText etSchool;
    private ImageView ivProfilePicture;
    private Button btnCompleteProfile;

    private byte[] profilePictureByteArray;

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

        Glide.with(getActivity()).load(R.drawable.placeholder_image).into(ivProfilePicture);

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
                pickPhoto(v);
            }
        });
        btnCompleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick complete profile button");
                String fullName = etFullName.getText().toString();
                String school = etSchool.getText().toString();

                if (isEmptyString(fullName)) {
                    Toast.makeText(getActivity(), "Please enter your full name.", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserAsync(fullName, school);
                    navigateToHome();
                }
            }
        });
    }

    private boolean isEmptyString(String string){
        return string.matches("");
    }

    private void navigateToHome(){
        Intent home = new Intent(getActivity(), HomescreenActivity.class);
        startActivity(home);
    }

    private ParseQuery<User> prepareUpdateUserParseQuery(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
        return query;
    }

    private void updateUserAsync(String fullName, String school){
        ParseQuery<User> query = prepareUpdateUserParseQuery();
        query.getFirstInBackground(new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    user.put("full_name",fullName);
                    if (!isEmptyString(school)){
                        user.put("school", school);
                    }
                    if (profilePictureByteArray != null) {
                        ParseFile profilePicture = new ParseFile("profile_pic", profilePictureByteArray);
                        user.put("profile_picture", profilePicture);
                    }
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
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            ivProfilePicture.setImageBitmap(selectedImage);
            profilePictureByteArray = bitmapToByteArray(selectedImage);
        }
    }
}