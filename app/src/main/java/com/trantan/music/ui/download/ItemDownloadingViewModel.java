package com.trantan.music.ui.download;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;

import com.trantan.music.data.Track;

public class ItemDownloadingViewModel {
    public String position;
    public MutableLiveData<Track> mTrack = new MutableLiveData<>();
    public ObservableBoolean isPaused = new ObservableBoolean(false);
}
