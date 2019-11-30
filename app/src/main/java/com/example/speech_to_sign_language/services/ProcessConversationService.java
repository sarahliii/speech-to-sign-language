package com.example.speech_to_sign_language.services;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProcessConversationService {
    private static final int TOKENS_LENGTH = 4;
    private SignLanguageConversions signLanguageConversions;

    public ProcessConversationService() {
        this.signLanguageConversions = new SignLanguageConversions();
    }

    public List<Path> processConversation(String conversation) {
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

    private List<Path> convertToASLURLs(List<String> wordsList) {
        int i = 0;
        int len = wordsList.size();
        List<Path> aslSignImageURLs = new ArrayList<>();

        while(i < len) {
            ASLURLModel aslModel = getURLsFromList(i, len, wordsList);
            aslSignImageURLs.addAll(aslModel.finalURLs);
            i += aslModel.wordsProcessed;
        }
        return aslSignImageURLs;
    }

    private ASLURLModel getURLsFromList(int start, int listSize, List<String> wordsList) {
        int end = start + TOKENS_LENGTH < listSize - 1 ? start + TOKENS_LENGTH : listSize - 1;
        // find if any phrase matches to asl
        for (; end >= start; end--) {
            String termToProcess = joinWithWhitespace(wordsList.subList(start, end));
            Path url = this.signLanguageConversions.getASLImageUrl(termToProcess);
            if (url != null) {
                return new ASLURLModel(end - start + 1, Collections.singletonList(url));
            }
        }

        // process individual letters
        String wordToProcess = wordsList.get(start);
        List<Path> letterUrls = new ArrayList<>();
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
        public List<Path> finalURLs;

        public ASLURLModel(int wordsProcessed, List<Path> urls)  {
            this.wordsProcessed = wordsProcessed;
            this.finalURLs = urls;
        }
    }
}