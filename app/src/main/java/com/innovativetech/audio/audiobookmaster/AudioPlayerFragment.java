package com.innovativetech.audio.audiobookmaster;

import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
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

import com.innovativetech.audio.audiobookmaster.fabreveallayout.FABRevealLayout;
import com.innovativetech.audio.audiobookmaster.fabreveallayout.OnRevealChangeListener;

/**
 * Created by TMiller on 8/31/2016.
 */
public class AudioPlayerFragment extends Fragment {

    private static final String TAG        = "AudioPlayerFragment";
    private static final String BOOK_ARG   = "book_arg";
    private static final String PLAYER_ARG = "player_extra";

    private FABRevealLayout mFabRevealLayout;
    private TextView        mAlbumTitleText;
    private TextView        mArtistNameText;
    private SeekBar         mSongProgress;
    private TextView        mSongTitleText;
    private ImageView       mPrevButton;
    private ImageView       mCenterPlayButton;
    private ImageView       mStopButton;
    private ImageView       mNextButton;
    private ImageView       mAlbumCoverImage;
    private MediaPlayer     mMediaPlayer;
    private View            mView;
    private AudioBook       mBook;

    public static AudioPlayerFragment newInstance(AudioBook book) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();

        Bundle args = new Bundle();
        args.putSerializable(BOOK_ARG, book);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        mBook = (AudioBook) getArguments().getSerializable(BOOK_ARG);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMediaPlayer.isPlaying()) {
            mFabRevealLayout.revealMainView();
            mFabRevealLayout.revealSecondaryView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {

        // inflate the view.
        mView = inflater.inflate(R.layout.fragment_audio_player, container, false);

        // get references to on-screen widgets.
        mFabRevealLayout  = (FABRevealLayout) mView.findViewById(R.id.fab_reveal_layout);
        mAlbumTitleText   = (TextView)        mView.findViewById(R.id.album_title_text);
        mArtistNameText   = (TextView)        mView.findViewById(R.id.artist_name_text);
        mSongProgress     = (SeekBar)         mView.findViewById(R.id.song_progress_bar);
        mAlbumCoverImage  = (ImageView)       mView.findViewById(R.id.album_cover_image);
        mSongTitleText    = (TextView)        mView.findViewById(R.id.song_title_text);
        mPrevButton       = (ImageView)       mView.findViewById(R.id.previous);
        mStopButton       = (ImageView)       mView.findViewById(R.id.stop);
        mNextButton       = (ImageView)       mView.findViewById(R.id.next);
        mCenterPlayButton = (ImageView)       mView.findViewById(R.id.centerPlay);

        // setup seekbar.
        styleSeekbar(mSongProgress);

        // set title and author textviews.
        mAlbumTitleText.setText(mBook.getTitle());
        mArtistNameText.setText(mBook.getAuthor());

        // check for album artwork.
        if (mBook.hasBitmapArray()) {
            mAlbumCoverImage.setImageBitmap(Utilities.convertByteArrayToBitmap(mBook.getArtworkArray()));
        } else if (mBook.getImageDir() != null) {
            mAlbumCoverImage.setImageBitmap(BitmapFactory.decodeFile(mBook.getImageDir()));
        }

        // initialize media player if nothing is currently playing.
        if (mMediaPlayer == null) {
            initializeMediaPlayer();
        }

        // set reveal change listeners.
        mFabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                showMainViewItems();
            }
            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                showSecondaryViewItems();
                if (mMediaPlayer.isPlaying() == false) {
                    onPlayTrack();
                }
            }
        });

        // set on click listeners.
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPreviousTrack();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStopTrack();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNextTrack();
            }
        });

        mCenterPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPlayTrack();
            }
        });

        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }

    private void styleSeekbar(SeekBar songProgress) {
        int color = getResources().getColor(R.color.background);
        songProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        songProgress.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void showMainViewItems() {
        scale(mAlbumTitleText, 50);
        scale(mArtistNameText, 150);
    }

    private void showSecondaryViewItems() {
        scale ( mSongProgress, 0 );
        animateSeekBar(mSongProgress );
        scale ( mSongTitleText, 100 );
        scale ( mPrevButton, 150 );
        scale ( mStopButton, 100 );
        scale ( mNextButton, 200 );

        // setSecondaryViewListeners();
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


    }

    private void initializeMediaPlayer() {
        // todo: hardcoded to do track 0 only for testing. Remove this.
        mMediaPlayer = null;
        mMediaPlayer = MediaPlayer.create(getActivity()
                , Uri.parse(mBook.getTracks()[mBook.getCurrTrack()].toString()));
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                onPlayerComplete();
            }
        });
    }

    private void onPlayerComplete() {
        // pause the seek bar.
        if (mBook.getCurrTrack() < mBook.numberTracks() - 1) {
            onNextTrack();
        } else {
            mMediaPlayer.pause();
            mFabRevealLayout.revealMainView();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void onNextTrack() {
        // todo: complete the next track implementation.
    }

    private void onPreviousTrack() {
        // todo: complete the previous track implementation.
    }

    private void onStopTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mStopButton.setVisibility(View.GONE);
        mCenterPlayButton.setVisibility(View.VISIBLE);
        // else {
        //     mFabRevealLayout.revealMainView();
        // }
    }

    private void onPlayTrack() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        mCenterPlayButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);
    }

}
