package com.innovativetech.audio.audiobookmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TMiller on 8/31/2016.
 */
public class AudioBookLibrary {

    public static List<AudioBook> getLibrary(File topLevelDir) {
        List<AudioBook> books = new ArrayList<AudioBook>();


        // todo: this is a sample audiobook for testing the recyclerview.
        AudioFileSearch searchEngine = new AudioFileSearch(topLevelDir);
        searchEngine.searchForAudioBooks();

        return searchEngine.getSearchResults();
    }

}
