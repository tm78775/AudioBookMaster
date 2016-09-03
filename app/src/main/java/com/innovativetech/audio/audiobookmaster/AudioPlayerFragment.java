package com.innovativetech.audio.audiobookmaster;

import android.animation.ObjectAnimator;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovativetech.audio.audiobookmaster.fabreveallayout.FABRevealLayout;
import com.innovativetech.audio.audiobookmaster.fabreveallayout.OnRevealChangeListener;

import java.util.UUID;

/**
 * Created by TMiller on 8/31/2016.
 */
public class AudioPlayerFragment extends Fragment {

    private static final String TAG = "AudioPlayerFragment";
    private static final String BOOK_ID_ARG = "book_id_arg";

    private View mView;
    private UUID mBookId;

    private FABRevealLayout mFabRevealLayout;
    private TextView        mAlbumTitleText;
    private TextView        mArtistNameText;
    private SeekBar         mSongProgress;
    private TextView        mSongTitleText;
    private ImageView       mPrevButton;
    private ImageView       mStopButton;
    private ImageView       mNextButton;
    private ImageView       mAlbumCoverImage;

    public static AudioPlayerFragment newInstance(UUID bookId) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();

        Bundle args = new Bundle();
        args.putSerializable(BOOK_ID_ARG, bookId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        mBookId = (UUID) getArguments().getSerializable(BOOK_ID_ARG);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
        mView = inflater.inflate(R.layout.fragment_audio_player, container, false);

        findViews();
        configureFABReveal();
        setAlbumImage();

        return mView;
    }

    private void findViews() {
        mFabRevealLayout = (FABRevealLayout) mView.findViewById(R.id.fab_reveal_layout);
        mAlbumTitleText  = (TextView)        mView.findViewById(R.id.album_title_text);
        mArtistNameText  = (TextView)        mView.findViewById(R.id.artist_name_text);
        mSongProgress    = (SeekBar)         mView.findViewById(R.id.song_progress_bar);
        mAlbumCoverImage = (ImageView)       mView.findViewById(R.id.album_cover_image);
        mSongTitleText   = (TextView)        mView.findViewById(R.id.song_title_text);
        mPrevButton      = (ImageView)       mView.findViewById(R.id.previous);
        mStopButton      = (ImageView)       mView.findViewById(R.id.stop);
        mNextButton      = (ImageView)       mView.findViewById(R.id.next);

        styleSeekbar(mSongProgress);
    }

    private void styleSeekbar(SeekBar songProgress) {
        int color = getResources().getColor(R.color.background);
        songProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        songProgress.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void configureFABReveal() {
        mFabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                showMainViewItems();
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                showSecondaryViewItems();
            }
        });
    }

    private void showMainViewItems() {
        scale(mAlbumTitleText, 50);
        scale(mArtistNameText, 150);
    }

    private void showSecondaryViewItems() {
        scale(mSongProgress, 0);
        animateSeekBar(mSongProgress);
        scale(mSongTitleText, 100);
        scale(mPrevButton, 150);
        scale(mStopButton, 100);
        scale(mNextButton, 200);
        setSecondaryViewListeners();
    }

    private void scale(View view, long delay){
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void animateSeekBar(SeekBar seekBar){
        seekBar.setProgress(15);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(seekBar, "progress", 15, 0);
        progressAnimator.setDuration(300);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }

    private void setSecondaryViewListeners() {

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Previous button pressed.", Toast.LENGTH_SHORT).show();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFabRevealLayout.revealMainView();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Next button pressed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAlbumImage() {
        mAlbumCoverImage.setImageResource(R.mipmap.dresden_ghost_story);
    }

}
