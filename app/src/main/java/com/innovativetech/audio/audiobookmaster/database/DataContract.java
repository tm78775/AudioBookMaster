package com.innovativetech.audio.audiobookmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.innovativetech.audio.audiobookmaster.AudioBook;
import com.innovativetech.audio.audiobookmaster.AudioTrack;

import java.util.ArrayList;

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

        try {
            String[] columns = new String[] { UniqueBooks.COL_UUID };

            cursor = database.query(UniqueBooks.TABLE_NAME
                    , columns
                    , null, null, null, null, null);

            if (cursor.getCount() == 0) {
                return null;
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

    // method used for determining if there is a UUID associated with a directory.
    public String getBookFromDir(String directory) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor;

        try {
            String[] columns = new String[]{UniqueBooks.COL_UUID};
            String whereClause = UniqueBooks.COL_BOOK_DIR + " = ? ";
            String[] whereArgs = new String[]{directory};

            cursor = database.query(UniqueBooks.TABLE_NAME
                    , columns
                    , whereClause
                    , whereArgs
                    , null
                    , null
                    , null);

            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
        } finally {
            database.close();
        }

        return cursor.getString(0);
    }

    // fetch all data associated with an audiobook's UUID.
    public void fetchBook(AudioBook book) {
        String bookId = book.getId().toString();

        SQLiteDatabase database = getReadableDatabase();
        try {
            Cursor uniqueBooksCursor = simpleBookDetailsQuery(database, bookId, UniqueBooks.COL_UUID, UniqueBooks.TABLE_NAME,
                    new String[] { UniqueBooks.COL_BOOK_DIR });
            if (uniqueBooksCursor != null) {
                uniqueBooksCursor.moveToFirst();
                book.setBookDir(uniqueBooksCursor.getString(0));
            }
        } catch (Exception ex) {
            Log.e("TAG", "Exception " + ex.toString());
        } finally {
            database.close();
        }

        database = null;
        database = getReadableDatabase();
        try {

            Cursor bookDataCursor = simpleBookDetailsQuery(database, bookId, BookData.COL_UUID, BookData.TABLE_NAME,
                    new String[] { BookData.COL_BOOK_TITLE, BookData.COL_BOOK_AUTHOR, BookData.COL_IMAGE_DIR, BookData.COL_CURR_TRACK });
            if (bookDataCursor != null) {
                bookDataCursor.moveToFirst();

                book.setTitle(bookDataCursor.getString(0));
                book.setAuthor(bookDataCursor.getString(1));
                book.setImageDir(bookDataCursor.getString(2));
                book.setCurrTrack(bookDataCursor.getInt(3));
            }
        } catch (Exception ex) {
            Log.e("TAG", "Exception " + ex.toString());
        } finally {
            database.close();
        }

        database = null;
        database = getReadableDatabase();
        try {
            Cursor bookTracksCursor = simpleBookDetailsQuery(database, bookId, BookTracks.COL_UUID, BookTracks.TABLE_NAME,
                    new String[] { BookTracks.COL_UUID, BookTracks.COL_PLAY_SEQUENCE, BookTracks.COL_TRACK_DIR, BookTracks.COL_TRACK_TITLE, BookTracks.COL_TRACK_TIME });
            ArrayList<AudioTrack> tracks = new ArrayList<>();
            if (bookTracksCursor != null) {
                bookTracksCursor.moveToFirst();
                while (!bookTracksCursor.isAfterLast()) {
                    AudioTrack t = new AudioTrack(bookTracksCursor.getString(2));

                    t.setPlaySequence(bookTracksCursor.getInt(1));
                    t.setTrackTitle(bookTracksCursor.getString(3));
                    t.setTimeIntoTrack(bookTracksCursor.getInt(4));
                    tracks.add(t);

                    bookTracksCursor.moveToNext();
                }
                book.setTracksList(tracks);
            }
        } catch (Exception ex) {
            Log.e("TAG", "Exception " + ex.toString());
        } finally {
            database.close();
        }
    }

    // helper method to query the database for book details.
    private Cursor simpleBookDetailsQuery(SQLiteDatabase database, String uuid, String tableKey, String tableName, String[] columnsToRetrieve) {
        Cursor cursor = null;

        String[] whereArgs = { uuid };
        String   whereClause = tableKey + " = ? ";
        String[] columns = columnsToRetrieve;
        try {
            cursor = database.query(
                    tableName,
                    columns,
                    whereClause,
                    whereArgs,
                    null, null, null
            );
            return cursor;
        } catch (Exception ex) {
            Log.e(TAG, "Error fetching data from " + tableName + " table.");
        }

        return cursor;
    }

    // add book details to the database.
    public void addBookToDatabase(AudioBook book) {
        addUniqueBook (book.getId().toString(), book.getBookDir());
        addBookData   (book.getId().toString(), book.getTitle(), book.getAuthor(), book.getImageDir());
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
        private void addBookData(String uuid, String bookTitle, String bookAuthor, String imageDir) {
            SQLiteDatabase database = getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put (BookData.COL_UUID       , uuid);
                values.put (BookData.COL_BOOK_TITLE , bookTitle);
                values.put (BookData.COL_BOOK_AUTHOR, bookAuthor);
                values.put (BookData.COL_IMAGE_DIR  , imageDir);

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

                    database.insert(BookTracks.TABLE_NAME, null, values);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error inserting into BookTracks table.");
            } finally {
                database.close();
            }
        }




    public abstract class UniqueBooks implements BaseColumns {
        public static final String TABLE_NAME   = "unique_books";
        public static final String COL_UUID     = "uuid";
        public static final String COL_BOOK_DIR = "book_dir";
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
