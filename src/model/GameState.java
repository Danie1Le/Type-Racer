package model;

public class GameState {
    private boolean isGameStarted;
    private long gameStartTimeMillis;
    private int gameDurationInSeconds;
    private Quote currentQuote;
    
    public GameState() {
        this.isGameStarted = false;
        this.gameDurationInSeconds = 10; // default time
    }
    
    public void prepareGame() {
        isGameStarted = false;
        currentQuote = Quote.generateRandom(gameDurationInSeconds);
    }
    
    public void startTimer() {
        isGameStarted = true;
        gameStartTimeMillis = System.currentTimeMillis();
    }
    
    public void endGame() {
        isGameStarted = false;
    }
    
    public double calculateAccuracy(String typed) {
        if (currentQuote == null) return 0.0;
        
        String original = currentQuote.getContent();
        int correctChars = 0;
        for (int i = 0; i < Math.min(original.length(), typed.length()); i++) {
            if (original.charAt(i) == typed.charAt(i)) {
                correctChars++;
            }
        }
        return(double) correctChars / original.length();
    }
    
    public long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTimeMillis;
    }
    
    public boolean isGameStarted() {
        return isGameStarted;
    }
    
    public Quote getCurrentQuote() {
        return currentQuote;
    }
    
    public void setSelectedDuration(int duration) {
        this.gameDurationInSeconds = duration;
    }
    
    public int getSelectedDuration() {
        return gameDurationInSeconds;
    }
}
