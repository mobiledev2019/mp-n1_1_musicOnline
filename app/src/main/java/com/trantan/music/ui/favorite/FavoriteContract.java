package com.trantan.music.ui.favorite;

import com.trantan.music.data.Track;

import java.util.List;

public class FavoriteContract {
    interface View {
        void loadFavorites(List<Track> tracks);
    }

    interface Presenter {
        void getFavorite();

        void addFavorite(Track track);

        void removeFavorite(Track track);
    }
}

