package com.trantan.music.service.music;

import com.trantan.music.data.Track;
import com.trantan.music.tracksplayer.PlayerSetting;

import java.util.List;

public interface PlayServiceInterface {
    void onFailure();

    void changedTrack(Track track);

    void startTrack();

    void pauseTrack();

    boolean isPlaying();

    void seek(int position);

    Track getCurrentTrack();

    void nextTrack();

    void previousTrack();

    void shuffledTracks();

    void unShuffledTracks();

    void setLoopType(@PlayerSetting.LoopType int type);

    void addTrack(Track track);

    void addTracks(List<Track> tracks);

    List<Track> getTracks();

    void addPlayServiceListener(PlayServiceListener listener);

    int getShuffleType();

    int getLoopType();

    void removeTrack(Track track);

    void removeListener(PlayServiceListener listener);

    void clearTracks();

    int getAudioSessionId();
}
