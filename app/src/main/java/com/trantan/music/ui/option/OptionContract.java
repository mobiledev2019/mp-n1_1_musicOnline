package com.trantan.music.ui.option;

import com.trantan.music.data.Track;

public class OptionContract {
    interface View {
        void showFavorite(boolean isFavorite);
    }

    interface Presenter {
        boolean checkFavorite(Track track);

        void addFavorite(Track track);

        void removeFavorite(Track track);
    }
}
