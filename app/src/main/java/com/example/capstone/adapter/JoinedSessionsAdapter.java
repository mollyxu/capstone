package com.example.capstone.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.model.StudySession;
import com.example.capstone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class JoinedSessionsAdapter extends RecyclerView.Adapter<JoinedSessionsAdapter.ViewHolder> {
    public static final String TAG = "JoinedSessionsAdapter";

    private Context context;
    private List<StudySession> studySessions;

    public JoinedSessionsAdapter(Context context, List<StudySession> studySessions) {
        this.context = context;
        this.studySessions = studySessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_joined_study_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudySession studySession = studySessions.get(position);
        try {
            holder.bind(studySession);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return studySessions.size();
    }

    public void removeItem(int position) {
        studySessions.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(StudySession studySession, int position) {
        studySessions.add(position, studySession);
        notifyItemInserted(position);
    }

    public List<StudySession> getData() {
        return studySessions;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FirebaseAuth mAuth;

        private TextView tvStudySessionName;
        private ImageView ivOrganizerProfilePicture;
        private TextView tvStartTime;

        public Uri getUserProfilePicture() throws ParseException {
            FirebaseUser user = mAuth.getCurrentUser();
            String firebase_uid = user.getUid();
            ParseQuery<User> query = ParseQuery.getQuery(User.class);
            query.include(User.KEY_FIREBASE_UID);
            query.include(User.KEY_PROFILE_PICTURE);
            query.whereEqualTo(User.KEY_FIREBASE_UID, firebase_uid);
            Log.i(TAG, query.getFirst().getProfilePicture().getUrl());
            return Uri.parse(query.getFirst().getProfilePicture().getUrl());
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            tvStudySessionName = itemView.findViewById(R.id.tv_study_session_name);
            ivOrganizerProfilePicture = itemView.findViewById(R.id.iv_organizer_profile_picture);
            tvStartTime = itemView.findViewById(R.id.tv_study_session_start_time);
        }

        public void bind(StudySession studySession) throws ParseException {
            // Bind the post data to the view elements
            tvStudySessionName.setText(studySession.getName());
            tvStartTime.setText(studySession.getStartTime().toString());

            Uri profilePictureUrl = getUserProfilePicture();

            if (profilePictureUrl != null) {
                Glide.with(context).load(profilePictureUrl).into(ivOrganizerProfilePicture);
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}