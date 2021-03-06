package com.example.speech_to_sign_language.services;

import android.content.res.Resources;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProcessConversationService {
    private static final int TOKENS_LENGTH = 4;
    private SignLanguageConversions signLanguageConversions;

    public ProcessConversationService(Resources resources) {
        this.signLanguageConversions = new SignLanguageConversions(resources);
    }

    public List<String> processConversation(String conversation) {
        List<String> wordsList = splitIntoWords(conversation);
        return convertToASLURLs(wordsList);
    }

    private List<String> splitIntoWords(String conversation) {
        String lowercased = conversation.toLowerCase();
        String withoutPunctuation = lowercased.replaceAll("[^a-z ]", "");
        String[] words = withoutPunctuation.split(" ");
        List<String> wordsList = Arrays.asList(words);
        return wordsList;
    }

    private List<String> convertToASLURLs(List<String> wordsList) {
        int i = 0;
        int len = wordsList.size();
        List<String> aslSignImageURLs = new ArrayList<>();

        while(i < len) {
            ASLURLModel aslModel = getURLsFromList(i, len, wordsList);
            aslSignImageURLs.addAll(aslModel.finalURLs);
            aslSignImageURLs.add(signLanguageConversions.getASLImageUrl(" "));
            i += aslModel.wordsProcessed;
        }
        return aslSignImageURLs;
    }

    private ASLURLModel getURLsFromList(int start, int listSize, List<String> wordsList) {
        int end = start + TOKENS_LENGTH < listSize - 1 ? start + TOKENS_LENGTH : listSize - 1;
        // find if any phrase matches to asl
        for (; end >= start; end--) {
            String termToProcess = joinWithWhitespace(wordsList.subList(start, end + 1));
            String url = this.signLanguageConversions.getASLImageUrl(termToProcess);
            if (url != null) {
                return new ASLURLModel(end - start + 1, Collections.singletonList(url));
            }
        }

        // process individual letters
        String wordToProcess = wordsList.get(start);
        List<String> letterUrls = new ArrayList<>();
        for (int i = 0, len = wordToProcess.length(); i < len; i++) {
            String letter = String.valueOf(wordToProcess.charAt(i));
            letterUrls.add(this.signLanguageConversions.getASLImageUrl(letter));
        }
        return new ASLURLModel(1, letterUrls);
    }

    private String joinWithWhitespace(List<String> subList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = subList.size() - 1; i < len; i++) {
            builder.append(subList.get(i));
            builder.append(" ");
        }
        builder.append(subList.get(subList.size() - 1));
        return builder.toString();
    }

    private class ASLURLModel {
        public int wordsProcessed;
        public List<String> finalURLs;

        public ASLURLModel(int wordsProcessed, List<String> urls)  {
            this.wordsProcessed = wordsProcessed;
            this.finalURLs = urls;
        }
    }
}