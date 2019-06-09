package com.trantan.music.data.source.local.contract;

import android.provider.BaseColumns;

public final class SearchHistoryContract {
    public static class SearchHistoryEntry implements BaseColumns {
        public static final String TABLE_SEARCH_HISTORY = "search";
        public static final String SEARCH_KEY = "search_key";
    }
}
