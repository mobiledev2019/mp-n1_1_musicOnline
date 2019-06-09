package com.trantan.music.data.source.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.CommentDataSource;

import java.util.Date;

public class CommentRemoteDataSource implements CommentDataSource.Remote {
    private static final String TAG = "CommentRemoteDataSource";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public CommentRemoteDataSource() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Comments");
    }

    @Override
    public void loadComments(String url, CommentDataSource.CommentsCallback callback) {
        new CommentAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void comment(Track track, FirebaseUser user, Comment comment,
                        CommentDataSource.SendCommentCallback callback) {
        mDatabaseReference
                .child(String.valueOf(track.getId()))
                .child(new Date().toString() + "_" + user.getUid())
                .setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void listensFirebaseComment(Track track, CommentDataSource.ListenFirebaseCallback callback) {
        mDatabaseReference.child(String.valueOf(track.getId()))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        Log.d(TAG, "onChildAdded: "+comment.getBody());
                        callback.onCommentAdded(comment);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadRate(Track track, CommentDataSource.RateCallback callback) {
        DatabaseReference reference = mFirebaseDatabase.getReference("Rate");
        reference.child(String.valueOf(track.getId()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) callback.onRateLoaded(0);
                        double point = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            point += snapshot.getValue(Double.class);
                        }
                        callback.onRateLoaded(point / dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailure(databaseError.getMessage());
                    }
                });
    }

    @Override
    public void ratingTrack(Track track, double point) {
        DatabaseReference reference = mFirebaseDatabase.getReference("Rate");
        reference.child(String.valueOf(track.getId()))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(point);
    }

}
