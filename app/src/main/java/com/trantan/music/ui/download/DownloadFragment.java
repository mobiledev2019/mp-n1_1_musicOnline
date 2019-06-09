package com.trantan.music.ui.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.local.LocalDataSource;
import com.trantan.music.data.source.remote.TracksRemoteDataSource;
import com.trantan.music.service.download.DownloadListener;
import com.trantan.music.service.download.MyDownloadManager;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.ui.option.OptionFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment implements DownloadContract.View,
        DownloadAdapter.TrackClickListener,
        DownloadListener, DownloadingAdapter.DownloadingClickListner, DownloadContract.Callback {
    private static final String TAG = "DownloadFragment";
    private RecyclerView mRecyclerDownloading;
    private RecyclerView mRecyclerDownloaded;
    private DownloadingAdapter mAdapterDownloading;
    private DownloadAdapter mAdapterDownloaded;
    private DownloadPresenter mPresenter;
    private PlayService mService;
    private ServiceConnection mConnection;
    private List<Track> mTracksDownled;
    private List<Track> mTracksDownloading;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        initUi(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        getActivity().unbindService(mConnection);
        mPresenter.removeDownloadListener();
        super.onDestroyView();
    }

    private void initUi(View view) {
        mRecyclerDownloaded = view.findViewById(R.id.recycler_downloaded);
        mRecyclerDownloading = view.findViewById(R.id.recycler_downloading);

        mPresenter = new DownloadPresenter(TrackRepository.getInstance(TracksRemoteDataSource.getInstance(),
                LocalDataSource.getInstance(getContext())),
                MyDownloadManager.getInstance(getActivity().getApplicationContext()),
                this);
        mPresenter.getTracksDownloaded();
        mPresenter.addDownloadListener(this);
        mPresenter.getTracksDownloading();

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayService.MyBinder myBinder = (PlayService.MyBinder) service;
                mService = myBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        getActivity().bindService(PlayService.getIntent(getContext()), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void loadTracksDownloaded(List<Track> tracks) {
        mTracksDownled = tracks;
        mAdapterDownloaded = new DownloadAdapter(mTracksDownled, this);
        mRecyclerDownloaded.setAdapter(mAdapterDownloaded);
        mAdapterDownloaded.notifyDataSetChanged();
    }

    @Override
    public void loadTracksDownloadding(List<Track> tracks) {
        mAdapterDownloading = new DownloadingAdapter(tracks, this);
        mRecyclerDownloading.setAdapter(mAdapterDownloading);
        mAdapterDownloading.notifyDataSetChanged();
    }

    @Override
    public void onTrackClick(Track track) {
        mService.pauseTrack();
        mService.addTracks(mTracksDownled);
        mService.unShuffledTracks();
        mService.changedTrack(track);
    }

    @Override
    public void onOptionClick(Track track) {
        OptionFragment optionFragment = OptionFragment.getIntance(track);
        optionFragment.setCallback(this);
        optionFragment.show(getActivity().getSupportFragmentManager(), optionFragment.getTag());
    }

    @Override
    public void onDownloadComplet() {
        mAdapterDownloading.notifyDataSetChanged();
        new Handler().postDelayed(() -> mPresenter.getTracksDownloaded(), 500);
    }

    @Override
    public void onCancelClick(Track track) {
        mPresenter.onCancel(track.getIdDowload());
    }

    @Override
    public void onPauseClick(Track track) {
        mPresenter.onPause(track.getIdDowload());
    }

    @Override
    public void onResumlClick(Track track) {
        mPresenter.onResume(track.getIdDowload());
    }

    @Override
    public void deleteTrack() {
        mPresenter.getTracksDownloaded();
    }
}
