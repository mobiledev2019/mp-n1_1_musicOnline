package com.trantan.music.ui.setting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPresenter implements SettingContract.Presenter {
    private FirebaseAuth mFirebaseAuth;
    private SettingContract.View mView;

    public SettingPresenter(SettingContract.View view) {
        mView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getCurentUser() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        mView.showUserInfo(user);
    }

    @Override
    public void logOut() {
        mFirebaseAuth.signOut();
        getCurentUser();
    }
}
