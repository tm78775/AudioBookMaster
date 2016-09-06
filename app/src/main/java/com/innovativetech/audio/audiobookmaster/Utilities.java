package com.innovativetech.audio.audiobookmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Timothy on 9/3/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";


    /*
     *  FilenameFilter definitions used in AudioFileSearch.
     */
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
    private static final FilenameFilter IMAGE_FILES_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            String fname = filename.toLowerCase();
            if (fname.endsWith(".jpg")
                    || fname.endsWith(".jpeg")
                    || fname.endsWith(".gif")
                    || fname.endsWith(".bmp")
                    || fname.endsWith(".png")) {
                return true;
            }
            return false;
        }
    };
    private static final FilenameFilter DIRECTORIES_ONLY_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return new File(dir, name).isDirectory();
        }

    };

    public static FilenameFilter getAudioFilesFilter() {
        return AUDIO_FILES_FILTER;
    }

    public static FilenameFilter getImageFilesFilter() {
        return IMAGE_FILES_FILTER;
    }

    public static FilenameFilter getDirectoriesOnlyFilter() {
        return DIRECTORIES_ONLY_FILTER;
    }

    /*
     *  Image tools.
     */
    public static Bitmap convertByteArrayToBitmap(byte[] imageData) {
        Bitmap bitmap = null;
        if (imageData != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                imageData = null;
            } catch (Exception ex) {
                Log.e(TAG, "Error loading Bitmap from byte[].");
            }
        }
        return bitmap;
    }

    /*
     *  Read data from mp3 files.
     */
    // todo: Reading ID3 tags from mp3 files needs to be here.


}
