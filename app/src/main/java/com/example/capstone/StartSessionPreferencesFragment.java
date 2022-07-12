package com.example.capstone;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class StartSessionPreferencesFragment extends Fragment {
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
        btnConfirmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");
                updateDatabase();

                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, HomeFragment.newInstance());
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
//                Toast.makeText(getContext(), "Selected",
//                        Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public int studyPreferenceStringtoInt(String studyPreference){
        if (studyPreference == "Silent study session"){
            return 1;
        } else if (studyPreference == "Quiet study session"){
            return 2;
        } else if (studyPreference == "Some discussion"){
            return 3;
        } else if (studyPreference == "Lots of discussion"){
            return 4;
        } else {
            return 5;
        }

    }

    public void updateDatabase(){
        String subjects = etStartSessionSubjects.getText().toString();
        Number studyPreference = studyPreferenceStringtoInt(studyPreferenceSelected);
        Boolean openSession = checkBoxOtherSubjects.isChecked();

        Log.i(TAG, "objectId: " + studySessionId);

        if (subjects.matches("")) {
            Toast.makeText(getActivity(), "Please enter your full name.", Toast.LENGTH_SHORT).show();
        } else {
            ParseQuery<StudySession> query = ParseQuery.getQuery(StudySession.class);
            query.whereEqualTo(StudySession.KEY_OBJECT_ID, studySessionId);

            query.getFirstInBackground(new GetCallback<StudySession>() {
                public void done(StudySession studySession, ParseException e) {
                    if (e == null) {
                        studySession.put("subjects",subjects);
                        studySession.put("study_preference", studyPreference);
                        studySession.put("open_session", openSession);

                        studySession.saveInBackground(new SaveCallback() {
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
    }
}