package com.example.capstone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class StartSessionPreferencesFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "StartSessionPreferencesFragment";

    private EditText etStartSessionSubjects;
    private EditText etStartSessionStudyPreference;
    private CheckBox checkBoxOtherSubjects;
    private Button btnConfirmSession;

    public StartSessionPreferencesFragment() {}

    public static StartSessionPreferencesFragment newInstance() {
        StartSessionPreferencesFragment fragment = new StartSessionPreferencesFragment();
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
        return inflater.inflate(R.layout.fragment_start_session_preferences, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etStartSessionSubjects = getActivity().findViewById(R.id.et_start_session_subjects);
        etStartSessionStudyPreference = getActivity().findViewById(R.id.et_start_session_study_preference);
        checkBoxOtherSubjects = getActivity().findViewById(R.id.check_box_other_subjects);
        btnConfirmSession = ((HomescreenActivity)getActivity()).findViewById(R.id.btn_confirm_session);
        btnConfirmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");
                updateDatabase();
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, HomeFragment.class);
            }
        });
    }

    public void updateDatabase(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();

        String subjects = etStartSessionSubjects.getText().toString();
        Number studyPreference = Integer.parseInt(etStartSessionStudyPreference.getText().toString());
        Boolean openSession = checkBoxOtherSubjects.isChecked();

        // update parse db
        StudySession studySession = new StudySession();
        studySession.setSubjects(subjects);
        studySession.setStudyPreference(studyPreference);
        studySession.setOpenSession(openSession);
        studySession.setOrganizerId(firebase_uid);

        studySession.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getActivity(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "New study session was created successfully!");
            }
        });
    }
}