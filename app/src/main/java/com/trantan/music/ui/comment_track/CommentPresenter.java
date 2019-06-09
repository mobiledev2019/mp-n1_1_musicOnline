package com.trantan.music.ui.comment_track;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.CommentDataSource;
import com.trantan.music.data.source.CommentRepository;
import com.trantan.music.utils.StringUtils;

import java.util.List;

public class CommentPresenter implements CommentContract.Presenter {
    private static final String TAG = "CommentPresenter";
    private CommentRepository mCommentRepository;
    private CommentContract.View mView;
    private static final int LIMMIT = 30;
    private int mOffset;
    private FirebaseAuth mFirebaseAuth;

    public CommentPresenter(CommentContract.View view) {
        mView = view;
        mCommentRepository = CommentRepository.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getComments(int trackId) {
        String url = StringUtils.initCommentsUrl(trackId, LIMMIT, mOffset);
        mCommentRepository.loadComments(url, new CommentDataSource.CommentsCallback() {
            @Override
            public void onCommentsLoaded(List<Comment> comments) {
                mView.getCommentsSuccess(comments);
            }

            @Override
            public void onFailure(String message) {
                mView.onToast(message);
            }
        });
    }

    @Override
    public void loadMoreComments(int trackId) {
        mOffset += LIMMIT;
        String url = StringUtils.initCommentsUrl(trackId, LIMMIT, mOffset);
        mCommentRepository.loadComments(url, new CommentDataSource.CommentsCallback() {
            @Override
            public void onCommentsLoaded(List<Comment> comments) {
                mView.onLoadedMoreComment(comments, mOffset);
            }

            @Override
            public void onFailure(String message) {
                mView.onToast(message);
            }
        });
    }

    @Override
    public boolean isLogin() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    @Override
    public void sendComment(Track track, FirebaseUser user, Comment comment) {
        mCommentRepository.comment(track, user, comment, new CommentDataSource.SendCommentCallback() {
            @Override
            public void onSuccess() {
                mView.sendCommentSuccess();
            }

            @Override
            public void onFailure(String mess) {
                mView.onToast(mess);
            }
        });
    }

    @Override
    public void listenFirebaseComment(Track track) {
        mCommentRepository.listensFirebaseComment(track, comment -> mView.newComment(comment));
    }

    @Override
    public void loadRate(Track track) {
        mCommentRepository.loadRate(track, new CommentDataSource.RateCallback() {
            @Override
            public void onRateLoaded(double point) {
                mView.onRateLoaded(StringUtils.formatRate(point));
            }

            @Override
            public void onFailure(String mess) {
                mView.onToast(mess);
            }
        });
    }

    @Override
    public void rateTrack(Track track, Double point) {
        mCommentRepository.ratingTrack(track, point);
    }
}
