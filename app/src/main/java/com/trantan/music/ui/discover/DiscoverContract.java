package com.trantan.music.ui.discover;

import com.trantan.music.data.Genre;
import com.trantan.music.data.Track;

import java.util.List;

public class DiscoverContract {
    interface View {
        void showSuggestedTrack(List<Track> tracks);

        void showSuggestedTrackFailure();

        void loadedGenres(List<Genre> genres);
    }

    interface Presenter {
        void loadSuggestedTrack();

        void loadGenres();
    }
}
