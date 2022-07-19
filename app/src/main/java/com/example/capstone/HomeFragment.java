package com.example.capstone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "HomeFragment";

    private RecyclerView rvJoinedStudySessions;
    protected JoinedSessionsAdapter adapter;
    protected List<StudySession> allJoinedStudySessions;

    FloatingActionButton mAddFab, mStartSessionFab, mJoinSessionFab;

    TextView startSessionText, joinSessionText;

    Boolean isAllFabsVisible;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAddFab = getActivity().findViewById(R.id.add_fab);
        mStartSessionFab = getActivity().findViewById(R.id.start_session_fab);
        mJoinSessionFab = getActivity().findViewById(R.id.join_session_fab);
        startSessionText = getActivity().findViewById(R.id.start_session_text);
        joinSessionText = getActivity().findViewById(R.id.join_session_text);
        rvJoinedStudySessions = getActivity().findViewById(R.id.rv_joined_study_sessions);

        mStartSessionFab.setVisibility(View.GONE);
        mJoinSessionFab.setVisibility(View.GONE);
        startSessionText.setVisibility(View.GONE);
        joinSessionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mStartSessionFab.show();
                            mJoinSessionFab.show();
                            startSessionText.setVisibility(View.VISIBLE);
                            joinSessionText.setVisibility(View.VISIBLE);
                            isAllFabsVisible = true;
                        } else {
                            mStartSessionFab.hide();
                            mJoinSessionFab.hide();
                            startSessionText.setVisibility(View.GONE);
                            joinSessionText.setVisibility(View.GONE);
                            isAllFabsVisible = false;
                        }
                    }
                });

        mStartSessionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick start session button");
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, StartSessionLogisticsFragment.newInstance(""));
            }
        });

        allJoinedStudySessions = new ArrayList<>();
        adapter = new JoinedSessionsAdapter(getActivity(), allJoinedStudySessions);

        rvJoinedStudySessions.setAdapter(adapter);
        rvJoinedStudySessions.setLayoutManager(new LinearLayoutManager(getActivity()));
        queryEachJoinedStudySessions();
    }

    private List<String> getListFromJsonArray(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < jsonArray.length(); i++){
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    private ParseQuery<User> prepareGetUserJoinedStudySessionsQuery(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
        query.include(User.KEY_JOINED_SESSIONS);
        return query;
    }

    private ParseQuery<StudySession> prepareStudySessionQuery(List<String> joinedSessionIds){
        ParseQuery<StudySession> query = ParseQuery.getQuery(StudySession.class);
        query.whereContainedIn(StudySession.KEY_OBJECT_ID, joinedSessionIds);
        query.include(StudySession.KEY_ORGANIZER_ID);
        query.include(StudySession.KEY_START_TIME);
        query.include(StudySession.KEY_NAME);
        query.include(StudySession.KEY_CREATED_AT);
        query.addDescendingOrder("createdAt");
        return query;
    }

    private void queryEachJoinedStudySessions() {
        ParseQuery<User> userQuery = prepareGetUserJoinedStudySessionsQuery();
        userQuery.getFirstInBackground(new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    try {
                        List<String> joinedSessionIds = getListFromJsonArray(user.getJoinedSessions());
                        ParseQuery<StudySession> studySessionQuery = prepareStudySessionQuery(joinedSessionIds);

                        studySessionQuery.findInBackground(new FindCallback<StudySession>() {
                            @Override
                            public void done(List<StudySession> joinedStudySessions, ParseException e) {
                                allJoinedStudySessions.addAll(joinedStudySessions);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}