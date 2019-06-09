package com.trantan.music.ui.detail_genre;

import com.trantan.music.data.Genre;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;
import com.trantan.music.utils.StringUtils;

import java.util.List;

public class DetailGenrePresenter implements DetailGenreContract.Presenter {
    private static final String GENRE_KIND = "top";
    private static final int LIMIT = 100;
    private static final int OFFSET = 0;
    private TrackRepository mRepository;
    private DetailGenreContract.View mView;

    public DetailGenrePresenter(TrackRepository repository, DetailGenreContract.View view) {
        mRepository = repository;
        mView = view;
    }

    @Override
    public void loadTracks(Genre genre) {
        String url = StringUtils.initGenreApi(GENRE_KIND, genre.getKeyGenre(), LIMIT, OFFSET);
        mRepository.loadTracks(url, new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.showTracks(tracks);
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
