package com.trantan.music.ui.download;

import com.trantan.music.data.Track;
import com.trantan.music.service.download.DownloadListener;

import java.util.List;

public class DownloadContract {
    interface View {
        void loadTracksDownloaded(List<Track> tracks);

        void loadTracksDownloadding(List<Track> tracks);
    }

    interface Presenter {
        void getTracksDownloaded();

        void getTracksDownloading();

        void removeDownloadListener();

        void addDownloadListener(DownloadListener downloadListener);

        void onCancel(int downloadId);

        void onPause(int downloadId);

        void onResume(int downloadId);
    }

    public interface Callback {
        void deleteTrack();
    }
}
