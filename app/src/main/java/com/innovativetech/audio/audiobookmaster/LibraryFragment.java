package com.innovativetech.audio.audiobookmaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
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
    // private static final int PHONE_PORTRAIT_MODE_COLUMNS = 2;
    // private static final int PHONE_LANDSCAPE_MODE_COLUMNS = 3;

    private RecyclerView mLibraryView;
    private AudioBookAdapter mAudioBookAdapter;
    private File mInternalAudioBookDir;
    private File mExternalAudioBookDir;
    private Librarian mLibrarian;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setupAudioBookDirectory();
        // todo: get the "last book played" out of shared preferences. Load it.
        mAudioBookAdapter = new AudioBookAdapter();
        mLibrarian = new Librarian(getActivity());
        new FetchLibraryTask().execute();
        //mAudioBookAdapter = new AudioBookAdapter(mLibrarian.getLibrary());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
        super.onCreateView(inflater, container, savedStateInstance);

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mLibraryView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mLibraryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mLibraryView.setAdapter(mAudioBookAdapter);

        return view;
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
                mImageView.setImageResource(R.mipmap.no_artwork_found);
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

        private List<AudioBook> mAudioBooks;


        public AudioBookAdapter(List<AudioBook> audioBooks) {
            mAudioBooks = audioBooks;
        }
        public AudioBookAdapter() {
            mAudioBooks = new ArrayList<>();
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.audiobook_details_view, parent, false);

            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            AudioBook audioBook = mAudioBooks.get(position);
            holder.bindAudioBook(audioBook);
        }

        @Override
        public int getItemCount() {
            return mAudioBooks.size();
        }

        public void addBookToAdapter(AudioBook audioBook) {
            mAudioBooks.add(audioBook);
            notifyDataSetChanged();
        }
    }


    private class FetchLibraryTask extends AsyncTask<Void, Void, List<AudioBook>> {
        @Override
        protected List<AudioBook> doInBackground(Void... Void) {
            // todo: this is hardcoded to use the one PERSONAL external directory. Needs to get users multiple directories configured.
            // mLibrarian.searchAndAddToLibrary(mExternalAudioBookDir);
            // return mLibrarian.getLibrary();
            ArrayList<String> bookIds = mLibrarian.getLibraryIdsFromDb();
            ArrayList<AudioBook> audioBooks = new ArrayList<>();

            for (int i = 0; i < bookIds.size(); i++) {
                AudioBook b = new AudioBook(UUID.fromString(bookIds.get(i)));
                mLibrarian.getBookDetails(b);
                audioBooks.add(b);
            }

            return audioBooks;
        }

        @Override
        protected void onPostExecute(List<AudioBook> audioBook) {
            super.onPostExecute(audioBook);
            for (int i = 0; i < audioBook.size(); i++) {
                mAudioBookAdapter.addBookToAdapter(audioBook.get(i));
            }
        }
    }

}
