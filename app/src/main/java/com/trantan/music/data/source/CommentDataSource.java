package com.trantan.music.data.source;

import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;

import java.util.List;

public interface CommentDataSource {
    interface CommentsCallback {
        void onCommentsLoaded(List<Comment> comments);

        void onFailure(String message);
    }

    interface SendCommentCallback {
        void onSuccess();

        void onFailure(String mess);
    }

    interface RateCallback {
        void onRateLoaded(double point);

        void onFailure(String mess);
    }

    interface ListenFirebaseCallback {
        void onCommentAdded(Comment comment);
    }

    interface Remote {
        void loadComments(String url, CommentsCallback callback);

        void comment(Track track, FirebaseUser user, Comment comment, SendCommentCallback callback);

        void listensFirebaseComment(Track track, ListenFirebaseCallback callback);

        void loadRate(Track track, RateCallback callback);

        void ratingTrack(Track track, double point);
    }
}
