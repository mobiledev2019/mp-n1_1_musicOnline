package com.trantan.music.service.music;

import com.trantan.music.data.Track;

public interface PlayServiceListener {
    void listenCurrentTime(int currentTime);

    void listenChangeSong(Track track);

    void listenPlayingState(boolean isPlaying);

    void listenPrepared();
}
