package com.trantan.music.ui.favorite;

import android.util.Log;

import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;

import java.util.List;

public class FavoritePresenter implements FavoriteContract.Presenter {
    private static final String TAG = "FavoritePresenter";
    private TrackRepository mRepository;
    private FavoriteContract.View mView;

    public FavoritePresenter(TrackRepository repository, FavoriteContract.View view) {
        mRepository = repository;
        mView = view;
    }

    @Override
    public void getFavorite() {
        mRepository.getFavorites(new TracksDataSource.FavoriteCallback() {
            @Override
            public void onFavoritesGetted(List<Track> tracks) {
                mView.loadFavorites(tracks);
            }

            @Override
            public void onFavoriteAdded() {

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
    public void addFavorite(Track track) {
        mRepository.addFavorite(track, new TracksDataSource.FavoriteCallback() {
            @Override
            public void onFavoritesGetted(List<Track> tracks) {

            }

            @Override
            public void onFavoriteAdded() {
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
