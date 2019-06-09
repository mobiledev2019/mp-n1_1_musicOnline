package com.trantan.music.ui.rate;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.trantan.music.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends DialogFragment {

    private RatingBar mRatingBar;
    private TextView mRate;
    private TextView mCancel;
    private ListenerRatingFragment mListenerRatingFragment;

    public RatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        initUi(view);
        return view;
    }

    public void setListenerRatingFragment(ListenerRatingFragment listenerRatingFragment) {
        mListenerRatingFragment = listenerRatingFragment;
    }

    private void initUi(View view) {
        mRatingBar = view.findViewById(R.id.rating_bar);
        mRate = view.findViewById(R.id.button_rate);
        mCancel = view.findViewById(R.id.button_cancel);

        mRate.setOnClickListener(v -> {
            mListenerRatingFragment.rated(mRatingBar.getRating());
            dismiss();
        });
        mCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

}
