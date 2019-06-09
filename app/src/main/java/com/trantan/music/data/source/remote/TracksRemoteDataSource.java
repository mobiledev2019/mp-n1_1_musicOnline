package com.trantan.music.data.source.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trantan.music.data.Track;
import com.trantan.music.data.TrackFavorite;
import com.trantan.music.data.source.TracksDataSource;

import java.util.ArrayList;
import java.util.List;

public class TracksRemoteDataSource implements TracksDataSource.Remote {
    private static final String TAG = "TracksRemoteDataSource";
    private static TracksRemoteDataSource sInstance;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private TracksRemoteDataSource() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Favorites");
    }

    public static TracksRemoteDataSource getInstance() {
        if (sInstance == null) sInstance = new TracksRemoteDataSource();
        return sInstance;
    }

    @Override
    public void loadTracks(String url, TracksDataSource.LoadTracksCallback callback) {
        new TracksAsyncTask(false, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void searchTracks(String url, TracksDataSource.LoadTracksCallback callback) {
        new TracksAsyncTask(true, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void getFavoritesOnline(TracksDataSource.FavoriteCallback callback) {
        List<Track> tracks = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabaseReference = mFirebaseDatabase.getReference("Favorites")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else return;
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TrackFavorite trackFavorite = snapshot.getValue(TrackFavorite.class);
                    tracks.add(new Track(trackFavorite.mId, trackFavorite.mDuration, trackFavorite.mTitle,
                            trackFavorite.mArtist, trackFavorite.mStreamUrl, trackFavorite.mDownloadUrl,
                            trackFavorite.mArtworkUrl, true));
                }
                callback.onFavoritesGetted(tracks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addFavorite(Track track, TracksDataSource.FavoriteCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabaseReference = mFirebaseDatabase.getReference("Favorites")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else return;
        mDatabaseReference.child(String.valueOf(track.getId()))
                .setValue(new TrackFavorite(track.getId(), track.getDuration(), track.getTitle(),
                        track.getArtist(), track.getStreamUrl(), track.getDownloadUrl(), track.getArtworkUrl()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onFavoriteAdded();
                });
    }

    @Override
    public void removeFavorite(Track track, TracksDataSource.FavoriteCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabaseReference = mFirebaseDatabase.getReference("Favorites")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else return;
        mDatabaseReference.child(String.valueOf(track.getId()))
                .removeValue();
    }
}
