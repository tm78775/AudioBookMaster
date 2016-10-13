package com.innovativetech.audio.audiobookmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

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
    public static void id3TagUpdateTitle(AudioBook book, String newTitle) {
        // todo: implement this.
    }

    public static void id3TagUpdateAuthor(AudioBook book, String newAuthor) {
        // todo: implement this.
    }

    public static void id3TagUpdateArtwork(AudioBook book, byte[] newBitmapArtwork) {
        // todo: implement this.
    }

    public static void id3TagUpdateTrackTitle(File mp3File, String newTrackTitle) {
        // todo:implement this.
    }

    // given an AudioBook object, this method will sort the tracks associated with it.
    public static void sortTracks(AudioBook audioBook) {
        Log.i(TAG, "Sorting tracks.");
        Collections.sort(audioBook.getTracks());
        for (int i = 0; i < audioBook.getTracks().size(); i++) {
            audioBook.getTracks().get(i).setPlaySequence(i);
        }
    }

    public static void readId3TagTitleAuthorImage( AudioBook book, String mp3Directory ) {
        try {
            Mp3File mp3 = new Mp3File( mp3Directory );

            if ( mp3.hasId3v1Tag() ) {
                ID3v1 tag = mp3.getId3v1Tag();

                book.setTitle  ( tag.getAlbum()  != null ? tag.getAlbum()  : book.getTitle() );
                book.setAuthor ( tag.getArtist() != null ? tag.getArtist() : book.getAuthor());

            } else if ( mp3.hasId3v2Tag() ) {
                ID3v2 tag = mp3.getId3v2Tag();

                book.setTitle        ( tag.getAlbum()      != null ? tag.getAlbum()      : book.getTitle() );
                book.setAuthor       ( tag.getArtist()     != null ? tag.getArtist()     : book.getAuthor() );
                book.setArtworkArray ( tag.getAlbumImage() != null ? tag.getAlbumImage() : null );
            }
        } catch ( Exception ex ) {
            Log.e( TAG, "Unable to read Id3 tag.", ex);
        }
    }

    public static void readId3Tag(AudioBook book) {
        try {

            ArrayList<AudioTrack> tracks = book.getTracks();

            for (int i = 0; i < book.getTracks().size(); i++) {
                if (tracks.get(i).getTrackDir().toString().endsWith(".mp3")) {
                    Mp3File mp3 = new Mp3File(tracks.get(i).getTrackDir().toString());
                    if (mp3.hasId3v1Tag()) {
                        ID3v1 tag = mp3.getId3v1Tag();

                        if (i < 1) {
                            book.setTitle(tag.getAlbum() == null ? book.getTitle() : tag.getAlbum());
                            book.setAuthor(tag.getArtist() == null ? null : tag.getArtist());
                        }
                        book.getTracks().get(i).setTrackTitle(tag.getTrack() == null ? (i+1) + " of " + tracks.size() : tag.getTrack());

                    } else if (mp3.hasId3v2Tag()) {
                        ID3v2 tag = mp3.getId3v2Tag();

                        if (i < 1) {
                            book.setTitle(tag.getAlbum() == null ? book.getTitle() : tag.getAlbum());
                            book.setAuthor(tag.getArtist() == null ? null : tag.getArtist());
                            book.setArtworkArray(tag.getAlbumImage() == null ? null : tag.getAlbumImage());
                        }
                        if (!book.hasBitmapArray()) {
                            book.setArtworkArray(tag.getAlbumImage() == null ? null : tag.getAlbumImage());
                        }

                        book.getTracks().get(i).setTrackTitle( tag.getTrack() == null ?  (i+1) + " of " + tracks.size() : tag.getTrack() );
                    } else {
                        if (i < 1) {
                            String bookTitle = book.getTitle();
                            if (bookTitle.contains("/")) {
                                int index = bookTitle.lastIndexOf("/");
                                String newTitle = book.getTitle().substring(index + 1);
                                if (newTitle.length() > 1) {
                                    book.setTitle(newTitle);
                                }
                            }
                        }
                        book.getTracks().get(i).setTrackTitle( (i+1) + " of " + tracks.size() );
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in reading ID3 tag.");
        }
    }

}
