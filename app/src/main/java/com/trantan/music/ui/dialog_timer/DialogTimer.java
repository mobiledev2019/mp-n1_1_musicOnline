package com.trantan.music.ui.dialog_timer;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.trantan.music.R;
import com.trantan.music.service.music.PlayService;
import com.trantan.music.utils.StringUtils;

public class DialogTimer extends DialogFragment {
    private static final String TAG = "DialogTimer";
    private PlayService mPlayService;
    private ServiceConnection mConnection;
    private TextView mTimeRemain;
    private Switch mSwitch;
    private RadioButton mButtonThirty;
    private RadioButton mButtonSixty;
    private RadioButton mButtonNinety;
    private RadioButton mButtonTwoHours;
    private EditText mInputMinutes;
    private TextView mCancle;
    private TextView mSave;
    private int mMinutes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timer_layout, container, false);
        initUi(view);
        initListener();
        return view;
    }

    private void initListener() {
        mButtonThirty.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMinutes = 30;
                mSwitch.setChecked(true);
            }
        });
        mButtonSixty.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMinutes = 60;
                mSwitch.setChecked(true);
            }
        });
        mButtonNinety.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMinutes = 90;
                mSwitch.setChecked(true);
            }
        });
        mButtonTwoHours.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMinutes = 120;
                mSwitch.setChecked(true);
            }
        });
        mSave.setOnClickListener(v -> {
            if (mSwitch.isChecked()) {
                if (!mInputMinutes.getText().toString().equals("")) {
                    mMinutes = Integer.parseInt(mInputMinutes.getText().toString());
                }
                mPlayService.setMinutes(mMinutes);
                mPlayService.startTimer();
            } else {
                mPlayService.stopTimer();
                mPlayService.setMinutes(0);
            }
            dismiss();
        });
        mCancle.setOnClickListener(v -> dismiss());
        mInputMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mInputMinutes.getText().toString().equals("")) {
                    mSwitch.setChecked(true);
                    mButtonThirty.setChecked(false);
                    mButtonSixty.setChecked(false);
                    mButtonNinety.setChecked(false);
                    mButtonTwoHours.setChecked(false);
                } else mSwitch.setChecked(false);
            }
        });
    }

    private void initUi(View view) {
        mTimeRemain = view.findViewById(R.id.text_time_remian);
        mSwitch = view.findViewById(R.id.switch_timer);
        mButtonThirty = view.findViewById(R.id.radio_thirty_minutes);
        mButtonSixty = view.findViewById(R.id.radio_sixty_minutes);
        mButtonNinety = view.findViewById(R.id.radio_ninety_minutes);
        mButtonTwoHours = view.findViewById(R.id.radio_two_hours);
        mInputMinutes = view.findViewById(R.id.text_input_time);
        mCancle = view.findViewById(R.id.text_cancle);
        mSave = view.findViewById(R.id.text_save);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayService.MyBinder myBinder = (PlayService.MyBinder) service;
                mPlayService = myBinder.getService();
                mTimeRemain.setText(StringUtils.passTimer(mPlayService.getTimeRemain()));
                if (mPlayService.getTimeRemain() > 0) mSwitch.setChecked(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        getActivity().bindService(PlayService.getIntent(getContext()), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }
}
