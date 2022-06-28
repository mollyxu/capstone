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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartSessionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "StartSessionFragment";


    private EditText etStartSessionNumParticipants;
    private EditText etStartSessionLocation;
    private EditText etStartSessionStartTime;
    private EditText etStartSessionEndTime;
    private EditText etStartSessionSubjects;
    private EditText etStartSessionStudyPreference;
    private CheckBox checkBoxOtherSubjects;

    private Button btnConfirmSession;

    public StartSessionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartSessionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartSessionFragment newInstance(String param1, String param2) {
        StartSessionFragment fragment = new StartSessionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
//
//                if (email.matches("") || password.matches("")) {
//                    Toast.makeText(getActivity(), "Please enter your email and password.", Toast.LENGTH_SHORT).show();
//                } else {
//                    createAccount(email, password);
//
//                    // profile creation
//                    ((AuthenticationActivity)getActivity()).replaceFragment(R.id.authentication, ProfileCreationFragment.class);
//                }
            }
        });
    }
}