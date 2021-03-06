package com.innovativetech.audio.audiobookmaster;

import android.util.Log;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Timothy on 8/31/16.
 */
public class AudioFileSearch {

    private static final String TAG = "AudioFileSearch";
    private File mTopLevelDir;
    private List<AudioBook> mAudioBooks;

    public AudioFileSearch(File topLevelDir) {
        mTopLevelDir = topLevelDir;
        mAudioBooks = new ArrayList<>();
    }

    public List<AudioBook> getSearchResults() {
        return mAudioBooks;
    }

    // each folder THAT CONTAINS audio files is considered a book folder.
    public void searchForAudioBooks() {
        File[] allFiles = mTopLevelDir.listFiles();
        if (allFiles != null && allFiles.length > 0) {
            for (int i = 0; i < allFiles.length; i++) {
                recursiveSearch(allFiles[i]);
            }
        }
        Log.i(TAG, "Finished searching for books.");
        sortTracks();
        readMp3Data();
    }
    private boolean recursiveSearch(File topFile) {

        if (topFile == null) {
            return true;
        }

        // in this new "topfile", search for sub-directories and audio files in directory.
        File[] audioFilesInDir = topFile.listFiles(Utilities.getAudioFilesFilter());
        File[] filesInTopFile = topFile.listFiles(Utilities.getDirectoriesOnlyFilter());

        // If we land in a directory that has audio files.
        if (audioFilesInDir != null && audioFilesInDir.length > 0) {
            // create new audiobook from these files, add them to mAudioBooks.
            AudioBook book = new AudioBook();
            book.setTitle(audioFilesInDir[0].toString());
            book.setTracks(audioFilesInDir);

            // while we're in the directory with audio, check for cover images.
            File[] imagePaths = topFile.listFiles(Utilities.getImageFilesFilter());
            if (imagePaths != null && imagePaths.length > 0) {
                book.setImageDir(imagePaths[0].toString());
            }

            mAudioBooks.add(book);
        }

        // if there are more files to be searched, recurse.
        if (filesInTopFile != null && filesInTopFile.length > 0) {
            for (int i = 0; i < filesInTopFile.length; i++) {
                recursiveSearch(filesInTopFile[i]);
            }
        } else {
            return true;
        }
        return false;
    }

    private void sortTracks() {
        Log.i(TAG, "Sorting tracks.");
        for (int i = 0; i < mAudioBooks.size(); i++) {
            File[] tracks = mAudioBooks.get(i).getTracks();

            if (tracks != null) {
                ArrayList<File> aTracks = new ArrayList<>();
                for (int j = 0; j < tracks.length; j++) {
                    aTracks.add(tracks[j]);
                }
                Collections.sort(aTracks);

                tracks = new File[aTracks.size()];
                for (int k = 0; k < aTracks.size(); k++) {
                    tracks[k] = aTracks.get(k);
                }
                mAudioBooks.get(i).setTracks(tracks);
            }
        }
    }

    private void readMp3Data() {
        for (int i = 0; i < mAudioBooks.size(); i++) {
            AudioBook book = mAudioBooks.get(i);
            if (book.getTracks()[0].toString().endsWith(".mp3")) {
                book = readMp3Tag(book);
                if (book != null) {
                    mAudioBooks.set(i, book);
                }
            }
        }
    }

    private AudioBook readMp3Tag(AudioBook book) {
        try {
            Mp3File mp3 = new Mp3File(book.getTracks()[0]);
            if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();

                book.setTitle  ( tag.getAlbum()  == null ? book.getTitle() : tag.getAlbum() );
                book.setAuthor ( tag.getArtist() == null ? null            : tag.getArtist() );

            }
            else if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();

                book.setTitle        ( tag.getAlbum()      == null ? book.getTitle() : tag.getAlbum() );
                book.setAuthor       ( tag.getArtist()     == null ? null            : tag.getArtist() );
                book.setArtworkArray ( tag.getAlbumImage() == null ? null            : tag.getAlbumImage() );

            }
            else {
                String bookTitle = book.getTitle();
                if (bookTitle.contains("/")) {
                    int index = bookTitle.lastIndexOf("/");
                    String newTitle = book.getTitle().substring(index + 1);
                    if (newTitle.length() > 1) {
                        book.setTitle(newTitle);
                    }
                }
            }
            return book;
        } catch (Exception e) {
            Log.e(TAG, "Error in reading ID3 tag.");
            return null;
        }
    }

}
