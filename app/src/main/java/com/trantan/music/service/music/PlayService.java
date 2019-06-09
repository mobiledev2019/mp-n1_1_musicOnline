package com.trantan.music.service.music;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.tracksplayer.PlayerManager;
import com.trantan.music.tracksplayer.PlayerSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayService extends Service
        implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        PlayServiceInterface {
    private static final String TAG = "PlayService";
    public static final String ACTION_PREVIOUS = "com.trantan.music.ACTION_PREVIOUS";
    public static final String ACTION_PLAY_PAUSE = "com.trantan.music.ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.trantan.music.ACTION_NEXT";
    public static final String ACTION_CLOSE = "com.trantan.music.ACTION_CLOSE";
    private static final int PAUSE_TRACK = 0;
    private PlayerManager mPlayerManager;
    private IBinder mBinder;
    private List<PlayServiceListener> mListeners;
    private CurrentTimeTask mTimeTask;
    private ChangeNotificationTask mNotiTask;
    private boolean isBind;
    private int mTimeRemain;
    private Timer mTimer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PAUSE_TRACK) {
                stopTimer();
                pauseTrack();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyBinder();
        mPlayerManager = PlayerManager.getIntance(this);
        mListeners = new ArrayList<>();
        mTimeTask = new CurrentTimeTask();
        mTimeTask.execute(this);
        mTimer = new Timer();

        mNotiTask = new ChangeNotificationTask();
        mNotiTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBind = true;
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) return START_NOT_STICKY;
        switch (intent.getAction()) {
            case ACTION_PREVIOUS:
                previousTrack();
                break;
            case ACTION_PLAY_PAUSE:
                if (isPlaying()) pauseTrack();
                else startTrack();
                PlayNotification.upDateImagePlay(isPlaying());
                break;
            case ACTION_NEXT:
                nextTrack();
                break;
            case ACTION_CLOSE:
                if (!isBind) {
                    mTimeTask.setRunable(false);
                    mPlayerManager.release();
                    stopSelf();
                } else {
                    pauseTrack();
                    stopForeground(true);
                }
                break;
            default:
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBind = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mPlayerManager.release();
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        for (PlayServiceListener listener : mListeners) {
            listener.listenPlayingState(isPlaying());
        }
        switch (mPlayerManager.getLoopType()) {
            case PlayerSetting.LoopType.NONE:
                if (mPlayerManager.isEndOfList()) {
                    pauseTrack();
                } else nextTrack();
                break;
            case PlayerSetting.LoopType.ALL:
                nextTrack();
                break;
            case PlayerSetting.LoopType.ONE:
                mPlayerManager.start();
                synTrackPlaying();
                break;
            default:
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        onFailure();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayerManager.start();
        Track track = mPlayerManager.getCurrentTrack();
        Notification notification = PlayNotification.setUpNotification(this, track);
        PlayNotification.upDateImagePlay(isPlaying());
        startForeground(PlayNotification.NOTIFICATION_ID, notification);

        for (PlayServiceListener listener : mListeners) {
            listener.listenPrepared();
        }
    }

    @Override
    public void onFailure() {
        Toast.makeText(this,
                String.format(getString(R.string.err_service_on_failure), getCurrentTrack().getTitle()),
                Toast.LENGTH_SHORT).show();
        nextTrack();
    }

    @Override
    public void changedTrack(Track track) {
        mPlayerManager.changeTrack(track);
        synTrackPlaying();
    }

    private void synTrackPlaying() {
        PlayNotification.upDateImagePlay(isPlaying());
        for (PlayServiceListener listener : mListeners) {
            listener.listenChangeSong(mPlayerManager.getCurrentTrack());
        }
    }

    @Override
    public void startTrack() {
        Track track = mPlayerManager.getCurrentTrack();
        Notification notification = PlayNotification.setUpNotification(this, track);
        PlayNotification.upDateImagePlay(isPlaying());
        startForeground(PlayNotification.NOTIFICATION_ID, notification);

        mPlayerManager.start();
        synPlayingState();
    }

    @Override
    public void pauseTrack() {
        stopForeground(false);
        mPlayerManager.pause();
        synPlayingState();
    }

    private void synPlayingState() {
        PlayNotification.upDateImagePlay(isPlaying());
        PlayNotification.upDataTrack(getCurrentTrack(), false);
        for (PlayServiceListener listener : mListeners) {
            listener.listenPlayingState(isPlaying());
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }

    @Override
    public void seek(int position) {
        mPlayerManager.seek(position);
    }

    @Override
    public Track getCurrentTrack() {
        return mPlayerManager.getCurrentTrack();
    }

    @Override
    public void nextTrack() {
        mPlayerManager.nextTrack();
        synTrackPlaying();
    }

    @Override
    public void previousTrack() {
        mPlayerManager.previousTrack();
        synTrackPlaying();
    }

    @Override
    public void shuffledTracks() {
        mPlayerManager.shuffledTracks();
    }

    @Override
    public void unShuffledTracks() {
        mPlayerManager.unShuffledTracks();
    }

    @Override
    public void setLoopType(int type) {
        mPlayerManager.setLoopType(type);
    }

    @Override
    public void addTrack(Track track) {
        mPlayerManager.addTrack(track);
        if (getTracks().size() == 1) changedTrack(track);
    }

    @Override
    public List<Track> getTracks() {
        return mPlayerManager.getTracks();
    }

    @Override
    public void addPlayServiceListener(PlayServiceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public int getShuffleType() {
        return mPlayerManager.getShuffleType();
    }

    @Override
    public int getLoopType() {
        return mPlayerManager.getLoopType();
    }

    @Override
    public void removeTrack(Track track) {
        mPlayerManager.removeTrack(track);
    }

    @Override
    public void removeListener(PlayServiceListener listener) {
        for (int i = 0; i < mListeners.size(); i++) {
            if (mListeners.get(i) == listener) {
                mListeners.remove(i);
            }
        }
    }

    @Override
    public void clearTracks() {
        mPlayerManager.clearTracks();
    }

    @Override
    public void addTracks(List<Track> tracks) {
        mPlayerManager.addTracks(tracks);
    }

    @Override
    public int getAudioSessionId() {
        return mPlayerManager.getAudioSessionId();
    }

    public int getTimeRemain() {
        return mTimeRemain;
    }

    public void setMinutes(int minutes) {
        mTimeRemain = minutes * 60;
    }

    public void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeRemain--;
                if (mTimeRemain <= 0) {
                    mTimeRemain = 0;
                    mHandler.sendEmptyMessage(PAUSE_TRACK);
                }
            }
        }, 1000, 1000);
    }

    public void stopTimer() {
        if (mTimer != null) mTimer.cancel();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PlayService.class);
        return intent;
    }

    public class MyBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    private static class CurrentTimeTask extends AsyncTask<PlayService, Integer, Void> {
        private static final long SLEEP_TIME = 1000;
        private boolean mRunable;
        PlayService mService;

        public CurrentTimeTask() {
            mRunable = true;
        }

        public void setRunable(boolean runable) {
            mRunable = runable;
        }

        @Override
        protected Void doInBackground(PlayService... playServices) {
            mService = playServices[0];
            while (mRunable) {
                publishProgress(mService.mPlayerManager.getCurrentTime());
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    mService.onFailure();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            for (PlayServiceListener listener : mService.mListeners) {
                listener.listenCurrentTime(values[0]);
            }
        }
    }

    private static class ChangeNotificationTask extends AsyncTask<PlayService, Track, Void> {
        private static final long SLEEP_TIME = 6000;
        private boolean mRunable = true;
        PlayService mService;

        public void setRunable(boolean runable) {
            mRunable = runable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(PlayService... playServices) {
            mService = playServices[0];
            while (mRunable) {
                if (mService.mPlayerManager.getTracks().size() <= 0) continue;
                if (mService.isPlaying()) {
                    try {
                        publishProgress(mService.getCurrentTrack());
                        Thread.sleep(SLEEP_TIME);
                        if (mService.getCurrentTrack().equals(mService.mPlayerManager.getNextTrack())) {
                            continue;
                        }
                        publishProgress(mService.mPlayerManager.getNextTrack());
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        mService.onFailure();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Track... values) {
            super.onProgressUpdate(values);
            Track track = values[0];
            boolean isNextTrack = !track.equals(mService.getCurrentTrack());

            PlayNotification.upDataTrack(track, isNextTrack);
        }
    }
}
