package com.innovativetech.audio.audiobookmaster;

import java.io.Serializable;

/**
 * Created by Timothy on 9/5/16.
 */
public class AudioTrack implements Comparable<AudioTrack>, Serializable {

    private int mPlaySequence;      // play order.
    private String mTrackDir;       // directory of the track.
    private String mTrackTitle;     // title of the track (assigned by app or user).
    private int mTimeIntoTrack;     // position of where to pickup playing again.

    public int compareTo(AudioTrack other) {
        return mTrackDir.compareTo(other.getTrackDir());
    }

    public AudioTrack(String trackDir) {
        mTrackDir = trackDir;
    }

    public int getPlaySequence() {
        return mPlaySequence;
    }

    public void setPlaySequence(int mPlaySequence) {
        this.mPlaySequence = mPlaySequence;
    }

    public String getTrackDir() {
        return mTrackDir;
    }

    public void setTrackDir(String mTrackDir) {
        this.mTrackDir = mTrackDir;
    }

    public String getTrackTitle() {
        return mTrackTitle;
    }

    public void setTrackTitle(String mTrackTitle) {
        this.mTrackTitle = mTrackTitle;
    }

    public int getTimeIntoTrack() {
        return mTimeIntoTrack;
    }

    public void setTimeIntoTrack(int mTimeIntoTrack) {
        this.mTimeIntoTrack = mTimeIntoTrack;
    }
}
