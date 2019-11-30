import java.util.Arrays;
import java.util.List;

public class ProcessConversationService {
    public static final int TOKENS_LENGTH = 4;
    private SignLanguageConversions signLanguageConversions;

    public ProcessConversationService() {
        this.signLanguageConversions = new SignLanguageConversions();
    }
    
    public processConversation(String conversation) {
        conversation = conversation.toLowerCase();
        List<String> wordsList = splitIntoWords(conversation);    
        processSentences(wordsList);
    }

    public List<String> splitIntoWords(String conversation) {
        String withoutPunctuation = conversation.replaceAll("[^a-zA-Z ]", "");
        String[] words = withoutPunctuation.split(" ");
        ArrayList<String> wordsList = Arrays.asList(words);
        return wordsList;
    }

    private List<String> convertToASLURLs(List<String> wordsList) {
        int i = 0;
        int len = wordsList.size();
        List<String> aslSignImageURLs = new ArrayList<>();

        while(i < len) {
            ASLURLModel aslModel = getURLsFromList(i, len, wordsList);
            aslSignImageURLs.addAll(aslModel.finalURLs);
            i += aslModel.wordsProcessed;
        }
        return aslSignImageURLs;
    }

    private getURLsFromList(int start, int listSize, List<String> wordsList) {
        int end = start + TOKENS_LENGTH < listSize - 1 ? start + TOKENS_LENGTH : listSize - 1;
        // find if any phrase matches to asl 
        for (; end >= start; end--) {
            String termToProcess = String.join(" ", wordsList.subList(start, end));
            String url = this.signLanguageConversions.getASLImageURL(termToProcess);
            if (url != null) {
                return new ASLURLModel(end - start + 1, List.of(url));
            }
        }

        // process individual letters
        String wordToProcess = wordsList.get(start);
        List<String> letterUrls = new ArrayList<>();
        for (int i = 0, len = wordToProcess.length(); i < len; i++) {
            String letter = String.valueOf(wordToProcess.charAt(i));
            letterUrls.add(this.signLanguageConversions.getASImageURL(letter));
        }
        return new ASLURLModel(1, letterUrls);
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