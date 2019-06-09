package com.trantan.music.ui.tracks;

import com.trantan.music.data.Track;

import java.util.List;

public class TracksContract {
    interface View {
        void loadTracks(List<Track> tracks);
    }

    interface Presenter {
        void getTracks();
    }
}
