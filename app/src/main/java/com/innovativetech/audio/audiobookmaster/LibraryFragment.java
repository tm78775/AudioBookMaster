package com.innovativetech.audio.audiobookmaster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovativetech.audio.audiobookmaster.database.DataContract;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by TMiller on 8/30/2016.
 */
public class LibraryFragment extends Fragment {

    // todo: these are for testing!!! These must by set by end-user to fit their file structure.
    private static final String INTERNAL_BOOK_DIR = "/audiobooks/";
    private static final String EXTERNAL_BOOK_DIR = "/AudioBooks/";
    private static final String TAG = "LibraryFragment";

    private View             mView;
    private RecyclerView     mLibraryView;
    private AudioBookAdapter mAudioBookAdapter;
    private File             mInternalAudioBookDir;
    private File             mExternalAudioBookDir;
    private DataContract     mDatabase;

    private ArrayList<AudioBookQuickLoad> mAudioBookQuickLoads;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        Log.i(TAG, "Entered onCreate");
        setupAudioBookDirectory();
        // todo: get the "last book played" out of shared preferences. Load it.
        mDatabase = new DataContract(getActivity(), "", null, 1, null);
        mAudioBookAdapter = new AudioBookAdapter();
        mAudioBookQuickLoads = new ArrayList<>();

        // get a list of all the books in the database, populate library from this list.
        List<String> audioBookIds = mDatabase.getAllBookIds();
        for (int i = 0; i < audioBookIds.size(); i++) {
            new FetchBookFromIdTask(getActivity()).execute(audioBookIds.get(i));
        }

        // based on user's directories configured, search those directories for books not yet added.
        // the logic dictates that each folder which CONTAINS audio files is a folder which IS a book.
        File[] allFiles = mExternalAudioBookDir.listFiles();
        if (allFiles != null && allFiles.length > 0) {
            for (int i = 0; i < allFiles.length; i++) {
                new SearchForNewlyAddedAudioBooksTask(getActivity()).execute(allFiles[i]);
            }
        }
        Log.i(TAG, "Finished searching for books.");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
        super.onCreateView(inflater, container, savedStateInstance);

        Log.i(TAG, "Entered onCreateView.");
        mView = inflater.inflate(R.layout.fragment_library, container, false);

        mLibraryView = (RecyclerView) mView.findViewById(R.id.book_recycler_view);
        mLibraryView.setAdapter(mAudioBookAdapter);
        mLibraryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Log.i(TAG, "Exiting onCreateView");

        return mView;
    }

    private void setupAudioBookDirectory() {
        // gets all books in built-in SD card.
        mInternalAudioBookDir = new File(android.os.Environment.getExternalStorageDirectory() + INTERNAL_BOOK_DIR);
        File[] internalBooks = mInternalAudioBookDir.listFiles();
        if (internalBooks == null) {
            Log.i(TAG, "Internal SD Card directory is empty.");
        }

        // gets all books in removable SD card.
        mExternalAudioBookDir = new File(System.getenv("SECONDARY_STORAGE") + EXTERNAL_BOOK_DIR);
        File[] externalBooks = mExternalAudioBookDir.listFiles();
        if (internalBooks == null) {
            Log.i(TAG, "removable SD Card directory is empty.");
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AudioBook mAudioBook;
        private ImageView mImageView;
        private TextView mTitleView;
        private TextView mAuthorView;

        public BookHolder(View itemView) {
            super(itemView);

            mImageView  = (ImageView) itemView.findViewById(R.id.book_cover_view);
            mTitleView  = (TextView)  itemView.findViewById(R.id.title_view);
            mAuthorView = (TextView)  itemView.findViewById(R.id.author_view);

            itemView.setOnClickListener(this);
        }

        public void bindAudioBook(AudioBook book) {
            mAudioBook = book;

            if (mAudioBook.hasBitmapArray()) {
                mImageView.setImageBitmap(Utilities.convertByteArrayToBitmap(mAudioBook.getArtworkArray()));
            } else if (mAudioBook.getImageDir() != null) {
                try {
                    Bitmap bookCover = BitmapFactory.decodeFile(mAudioBook.getImageDir());
                    mImageView.setImageBitmap(bookCover);
                } catch(Exception e) {
                    Log.e(TAG, "Retrieving book cover image failed.");
                }
            } else {
                // mImageView.setImageResource(R.mipmap.no_artwork_found);
            }
            mTitleView.setText  (mAudioBook.getTitle());
            mAuthorView.setText (mAudioBook.getAuthor());
        }

        public void onClick(View v) {
            // todo: update shared preferences to reflect the most recent book played.
            Intent intent = AudioPlayerActivity.newInstance(getActivity(), mAudioBook);
            startActivity(intent);
        }

    }

    private class AudioBookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<AudioBook> mAudioBook;

        public AudioBookAdapter() {
            mAudioBook = new ArrayList<>();
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.audiobook_details_view, parent, false);

            return new BookHolder(view);
        }
        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            AudioBook audioBook = mAudioBook.get(position);
            holder.bindAudioBook(audioBook);
        }
        @Override
        public int getItemCount() {
            return mAudioBook.size();
        }

        public void addBookToAdapter(AudioBook audioBook) {
            mAudioBook.add(audioBook);
        }
    }


    private class FetchBookFromIdTask extends AsyncTask<String, Void, AudioBook> {

        private Context mContext;

        public FetchBookFromIdTask(Context context) {
            mContext = context;
        }

        @Override
        protected AudioBook doInBackground(String... strings) {
            AudioBook book = new AudioBook(UUID.fromString( strings[0] ));
            mDatabase.fetchBook(book);
            book.setAlbumArtwork();

            return book;
        }
        @Override
        protected void onPostExecute(AudioBook audioBook) {
            mAudioBookAdapter.addBookToAdapter(audioBook);

            Handler uiThreadHandler = new Handler(mContext.getMainLooper());

            Runnable dataSetChangedRunnable = new Runnable() {
                public void run() {
                    mAudioBookAdapter.notifyDataSetChanged();
                }
            };

            uiThreadHandler.post(dataSetChangedRunnable);
        }

    }


    private class SearchForNewlyAddedAudioBooksTask extends AsyncTask<File, Void, String> {

        private Context mContext;

        public SearchForNewlyAddedAudioBooksTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(File... args) {
            HashMap<String,String> bookPairs = mDatabase.getBookIdAndDirectoryPairs();
            recursiveSearch(args[0], bookPairs);
            return "";
        }
        @Override
        protected void onProgressUpdate(Void... args) {
            Handler uiThreadHandler = new Handler(mContext.getMainLooper());

            Runnable dataSetChangedRunnable = new Runnable() {
                public void run() {
                    mAudioBookAdapter.notifyDataSetChanged();
                    Snackbar.make(getView(), "New book found! Added to library.", Snackbar.LENGTH_LONG).show();
                }
            };

            uiThreadHandler.post(dataSetChangedRunnable);
        }
        @Override
        protected void onPostExecute(String nothing) {
            // todo: load quickload books into shared preferences.
        }



        private boolean recursiveSearch(File topFile, HashMap<String,String> bookDirPairs) {

            Log.i(TAG, "Recursing...");
            if (topFile == null) {
                return true;
            }

            // in this new "topfile", search for sub-directories and audio files in directory.
            File[] audioFilesInDir = topFile.listFiles(Utilities.getAudioFilesFilter());
            File[] filesInTopFile  = topFile.listFiles(Utilities.getDirectoriesOnlyFilter());

            boolean bookExists = true;
            // If we land in a directory that has audio files.
            if (audioFilesInDir != null && audioFilesInDir.length > 0) {

                // if bookDirPairs is empty, then it's not in the database and db has nothing in it.
                if (bookDirPairs.size() == 0) {
                    assembleAndStoreBook(audioFilesInDir, topFile);
                } else {
                    // check to see if the directory (topFile) has a UUID
                    for (int i = 0; i < bookDirPairs.size(); i++) {
                        String strDir = bookDirPairs.get(topFile.toString());
                        if (strDir == null || strDir.equals("")) {
                            bookExists = false;
                        } else {
                            bookExists = true;
                        }

                        if (!bookExists) {
                            assembleAndStoreBook(audioFilesInDir, topFile);
                        }
                    }
                }
            }

            // if there are more files to be searched, recurse.
            if (filesInTopFile != null && filesInTopFile.length > 0) {
                for (int i = 0; i < filesInTopFile.length; i++) {
                    recursiveSearch(filesInTopFile[i], bookDirPairs);
                }
            } else {
                return true;
            }
            return false;
        }

        private void assembleAndStoreBook(File[] audioFilesInDir, File topFile) {
            // create new audiobook from these files, add them to mAudioBooks.
            AudioBook book;
            book = new AudioBook();

            book.setTitle(audioFilesInDir[0].toString());

            ArrayList<AudioTrack>  tracksList = new ArrayList<>();
            for (int i = 0; i < audioFilesInDir.length; i++) {

                AudioTrack t = new AudioTrack();

                t.setTrackDir(audioFilesInDir[i].toString());
                t.setTimeIntoTrack(0);

                tracksList.add(t);
            }

            book.setTracksList(tracksList);
            book.setBookDir(topFile.toString());

            Utilities.sortTracks(book);
            Utilities.readId3Tag(book);

            // add found audiobook to the AudioBookQuickLoad collection.
            AudioBookQuickLoad quickLoadBook = new AudioBookQuickLoad(book.getId());
            quickLoadBook.setImageArr   (book.getArtworkArray());
            quickLoadBook.setBookTitle  (book.getTitle());
            quickLoadBook.setBookAuthor (book.getAuthor());

            mAudioBookQuickLoads.add(quickLoadBook);

            // add found audiobook to AudioBook Adapter.
            mAudioBookAdapter.addBookToAdapter(book);
            mDatabase.addBookToDatabase(book);

            publishProgress();
        }

    }

}
