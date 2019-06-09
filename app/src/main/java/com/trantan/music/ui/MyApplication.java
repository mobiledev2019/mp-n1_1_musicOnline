package com.trantan.music.ui;

import android.app.Application;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setConnectTimeout(30000)
                .setReadTimeout(30000)
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(this, config);
    }
}
