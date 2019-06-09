package com.trantan.music.data.source.remote;

import android.os.AsyncTask;

import com.trantan.music.data.Comment;
import com.trantan.music.data.source.CommentDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommentAsyncTask extends AsyncTask<String, Comment, List<Comment>> {
    private static final String END_LINE = "\n";
    private static final String REQUEST_METHOD = "GET";
    private static final int CONNECT_TIMEOUT = 20000;
    private static final int READ_TIMEOUT = 20000;
    private CommentDataSource.CommentsCallback mCallback;

    public CommentAsyncTask(CommentDataSource.CommentsCallback callback) {
        mCallback = callback;
    }

    @Override
    protected List<Comment> doInBackground(String... strings) {
        List<Comment> comments = null;
        try {
            URL url = new URL(strings[0]);
            String jsonText = getJsonText(url);
            comments = convertJsonText(jsonText);
        } catch (IOException | JSONException e) {
            mCallback.onFailure(e.getMessage());
        }
        return comments;
    }

    @Override
    protected void onPostExecute(List<Comment> comments) {
        super.onPostExecute(comments);
        if (comments == null) mCallback.onFailure("null");
        else mCallback.onCommentsLoaded(comments);
    }

    private List<Comment> convertJsonText(String jsonText) throws JSONException {
        List<Comment> comments = new ArrayList<>();
        JSONArray jsonComments = new JSONArray(jsonText);
        for (int i = 0; i < jsonComments.length(); i++) {
            JSONObject jsonComment = jsonComments.getJSONObject(i);
            String body = jsonComment.getString("body");
            String username = jsonComment.getJSONObject("user").getString("username");
            String avatarUrl = jsonComment.getJSONObject("user").getString("avatar_url");
            Comment comment = new Comment(body, username, avatarUrl);
            comments.add(comment);
        }
        return comments;
    }

    private String getJsonText(URL url) throws IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder jsontext = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(REQUEST_METHOD);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.connect();
        int rescode = connection.getResponseCode();
        if (rescode == HttpURLConnection.HTTP_OK) {
            inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                jsontext.append(tmp);
                jsontext.append(END_LINE);
            }
        }

        return jsontext.toString();
    }
}
