package com.trantan.music.ui.genres;

import com.trantan.music.data.Genre;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;

import java.util.List;

public class GenresPresenter implements GenresContract.Presenter {
    private TrackRepository mRepository;
    private GenresContract.View mView;

    public GenresPresenter(TrackRepository repository, GenresContract.View view) {
        mRepository = repository;
        mView = view;
    }

    @Override
    public void loadGenres() {
        mRepository.getGenres(new TracksDataSource.GetGenresCallback() {
            @Override
            public void onGenresGetted(List<Genre> genres) {
                mView.showGenres(genres);
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
