package com.example.speech_to_sign_language.services;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class SignLanguageConversions {
    public HashMap<String, Path> wordsToASL;
    public HashMap<Path, String> aslToWords;
    private static final String WHITE_SPACE_SEPARATOR = "_";

    public SignLanguageConversions() {
        this.wordsToASL = new HashMap<String, Path>();
        this.aslToWords = new HashMap<Path, String>();
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
                wordsToASL.put(withWhiteSpace, word.toPath());
                aslToWords.put(word.toPath(), withWhiteSpace);
            }
        }
    }

    // returns null if not found
    public Path getASLImageUrl(String term) {
        return wordsToASL.get(term);
    }
} 