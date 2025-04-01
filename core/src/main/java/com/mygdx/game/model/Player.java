public class Player {
    
    private String id;
    private String name;
    private int totalGamesPlayed;
    private int correctGuesses;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.totalGamesPlayed = 0;
        this.correctGuesses = 0;
    }

    public void updateStats(boolean isCorrect) {
        totalGamesPlayed++;
        if (isCorrect) {
            correctGuesses++;
        }
    }

    public double getAccuracy() {
        if (totalGamesPlayed == 0) {
            return 0.0; // Avoid division by zero
        }
        return (double) correctGuesses / totalGamesPlayed;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public int getCorrectGuesses() {
        return correctGuesses;
    }
}