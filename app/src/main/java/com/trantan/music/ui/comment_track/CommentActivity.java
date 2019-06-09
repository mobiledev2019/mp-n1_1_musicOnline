package com.trantan.music.ui.comment_track;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.trantan.music.R;
import com.trantan.music.data.Comment;
import com.trantan.music.data.Track;
import com.trantan.music.ui.rate.ListenerRatingFragment;
import com.trantan.music.ui.rate.RatingFragment;
import com.trantan.music.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener
        , CommentContract.View, ListenerRatingFragment {
    private static final String TAG = "CommentActivity";
    private static final String ARGUMENT_TRACK = "ARGUMENT_TRACK";
    private static final int INT_ROUNDING_RADIUS = 10;
    private ImageView mImageArtwork;
    private TextView mTextTitle;
    private TextView mTextArtist;
    private TextView mTextComment;
    private TextView mTextPoint;
    private TextView mTextPlay;
    private TextView mTextLike;
    private Track mTrack;
    private View mView;
    private TextView mTextUploadDate;
    private RecyclerView mRecyclerComments;
    private EditText mInputComment;
    private ImageView mImageSend;
    private ProgressBar mProgressBar;
    private TextView mTextLoadMore;
    private CommentAdapter mCommentAdapter;
    private CommentPresenter mPresenter;
    private List<Comment> mComments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initViews();
        initClickListener();
        Bundle bundle = getIntent().getBundleExtra(ARGUMENT_TRACK);
        mTrack = (Track) bundle.getSerializable(ARGUMENT_TRACK);
        bindData(mTrack);
    }

    private void initClickListener() {
        mImageSend.setOnClickListener(this);
        mTextLoadMore.setOnClickListener(this);
        mTextPoint.setOnClickListener(this);
    }

    private void bindData(Track track) {
        Glide.with(this)
                .load(track.getArtworkUrl())
                .apply(new RequestOptions().transforms(new RoundedCorners(INT_ROUNDING_RADIUS)))
                .into(mImageArtwork);
        mTextTitle.setText(track.getTitle());
        mTextArtist.setText(track.getArtist());
        if (mTrack.isOffline()) mView.setVisibility(View.GONE);
        mTextComment.setText(StringUtils.passCount(track.getCommentCount()));
        mTextLike.setText(StringUtils.passCount(track.getLikesCount()));
        mTextPlay.setText(StringUtils.passCount(track.getPlaybackCount()));
        mTextUploadDate.setText(new StringBuilder(mTextUploadDate.getText()).append(track.getCreatedAt()));

        mCommentAdapter = new CommentAdapter(new ArrayList<>());
        mRecyclerComments.setAdapter(mCommentAdapter);

        mPresenter = new CommentPresenter(this);

        mPresenter.getComments(mTrack.getId());
        mPresenter.listenFirebaseComment(mTrack);
        mPresenter.loadRate(mTrack);
    }

    private void initViews() {
        mView = findViewById(R.id.layout_info);
        mImageArtwork = findViewById(R.id.image_artwork);
        mTextTitle = findViewById(R.id.text_title);
        mTextArtist = findViewById(R.id.text_artist);
        mTextComment = findViewById(R.id.text_comment);
        mTextPlay = findViewById(R.id.text_play);
        mTextLike = findViewById(R.id.text_like);
        mTextUploadDate = findViewById(R.id.text_upload_date);
        mRecyclerComments = findViewById(R.id.recycle_comments);
        mInputComment = findViewById(R.id.input_comment);
        mImageSend = findViewById(R.id.image_send);
        mProgressBar = findViewById(R.id.progress_load);
        mTextLoadMore = findViewById(R.id.text_load_more);
        mTextPoint = findViewById(R.id.text_point);
    }

    public static Intent getIntent(Context context, Track track) {
        Intent intent = new Intent(context, CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_TRACK, track);
        intent.putExtra(ARGUMENT_TRACK, bundle);
        return intent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_send:
                sendComment();
                break;

            case R.id.text_load_more:
                mProgressBar.setVisibility(View.VISIBLE);
                mPresenter.loadMoreComments(mTrack.getId());
                break;

            case R.id.text_point:
                showRatingTrack();
                break;
        }
    }

    private void showRatingTrack() {
        FragmentManager manager = getSupportFragmentManager();
        RatingFragment ratingFragment = new RatingFragment();
        ratingFragment.setListenerRatingFragment(this);
        ratingFragment.show(manager, "rate");
    }

    private void sendComment() {
        Comment comment = new Comment();
        if (mInputComment.getText().toString().equals("")) {
            Toast.makeText(this, "Please input commnet!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mPresenter.isLogin()) {
            Toast.makeText(this, "Please login!", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mPresenter.getCurrentUser();
        comment.setUserName(user.getDisplayName());
        comment.setBody(mInputComment.getText().toString());
        comment.setAvatarUrl(user.getPhotoUrl().toString());

        mPresenter.sendComment(mTrack, user, comment);

        mInputComment.setText("");
        mInputComment.clearFocus();
        hideKeyboard(mInputComment);
    }

    @Override
    public void getCommentsSuccess(List<Comment> comments) {
        mComments.addAll(comments);
        showCommnets(mComments);
    }

    @Override
    public void showCommnets(List<Comment> comments) {
        mCommentAdapter.addComments(comments);
        mProgressBar.setVisibility(View.GONE);
        mTextLoadMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendCommentSuccess() {
        onToast("Sent!");
    }

    @Override
    public void newComment(Comment comment) {
        mCommentAdapter.newComment(comment);
    }

    @Override
    public void onLoadedMoreComment(List<Comment> comments, int offset) {
        showCommnets(comments);
        if (offset >= mTrack.getCommentCount()) {
            mTextLoadMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void onToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRateLoaded(String point) {
        mTextPoint.setText(point);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void rated(double point) {
        onToast("Rate : " + point);
        mPresenter.rateTrack(mTrack, point);
    }
}
