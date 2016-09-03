package com.innovativetech.audio.audiobookmaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.io.File;
import java.util.List;

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

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setupAudioBookDirectory();
        mAudioBookAdapter = new AudioBookAdapter(AudioBookLibrary.getLibrary(mExternalAudioBookDir));
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

    // todo: implement BookHolder.
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
            if (mAudioBook.getImageDir() != null) {
                try {
                    Bitmap bookCover = BitmapFactory.decodeFile(mAudioBook.getImageDir());
                    mImageView.setImageBitmap(bookCover);

                }catch(Exception e){
                    Log.e(TAG, "Retrieving book cover image failed.");
                }
            } else if (mAudioBook.hasBitmapArray()) {
                mImageView.setImageBitmap(Utilities.convertByteArrayToBitmap(mAudioBook.getArtworkArray()));
            } else {
                mImageView.setImageBitmap(null);
            }
            mTitleView.setText (mAudioBook.getTitle());
            mAuthorView.setText(mAudioBook.getAuthor());
        }

        public void onClick(View v) {
            // todo: the onClick needs to be implemented.
            Snackbar.make(v, "OnClick was called", Snackbar.LENGTH_SHORT).show();
            Intent intent = AudioPlayerActivity.newInstance(getActivity(), mAudioBook);
            startActivity(intent);
        }

    }

    // todo: implement the AudioBookAdapter class.
    private class AudioBookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<AudioBook> mAudioBooks;


        public AudioBookAdapter(List<AudioBook> audioBooks) {
            mAudioBooks = audioBooks;
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
    }

}
