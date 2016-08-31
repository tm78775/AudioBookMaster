package com.innovativetech.audio.audiobookmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by TMiller on 8/30/2016.
 */
public class AudioBook {

    private UUID   mId;
    private String mTitle;
    private String mAuthor;
    private File   mBookDir;
    private File   mImageDir;
    private int    mCurrTrack;
    private int    mTrackTime;
    private File[] mTracks;

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

    public File getImageDir() {
        return mImageDir;
    }
    public void setImageDir(File imageDir) {
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
    }

    public ArrayList<File> getPlayOrder() {
        return mPlayOrder;
    }
    public void setPlayOrder(ArrayList<File> playOrder) {
        mPlayOrder = playOrder;
    }
}
