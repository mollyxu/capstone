package com.example.capstone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    FloatingActionButton mAddFab, mStartSessionFab, mJoinSessionFab;

    TextView startSessionText, joinSessionText;

    Boolean isAllFabsVisible;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAddFab = getActivity().findViewById(R.id.add_fab);
        mStartSessionFab = getActivity().findViewById(R.id.start_session_fab);
        mJoinSessionFab = getActivity().findViewById(R.id.join_session_fab);
        startSessionText = getActivity().findViewById(R.id.start_session_text);
        joinSessionText = getActivity().findViewById(R.id.join_session_text);

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
                // changes here
                ((HomescreenActivity)getActivity()).replaceFragment(R.id.homescreen, StartSessionLogisticsFragment.newInstance(""));
            }
        });



    }

}