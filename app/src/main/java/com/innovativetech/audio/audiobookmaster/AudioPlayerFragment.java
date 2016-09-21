package com.innovativetech.audio.audiobookmaster;

import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private FABRevealLayout mFabRevealLayout;
    private TextView        mAlbumTitleText;
    private TextView        mArtistNameText;
    private SeekBar         mTrackSeekbar;
    private TextView        mTrackTitleText;
    private ImageView       mPrevButton;
    private ImageView       mCenterPlayButton;
    private ImageView       mStopButton;
    private ImageView       mNextButton;
    private ImageView       mAlbumCoverImage;
    private MediaPlayer     mMediaPlayer;
    private View            mView;
    private AudioBook       mBook;
    private Handler         mSeekBarHandler;
    private Thread          mSeekBarThread;

    /*
     *  This is the ONLY method which should be used to instantiate a new AudioPlayerFragment.
     */
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
        mSeekBarHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaPlayer.isPlaying()) {
            mFabRevealLayout.revealMainView();
            mFabRevealLayout.revealSecondaryView();
            onSeekBarStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onSeekBarPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {

        // inflate the view.
        mView = inflater.inflate(R.layout.fragment_audio_player, container, false);

        // get references to on-screen widgets.
        mFabRevealLayout  = (FABRevealLayout) mView.findViewById(R.id.fab_reveal_layout);
        mAlbumTitleText   = (TextView)        mView.findViewById(R.id.album_title_text);
        mArtistNameText   = (TextView)        mView.findViewById(R.id.artist_name_text);
        mTrackSeekbar     = (SeekBar)         mView.findViewById(R.id.track_seekbar);
        mAlbumCoverImage  = (ImageView)       mView.findViewById(R.id.album_cover_image);
        mTrackTitleText   = (TextView)        mView.findViewById(R.id.song_title_text);
        mPrevButton       = (ImageView)       mView.findViewById(R.id.previous);
        mStopButton       = (ImageView)       mView.findViewById(R.id.stop);
        mNextButton       = (ImageView)       mView.findViewById(R.id.next);
        mCenterPlayButton = (ImageView)       mView.findViewById(R.id.centerPlay);

        // setup seekbar.
        styleSeekbar(mTrackSeekbar);

        // set title and author textviews.
        mAlbumTitleText.setText(mBook.getTitle());
        mArtistNameText.setText(mBook.getAuthor());

        // check for album artwork.
        if (mBook.hasBitmapArray()) {
            mAlbumCoverImage.setImageBitmap(Utilities.convertByteArrayToBitmap(mBook.getArtworkArray()));
        } else if (mBook.getImageDir() != null) {
            mAlbumCoverImage.setImageBitmap(BitmapFactory.decodeFile(mBook.getImageDir()));
        } else {
            // mAlbumCoverImage.setImageResource(R.mipmap.no_artwork_found);
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
    public void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }

    private void setSeekBarListeners() {
        mTrackSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean wasPlaying = mMediaPlayer.isPlaying();
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mStopButton.setVisibility(View.GONE);
                    mCenterPlayButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    // todo: set audiobook current time in track.
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (wasPlaying) {
                    mStopButton.setVisibility(View.VISIBLE);
                    mCenterPlayButton.setVisibility(View.GONE);
                    mMediaPlayer.start();
                }
                setPlayerCompletedListener();
            }
        });
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
        scale (mTrackSeekbar, 0 );
        animateSeekBar(mTrackSeekbar);
        scale (mTrackTitleText, 100 );
        scale ( mPrevButton, 150 );
        scale ( mStopButton, 100 );
        scale ( mNextButton, 200 );
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

    private void initializeMediaPlayer() {
        mMediaPlayer = null;
        mMediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(mBook.getCurrentAudioTrack(mBook.getCurrTrack())));
        setPlayerCompletedListener();
    }

    private void setPlayerCompletedListener() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // todo: deal with seekbar.
                onPlayerComplete();
            }
        });
    }

    private void onPlayerComplete() {
        onSeekBarPause();
        onNextTrack();
    }

    private void onNextTrack() {
        // todo: complete the next track implementation.
        if (mBook.getCurrTrack() < mBook.numberTracks() - 1) {
            // todo: go to next track.
        } else {
            // we've reached the end of the book.
            mMediaPlayer.pause();
            mFabRevealLayout.revealMainView();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void onPreviousTrack() {
        // todo: complete the previous track implementation.
        if (mBook.getCurrTrack() > 0) {

        }
    }

    private void onStopTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mStopButton.setVisibility(View.GONE);
        mCenterPlayButton.setVisibility(View.VISIBLE);
    }

    private void onPlayTrack() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            onSeekBarStart();
        }
        mCenterPlayButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);
    }

    private void onSeekBarStart() {
        Log.i(TAG, "onSeekBarStart method called.");
        if (mTrackSeekbar == null) {
            mTrackSeekbar = (SeekBar) mView.findViewById(R.id.track_seekbar);
        }
        mTrackSeekbar.setMax(mMediaPlayer.getDuration());
        setSeekBarListeners();

        mSeekBarThread = new Thread(new Runnable() {
            public void run() {
                mSeekBarHandler.postDelayed(this, 500);
                if (mMediaPlayer != null && mTrackSeekbar != null) {
                    mTrackSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
                }
            }
        });
        mSeekBarThread.start();
    }

    private void onSeekBarPause() {
        Log.i(TAG, "onSeekBarPause method called.");
        if (mSeekBarThread != null) {
            mSeekBarThread.interrupt();
            mSeekBarThread = null;
        }
        mTrackSeekbar = null;
    }

}
