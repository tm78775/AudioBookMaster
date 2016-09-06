package com.innovativetech.audio.audiobookmaster;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Timothy on 8/31/16.
 */
public class AudioFileSearch {

    private static final String TAG = "AudioFileSearch";
    private File mTopLevelDir;
    private Librarian mLibrarian;

    public AudioFileSearch(Librarian librarian, File topLevelDir) {
        mTopLevelDir = topLevelDir;
        mLibrarian = librarian;
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
    }
    private boolean recursiveSearch(File topFile) {

        if (topFile == null) {
            return true;
        }

        // in this new "topfile", search for sub-directories and audio files in directory.
        File[] audioFilesInDir = topFile.listFiles(Utilities.getAudioFilesFilter());
        File[] filesInTopFile = topFile.listFiles(Utilities.getDirectoriesOnlyFilter());

        boolean bookExists;
        // If we land in a directory that has audio files.
        if (audioFilesInDir != null && audioFilesInDir.length > 0) {
            bookExists = mLibrarian.getBookFromDir(topFile.toString());

            if (bookExists) {
                // recall from the database.
            } else {
                // create new audiobook from these files, add them to mAudioBooks.
                AudioBook book;
                book = new AudioBook();
                book.setTitle(audioFilesInDir[0].toString());
                ArrayList<AudioTrack>  tracksList = new ArrayList<>();
                for (int i = 0; i < audioFilesInDir.length; i++) {
                    AudioTrack t = new AudioTrack(audioFilesInDir[i].toString());
                    tracksList.add(t);
                }
                book.setTracksList(tracksList);
                book.setBookDir(topFile.toString());

                mLibrarian.addBookToLibrary(book);
            }

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
}
