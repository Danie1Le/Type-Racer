package model;

import java.util.Random;

public class Quote {
    // list of common words
    private static final String[] COMMON_WORDS = {
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I", 
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also"
    };
    
    private final String content;
    
    public Quote(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public static Quote generateRandom(int duration) {
        Random random = new Random();
        StringBuilder quote = new StringBuilder();
        
        int targetTextLength = duration * 15; // calculate character cound based on time
        int currentTextLength = 0;
        
        while (currentTextLength < targetTextLength) {
            String word = COMMON_WORDS[random.nextInt(COMMON_WORDS.length)];
            
            // add a space if the quote is not empty
            if (currentTextLength > 0) {
                quote.append(" ");
                currentTextLength++;
            }
            
            quote.append(word); // add word
            currentTextLength += word.length(); // update length of text
        }
        
        return new Quote(quote.toString());
    }
}
