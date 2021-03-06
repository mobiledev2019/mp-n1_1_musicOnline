package com.trantan.music.ui.option;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.data.source.TrackRepository;
import com.trantan.music.data.source.local.LocalDataSource;
import com.trantan.music.data.source.remote.TracksRemoteDataSource;
import com.trantan.music.service.download.MyDownloadManager;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.ui.download.DownloadContract;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends BottomSheetDialogFragment implements View.OnClickListener,
        OptionContract.View {
    private static final String ARGUMENT_TRACK = "ARGUMENT_TRACK";
    private static final int MY_REQUEST_WRITE_STORAGE = 1;
    private static final int INT_TEN = 10;
    private Track mTrack;
    private ImageView mImageArtwork;
    private ImageView mImageFavorite;
    private ImageView mImageDownload;
    private ImageView mImageDelete;
    private TextView mTextTitle;
    private TextView mTextArtist;
    private TextView mTextFavorite;
    private TextView mTextDownload;
    private TextView mTextAddToQueue;
    private TextView mTextDelete;
    private PlayService mService;
    private ServiceConnection mConnection;
    private OptionContract.Presenter mPresenter;
    private DownloadContract.Callback mCallback;

    public OptionFragment() {
        // Required empty public constructor
    }

    public static OptionFragment getIntance(Track track) {
        OptionFragment optionFragment = new OptionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_TRACK, track);
        optionFragment.setArguments(bundle);
        return optionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        Bundle bundle = getArguments();
        mTrack = (Track) bundle.getSerializable(ARGUMENT_TRACK);
        initUi(view);
        bindData();
        initEvent();
        return view;
    }

    @Override
    public void onDestroyView() {
        getActivity().unbindService(mConnection);
        super.onDestroyView();
    }

    public void setCallback(DownloadContract.Callback callback) {
        mCallback = callback;
    }

    private void initEvent() {
        mTextFavorite.setOnClickListener(this);
        mTextDownload.setOnClickListener(this);
        mTextAddToQueue.setOnClickListener(this);
        mTextDelete.setOnClickListener(this);
    }

    private void bindData() {
        showFavorite(mPresenter.checkFavorite(mTrack));
        Glide.with(getContext())
                .load(mTrack.getArtworkUrl())
                .apply(new RequestOptions().transforms(new RoundedCorners(INT_TEN)))
                .into(mImageArtwork);
        mTextTitle.setText(mTrack.getTitle());
        mTextArtist.setText(mTrack.getArtist());
        if (!mTrack.isDownloadable()) {
            mTextDownload.setVisibility(View.GONE);
            mImageDownload.setVisibility(View.GONE);
        }
        if (mTrack.isOffline()) {
            mTextFavorite.setVisibility(View.GONE);
            mImageFavorite.setVisibility(View.GONE);
            mImageDelete.setVisibility(View.VISIBLE);
            mTextDelete.setVisibility(View.VISIBLE);
        }
    }

    private void initUi(View view) {
        mImageDownload = view.findViewById(R.id.image_download);
        mImageFavorite = view.findViewById(R.id.image_favorite);
        mImageArtwork = view.findViewById(R.id.image_artwork);
        mImageDelete = view.findViewById(R.id.image_delete);
        mTextTitle = view.findViewById(R.id.text_title);
        mTextArtist = view.findViewById(R.id.text_artist);
        mTextDownload = view.findViewById(R.id.text_download);
        mTextFavorite = view.findViewById(R.id.text_favorite);
        mTextAddToQueue = view.findViewById(R.id.text_add_to_queue);
        mTextDelete = view.findViewById(R.id.text_delete);

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

        mPresenter = new OptionPresenter(TrackRepository.getInstance(TracksRemoteDataSource.getInstance(),
                LocalDataSource.getInstance(getContext())), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_favorite:
                if (mPresenter.checkFavorite(mTrack)) mPresenter.removeFavorite(mTrack);
                else mPresenter.addFavorite(mTrack);
                break;
            case R.id.text_download:
                if (mTrack.isDownloadable()) {
                    checkWriteStoragePremission();
                }
                break;
            case R.id.text_add_to_queue:
                mService.addTrack(mTrack);
                Toast.makeText(getContext(), getString(R.string.toast_added), Toast.LENGTH_SHORT).show();
                break;
            case R.id.text_delete:
                File file = new File(mTrack.getStreamUrl());
                file.delete();
                getContext().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Audio.Media._ID + "=" + mTrack.getId(), null);
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                this.dismiss();
                mCallback.deleteTrack();
                break;
            default:
        }

    }

    @Override
    public void showFavorite(boolean isFavorite) {
        if (isFavorite) mImageFavorite.setImageResource(R.drawable.ic_favorited_24dp);
        else mImageFavorite.setImageResource(R.drawable.ic_favorite_24dp);
    }

    private void checkWriteStoragePremission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            MyDownloadManager.getInstance(getContext()).download(mTrack);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_REQUEST_WRITE_STORAGE) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MyDownloadManager.getInstance(getContext()).download(mTrack);
        } else {
            Toast.makeText(getContext(), getString(R.string.err_download_permistion_false), Toast.LENGTH_SHORT).show();
        }
    }

}
