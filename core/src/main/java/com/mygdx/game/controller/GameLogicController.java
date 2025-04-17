public class GameLogicController {
    import java.util.Random;

public class GameLogicController {
    private GameStateModel gameState;
    private WordDatabase wordDatabase;

    public GameLogicController(GameStateModel gameState, WordDatabase wordDatabase) {
        this.gameState = gameState;
        this.wordDatabase = wordDatabase;
    }

    public void startGame() {
        startNewRound();
    }

    public void startNewRound() {
        String randomWord = wordDatabase.getRandomWord();
        gameState.startNewRound(randomWord);
        System.out.println("New round started! Drawer: " + gameState.getCurrentDrawer().getName());
    }

    public void submitGuess(Player player, String guess) {
        boolean isCorrect = guess.equalsIgnoreCase(gameState.getCurrentWord());
        gameState.addGuess(player, guess, isCorrect);
        player.updateStats(isCorrect);
        
        if (isCorrect) {
            System.out.println(player.getName() + " guessed correctly! Points awarded.");
        } else {
            System.out.println(player.getName() + " guessed: " + guess);
        }
    }
}
