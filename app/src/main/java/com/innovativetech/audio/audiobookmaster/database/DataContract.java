package com.innovativetech.audio.audiobookmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.innovativetech.audio.audiobookmaster.AudioBook;
import com.innovativetech.audio.audiobookmaster.AudioTrack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Timothy on 9/4/16.
 */
public class DataContract extends SQLiteOpenHelper {

    private static final String TAG = "DataContract";
    private static final String SQL_CREATE_UNIQUE_BOOKS_TABLE =
            "CREATE TABLE "
                + UniqueBooks.TABLE_NAME
                + "(" + UniqueBooks.COL_UUID     + " TEXT PRIMARY KEY "
                + "," + UniqueBooks.COL_BOOK_DIR + " TEXT"
                + ")";
    private static final String SQL_CREATE_BOOK_DATA_TABLE =
            "CREATE TABLE "
                + BookData.TABLE_NAME
                + "(" + BookData.COL_UUID        + " TEXT"
                + "," + BookData.COL_BOOK_TITLE  + " TEXT"
                + "," + BookData.COL_BOOK_AUTHOR + " TEXT"
                + "," + BookData.COL_IMAGE_DIR   + " TEXT"
                + "," + BookData.COL_CURR_TRACK  + " SMALL INT"
                + ")";
    private static final String SQL_CREATE_BOOK_TRACKS_TABLE =
            "CREATE TABLE "
            + BookTracks.TABLE_NAME
            + "(" + BookTracks.COL_UUID          + " TEXT"
            + "," + BookTracks.COL_PLAY_SEQUENCE + " SMALL INT"
            + "," + BookTracks.COL_TRACK_DIR     + " TEXT"
            + "," + BookTracks.COL_TRACK_TITLE   + " TEXT"
            + "," + BookTracks.COL_TRACK_TIME    + " INT"
            + ")";
    private static final String DATABASE_NAME = "AudioBookMasterDb";
    private static int DATABASE_VERSION = 1;

    public DataContract(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "Creating database");

        sqLiteDatabase.execSQL (SQL_CREATE_UNIQUE_BOOKS_TABLE);
        sqLiteDatabase.execSQL (SQL_CREATE_BOOK_DATA_TABLE);
        sqLiteDatabase.execSQL (SQL_CREATE_BOOK_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getAllBookIds() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor;
        ArrayList<String> bookIds = new ArrayList<>();


        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(
            UniqueBooks.TABLE_NAME +
                " LEFT JOIN " + BookData.TABLE_NAME + " ON " + UniqueBooks.TABLE_NAME + "." + UniqueBooks.COL_UUID + " = " + BookData.TABLE_NAME + "." + BookData.COL_UUID
        );

        try {
            String[] columns = new String[] { (UniqueBooks.TABLE_NAME + "." + UniqueBooks.COL_UUID), (BookData.TABLE_NAME + "." + BookData.COL_BOOK_TITLE) };

            cursor = qBuilder.query(
                    database
                    , columns
                    , null
                    , null
                    , null
                    , null
                    , BookData.COL_BOOK_TITLE);

            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                bookIds.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error getting all books from database.");
        }

        return bookIds;
    }

    // Get all book ID / directory associations.
    public HashMap<String,String> getBookIdAndDirectoryPairs() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor;
        HashMap<String,String> idDirPairs = new HashMap<>();

        try {
            String[] columns = new String[]{UniqueBooks.COL_BOOK_DIR, UniqueBooks.COL_UUID};

            cursor = database.query(UniqueBooks.TABLE_NAME
                    , columns
                    , null, null, null, null, null);

            if (cursor.getCount() == 0) {
                return new HashMap<String,String>();
            }

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                idDirPairs.put(cursor.getString(0), cursor.getString(1));
                cursor.moveToNext();
            }

        } finally {
            database.close();
        }

        return idDirPairs;
    }

    // fetch all data associated with an audiobook's UUID.
    public void fetchBook(AudioBook book) {
        String bookId = book.getId().toString();

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(
                UniqueBooks.TABLE_NAME +
                        " LEFT JOIN " + BookData.TABLE_NAME + " ON " + UniqueBooks.TABLE_NAME + "." + UniqueBooks.COL_UUID + " = " + BookData.TABLE_NAME + "." + BookData.COL_UUID +
                        " LEFT JOIN " + BookTracks.TABLE_NAME + " ON " + UniqueBooks.TABLE_NAME + "." + UniqueBooks.COL_UUID + " = " + BookTracks.TABLE_NAME + "." + BookTracks.COL_UUID
        );

        SQLiteDatabase database = getReadableDatabase();
        try {
            String[] columns = new String[]{
                    UniqueBooks.COL_BOOK_DIR,
                    BookData.COL_BOOK_TITLE,
                    BookData.COL_BOOK_AUTHOR,
                    BookData.COL_IMAGE_DIR,
                    BookData.COL_CURR_TRACK,
                    BookTracks.COL_PLAY_SEQUENCE,
                    BookTracks.COL_TRACK_DIR,
                    BookTracks.COL_TRACK_TITLE,
                    BookTracks.COL_TRACK_TIME
            };
            String whereStatement = UniqueBooks.TABLE_NAME + "." + UniqueBooks.COL_UUID + " = ? ";
            String whereArgs[] = new String[] { bookId };
            String orderBy = BookData.COL_BOOK_TITLE + ", " + BookTracks.COL_PLAY_SEQUENCE + " ASC ";

            Cursor cursor = qBuilder.query(database, columns, whereStatement, whereArgs, null, null, orderBy);
            if (cursor == null) {
                return;
            }

            ArrayList<AudioTrack> tracks = new ArrayList<>();

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                if (i == 0) {
                    book.setBookDir(cursor.getString(0) == null ? "" : cursor.getString(0));
                    book.setTitle(cursor.getString(1) == null ? "" : cursor.getString(1));
                    book.setAuthor(cursor.getString(2) == null ? "" : cursor.getString(2));
                    book.setImageDir(cursor.getString(3) == null ? "" : cursor.getString(3));
                    book.setCurrTrack(cursor.getInt(4));
                }
                if (cursor.getString(5) != null) {
                    AudioTrack track = new AudioTrack();
                    track.setPlaySequence(cursor.getInt(5));
                    track.setTrackDir(cursor.getString(6));
                    track.setTrackTitle(cursor.getString(7));
                    track.setTimeIntoTrack(cursor.getInt(8));

                    tracks.add(track);
                }
                cursor.moveToNext();
            }

            book.setTracksList(tracks);

        } catch (Exception ex) {
            Log.e(TAG, "Error fetchingBook in method fetchBook.");
        } finally {
            database.close();
        }

    }

    // add book details to the database.
    public void addBookToDatabase(AudioBook book) {
        addUniqueBook (book.getId().toString(), book.getBookDir());
        addBookData   (book.getId().toString(), book.getTitle(), book.getAuthor(), book.getImageDir(), book.getCurrTrack());
        addBookTracks (book);
    }
        private void addUniqueBook(String uuid, String bookDir) {
            SQLiteDatabase database= getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put (UniqueBooks.COL_UUID    , uuid);
                values.put (UniqueBooks.COL_BOOK_DIR, bookDir);

                database.insert(UniqueBooks.TABLE_NAME, null, values);
            } catch (Exception ex) {
                Log.e(TAG, "Error inserting into UniqueBook table.");
            } finally {
                database.close();
            }
        }
        private void addBookData(String uuid, String bookTitle, String bookAuthor, String imageDir, int currTrack) {
            SQLiteDatabase database = getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put (BookData.COL_UUID       , uuid);
                values.put (BookData.COL_BOOK_TITLE , bookTitle);
                values.put (BookData.COL_BOOK_AUTHOR, bookAuthor);
                values.put (BookData.COL_IMAGE_DIR  , imageDir);
                values.put (BookData.COL_CURR_TRACK , currTrack);

                database.insert(BookData.TABLE_NAME, null, values);
            } catch (Exception ex) {
                Log.e(TAG, "Error inserting into BookData table.");
            } finally {
                database.close();
            }
        }
        private void addBookTracks(AudioBook book) {
            SQLiteDatabase database = getWritableDatabase();
            try {
                ArrayList<AudioTrack> bookTracks = book.getTracks();

                for (int i = 0; i < bookTracks.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put (BookTracks.COL_UUID         , book.getId().toString());
                    values.put (BookTracks.COL_PLAY_SEQUENCE, bookTracks.get(i).getPlaySequence());
                    values.put (BookTracks.COL_TRACK_DIR    , bookTracks.get(i).getTrackDir());
                    values.put (BookTracks.COL_TRACK_TITLE  , bookTracks.get(i).getTrackTitle());
                    values.put (BookTracks.COL_TRACK_TIME   , bookTracks.get(i).getTimeIntoTrack());

                    database.insert(BookTracks.TABLE_NAME, null, values);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error inserting into BookTracks table.");
            } finally {
                database.close();
            }
        }




    public abstract class UniqueBooks implements BaseColumns {
        public static final String TABLE_NAME           = "unique_books";
        public static final String COL_UUID             = "uuid";
        public static final String COL_BOOK_DIR         = "book_dir";
    }

    public abstract class BookData implements BaseColumns {
        public static final String TABLE_NAME      = "book_data";
        public static final String COL_UUID        = "uuid";
        public static final String COL_BOOK_TITLE  = "book_title";
        public static final String COL_BOOK_AUTHOR = "book_author";
        public static final String COL_IMAGE_DIR   = "image_dir";
        public static final String COL_CURR_TRACK  = "current_track";
    }

    public abstract class BookTracks implements BaseColumns {
        public static final String TABLE_NAME        = "book_tracks";
        public static final String COL_UUID          = "uuid";
        public static final String COL_PLAY_SEQUENCE = "play_sequence";
        public static final String COL_TRACK_DIR     = "track_dir";
        public static final String COL_TRACK_TITLE   = "track_title";
        public static final String COL_TRACK_TIME    = "track_time";
    }

}
