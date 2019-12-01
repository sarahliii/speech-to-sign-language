package com.example.speech_to_sign_language.services;

import android.content.res.Resources;

import com.example.speech_to_sign_language.R;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class SignLanguageConversions {
    public HashMap<String, String> wordsToASL;
    public HashMap<String, String> aslToWords;
    private static final String WHITE_SPACE_SEPARATOR = "_";
    private Resources resources;

    public SignLanguageConversions(Resources resources) {
        this.resources = resources;
        this.wordsToASL = new HashMap<String, String>();
        this.aslToWords = new HashMap<String, String>();
        initWordsToASLDict();
    }

    private void initWordsToASLDict() {
        try {
            String[] images = resources.getAssets().list("images");

            for (String image : images) {
                String letterName = image.replaceAll(".png|.jpg|.jpeg", "");
                String withWhiteSpace = letterName.replaceAll(WHITE_SPACE_SEPARATOR, " ");
                wordsToASL.put(withWhiteSpace, image);
                aslToWords.put(image, withWhiteSpace);
            }
            wordsToASL.put(" ", "images/_.png");
        } catch(Exception e) {
            System.out.println("oopsies");
        }
    }

    // returns null if not found
    public String getASLImageUrl(String term) {
        return wordsToASL.get(term);
    }
}
