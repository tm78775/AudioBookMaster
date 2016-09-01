package com.innovativetech.audio.audiobookmaster.filesearch;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Timothy on 8/31/16.
 */
public class AudioFileSearch {

    private String mTopLevelDir;

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

    public AudioFileSearch(String topLevelDir) {
        mTopLevelDir = topLevelDir;
    }

}
