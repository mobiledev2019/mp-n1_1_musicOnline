package com.trantan.music.ui.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.trantan.music.R;
import com.trantan.music.ui.change_password.ChangePasswordActivity;
import com.trantan.music.ui.editprofile.EditProfileActivity;
import com.trantan.music.ui.sign_in.SignInActivity;

public class SettingFragment extends Fragment implements View.OnClickListener,
        SettingContract.View {
    private static final String TAG = "SettingFragment";
    private SettingPresenter mPresenter;
    private TextView mTextChangePassword;
    private TextView mTextEditProfile;
    private TextView mTextApplicationInfo;
    private TextView mTextLogin;
    private TextView mTextLogout;
    private TextView mTextUserName;
    private ImageView mImageLogin;
    private ImageView mImageLogout;
    private ImageView mImageAvatar;
    private ImageView mImageCover;
    private ImageView mImageKey;
    private View mGroupViewLogin;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initUi(view);
        initClickListener();
        return view;
    }

    private void initClickListener() {
        mTextChangePassword.setOnClickListener(this);
        mTextEditProfile.setOnClickListener(this);
        mTextApplicationInfo.setOnClickListener(this);
        mTextLogin.setOnClickListener(this);
        mTextLogout.setOnClickListener(this);
    }

    private void initUi(View view) {
        mTextChangePassword = view.findViewById(R.id.text_change_password);
        mTextEditProfile = view.findViewById(R.id.text_edit_profile);
        mTextApplicationInfo = view.findViewById(R.id.text_info);
        mTextLogin = view.findViewById(R.id.text_login);
        mTextLogout = view.findViewById(R.id.text_logout);
        mImageLogin = view.findViewById(R.id.image_login);
        mImageLogout = view.findViewById(R.id.image_logout);
        mImageAvatar = view.findViewById(R.id.image_avatar);
        mImageCover = view.findViewById(R.id.image_cover);
        mTextUserName = view.findViewById(R.id.text_user_name);
        mImageKey = view.findViewById(R.id.image_password);
        mGroupViewLogin = view.findViewById(R.id.layout_login);
        mPresenter = new SettingPresenter(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        mPresenter.getCurentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_change_password:
                startActivity(ChangePasswordActivity.getIntent(getContext()));
                break;
            case R.id.text_edit_profile:
                startActivity(EditProfileActivity.getIntent(getContext()));
                break;
            case R.id.text_info:
                showDialogInfo();
                break;
            case R.id.text_login:
                startActivity(SignInActivity.getIntent(getContext()));
                break;
            case R.id.text_logout:
                mPresenter.logOut();
                break;
        }
    }

    @Override
    public void showUserInfo(FirebaseUser user) {
        Log.d(TAG, "showUserInfo: ");
        if (user != null) {
            for (UserInfo userInfo : user.getProviderData()) {
                if (userInfo.getProviderId().equals("google.com")) {
                    mTextChangePassword.setVisibility(View.GONE);
                    mImageKey.setVisibility(View.GONE);
                }
            }
        }
        if (user == null) {
            Glide.with(this)
                    .load(R.drawable.no_avatar)
                    .into(mImageAvatar);
            mTextUserName.setText("Please Login");
            showLoginFeature(false);
            return;
        }
        Glide.with(this)
                .load(user.getPhotoUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.no_avatar)
                        .transform(new CircleCrop())
                        .error(R.drawable.no_avatar))
                .into(mImageAvatar);
        mTextUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
        showLoginFeature(true);
    }

    private void showLoginFeature(boolean isLogged) {
        if (!isLogged) {
            mImageLogin.setVisibility(View.VISIBLE);
            mTextLogin.setVisibility(View.VISIBLE);
            mImageLogout.setVisibility(View.GONE);
            mTextLogout.setVisibility(View.GONE);
            mGroupViewLogin.setVisibility(View.GONE);
        } else {
            mImageLogin.setVisibility(View.GONE);
            mTextLogin.setVisibility(View.GONE);
            mImageLogout.setVisibility(View.VISIBLE);
            mTextLogout.setVisibility(View.VISIBLE);
            mGroupViewLogin.setVisibility(View.VISIBLE);
        }
    }

    private void showDialogInfo(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("App information");
        alertDialog.setMessage("Listen to music your way!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
