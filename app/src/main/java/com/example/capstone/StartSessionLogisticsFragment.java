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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class StartSessionLogisticsFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "StartSessionLogisticsFragment";

    private EditText etStartSessionNumParticipants;
    private EditText etStartSessionStartTime;
    private EditText etStartSessionEndTime;
    private Button btnStartMap;

    private String studySessionId;

    public StartSessionLogisticsFragment() {}

    public static StartSessionLogisticsFragment newInstance(String studySessionId) {
        StartSessionLogisticsFragment fragment = new StartSessionLogisticsFragment();
        Bundle args = new Bundle();
        args.putString("studySessionId", studySessionId);
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_start_session_logistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etStartSessionNumParticipants = getActivity().findViewById(R.id.et_start_session_num_participants);
        etStartSessionStartTime = getActivity().findViewById(R.id.et_start_session_start_time);
        etStartSessionEndTime = getActivity().findViewById(R.id.et_start_session_end_time);
        btnStartMap = getActivity().findViewById(R.id.btn_start_map);
        btnStartMap.setOnClickListener(new View.OnClickListener() {
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
        String startTime = etStartSessionStartTime.getText().toString();
        String endTime = etStartSessionEndTime.getText().toString();

        // update parse db
        StudySession studySession = new StudySession();
        studySession.setNumParticipants(numParticipants);
        studySession.setStartTime(startTime);
        studySession.setEndTime(endTime);

        // placeholder
        studySession.setSubjects("fdsafds");
        studySession.setStudyPreference(1);
        studySession.setOpenSession(false);

        studySession.setOrganizerId(firebase_uid);

        studySession.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getActivity(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                studySessionId = studySession.getObjectId();
                Log.i(TAG, "objectId: " + studySessionId);
                Log.i(TAG, "New study session was created successfully!");
                StartSessionMapFragment startSessionMapFragment = StartSessionMapFragment.newInstance(studySessionId);
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, startSessionMapFragment);
            }
        });
    }
}