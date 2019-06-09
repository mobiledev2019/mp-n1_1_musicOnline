package com.trantan.music.utils;

import android.content.Context;
import android.os.Environment;

import com.trantan.music.BuildConfig;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {
    private static final String MILLION = "%.1fM";
    private static final String THOUSAND = "%.1fK";
    private static String TIME_FORMAT = "%02d:%02d";

    public static String initGenreApi(String kind, String keyGenre, int limit, int offset) {
        return String.format(Locale.US, Constants.BASE_URL_GENRE
                , kind
                , keyGenre
                , BuildConfig.CLIENT_ID
                , limit
                , offset);
    }

    public static String initSearchApi(String keySearch, int limit, int offset) {
        return String.format(Locale.US, Constants.BASE_URL_SEARCH
                , keySearch
                , BuildConfig.CLIENT_ID
                , limit
                , offset);
    }

    public static String initStreamUrl(int trackId) {
        return String.format(Constants.BASE_URL_STREAM
                , trackId
                , BuildConfig.CLIENT_ID);
    }

    public static String initDownloadUrl(int trackId) {
        return String.format(Constants.BASE_URL_DOWNLOAD
                , trackId
                , BuildConfig.CLIENT_ID);
    }

    public static String initCommentsUrl(int trackId, int limit, int offset) {
        return String.format(Locale.US, Constants.BASE_URL_COMMENT
                , trackId
                , BuildConfig.CLIENT_ID
                , limit
                , offset);
    }

    public static String passTimeToString(int millisecond) {
        int minute = (int) ((millisecond / 1000) / 60);
        int second = (int) (millisecond / 1000) % 60;
        return String.format((TIME_FORMAT), minute, second);
    }

    public static String passTimer(int second) {
        int minute = second / 60;
        int secondOut = second % 60;
        return String.format((TIME_FORMAT), minute, secondOut);
    }

    public static String passCount(int count) {
        if (count > 1000000) {
            double tmp = (double) count / 100000;
            return String.format(Locale.US, MILLION, tmp);
        }
        if (count > 1000) {
            double tmp = (double) count / 1000;
            return String.format(Locale.US, THOUSAND, tmp);
        }
        return String.valueOf(count);

    }

    public static String formatRate(double point) {
        NumberFormat numberFormat = new DecimalFormat("#0.0");
        return numberFormat.format(point);
    }

    public static String getRootDirPath(Context context) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if (!file.exists()) file.mkdir();
        return file.getAbsolutePath() + "/SoundCloud";
    }
}
