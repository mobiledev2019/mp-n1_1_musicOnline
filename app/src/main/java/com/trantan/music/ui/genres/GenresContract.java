package com.trantan.music.ui.genres;

import com.trantan.music.data.Genre;

import java.util.List;

public class GenresContract {
    interface View {
        void showGenres(List<Genre> genres);
    }

    interface Presenter {
        void loadGenres();
    }
}
