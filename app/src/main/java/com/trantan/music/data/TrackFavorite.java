package com.trantan.music.data;

public class TrackFavorite {
    public int mId;
    public int mDuration;
    public String mTitle;
    public String mArtist;
    public String mStreamUrl;
    public String mDownloadUrl;
    public String mArtworkUrl;

    public TrackFavorite() {
    }

    public TrackFavorite(int id, int duration, String title, String artist, String streamUrl,
                         String downloadUrl, String artworkUrl) {
        mId = id;
        mDuration = duration;
        mTitle = title;
        mArtist = artist;
        mStreamUrl = streamUrl;
        mDownloadUrl = downloadUrl;
        mArtworkUrl = artworkUrl;
    }
}
