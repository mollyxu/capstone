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

public class StartSessionFragment extends Fragment {
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
            }
        });
    }
}