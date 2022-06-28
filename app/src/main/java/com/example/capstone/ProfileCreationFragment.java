package com.example.capstone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileCreationFragment extends Fragment {

    public ProfileCreationFragment() {
        // Required empty public constructor
    }

    public static ProfileCreationFragment newInstance(String param1, String param2) {
        ProfileCreationFragment fragment = new ProfileCreationFragment();
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
        return inflater.inflate(R.layout.fragment_profile_creation, container, false);
    }
}