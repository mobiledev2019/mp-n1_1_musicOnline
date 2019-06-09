package com.trantan.music.data;

public class Comment {
    private String mBody;
    private String mUserName;
    private String mAvatarUrl;

    public Comment() {
    }

    public Comment(String body, String userName, String avatarUrl) {
        mBody = body;
        mUserName = userName;
        mAvatarUrl = avatarUrl;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }
}
