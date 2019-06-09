package com.trantan.music.ui.detail_genre;

import com.trantan.music.data.Genre;
import com.trantan.music.data.Track;

import java.util.List;

public class DetailGenreContract {
    interface View {
        void showTracks(List<Track> tracks);
    }

    interface Presenter {
        void loadTracks(Genre genre);
    }
}
