package com.innovativetech.audio.audiobookmaster;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class AudioPlayerActivity extends SingleFragmentActivity {

    public static final String TAG = "AudioPlayerActivity";
    public static final String EXTRA_BOOK_ID = "book_id_extra";

    public static Intent newInstance(Context context, UUID bookId) {
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    protected Fragment createFragment() {
        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        return AudioPlayerFragment.newInstance(bookId);
    }

}
