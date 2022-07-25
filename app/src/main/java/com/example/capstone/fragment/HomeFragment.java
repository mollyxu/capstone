package com.example.capstone.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.capstone.adapter.JoinedSessionsAdapter;
import com.example.capstone.R;
import com.example.capstone.model.StudySession;
import com.example.capstone.model.User;
import com.example.capstone.activity.HomescreenActivity;
import com.example.capstone.utils.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;

    private static final String TAG = "HomeFragment";

    private RecyclerView rvJoinedStudySessions;
    protected JoinedSessionsAdapter adapter;
    private List<String> joinedStudySessionIds = new ArrayList<>();

    FloatingActionButton mAddFab, mStartSessionFab, mJoinSessionFab;

    TextView startSessionText, joinSessionText;

    CoordinatorLayout homeCoordinatorLayout;

    Boolean isAllFabsVisible = false;

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
        homeCoordinatorLayout = getActivity().findViewById(R.id.homes_coordinator_layout);

        mStartSessionFab.setVisibility(View.GONE);
        mJoinSessionFab.setVisibility(View.GONE);
        startSessionText.setVisibility(View.GONE);
        joinSessionText.setVisibility(View.GONE);

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

        mJoinSessionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick join session button");
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, JoinSessionFragment.newInstance(""));
            }
        });

        adapter = new JoinedSessionsAdapter(getActivity(), getHomescreenActivity().getAllJoinedStudySessions());

        rvJoinedStudySessions.setAdapter(adapter);
        rvJoinedStudySessions.setLayoutManager(new LinearLayoutManager(getActivity()));

        queryEachJoinedStudySessions();
        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                joinedStudySessionIds = getHomescreenActivity().getAllJoinedStudySessionIds();

                final int position = viewHolder.getAdapterPosition();
                final StudySession studySession = adapter.getData().get(position);

                Log.i(TAG, "onSwipe session id " + studySession.getObjectId());

                adapter.removeItem(position);
                joinedStudySessionIds.remove(studySession.getObjectId());

                Snackbar snackbar = Snackbar
                        .make(homeCoordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(mAddFab);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(studySession, position);
                        joinedStudySessionIds.add(studySession.getObjectId());

                        getHomescreenActivity().updateUserJoinedStudySessions(joinedStudySessionIds, getHomescreenActivity().getCurrentUser());

                        rvJoinedStudySessions.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

                getHomescreenActivity().updateUserJoinedStudySessions(joinedStudySessionIds, getHomescreenActivity().getCurrentUser());
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvJoinedStudySessions);
    }

    private ParseQuery<User> getUserJoinedStudySessionsQuery(){
        FirebaseUser user = mAuth.getCurrentUser();
        String firebase_uid = user.getUid();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
        query.include(User.KEY_JOINED_SESSIONS);
        return query;
    }

    private ParseQuery<StudySession> getStudySessionQuery(List<String> joinedSessionIds){
        ParseQuery<StudySession> query = ParseQuery.getQuery(StudySession.class);
        query.whereContainedIn(StudySession.KEY_OBJECT_ID, joinedSessionIds);
        query.include(StudySession.KEY_ORGANIZER_ID);
        query.include(StudySession.KEY_START_TIME);
        query.include(StudySession.KEY_NAME);
        query.include(StudySession.KEY_CREATED_AT);
        query.addDescendingOrder("createdAt");
        return query;
    }

    private HomescreenActivity getHomescreenActivity (){
        return ((HomescreenActivity) getActivity());
    }

    private void queryEachJoinedStudySessions() {
        ParseQuery<User> userQuery = getUserJoinedStudySessionsQuery();
        userQuery.getFirstInBackground(new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    try {
                        List<String> joinedSessionIds = ((HomescreenActivity) getActivity()).getListFromJsonArray(user.getJoinedSessions());
                        if (joinedSessionIds == null) {
                            return;
                        }
                        ParseQuery<StudySession> studySessionQuery = getStudySessionQuery(joinedSessionIds);

                        List<StudySession> joinedStudySessions = studySessionQuery.find();
                        getHomescreenActivity().getAllJoinedStudySessions().clear();
                        getHomescreenActivity().getAllJoinedStudySessions().addAll(joinedStudySessions);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException | ParseException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}