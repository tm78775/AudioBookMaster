package com.innovativetech.audio.audiobookmaster.filesearch;

import com.innovativetech.audio.audiobookmaster.AudioBook;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Created by Timothy on 8/31/16.
 */
public class AudioFileSearch {

    private static final FilenameFilter AUDIO_FILES_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            if (    lowercaseName.endsWith(".mp3")
                    || lowercaseName.endsWith(".3gp")
                    || lowercaseName.endsWith(".mp4")
                    || lowercaseName.endsWith(".m4a")
                    || lowercaseName.endsWith(".aac")
                    || lowercaseName.endsWith(".flac")
                    || lowercaseName.endsWith(".mkv")
                    || lowercaseName.endsWith(".ogg")
                    ) {
                return true;
            }
            return false;
        }
    };

    private File mTopLevelDir;
    private List<AudioBook> mAudioBooks;

    public AudioFileSearch(File topLevelDir) {
        mTopLevelDir = topLevelDir;
    }

    // each folder THAT CONTAINS audio files is considered a book folder.
    public void searchForAudioBooks() {
        File[] allTheFiles = mTopLevelDir.listFiles();
        String stop = "stophere";
        // todo: this is where you need to continue.
    }

}
