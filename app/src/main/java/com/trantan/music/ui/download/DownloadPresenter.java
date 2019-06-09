package com.trantan.music.ui.download;

import com.downloader.PRDownloader;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.TracksDataSource;
import com.trantan.music.service.download.DownloadListener;
import com.trantan.music.service.download.MyDownloadManager;

import java.util.List;

public class DownloadPresenter implements DownloadContract.Presenter {
    private TrackRepository mRepository;
    private DownloadContract.View mView;
    private MyDownloadManager mMyDownloadManager;

    public DownloadPresenter(TrackRepository repository,
                             MyDownloadManager myDownloadManager,
                             DownloadContract.View view) {
        mRepository = repository;
        mView = view;
        mMyDownloadManager = myDownloadManager;
    }

    @Override
    public void getTracksDownloaded() {
        mRepository.getTracksDownloaded(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.loadTracksDownloaded(tracks);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void getTracksDownloading() {
        mView.loadTracksDownloadding(mMyDownloadManager.getTracksDownload());
    }

    @Override
    public void removeDownloadListener() {
        mMyDownloadManager.removeDownloadListener();
    }

    @Override
    public void addDownloadListener(DownloadListener downloadListener) {
        mMyDownloadManager.setDownloadListener(downloadListener);
    }

    @Override
    public void onCancel(int downloadId) {
        PRDownloader.cancel(downloadId);
    }

    @Override
    public void onPause(int downloadId) {
        PRDownloader.pause(downloadId);
    }

    @Override
    public void onResume(int downloadId) {
        PRDownloader.resume(downloadId);
    }
}
