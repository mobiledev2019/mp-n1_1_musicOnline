package com.trantan.music.service.download;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.trantan.music.R;
import com.trantan.music.data.Track;

public class DowloadNotification {
    private static final int REQUEST_CODE = 0;
    private static final String NAME_CHANNEL = "com.trantan.music.NAME_CHANNEL";
    private static final String ID_CHANNEL = "com.trantan.music.ID_CHANNEL";
    private static NotificationCompat.Builder sBuilder;
    private static NotificationManager sManager;

    public static void setUpNotification(Context context, Track track) {
        sBuilder = new NotificationCompat.Builder(context, ID_CHANNEL);
        sBuilder.setContentText("Downloading... ")
                .setContentTitle(track.getTitle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, 0, false)
                .setSmallIcon(R.drawable.ic_music_24dp)
                .setAutoCancel(true);
        sManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        sManager.notify(track.getId(), sBuilder.build());
    }

    public static void upDateNotification(Track track) {
        sBuilder.setContentText("Download : " + track.getProgessDownload().get() + "%")
                .setContentTitle(track.getTitle())
                .setProgress(100, track.getProgessDownload().get(), false);
        sManager.notify(track.getId(), sBuilder.build());
    }
}
