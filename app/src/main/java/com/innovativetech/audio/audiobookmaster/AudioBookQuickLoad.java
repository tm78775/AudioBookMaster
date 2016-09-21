package com.innovativetech.audio.audiobookmaster;

import java.util.UUID;

/**
 * Created by Timothy on 9/13/16.
 */
public class AudioBookQuickLoad {

    private UUID   mBookId;
    private String mBookTitle;
    private String mBookAuthor;
    private byte[] mImageArr;

    public AudioBookQuickLoad(UUID bookId) {
        mBookId = bookId;
    }


    public UUID getBookId() {
        return mBookId;
    }

    public String getBookTitle() {

        return mBookTitle;
    }

    public void setBookTitle(String mBookTitle) {
        this.mBookTitle = mBookTitle;
    }

    public String getBookAuthor() {
        return mBookAuthor;
    }

    public void setBookAuthor(String mBookAuthor) {
        this.mBookAuthor = mBookAuthor;
    }

    public byte[] getImageArr() {
        return mImageArr;
    }

    public void setImageArr(byte[] mImageArr) {
        this.mImageArr = mImageArr;
    }
}
