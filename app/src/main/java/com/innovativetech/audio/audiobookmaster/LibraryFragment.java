package com.innovativetech.audio.audiobookmaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

/**
 * Created by TMiller on 8/30/2016.
 */
public class LibraryFragment extends Fragment {

    // todo: these are for testing!!! These must by set by end-user to fit their file structure.
    private static final String INTERNAL_BOOK_DIR = "/audiobooks/Dresden";
    private static final String EXTERNAL_BOOK_DIR = "/AudioBooks/Dresden";

    private static final String TAG = "LibraryFragment";
    // private static final int PHONE_PORTRAIT_MODE_COLUMNS = 2;
    // private static final int PHONE_LANDSCAPE_MODE_COLUMNS = 3;

    private RecyclerView mLibraryView;
    private File mInternalAudioBookDir;
    private File mExternalAudioBookDir;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setupAudioBookDirectory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
        super.onCreateView(inflater, container, savedStateInstance);

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mLibraryView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mLibraryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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

}
