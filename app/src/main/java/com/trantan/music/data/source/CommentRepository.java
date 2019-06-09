package com.trantan.music.data.source;

import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.remote.CommentRemoteDataSource;

public class CommentRepository implements CommentDataSource.Remote {
    private static CommentRepository sRepository;
    private CommentRemoteDataSource mRemoteDataSource;

    private CommentRepository() {
        mRemoteDataSource = new CommentRemoteDataSource();
    }

    public static CommentRepository getInstance() {
        if (sRepository == null) {
            sRepository = new CommentRepository();
        }
        return sRepository;
    }

    @Override
    public void loadComments(String url, CommentDataSource.CommentsCallback callback) {
        mRemoteDataSource.loadComments(url, callback);
    }


    @Override
    public void comment(Track track, FirebaseUser user, Comment comment
            , CommentDataSource.SendCommentCallback callback) {
        mRemoteDataSource.comment(track, user, comment, callback);
    }

    @Override
    public void listensFirebaseComment(Track track, CommentDataSource.ListenFirebaseCallback callback) {
        mRemoteDataSource.listensFirebaseComment(track, callback);
    }

    @Override
    public void loadRate(Track track, CommentDataSource.RateCallback callback) {
        mRemoteDataSource.loadRate(track, callback);
    }

    @Override
    public void ratingTrack(Track track, double point) {
        mRemoteDataSource.ratingTrack(track, point);
    }
}
