package com.innovativetech.audio.audiobookmaster;

import android.support.v4.app.Fragment;

public class LibraryActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return LibraryFragment.newInstance();
    }

}
