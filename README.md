# progark-project

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


# How to play the game

### Main menu

When you first open the game you will be presented with the main menu. Here you can choose between creating a new game lobby, joining an existing lobby or seeing a tutorial video explaining how the game is played. You can navigate by touching the screen to press the buttons.

![main menu.png](attachment:4e5957a4-45d9-47f2-ac1d-8d0e0899b14f:main_menu.png)

### Tutorial

The tutorial displays a video showing how to play the game to help inexperienced players understand the functionality the game offers

### Create or join lobby

To create a new lobby, press the create lobby button and enter the name you want to be displayed in the game. You will be the game host for the lobby you create. The screen will then display a lobby code that you can share with friends or online to have them join the same game as you. When all your friends have joined you can press the start game button to start the game.

If you want of join an existing lobby and have already received a lobby code to a currently active lobby you can instead press the join lobby button. Here you get to enter a username and the lobby code to join the same lobby as your friends. You can se the other people in the that are joining the lobby displayed while you wait for the host to start the game.

<p align="center">
  <img src="(https://github.com/user-attachments/assets/efb70623-7cf8-4766-b258-fd8e9a7785aa)" width="300">
  <img src="https://github.com/user-attachments/assets/80816253-71f9-48a9-ae4b-97d0bd42b7f2" width="300">
  <img src="https://github.com/user-attachments/assets/bc74de66-3f0c-4ade-a7a2-2beda0df0de8" width="300">
</p>

### Gameplay/game loop

When the game starts the host player will start as the drawer while the other players are guessers. The drawer picks a word and draws in real time while the guessers guess at the correct word. After each turn of drawing/guessing a new drawer will be picked amongst all players to be the next drawer and a leaderboard will be displayed.

#### Drawing player

The drawing player first gets to choose a word to draw amongst a list of 6 word. The drawing player will then be taken to the drawing screen. Here they will see a white screen and can pick and switch between a number of colors by pressing on them to more accurately draw their creation. The drawing player uses their finger or mouse to draw depending on wether they are playing on a mobile or computer device.

#### Guessing player

The guessing players will see a screen with what the drawer is drawing in real time. They can enter the word they want to guess in the bar at the to of the screen and submit their guess by pressing the submit button. If the guess is incorrect red text will appear that tells them to “Try again” and the player may then guess a new word. If the guess is correct, green text will appear informing the guesser that their guess is “Correct”, the player will then be unable to write anymore guesses.


#### Timer & scoring

During each game turn a timer is displayed for both the drawers and the guessers. The timer is set to 60 seconds and the game turn ends when all players have guessed the correct word or when the time is up. Each player will receive a score for the turn based on the number of seconds remaining on the timer when they made a correct guess timed with the constant 3. This means faster guesses will get higher scores.

#### Leaderboard

At the end of each drawing/guessing turn a leaderboard is displayed. In the leaderboard the accumulated points across all turns for all players in the game lobby are shown. When the players are done playing the player with the highest score becomes the winner and the players may leave the game.

