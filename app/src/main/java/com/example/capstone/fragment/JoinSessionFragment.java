package com.example.capstone.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.capstone.utils.MapGestureHandler;
import com.example.capstone.R;
import com.example.capstone.feature.RecommendationEngine;
import com.example.capstone.model.StudySession;
import com.example.capstone.activity.HomescreenActivity;
import com.example.capstone.utils.NavigationProvider;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JoinSessionFragment extends Fragment implements MapGestureHandler, NavigationProvider {
    private FirebaseAuth mAuth;

    private static final String TAG = "JoinSessionFragment";

    private String studySessionId;

    private Button btnJoinSessionNext;
    private Button btnConfirmJoinSession;
    private SearchView svJoinSessionSubject;
    private EditText etJoinSessionStartDate;
    private EditText etJoinSessionEndDate;

    private final Calendar startDateTimeCalendar = Calendar.getInstance();
    private final Calendar endDateTimeCalendar = Calendar.getInstance();

    RecommendationEngine recommendationEngine = new RecommendationEngine();

    public JoinSessionFragment() {}

    public static JoinSessionFragment newInstance(String studySessionId) {
        JoinSessionFragment fragment = new JoinSessionFragment();
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
        View view = inflater.inflate(R.layout.fragment_join_session, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        svJoinSessionSubject = getActivity().findViewById(R.id.sv_join_session_subject);
        etJoinSessionStartDate = getActivity().findViewById(R.id.et_join_session_start_date);
        etJoinSessionEndDate = getActivity().findViewById(R.id.et_join_session_end_date);
        btnJoinSessionNext = getActivity().findViewById(R.id.btn_join_session_next);
        btnConfirmJoinSession = getActivity().findViewById(R.id.btn_confirm_join_session);

        btnConfirmJoinSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                filterStudySessions(((HomescreenActivity) getActivity()).getZoomLevel(), ((HomescreenActivity) getActivity()).getCurrentLatLng());
                getHomescreenActivity().updateUserJoinedStudySession(getHomescreenActivity().getSelectedMarkerStudySessionId(), getHomescreenActivity().getCurrentUser());
                navigate();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.join_session_map);
        getHomescreenActivity().prepareMap(mapFragment);

        getHomescreenActivity().mapGestureHandler = this;

        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateTimeCalendar.set(Calendar.YEAR, year);
                startDateTimeCalendar.set(Calendar.MONTH, month);
                startDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
                startDateTimeCalendar.set(Calendar.MINUTE, 0);

                updateDateTimeLabel(startDateTimeCalendar, etJoinSessionStartDate, true);
            }
        };
        etJoinSessionStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), startDate, startDateTimeCalendar.get(Calendar.YEAR), startDateTimeCalendar.get(Calendar.MONTH), startDateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateTimeCalendar.set(Calendar.YEAR, year);
                endDateTimeCalendar.set(Calendar.MONTH, month);
                endDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateTimeCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endDateTimeCalendar.set(Calendar.MINUTE, 59);

                updateDateTimeLabel(endDateTimeCalendar, etJoinSessionEndDate, true);
            }
        };
        etJoinSessionEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), endDate, endDateTimeCalendar.get(Calendar.YEAR), endDateTimeCalendar.get(Calendar.MONTH), endDateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateTimeLabel(Calendar calendar, EditText editText, Boolean isDate){
        String customDateTimeFormat;
        if (isDate) {
            customDateTimeFormat = "MM/dd/yyyy";
        } else {
            customDateTimeFormat = "HH:mm";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(customDateTimeFormat, Locale.US);
        editText.setText(dateFormat.format(calendar.getTime()));
        Log.i(TAG, calendar.getTime().toString());
    }

    private void navigateToNextFragment(){
        StartSessionPreferencesFragment startSessionPreferencesFragment = StartSessionPreferencesFragment.newInstance(studySessionId);
        ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, startSessionPreferencesFragment);
    }

    private void filterStudySessions(int zoomLevel, LatLng currentLatLng){
        //
        String subjects = svJoinSessionSubject.getQuery().toString();
        Date startDate = startDateTimeCalendar.getTime();
        Date endDate = endDateTimeCalendar.getTime();

        Log.i(TAG, "subject: " + subjects);
        Log.i(TAG, "start: " + startDate);
        Log.i(TAG, "end: " + endDate);

        ParseQuery<StudySession> studySessionParseQuery = getStudySessionQuery(zoomLevel, currentLatLng, subjects, startDate, endDate);

        studySessionParseQuery.findInBackground(new FindCallback<StudySession>() {
            @Override
            public void done(List<StudySession> studySessions, ParseException e) {
                if (studySessions == null) {
                    return;
                }
                for (int i = 0; i < studySessions.size(); i++) {
                    Log.i(TAG, studySessions.get(i).getObjectId());
                }

                // show all study sessions
                ((HomescreenActivity)getActivity()).showAllStudySessionsOnMap(studySessions);

                // call recommendation engine
                StudySession recommendedSession = recommendationEngine.getHighestScoredSession(studySessions, currentLatLng, ((HomescreenActivity)getActivity()).getCurrentUser());
                if (recommendedSession != null) {
                    Log.i(TAG, "recommendation: " + recommendedSession.getObjectId());

                }
            }
        });

    }

    public ParseQuery<StudySession> getStudySessionQuery(int zoomLevel, LatLng currentLatlng, String subjects, Date startDate, Date endDate){
        ParseQuery<StudySession> query = ParseQuery.getQuery(StudySession.class);
        // location
        query.whereEqualTo("tile_coordinate_zoom_" + zoomLevel, ((HomescreenActivity)getActivity()).getTileCoordinate(zoomLevel, currentLatlng).toString());

        // subject
        query.whereEqualTo("subjects", subjects);

        // datetime (KIND OF BUGGY, the start_time query isn't working)
//        query.whereGreaterThanOrEqualTo("start_time", startDate);
//        query.whereLessThanOrEqualTo("end_time", endDate);

        query.include("num_participants");
        query.include("study_preference");
        query.include("subjects");
        query.include("open_session");
//        query.include("location");
        query.include("name");
        query.include("start_time");
        query.include("end_time");
        return query;
    }

    @Override
    public void onZoomChange(int zoomLevel, LatLng currentLatLng) {
        // move logic to else where but still call that method
        filterStudySessions(zoomLevel, currentLatLng);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHomescreenActivity().mapGestureHandler = null;
    }

    private HomescreenActivity getHomescreenActivity(){
        return (HomescreenActivity) getActivity();
    }

    @Override
    public void navigate() {
        getHomescreenActivity().replaceFragment(R.id.homescreen, HomeFragment.newInstance());
    }
}