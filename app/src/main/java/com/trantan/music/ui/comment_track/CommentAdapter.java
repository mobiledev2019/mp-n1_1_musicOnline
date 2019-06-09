package com.trantan.music.ui.comment_track;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.trantan.music.R;
import com.trantan.music.data.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> mComments;

    public CommentAdapter(List<Comment> comments) {
        mComments = comments;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recycler_comment, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindData(mComments.get(i));
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public void addComments(List<Comment> comments) {
        int position = mComments.size();
        mComments.addAll(comments);
        notifyItemChanged(position);
    }

    public void newComment(Comment comment) {
        mComments.add(0, comment);
        notifyItemInserted(0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageAvatar;
        private TextView mTextUserName;
        private TextView mTextBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initUi();
        }

        private void initUi() {
            mImageAvatar = itemView.findViewById(R.id.image_avatar);
            mTextUserName = itemView.findViewById(R.id.text_user_name);
            mTextBody = itemView.findViewById(R.id.text_body);
        }

        public void bindData(Comment comment) {
            Glide.with(mImageAvatar.getContext())
                    .load(comment.getAvatarUrl())
                    .apply(new RequestOptions()
                            .transform(new CircleCrop())
                            .error(R.drawable.no_avatar))
                    .into(mImageAvatar);
            mTextUserName.setText(comment.getUserName());
            mTextBody.setText(comment.getBody());
        }
    }
}
