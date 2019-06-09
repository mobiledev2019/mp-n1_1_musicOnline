package com.trantan.music.ui.setting;

import com.google.firebase.auth.FirebaseUser;

interface SettingContract {
    interface View {
        void showUserInfo(FirebaseUser user);
    }

    interface Presenter {
        void getCurentUser();

        void logOut();
    }
}
