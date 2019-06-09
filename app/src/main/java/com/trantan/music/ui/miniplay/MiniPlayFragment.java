package com.trantan.music.ui.miniplay;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.service.music.PlayServiceListener;
import com.trantan.music.ui.BaseAcivity;
import com.trantan.music.ui.mainplay.MainPlayActivity;
import com.trantan.music.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayFragment extends Fragment implements PlayServiceListener,
        View.OnClickListener {
    private static final String ANIMATION_TYPE = "rotation";
    private static final float FROM_DEGREE = 0;
    private static final float TO_DEGREE = 360;
    private static final long TIME_ROTATE = 20000;
    private ImageView mImageArtwork;
    private ImageView mImageNext;
    private ImageView mImagePlay;
    private ImageView mImagePrevious;
    private TextView mTextTrack;
    private TextView mTextArtist;
    private ProgressBar mProgressTime;
    private ProgressBar mProgressLoad;
    private PlayService mService;
    private ServiceConnection mConnection;
    private BaseAcivity mActivity;
    private ObjectAnimator mObjectAnimator;
    private AnimatorSet mAnimatorSet;

    public MiniPlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_play, container, false);
        initUi(view);
        initEvent(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        if (mService != null) {
            if (mService.getCurrentTrack() != null) {
                bindData(mService.getCurrentTrack());
                mActivity.showMiniPlayFragment(true);
                listenPrepared();
            }
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mService.removeListener(this);
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }

    private void initEvent(View view) {
        mImagePrevious.setOnClickListener(this);
        mImagePlay.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
        view.setOnClickListener(this);
    }

    public void bindData(Track track) {
        mProgressLoad.setVisibility(View.VISIBLE);
        mImagePlay.setVisibility(View.GONE);

        mTextTrack.setText(track.getTitle());
        mTextArtist.setText(track.getArtist());
        GlideApp.with(getContext())
                .load(track.getArtworkUrl())
                .circleCrop()
                .into(mImageArtwork);
        mImagePlay.setVisibility(View.GONE);
        mProgressTime.setMax(track.getDuration());
    }

    private void initUi(View view) {
        mActivity = (BaseAcivity) getActivity();
        mImageArtwork = view.findViewById(R.id.image_artwork);
        mTextTrack = view.findViewById(R.id.text_name);
        mTextArtist = view.findViewById(R.id.text_artist);
        mImageNext = view.findViewById(R.id.image_next);
        mImagePlay = view.findViewById(R.id.image_play);
        mImagePrevious = view.findViewById(R.id.image_previous);
        mProgressTime = view.findViewById(R.id.progress_time);
        mProgressLoad = view.findViewById(R.id.progress_load);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayService.MyBinder myBinder = (PlayService.MyBinder) service;
                mService = myBinder.getService();
                mService.addPlayServiceListener(MiniPlayFragment.this);
                if (mService.getCurrentTrack() != null) {
                    bindData(mService.getCurrentTrack());
                    mActivity.showMiniPlayFragment(true);
                    listenPrepared();
                    listenPlayingState(mService.isPlaying());

                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent intent = PlayService.getIntent(getContext());
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        initAnimation();
    }

    @Override
    public void listenCurrentTime(int currentTime) {
        mProgressTime.setProgress(currentTime);
    }

    @Override
    public void listenChangeSong(Track track) {
        bindData(track);
        mActivity.showMiniPlayFragment(true);
    }

    @Override
    public void listenPlayingState(boolean isPlaying) {
        if (isPlaying) {
            mImagePlay.setImageResource(R.drawable.ic_pause);
            if ((mObjectAnimator.isStarted())) {
                mObjectAnimator.resume();
            } else {
                mObjectAnimator.start();
            }
        }
        else {
            mObjectAnimator.pause();
            mImagePlay.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void listenPrepared() {
        bindData(mService.getCurrentTrack());
        mProgressLoad.setVisibility(View.GONE);
        mImagePlay.setVisibility(View.VISIBLE);
        listenPlayingState(mService.isPlaying());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_previous:
                mService.nextTrack();
                break;
            case R.id.image_play:
                if (mService.isPlaying()) mService.pauseTrack();
                else mService.startTrack();
                break;
            case R.id.image_next:
                mService.nextTrack();
                break;
            default:
                Intent intent = MainPlayActivity.getIntent(getContext());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
        }
    }
    private void initAnimation() {
        mObjectAnimator = ObjectAnimator.ofFloat(mImageArtwork, ANIMATION_TYPE, FROM_DEGREE, TO_DEGREE);
        mObjectAnimator.setDuration(TIME_ROTATE);
        mObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mObjectAnimator);
    }
}
