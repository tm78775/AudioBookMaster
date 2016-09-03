package com.innovativetech.audio.audiobookmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Timothy on 9/3/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";

    public static Bitmap convertByteArrayToBitmap(byte[] imageData) {
        Bitmap bitmap = null;
        if (imageData != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                imageData = null;
            } catch (Exception ex) {
                Log.e(TAG, "Error loading Bitmap from byte[].");
            }
        }
        return bitmap;
    }

}
