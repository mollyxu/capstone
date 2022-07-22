package com.example.capstone.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.capstone.utils.NavigationProvider;
import com.example.capstone.R;
import com.example.capstone.model.StudySession;
import com.example.capstone.activity.HomescreenActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class StartSessionPreferencesFragment extends Fragment implements NavigationProvider {
    private FirebaseAuth mAuth;

    private String studySessionId;

    private static final String TAG = "StartSessionPreferencesFragment";

    private EditText etStartSessionSubjects;
    private CheckBox checkBoxOtherSubjects;
    private Button btnConfirmSession;

    private String studyPreferenceSelected = "";

    String[] studyPreferences = { "Silent study session", "Quiet study session", "Some discussion", "Lots of discussion", "Discussion-based study session" };

    public StartSessionPreferencesFragment() {}

    public static StartSessionPreferencesFragment newInstance(String studySessionId) {
        StartSessionPreferencesFragment fragment = new StartSessionPreferencesFragment();
        Bundle args = new Bundle();
        args.putString("studySessionId", studySessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            studySessionId = getArguments().getString("studySessionId");
            Log.i(TAG, "check if arguments: " + studySessionId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_session_preferences, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etStartSessionSubjects = getActivity().findViewById(R.id.et_start_session_subjects);
        checkBoxOtherSubjects = getActivity().findViewById(R.id.check_box_other_subjects);
        btnConfirmSession = ((HomescreenActivity)getActivity()).findViewById(R.id.btn_confirm_session);
        StartSessionPreferencesFragment thisFragment = this;
        btnConfirmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");

                String subjects = etStartSessionSubjects.getText().toString();
                int studyPreference = studyPreferenceStringtoInt(studyPreferenceSelected);
                Boolean openSession = checkBoxOtherSubjects.isChecked();

                if (!isInputValid(subjects, studyPreference, openSession)){
                    Toast.makeText(getActivity(), "Please fill in your information correctly", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDraftStudySession(subjects, studyPreference, openSession);
                getHomescreenActivity().saveDraftStudySessionAndUser(thisFragment);
            }
        });

        Spinner studyPreferencesSpinner = getActivity().findViewById(R.id.spinner_start_session_study_preference);
        ArrayAdapter ad
                = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_spinner_item,
                studyPreferences);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_item);
        studyPreferencesSpinner.setAdapter(ad);

        studyPreferencesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(getContext(), item.toString(),
                            Toast.LENGTH_SHORT).show();
                    studyPreferenceSelected = item.toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int studyPreferenceStringtoInt(String studyPreference){
        return Arrays.asList(studyPreferences).indexOf(studyPreference) + 1;
    }

    public boolean isInputValid(String subjects, int studyPreference, Boolean openSession){
        return !subjects.matches("") && studyPreference >= 1 && studyPreference <= 5 && openSession != null;
    }

    public void updateDraftStudySession(String subjects, Number studyPreference, Boolean openSession){
        StudySession draftStudySession = getHomescreenActivity().getDraftStudySession();

        draftStudySession.setSubjects(subjects);
        draftStudySession.setStudyPreference(studyPreference);
        draftStudySession.setOpenSession(openSession);
    }

    private HomescreenActivity getHomescreenActivity(){
        return ((HomescreenActivity) getActivity());
    }

    @Override
    public void navigate() {
         getHomescreenActivity().replaceFragment(R.id.homescreen, HomeFragment.newInstance());
    }
}