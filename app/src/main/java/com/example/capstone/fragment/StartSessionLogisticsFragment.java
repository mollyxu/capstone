package com.example.capstone.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.example.capstone.R;
import com.example.capstone.model.StudySession;
import com.example.capstone.activity.HomescreenActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StartSessionLogisticsFragment extends Fragment {
    private static final String TAG = "StartSessionLogisticsFragment";

    private EditText etStartSessionName;
    private EditText etStartSessionNumParticipants;
    private EditText etStartSessionDate;
    private EditText etStartSessionStartTime;
    private EditText etStartSessionEndTime;
    private Button btnStartMap;

    private final Calendar startDateTimeCalendar = Calendar.getInstance();
    private final Calendar endDateTimeCalendar = Calendar.getInstance();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_session_logistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etStartSessionName = getActivity().findViewById(R.id.et_start_session_name);
        etStartSessionNumParticipants = getActivity().findViewById(R.id.et_start_session_num_participants);
        etStartSessionDate = getActivity().findViewById(R.id.et_start_session_date);
        etStartSessionStartTime = getActivity().findViewById(R.id.et_start_session_start_time);
        etStartSessionEndTime = getActivity().findViewById(R.id.et_start_session_end_time);
        btnStartMap = getActivity().findViewById(R.id.btn_start_map);
        btnStartMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick confirm button");
                updateDraftStudySession();
                navigateToNextFragment();
            }
        });

        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateTimeCalendar.set(Calendar.YEAR, year);
                startDateTimeCalendar.set(Calendar.MONTH, month);
                startDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endDateTimeCalendar.set(Calendar.YEAR, year);
                endDateTimeCalendar.set(Calendar.MONTH, month);
                endDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDateTimeLabel(startDateTimeCalendar, etStartSessionDate, true);
            }
        };
        etStartSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), startDate, startDateTimeCalendar.get(Calendar.YEAR), startDateTimeCalendar.get(Calendar.MONTH), startDateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        TimePickerDialog.OnTimeSetListener startTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startDateTimeCalendar.set(Calendar.MINUTE, minute);
                updateDateTimeLabel(startDateTimeCalendar, etStartSessionStartTime, false);
            }
        };
        etStartSessionStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), startTime, startDateTimeCalendar.get(Calendar.HOUR_OF_DAY), startDateTimeCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        TimePickerDialog.OnTimeSetListener endTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endDateTimeCalendar.set(Calendar.MINUTE, minute);
                updateDateTimeLabel(endDateTimeCalendar, etStartSessionEndTime, false);
            }
        };
        etStartSessionEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), endTime, endDateTimeCalendar.get(Calendar.HOUR_OF_DAY), endDateTimeCalendar.get(Calendar.MINUTE), true).show();
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

    public void updateDraftStudySession(){
        String name = etStartSessionName.getText().toString();
        int numParticipants = Integer.parseInt(etStartSessionNumParticipants.getText().toString());
        Date startTime = startDateTimeCalendar.getTime();
        Date endTime = endDateTimeCalendar.getTime();

        StudySession draftStudySession = ((HomescreenActivity) getActivity()).getDraftStudySession();

        draftStudySession.setName(name);
        draftStudySession.setNumParticipants(numParticipants);
        draftStudySession.setStartTime(startTime);
        draftStudySession.setEndTime(endTime);
    }

    private void navigateToNextFragment(){
        StartSessionMapFragment startSessionMapFragment = StartSessionMapFragment.newInstance(studySessionId);
        ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, startSessionMapFragment);
    }
}