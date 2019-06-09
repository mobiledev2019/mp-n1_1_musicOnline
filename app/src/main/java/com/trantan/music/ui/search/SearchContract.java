package com.trantan.music.ui.search;

import com.trantan.music.data.Track;

import java.util.List;

public class SearchContract {
    interface View {
        void showSearchResult(List<Track> tracks);

        void showSearchMoreResult(List<Track> tracks);

        void loadedSearchHistory(List<String> searchKeys);
    }

    interface Presenter {
        void search(String searchKey);

        void getSearchHistory();

        void addSearchKey(String searchKey);

        void searchMore(String searchKey);
    }
}
