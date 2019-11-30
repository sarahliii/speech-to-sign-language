package com.example.speech_to_sign_language.services;

import java.io.File;
import java.util.HashMap;

public class SignLanguageConversions {
    public HashMap<String, String> wordsToASL;
    public HashMap<String, String> aslToWords;
    private static final String WHITE_SPACE_SEPARATOR = "_";

    public SignLanguageConversions() {
        this.wordsToASL = new HashMap<String, String>();
        initWordsToASLDict();
    }

    private void initWordsToASLDict() {
        File folder = new File("resources");
        File[] words = folder.listFiles();

        for (File word : words) {
            if (word.isFile()) {
                String fileName = word.getName();
                String letterName = fileName.replaceAll(".png|.jpg|.jpeg", "");
                String withWhiteSpace = letterName.replaceAll(WHITE_SPACE_SEPARATOR, " ");
                wordsToASL.put(withWhiteSpace, fileName);
                aslToWords.put(fileName, withWhiteSpace);
            }
        }
    }

    // returns null if not found
    public String getASLImageUrl(String term) {
        return wordsToASL.get(term);
    }
} 