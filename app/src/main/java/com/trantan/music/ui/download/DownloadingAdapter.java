package com.trantan.music.ui.download;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trantan.music.R;
import com.trantan.music.data.Track;
import com.trantan.music.databinding.ItemDownloadingBinding;

import java.util.List;

public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingAdapter.ViewHolder> {
    private List<Track> mTracksDownloading;
    private DownloadingClickListner mDownloadingClickListner;

    public DownloadingAdapter(List<Track> tracksDownloading, DownloadingClickListner downloadingClickListner) {
        mTracksDownloading = tracksDownloading;
        mDownloadingClickListner = downloadingClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_downloading, viewGroup, false);
        ItemDownloadingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext())
                , R.layout.item_downloading, viewGroup, false);
        return new ViewHolder(binding, mDownloadingClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindData(mTracksDownloading.get(i), i);
    }

    @Override
    public int getItemCount() {
        return mTracksDownloading == null ? 0 : mTracksDownloading.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextNumber;
        private TextView mTextTitle;
        private TextView mTextProgress;
        private ProgressBar mProgressBar;
        private TextView mButtonCancel;
        private DownloadingClickListner mDownloadingClickListner;
        private ItemDownloadingBinding mBinding;
        private ItemDownloadingViewModel mViewModel;

        public ViewHolder(ItemDownloadingBinding binding, DownloadingClickListner listner) {
            super(binding.getRoot());
            mBinding = binding;
            mViewModel = new ItemDownloadingViewModel();
            mBinding.setViewModel(mViewModel);
            mDownloadingClickListner = listner;
        }

        public void bindData(Track track, int position) {
            mViewModel.position = String.valueOf(++position);
            mViewModel.mTrack.setValue(track);

            mBinding.buttonCancel.setOnClickListener(v -> mDownloadingClickListner.onCancelClick(track));
            mBinding.buttonPause.setOnClickListener(v -> {
                mDownloadingClickListner.onPauseClick(track);
                mViewModel.isPaused.set(true);
            });
            mBinding.buttonResume.setOnClickListener(v -> {
                mDownloadingClickListner.onResumlClick(track);
                mViewModel.isPaused.set(false);
            });
        }
    }

    interface DownloadingClickListner {
        void onCancelClick(Track track);

        void onPauseClick(Track track);

        void onResumlClick(Track track);
    }
}
