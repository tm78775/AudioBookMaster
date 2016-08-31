package com.innovativetech.audio.audiobookmaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TMiller on 8/31/2016.
 */
public class AudioBookLibrary {

    public static List<AudioBook> getLibrary() {
        List<AudioBook> books = new ArrayList<AudioBook>();


        // todo: this is a sample audiobook for testing the recyclerview.
        AudioBook book = new AudioBook();
        book.setAuthor("Jim Butcher");
        book.setTitle("Blood Rites");
        book.setImageDir("/06 - Blood Rites/BK-6 Blood Rites/bloodrites.jpg");

        books.add(book);

        return books;
    }

}
