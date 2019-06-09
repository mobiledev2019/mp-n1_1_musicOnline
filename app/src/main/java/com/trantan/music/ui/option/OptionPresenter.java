package com.trantan.music.ui.option;

import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;

import java.util.List;

public class OptionPresenter implements OptionContract.Presenter {
    private TrackRepository mRepository;
    private OptionContract.View mView;

    public OptionPresenter(TrackRepository repository, OptionContract.View view) {
        mRepository = repository;
        mView = view;
    }

    @Override
    public boolean checkFavorite(Track track) {
        return mRepository.checkFavorite(track);
    }

    @Override
    public void addFavorite(Track track) {
        mRepository.addFavorite(track, new TracksDataSource.FavoriteCallback() {
            @Override
            public void onFavoritesGetted(List<Track> tracks) {

            }

            @Override
            public void onFavoriteAdded() {
                mView.showFavorite(true);
            }

            @Override
            public void onFavoriteRemoved() {

            }

            @Override
            public void onFailure() {

            }

            @Override
            public void isFavorite(Boolean isFavorite) {

            }
        });
    }

    @Override
    public void removeFavorite(Track track) {
        mRepository.removeFavorite(track, new TracksDataSource.FavoriteCallback() {
            @Override
            public void onFavoritesGetted(List<Track> tracks) {

            }

            @Override
            public void onFavoriteAdded() {

            }

            @Override
            public void onFavoriteRemoved() {
                mView.showFavorite(false);
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void isFavorite(Boolean isFavorite) {

            }
        });
    }
}
