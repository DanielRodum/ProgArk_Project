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

# How to Run the Game

### Android

<strong>Step 1</strong> - Allow for the download of .apk files by going to Settings -> Apps -> Special app access -> Install unknown apps, and clicking "Allow" for the web browser of choice (e.g. Google Chrome)

<p align="center">
  <img src="https://github.com/user-attachments/assets/989c622a-5706-47b3-a87c-1be37d64942d" width="300">&nbsp;
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/b3f04e7c-9130-4a88-a702-fe01610b3874" width="300">&nbsp;
  <img src="https://github.com/user-attachments/assets/e33fc6e1-528c-4241-9b02-784fb386f38c" width="300">&nbsp;
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f45cbcef-3dbc-465d-8ee4-6af87a9468dc" width="300">&nbsp;
  <img src="https://github.com/user-attachments/assets/d5579e2f-386b-4e41-a5a0-9f15de1b68e7" width="300">&nbsp;
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f3846e95-ec00-46c5-a44d-7d17b079d962" width="300">&nbsp;
</p>

<strong>Step 2<strong> - Download .apk file of the game from this repository on the desired android device using the previously chosen web browser

<strong>Step 3<strong> - Open the file and install the application. Once finished the app should appear on the home screen of the device.


### PC

<strong>Step 1<strong> - Download Android Studios:

- Check that the PC in question has the minimum requirements for running the program [here](https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#1).
- If it meets the requirements, one can download Android Studios [here](https://developer.android.com/studio#get-android-studio).
- Open the downloaded file to start the installation.

<strong>Step 2<strong> - Get the Code:

- Download the code from the production branch of this repository 
- The easiest way to do this is by downloading it as a zip file

<p align="center">
  <img src="https://github.com/user-attachments/assets/d17911d2-76b1-4768-87cc-f50e3f163a8f" width="300">&nbsp;
  <img src="https://github.com/user-attachments/assets/2307a4e5-241b-446d-bc8e-bbbb7aa1b3e9" width="300">&nbsp;
</p>

<strong>Step 3<strong> - Open the Code:

- Unzip the file and open it using Android Studios
- Click the "run" button as shown in the figure below

<p align="center">
  <img src="https://github.com/user-attachments/assets/58bc1213-2bff-497f-a1a3-daaae22f8ae6" width="300">&nbsp;
</p>

<strong>Disclaimer!<strong> - As this is a multiplayer game only, one cannot play using a single device alone. Repeat the steps either for Android or PC on another device to be able to play.

# How to play the game

### Main menu

When you first open the game you will be presented with the main menu. Here you can choose between creating a new game lobby, joining an existing lobby or seeing a tutorial video explaining how the game is played. You can navigate by touching the screen to press the buttons.

<p align="center">
  <img src="https://github.com/user-attachments/assets/1835cc3e-2cc7-4790-b434-ec0e918e615a" width="300">&nbsp;
</p>

### Tutorial

The tutorial displays a video showing how to play the game to help inexperienced players understand the functionality the game offers

### Create or join lobby

To create a new lobby, press the create lobby button and enter the name you want to be displayed in the game. You will be the game host for the lobby you create. The screen will then display a lobby code that you can share with friends or online to have them join the same game as you. When all your friends have joined you can press the start game button to start the game.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e68d245d-51b4-477a-a0db-10a090abda51" width="300">&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/974bda48-b9ed-42d6-89f8-a66152714a05" width="300">&nbsp;&nbsp;
</p>

If you want of join an existing lobby and have already received a lobby code to a currently active lobby you can instead press the join lobby button. Here you get to enter a username and the lobby code to join the same lobby as your friends. You can se the other people in the that are joining the lobby displayed while you wait for the host to start the game.

<p align="center">
  <img src="https://github.com/user-attachments/assets/f9e8fad5-9c83-47cd-be3e-84427cd03684" width="300">&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/095b7cae-da20-4d6a-b1b5-1681709c30cb" width="300">&nbsp;
</p>

### Gameplay/game loop

When the game starts the host player will start as the drawer while the other players are guessers. The drawer picks a word and draws in real time while the guessers guess at the correct word. After each turn of drawing/guessing a new drawer will be picked amongst all players to be the next drawer and a leaderboard will be displayed.

#### Drawing player

The drawing player first gets to choose a word to draw amongst a list of 6 word. The drawing player will then be taken to the drawing screen. Here they will see a white screen and can pick and switch between a number of colors by pressing on them to more accurately draw their creation. The drawing player uses their finger or mouse to draw depending on wether they are playing on a mobile or computer device.

<p align="center">
  <img src="https://github.com/user-attachments/assets/0451fc23-87b8-406e-aa0b-dfcf09334123" width="300">&nbsp;
  <img src="https://github.com/user-attachments/assets/efb70623-7cf8-4766-b258-fd8e9a7785aa" width="300">&nbsp;
</p>


#### Guessing player

The guessing players will see a screen with what the drawer is drawing in real time. They can enter the word they want to guess in the bar at the to of the screen and submit their guess by pressing the submit button. If the guess is incorrect red text will appear that tells them to “Try again” and the player may then guess a new word. If the guess is correct, green text will appear informing the guesser that their guess is “Correct”, the player will then be unable to write anymore guesses.

<p align="center">
  <img src="https://github.com/user-attachments/assets/80816253-71f9-48a9-ae4b-97d0bd42b7f2" width="300">&nbsp;
</p>

#### Timer & scoring

During each game turn a timer is displayed for both the drawers and the guessers. The timer is set to 60 seconds and the game turn ends when all players have guessed the correct word or when the time is up. Each player will receive a score for the turn based on the number of seconds remaining on the timer when they made a correct guess timed with the constant 3. This means faster guesses will get higher scores.

#### Leaderboard

At the end of each drawing/guessing turn a leaderboard is displayed. In the leaderboard the accumulated points across all turns for all players in the game lobby are shown. When the players are done playing the player with the highest score becomes the winner and the players may leave the game.

<p align="center">
  <img src="https://github.com/user-attachments/assets/bc74de66-3f0c-4ade-a7a2-2beda0df0de8" width="300">&nbsp;
</p>
