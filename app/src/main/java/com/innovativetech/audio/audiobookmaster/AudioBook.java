package com.innovativetech.audio.audiobookmaster;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by TMiller on 8/30/2016.
 */
public class AudioBook implements Serializable{

    private UUID     mId;
    private String   mTitle;
    private String   mAuthor;
    private File     mBookDir;
    private String   mImageDir;
    private int      mCurrTrack;
    private int      mTrackTime;
    private File[]   mTracks;
    private String[] mTrackTitles;
    private byte[]   mArtworkArr;

    private ArrayList<File> mPlayOrder;


    /*
     *  Constructors
     */
    // for new additions to the library.
    public AudioBook() {
        mId = UUID.randomUUID();
    }
    // for database to re-instantiate existing books back to the library.
    public AudioBook(UUID id) {
        mId = id;
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

    public File getBookDir() {
        return mBookDir;
    }
    public void setBookDir(File bookDir) {
        mBookDir = bookDir;
    }

    public String getImageDir() {
        return mImageDir;
    }
    public void setImageDir(String imageDir) {
        mImageDir = imageDir;
    }

    public int getCurrTrack() {
        return mCurrTrack;
    }
    public void setCurrTrack(int currTrack) {
        mCurrTrack = currTrack;
    }

    public int getTrackTime() {
        return mTrackTime;
    }
    public void setTrackTime(int trackTime) {
        mTrackTime = trackTime;
    }

    public File[] getTracks() {
        return mTracks;
    }
    public void setTracks(File[] tracks) {
        mTracks = tracks;
        mTrackTitles = new String[mTracks.length];
    }

    public String getTrackTitle(int index) {
        return mTrackTitles[index];
    }
    public void setTrackTitle(int index, String trackTitle) {
        mTrackTitles[index] = trackTitle;
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

    public ArrayList<File> getPlayOrder() {
        return mPlayOrder;
    }
    public void setPlayOrder(ArrayList<File> playOrder) {
        mPlayOrder = playOrder;
    }

    public int numberTracks() {
        return mTracks.length;
    }
}
