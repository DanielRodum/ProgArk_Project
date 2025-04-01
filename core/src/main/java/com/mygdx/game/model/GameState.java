import java.util.*;

public class GameStateModel {
    private List<Player> players;
    private String currentWord;
    private Player currentDrawer;
    private int currentRound;
    private Map<String, Integer> scores;
    private int timeLimit; // Time per round (seconds)
    private List<Guess> guesses;

    public GameStateModel(List<Player> players, int timeLimit) {
        this.players = players;
        this.timeLimit = timeLimit;
        this.currentRound = 0;
        this.scores = new HashMap<>();
        this.guesses = new ArrayList<>();
        for (Player player : players) {
            scores.put(player.getId(), 0); // Initialize scores
        }
    }

    public void startNewRound(String newWord) {
        this.currentWord = newWord;
        this.currentDrawer = players.get(currentRound % players.size()); // Rotate drawer
        this.currentRound++;
        this.guesses.clear(); // Reset guesses for the new round
    }

    public void addGuess(Player player, String guess, boolean isCorrect) {
        guesses.add(new Guess(player, guess, isCorrect));
        if (isCorrect) {
            scores.put(player.getId(), scores.get(player.getId()) + calculatePoints());
        }
    }

    private int calculatePoints() {
        return Math.max(10 - guesses.size(), 1); // Example scoring logic
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public Player getCurrentDrawer() {
        return currentDrawer;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public List<Guess> getGuesses() {
        return guesses;
    }
}
