package com.trantan.music.ui.playing_list;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemMoved();
}
