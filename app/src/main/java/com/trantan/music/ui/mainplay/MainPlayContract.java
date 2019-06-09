package com.trantan.music.ui.mainplay;

import com.trantan.music.data.Track;

public class MainPlayContract {
    interface View {
        void showFavorite(boolean isFavorite);
    }

    interface Presenter {
        boolean checkFavorite(Track track);

        void addFavorite(Track track);

        void removeFavorite(Track track);
    }
}
