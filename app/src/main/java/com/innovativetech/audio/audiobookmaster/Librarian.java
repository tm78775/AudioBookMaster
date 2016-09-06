package com.innovativetech.audio.audiobookmaster;

import android.content.Context;
import android.util.Log;

import com.innovativetech.audio.audiobookmaster.database.DataContract;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by TMiller on 8/31/2016.
 */
public class Librarian {

    private static final String TAG = "Librarian";
    private DataContract mDatabase;
    private Context mContext;
    private ArrayList<AudioBook> mAudioBooks;

    public Librarian(Context context) {
        mDatabase = new DataContract(context, "this is ignored", null, 1, null); // The 1 is also ignored.
        mAudioBooks = new ArrayList<>();
        mContext = context;
    }

    public ArrayList<AudioBook> getLibrary() {
        return mAudioBooks;
    }

    public void searchAndAddToLibrary(File topLevelDir) {
        AudioFileSearch searchEngine = new AudioFileSearch(this, topLevelDir);
        searchEngine.searchForAudioBooks();
    }

    public boolean getBookFromDir(String directory) {
        String uuid = mDatabase.getBookFromDir(directory);
        if (uuid == null) {
            return false;
        }

        AudioBook book = new AudioBook(UUID.fromString(uuid));
        mDatabase.fetchBook(book);
        book.setAlbumArtwork();
        mAudioBooks.add(book);

        return true;
    }

    public void addBookToLibrary(AudioBook audioBook) {
        sortTracks(audioBook);
        readMp3Tag(audioBook);
        mDatabase.addBookToDatabase(audioBook);
        mAudioBooks.add(audioBook);
    }

    // given an AudioBook object, this method will sort the tracks associated with it.
    private void sortTracks(AudioBook audioBook) {
        Log.i(TAG, "Sorting tracks.");
        Collections.sort(audioBook.getTracks());
        for (int i = 0; i < audioBook.getTracks().size(); i++) {
            audioBook.getTracks().get(i).setPlaySequence(i);
        }
    }

    private void readMp3Tag(AudioBook book) {
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

    public ArrayList<String> getLibraryIdsFromDb() {
        return mDatabase.getAllBookIds();
    }

    public void getBookDetails(AudioBook book) {
        mDatabase.fetchBook(book);
    }

}
