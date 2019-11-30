public class SignLanguageConversions {
    public HashMap<String, String> wordsToASL;

    public SignLanguageConversions() {
        this.wordsToASL = new HashMap<String, String>();
    }

    // returns null if not found
    public String getASLImageUrl(String term) {
        String imageUrl = wordsToASL.get(term);
        return imageUrl;
    }
} 