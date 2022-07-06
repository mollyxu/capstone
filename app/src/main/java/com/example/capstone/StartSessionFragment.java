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

public class StartSessionFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "StartSessionFragment";

    private EditText etStartSessionNumParticipants;
    private EditText etStartSessionLocation;
    private EditText etStartSessionStartTime;
    private EditText etStartSessionEndTime;
    private EditText etStartSessionSubjects;
    private EditText etStartSessionStudyPreference;
    private CheckBox checkBoxOtherSubjects;
    private Button btnConfirmSession;

    public StartSessionFragment() {}

    public static StartSessionFragment newInstance() {
        StartSessionFragment fragment = new StartSessionFragment();
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
        return inflater.inflate(R.layout.fragment_start_session, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etStartSessionNumParticipants = getActivity().findViewById(R.id.et_start_session_num_participants);
        etStartSessionLocation = getActivity().findViewById(R.id.et_start_session_location);
        etStartSessionStartTime = getActivity().findViewById(R.id.et_start_session_start_time);
        etStartSessionEndTime = getActivity().findViewById(R.id.et_start_session_end_time);
        etStartSessionSubjects = getActivity().findViewById(R.id.et_start_session_subjects);
        etStartSessionStudyPreference = getActivity().findViewById(R.id.et_start_session_study_preference);
        checkBoxOtherSubjects = getActivity().findViewById(R.id.check_box_other_subjects);
        btnConfirmSession = ((HomescreenActivity)getActivity()).findViewById(R.id.btn_confirm_session);
        btnConfirmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");
                updateDatabase();
            }
        });
    }

    public void updateDatabase(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();

        Number numParticipants = Integer.parseInt(etStartSessionNumParticipants.getText().toString());
        String location = etStartSessionLocation.getText().toString();
        String startTime = etStartSessionStartTime.getText().toString();
        String endTime = etStartSessionEndTime.getText().toString();
        String subjects = etStartSessionSubjects.getText().toString();
        Number studyPreference = Integer.parseInt(etStartSessionStudyPreference.getText().toString());
        Boolean openSession = checkBoxOtherSubjects.isChecked();

        // update parse db
        StudySession studySession = new StudySession();
        studySession.setNumParticipants(numParticipants);
        studySession.setLocation(location);
        studySession.setStartTime(startTime);
        studySession.setEndTime(endTime);
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