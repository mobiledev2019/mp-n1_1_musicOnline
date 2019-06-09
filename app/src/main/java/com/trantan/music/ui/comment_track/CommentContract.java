package com.trantan.music.ui.comment_track;

import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;

import java.util.List;

interface CommentContract {
    interface View {
        void getCommentsSuccess(List<Comment> comments);

        void showCommnets(List<Comment> comments);

        void sendCommentSuccess();

        void newComment(Comment comment);

        void onLoadedMoreComment(List<Comment> comments, int offset);

        void onToast(String error);

        void onRateLoaded(String point);
    }

    interface Presenter {
        void getComments(int trackId);

        void loadMoreComments(int trackId);

        boolean isLogin();

        FirebaseUser getCurrentUser();

        void sendComment(Track track, FirebaseUser user, Comment comment);

        void listenFirebaseComment(Track track);

        void loadRate(Track track);

        void rateTrack(Track track, Double point);
    }
}
