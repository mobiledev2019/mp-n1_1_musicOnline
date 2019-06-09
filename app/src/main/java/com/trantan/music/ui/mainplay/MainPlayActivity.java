package com.trantan.music.ui.mainplay;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gauravk.audiovisualizer.model.AnimSpeed;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.local.LocalDataSource;
import com.trantan.music.data.source.remote.TracksRemoteDataSource;
import com.trantan.music.service.download.MyDownloadManager;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.service.music.PlayServiceListener;
import com.trantan.music.tracksplayer.PlayerSetting;
import com.trantan.music.ui.comment_track.CommentActivity;
import com.trantan.music.ui.dialog_timer.DialogTimer;
import com.trantan.music.ui.playing_list.PlayingListFragment;
import com.trantan.music.utils.BlurTransformation;
import com.trantan.music.utils.StringUtils;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainPlayActivity extends AppCompatActivity implements PlayServiceListener,
        View.OnClickListener,
        MainPlayContract.View {
    private static final String TAG = "MainPlayActivity";
    private static final String SHARE_VALUE = "Track: %s\nArtits: %s\nLink: %s";
    private static final int MY_REQUEST_READ_STORAGE = 1;
    private static final String SHARE_TYPE = "text/plain";
    private static final String ANIMATION_TYPE = "rotation";
    private static final float FROM_DEGREE = 0;
    private static final float TO_DEGREE = 360;
    private static final long TIME_ROTATE = 20000;
    private ImageView mImageTrack;
    private ImageView mImageBackground;
    private ImageView mImageBack;
    private ImageView mImageTracks;
    private ImageView mImageDownload;
    private ImageView mImageFavorite;
    private ImageView mImageShare;
    private ImageView mImageComment;
    private ImageView mImageTimer;
    private ImageView mImageShuffle;
    private ImageView mImagePrevious;
    private ImageView mImagePlay;
    private ImageView mImageNext;
    private ImageView mImageLoop;
    private TextView mTextTrack;
    private TextView mTextArtis;
    private TextView mTextCurrentTime;
    private TextView mTextDuration;
    private CircularSeekBar mSeekBar;
    private PlayService mService;
    private ServiceConnection mConnection;
    private MainPlayContract.Presenter mPresenter;
    private ObjectAnimator mObjectAnimator;
    private AnimatorSet mAnimatorSet;
//    private WaveVisualizer mWaveVisualizer;
    private BarVisualizer mBarVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_play);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        initUi();
        initPresenter();
    }

    private void initPresenter() {
        mPresenter = new MainPlayPresenter(TrackRepository.getInstance(TracksRemoteDataSource.getInstance(),
                LocalDataSource.getInstance(this)), this);
    }

    @Override
    protected void onStart() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayService.MyBinder myBinder = (PlayService.MyBinder) service;
                mService = myBinder.getService();
                mService.addPlayServiceListener(MainPlayActivity.this);
                bindData(mService.getCurrentTrack());
                initEvent();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = PlayService.getIntent(this);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mService.removeListener(this);
        unbindService(mConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (mBarVisualizer != null) {
            mBarVisualizer.release();
        }
        super.onDestroy();

    }

    private void initUi() {
        mImageBackground = findViewById(R.id.image_background);
        mImageBack = findViewById(R.id.image_back);
        mImageTracks = findViewById(R.id.image_tracks);
        mImageTrack = findViewById(R.id.image_artwork);
        mImageDownload = findViewById(R.id.image_download);
        mImageFavorite = findViewById(R.id.image_favorite);
        mImageShare = findViewById(R.id.image_share);
        mImageComment = findViewById(R.id.image_comment);
        mImageShuffle = findViewById(R.id.image_shuffle);
        mImagePrevious = findViewById(R.id.image_previous);
        mImagePlay = findViewById(R.id.image_play);
        mImageNext = findViewById(R.id.image_next);
        mImageLoop = findViewById(R.id.image_loop);
        mTextTrack = findViewById(R.id.text_track);
        mTextArtis = findViewById(R.id.text_artist);
        mTextCurrentTime = findViewById(R.id.text_current_time);
        mTextDuration = findViewById(R.id.text_duration);
        mSeekBar = findViewById(R.id.circle_seekbar);
        mImageTimer = findViewById(R.id.image_timer);
        initAnimation();
//        mWaveVisualizer = findViewById(R.id.wave_visualizer);
//        mWaveVisualizer.setAnimationSpeed(AnimSpeed.FAST);
//        mWaveVisualizer.setEnabled(false);

        mBarVisualizer = findViewById(R.id.bar_visualizer);
    }

    private void initAnimation() {
        mObjectAnimator = ObjectAnimator.ofFloat(mImageTrack, ANIMATION_TYPE, FROM_DEGREE, TO_DEGREE);
        mObjectAnimator.setDuration(TIME_ROTATE);
        mObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mObjectAnimator);
    }

    private void initEvent() {
        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            int mProgress;

            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                mProgress = (int) progress;
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                mService.seek(mProgress);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        mImageShuffle.setOnClickListener(this);
        mImageLoop.setOnClickListener(this);
        mImagePrevious.setOnClickListener(this);
        mImagePlay.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
        mImageDownload.setOnClickListener(this);
        mImageFavorite.setOnClickListener(this);
        mImageComment.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
        mImageTracks.setOnClickListener(this);
        mImageShare.setOnClickListener(this);
        mImageTimer.setOnClickListener(this);
    }

    private void bindData(Track currentTrack) {
        mTextTrack.setText(currentTrack.getTitle());
        mTextArtis.setText(currentTrack.getArtist());
        mTextDuration.setText(StringUtils.passTimeToString(currentTrack.getDuration()));
        mSeekBar.setMax(currentTrack.getDuration());
        showFavorite(mPresenter.checkFavorite(currentTrack));
        if (currentTrack.isOffline()) {
            mImageDownload.setVisibility(View.GONE);
            mImageComment.setVisibility(View.GONE);
            mImageFavorite.setVisibility(View.GONE);
            mImageShare.setVisibility(View.GONE);
        }
        Glide.with(mImageTrack)
                .load(currentTrack.getBigArtworkUrl())
                .apply(new RequestOptions().placeholder(R.drawable.default_artwork).transforms(new CircleCrop()))
                .into(mImageTrack);
        Glide.with(mImageBackground)
                .load(currentTrack.getArtworkUrl())
                .apply(new RequestOptions().transforms(new BlurTransformation(this)))
                .into(mImageBackground);

        loadShuffleState();

        loadLoopState();

        loadPlayingState();

        updateLoopState();

        updateShuffleState();
    }

    private void loadPlayingState() {
        if (mService.isPlaying()) {
            mImagePlay.setImageResource(R.drawable.ic_pause);
            if ((mObjectAnimator.isStarted())) {
                mObjectAnimator.resume();
            } else {
                mObjectAnimator.start();
            }
        } else {
            mImagePlay.setImageResource(R.drawable.ic_play);
            mObjectAnimator.pause();
        }
        try {
//            mWaveVisualizer.setAudioSessionId(mService.getAudioSessionId());
            mBarVisualizer.setAudioSessionId(mService.getAudioSessionId());
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void loadShuffleState() {
        if (mService.getShuffleType() == PlayerSetting.ShuffleType.ON)
            mImageShuffle.setImageResource(R.drawable.ic_shuffle);
        else mImageShuffle.setImageResource(R.drawable.ic_not_shuffle);
    }

    private void loadLoopState() {
        if (mService.getLoopType() == PlayerSetting.LoopType.NONE) {
            mImageLoop.setImageResource(R.drawable.ic_not_loop);
        } else if (mService.getLoopType() == PlayerSetting.LoopType.ONE) {
            mImageLoop.setImageResource(R.drawable.ic_loop_one);
        } else {
            mImageLoop.setImageResource(R.drawable.ic_loop);
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainPlayActivity.class);
        return intent;
    }

    @Override
    public void listenCurrentTime(int currentTime) {
        mSeekBar.setProgress(currentTime);
        mTextCurrentTime.setText(StringUtils.passTimeToString(currentTime));
    }

    @Override
    public void listenChangeSong(Track track) {
        mObjectAnimator.end();
        bindData(track);
    }

    @Override
    public void listenPlayingState(boolean isPlaying) {
        loadPlayingState();
    }

    @Override
    public void listenPrepared() {
        loadPlayingState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.image_download:
                checkWriteStoragePremission();
                break;
            case R.id.image_favorite:
                onClickFavorite();
                break;
            case R.id.image_shuffle:
                changeShuffleState();
                break;
            case R.id.image_previous:
                mService.previousTrack();
                break;
            case R.id.image_play:
                onClickPlayPause();
                break;
            case R.id.image_next:
                mService.nextTrack();
                break;
            case R.id.image_loop:
                changeLoopState();
                break;
            case R.id.image_tracks:
                showPlayingList();
                break;
            case R.id.image_share:
                shareTrack();
                break;
            case R.id.image_comment:
                showCommentActivity();
                break;
            case R.id.image_timer:
                pickTime();
            default:
        }
    }

    private void pickTime() {
        FragmentManager manager = getSupportFragmentManager();
        new DialogTimer().show(manager, "timer");
    }

    private void showCommentActivity() {
        Intent intent = CommentActivity.getIntent(this, mService.getCurrentTrack());
        startActivity(intent);
    }

    private void showPlayingList() {
        PlayingListFragment instance = new PlayingListFragment();
        instance.show(getSupportFragmentManager(), instance.getTag());
    }

    private void onClickPlayPause() {
        if (mService.isPlaying()) mService.pauseTrack();
        else mService.startTrack();
        loadPlayingState();
    }

    private void onClickFavorite() {
        if (mPresenter.checkFavorite(mService.getCurrentTrack())) {
            mPresenter.removeFavorite(mService.getCurrentTrack());
        } else {
            mPresenter.addFavorite(mService.getCurrentTrack());
        }
    }

    private void shareTrack() {
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType(SHARE_TYPE);
        String value = String.format(SHARE_VALUE,
                mService.getCurrentTrack().getTitle(),
                mService.getCurrentTrack().getArtist(),
                mService.getCurrentTrack().isOffline() ? null : mService.getCurrentTrack().getStreamUrl());
        myShareIntent.putExtra(Intent.EXTRA_TEXT, value);
        startActivity(Intent.createChooser(myShareIntent, mService.getCurrentTrack().getTitle()));
    }

    private void checkWriteStoragePremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            MyDownloadManager.getInstance(getApplicationContext()).download(mService.getCurrentTrack());
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_REQUEST_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_REQUEST_READ_STORAGE) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MyDownloadManager.getInstance(getApplicationContext()).download(mService.getCurrentTrack());
        } else {
            Toast.makeText(this, getString(R.string.err_download_permistion_false),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void changeLoopState() {
        switch (mService.getLoopType()) {
            case PlayerSetting.LoopType.NONE:
                mService.setLoopType(PlayerSetting.LoopType.ALL);
                break;
            case PlayerSetting.LoopType.ALL:
                mService.setLoopType(PlayerSetting.LoopType.ONE);
                break;
            case PlayerSetting.LoopType.ONE:
                mService.setLoopType(PlayerSetting.LoopType.NONE);
                break;
            default:
        }
        updateLoopState();
    }

    private void changeShuffleState() {
        if (mService.getShuffleType() == PlayerSetting.ShuffleType.OFF) mService.shuffledTracks();
        else mService.unShuffledTracks();

        updateShuffleState();
    }

    private void updateShuffleState() {
        if (mService.getShuffleType() == PlayerSetting.ShuffleType.OFF) {
            mImageShuffle.setImageResource(R.drawable.ic_not_shuffle);
        } else mImageShuffle.setImageResource(R.drawable.ic_shuffle);
    }

    private void updateLoopState() {
        if (mService.getLoopType() == PlayerSetting.LoopType.NONE) {
            mImageLoop.setImageResource(R.drawable.ic_not_loop);
        } else if (mService.getLoopType() == PlayerSetting.LoopType.ONE) {
            mImageLoop.setImageResource(R.drawable.ic_loop_one);
        } else {
            mImageLoop.setImageResource(R.drawable.ic_loop);
        }
    }

    @Override
    public void showFavorite(boolean isFavorite) {
        if (isFavorite) mImageFavorite.setImageResource(R.drawable.ic_favorited_24dp);
        else mImageFavorite.setImageResource(R.drawable.ic_favorite_24dp);
    }
}
