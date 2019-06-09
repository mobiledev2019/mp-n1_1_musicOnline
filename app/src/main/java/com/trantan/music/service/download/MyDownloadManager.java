package com.trantan.music.service.download;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.trantan.music.data.Track;
import com.trantan.music.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class MyDownloadManager {
    private static final String TAG = "MyDownloadManager";

    private static final String BASE_FILE_NAME = "%s.mp3";
    private static MyDownloadManager INSTANCE;
    private List<Track> mTracksDownload;
    private Context mContext;
    private DownloadListener mDownloadListener;

    public static MyDownloadManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MyDownloadManager(context);
        }
        return INSTANCE;
    }

    public MyDownloadManager(Context context) {
        mContext = context;
        mTracksDownload = new ArrayList<>();
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    public void removeDownloadListener() {
        mDownloadListener = null;
    }

    public void download(Track track) {
        if (mTracksDownload.contains(track)) return;
        DowloadNotification.setUpNotification(mContext, track);
        AtomicLong timeUpdateProgess = new AtomicLong(System.currentTimeMillis());
        String urlDownload = track.getStreamUrl();
        String fileName = String.format(BASE_FILE_NAME, track.getTitle().replaceAll("/", "_"));
        String path = StringUtils.getRootDirPath(mContext);
        DownloadRequest downloadRequest = PRDownloader.download(urlDownload, path, fileName)
                .build();

        downloadRequest
                .setOnStartOrResumeListener(() -> {
                    Toast.makeText(mContext, "Downloading!!", Toast.LENGTH_SHORT).show();
                })
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    if (System.currentTimeMillis() - timeUpdateProgess.get() > 1000) {
                        timeUpdateProgess.set(System.currentTimeMillis());
                        track.setProgessDownload((int) progressPercent);
                        Log.d(TAG, "download: " + progressPercent);
                        DowloadNotification.upDateNotification(track);
                    }
                })
                .setOnCancelListener(() -> {
                    mTracksDownload.remove(track);
                    Toast.makeText(mContext, "Canceled!!", Toast.LENGTH_SHORT).show();
                    mDownloadListener.onDownloadComplet();
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (mDownloadListener != null) {
                            mDownloadListener.onDownloadComplet();
                        }
                        track.setProgessDownload(100);
                        mTracksDownload.remove(track);
                        DowloadNotification.upDateNotification(track);
                        insertToContentProvider(track, new StringBuilder(path).append("/").append(fileName).toString());
                        Toast.makeText(mContext, "Download Complete!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
        track.setIdDowload(downloadRequest.getDownloadId());

        mTracksDownload.add(track);
    }

    private void insertToContentProvider(Track track, String filePath) {
        ContentValues values = new ContentValues(6);
        values.put(MediaStore.Audio.Media.TITLE, track.getTitle());
        values.put(MediaStore.Audio.Media._ID, track.getId());
        values.put(MediaStore.Audio.Media.DURATION, track.getDuration());
        values.put(MediaStore.Audio.Media.DATA, filePath);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.ARTIST, track.getArtist());
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");

        mContext.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filePath)));
    }

    public List<Track> getTracksDownload() {
        return mTracksDownload;
    }
}
