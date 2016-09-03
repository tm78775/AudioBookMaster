package com.innovativetech.audio.audiobookmaster;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class AudioPlayerActivity extends SingleFragmentActivity {

    private static final String EXTRA_BOOK = "book_extra";

    public static Intent newInstance(Context context, AudioBook audioBook) {
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra(EXTRA_BOOK, audioBook);
        return intent;
    }

    protected Fragment createFragment() {
        return AudioPlayerFragment.newInstance((AudioBook) getIntent().getSerializableExtra(EXTRA_BOOK));
    }

}
