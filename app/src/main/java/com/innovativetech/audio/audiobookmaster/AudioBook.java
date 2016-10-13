package com.innovativetech.audio.audiobookmaster;

import android.util.Log;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by TMiller on 8/30/2016.
 */
public class AudioBook implements Serializable{

    private static final String TAG = "AudioBook";
    private static final int    FIRST_TRACK = 1;

    private UUID     mId;
    private String   mTitle;
    private String   mAuthor;
    private String   mBookDir;
    private String   mImageDir;
    private byte[]   mArtworkArr;
    private AudioTrack mCurrentAudioTrack;
    private ArrayList<AudioTrack> mAudioTracks;

    /*
     *  Constructors
     */
    // for new additions to the library.
    public AudioBook() {
        mId = UUID.randomUUID();
        mAudioTracks = new ArrayList<>();
    }
    // for database to re-instantiate existing books back to the library.
    public AudioBook(UUID id) {
        mId = id;
        mAudioTracks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return mTitle;
    }

    /*
     *  Getters and Setters
     */
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }
    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getBookDir() {
        return mBookDir;
    }
    public void setBookDir(String bookDir) {
        mBookDir = bookDir;
    }

    public String getImageDir() {
        return mImageDir;
    }
    public void setImageDir(String imageDir) {
        mImageDir = imageDir;
    }

    public String getCurrentAudioTrack(int playSequence) {
        for (int i = 0; i < mAudioTracks.size(); i++) {
            if (mAudioTracks.get(i).getPlaySequence() == playSequence) {
                return mAudioTracks.get(i).getTrackDir();
            }
        }
        return null;
    }

    public int getCurrTrack() {
        if (mCurrentAudioTrack == null) {
            setCurrTrack( FIRST_TRACK );
        }
        return mCurrentAudioTrack.getPlaySequence();
    }
    public void setCurrTrack(int currTrack) {
        for (int i = 0; i < mAudioTracks.size(); i++) {
            if (mAudioTracks.get(i).getPlaySequence() == currTrack) {
                mCurrentAudioTrack = mAudioTracks.get(i);
                break;
            }
        }
    }

    public int getTrackTime() {
        return mCurrentAudioTrack.getTimeIntoTrack();
    }
    public void setTrackTime(int trackTime) {
        mCurrentAudioTrack.setTimeIntoTrack(trackTime);
    }

    public ArrayList<AudioTrack> getTracks() {
        return mAudioTracks;
    }
    public void setTracksList(ArrayList<AudioTrack> tracks) {
        mAudioTracks = tracks;
    }

    public byte[] getArtworkArray() {
        return mArtworkArr;
    }
    public void setArtworkArray(byte[] artworkArray) {
        mArtworkArr = artworkArray;
    }
    public boolean hasBitmapArray() {
        if (mArtworkArr == null) {
            return false;
        } else {
            return true;
        }
    }

    public int numberTracks() {
        return mAudioTracks.size();
    }

    public void setAlbumArtwork() {
        try {
            for (int i = 0; i < mAudioTracks.size(); i++) {
                if (mAudioTracks.get(i).getTrackDir().endsWith(".mp3")) {
                    Mp3File mp3 = new Mp3File(mAudioTracks.get(i).getTrackDir());
                    if (mp3.hasId3v2Tag()) {
                        ID3v2 tag = mp3.getId3v2Tag();
                        mArtworkArr = tag.getAlbumImage();
                        if (hasBitmapArray()) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error loading album artwork from AudioBook class.");
        }
    }
    public void setAlbumArtwork(byte[] artworkByteArr) {
        mArtworkArr = artworkByteArr;
    }
}
