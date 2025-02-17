package core;
import java.awt.*;


import tileengine.TERenderer;
import java.util.Objects;
import edu.princeton.cs.algs4.StdDraw;


public class Main extends prevProj3Play {
    World currentWorld = null;
    boolean isGameOver = false;
    boolean inputtedseed = false;
    World introScreen;
    boolean hasPressedN = false;
    boolean enteringName = false;
    boolean enteredName = false;

    StringBuilder prevActions = new StringBuilder();

    StringBuilder seed = new StringBuilder();

    StringBuilder avatarName = new StringBuilder();

    public String playerName;

    prevProj3Play storedPrevSeed = new prevProj3Play();
    boolean enteredSeed = false;
    private long prevActionTimestamp;

    private long prevFrameTimestamp;
    boolean hasGenerated = false;

    boolean quitSetup = false; //used to see if player inputted ":" so they can press "Q" and quit.

    public void runSeedIntake() {
        inputtedseed = false;
        while (!inputtedseed) {
            if (StdDraw.hasNextKeyTyped()) {
                if (enteringName && !enteredName) {
                    while (!enteredName) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char currentKey2 = StdDraw.nextKeyTyped();
                            if (currentKey2 == '~' || currentKey2 == '`') {
                                enteredName = true;
                                nameAvatarDisplay(String.valueOf(avatarName));
                            }
                            else {
                                if (avatarName.length() < 6) {
                                    avatarName.append(currentKey2);
                                }
                                nameAvatarDisplay(String.valueOf(avatarName));
                            }
                        }
                    }
                } //  This block renders the AvatarName on screen
                if (Objects.equals(readSeedFromFile(), "No previous save file")) {
                    seed = new StringBuilder();
                    seed.append(seedAndMoveExtractor()[0]);
                }
                if (StdDraw.hasNextKeyTyped()) {
                    char currentKey2 = StdDraw.nextKeyTyped();
                    if (currentKey2 == 'n' || currentKey2 == 'N' && !hasGenerated) {
                        System.out.print(seed);
                        hasPressedN = true;
                        while (hasPressedN && !hasGenerated) {
                            showSeed(seed.toString());
                            if (StdDraw.hasNextKeyTyped()) {
                                currentKey2 = StdDraw.nextKeyTyped();
                             if (Character.isDigit(currentKey2)) {
                                 seed.append(currentKey2);
                             }
                                if (currentKey2 == 'S' || currentKey2 == 's' || hasPressedN && (!Character.isDigit(currentKey2))) {
                                    if (seed.isEmpty()) {
                                        runWorld(String.valueOf(1), false); // If no seed is enter run it with 0
                                    }
                                    if (seed.length() < 17) {
                                        while (seed.length() < 17) {
                                            seed.append("9");
                                        }
                                    }
                                    runWorld(String.valueOf(seed), false);
                                }
                            }
                        }
                    }
                    else if (currentKey2 == 'O' || currentKey2 == 'o') {
                        enteringName = true;
                    }
                    else if (currentKey2 == 'L' || currentKey2 == 'l') {
                        //setting to true now, but must change to a check for whether there is a previous seed to load
                        if (seedAndMoveExtractor() != null) {
                            if (seedAndMoveExtractor()[0] == "") {
                                System.out.println("WEE WEE WOO seed below: ");
                            }
                            currentWorld = World.main(new String[]{seedAndMoveExtractor()[0]});
                            runGame(seedAndMoveExtractor()[1], false);
                        }
                        else {
                            System.exit(0);
                        }
                    }
                    else if (currentKey2 == 'R' || currentKey2 == 'r') {
                        //setting to true now, but must change to a check for whether there is a previous seed to load
                        if (seedAndMoveExtractor() != null) {
                            currentWorld = World.main(new String[]{seedAndMoveExtractor()[0]});
                            runGame(seedAndMoveExtractor()[1], true);
                        }
                        else {
                            System.out.print("*** SHOULD EXIT ***");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    public void runWorld(String seed, boolean replaying) {
        if (!replaying) {
            currentWorld = World.main(new String[]{seed});
        }
        hasGenerated = true;
        updateBoard();
        renderWorld(currentWorld);
        renderHUD(avatarName.toString());
        runGame();
    }
    public void showIntro () {
        Font font = new Font("Arial", Font.BOLD, 80);
        Font font2 = new Font("Arial", Font.BOLD, 40);
        Font font3 = new Font("Arial", Font.BOLD, 30);
        StdDraw.clear(Color.BLACK);
        nameAvatarDisplay(String.valueOf(avatarName));
        StdDraw.setFont(font);
        StdDraw.setPenColor(255, 0, 255);
        StdDraw.text(40, 33, "CS61B: THE GAME");
        StdDraw.setFont(font2);
        StdDraw.setFont(font3);
        StdDraw.text(40, 21, "NEW WORLD ('N') ");
        StdDraw.text(40, 17, "LOAD OLD WORLD ('L') ");
        StdDraw.text(40, 13, "REPLAY LAST RUN ('R') ");
        StdDraw.text(40, 9, "QUIT GAME (':Q') ");
        StdDraw.text(40, 5, "MOVE WITH W,A,S,D KEYS ");
        StdDraw.show();
    }

    public void showSeed(String seed) { // prints out the seed
        Font font2 = new Font("Arial", Font.BOLD, 40);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(255, 0, 255);
        StdDraw.setFont(font2);
        if (hasPressedN) {
            StdDraw.text(40, 25, "ENTER SEED: N " + seed);
        }
        else {
            StdDraw.text(40, 25, "ENTER SEED: ");
        }
        StdDraw.show();
    }

    public void nameAvatarDisplay(String avatarName) { // Displays the naming of their Avatar
        Font font3 = new Font("Arial", Font.BOLD, 30);
        Font font4 = new Font("Arial", Font.BOLD, 14);
        StdDraw.setFont(font3);
        StdDraw.setPenColor(Color.RED);
        StdDraw.textLeft(2, 18, "PRESS ''O'' TO NAME");
        StdDraw.textLeft(2, 16, "YOUR AVATAR:");
        StdDraw.textLeft(2, 5, "REGISTER WITH ''~''");
        StdDraw.setFont(font4);
        StdDraw.textLeft(5, 14, "6 CHARACTERS MAX");
        StdDraw.setFont(font3);
        StdDraw.setPenColor(Color.CYAN);
        StdDraw.textLeft(57, 18, "WATCH OUT FOR THE");
        StdDraw.textLeft(57, 16, "WALLS");
        StdDraw.textLeft(57, 9, "EAT 5 TREES");
        StdDraw.textLeft(57, 7, "TO REGAIN HEALTH");
        if (enteringName) {
            if (enteredName) { // changes color to verify name entry
                StdDraw.setPenColor(255, 0, 0);
            }
            else {
                StdDraw.setPenColor(Color.WHITE);
            }
            StdDraw.setFont(font3);
            StdDraw.textLeft(4, 11, avatarName);
            playerName = avatarName;
        }
        StdDraw.show();
    }

    public void runGame() {
        isGameOver = false;
        while (!isGameOver) {
            if (shouldRenderNewFrame()) {
                updateBoard();
            }
        }
    }
    public void renderHUD(String avatarName) {
        currentWorld.renderHUD(avatarName);
        currentWorld.avatarNameSaver(avatarName);
        currentWorld.renderHealthBarHUD();
        StdDraw.show();
    }



    public void runGame(String moves, boolean replaying) {
        isGameOver = false;
        String [] movements = moves.split("");
        int i = 0;
        while (i < movements.length) {
            String currMove = movements[i];
            System.out.println(currMove);
            if (replaying) {
                StdDraw.pause(50);
            }
            if (Objects.equals(currMove, "S")) {
                prevActions.append('S');
                quitSetup = false;
                currentWorld.moveAvatar(0, -1);
                renderWorld(currentWorld);
            }
            else if (Objects.equals(currMove, "W")) {
                prevActions.append('W');
                quitSetup = false;
                currentWorld.moveAvatar(0, 1);
                renderWorld(currentWorld);
            }
            else if (Objects.equals(currMove, "A")) {
                prevActions.append('A');
                quitSetup = false;
                currentWorld.moveAvatar(-1, 0);
                renderWorld(currentWorld);
            }
            else if (Objects.equals(currMove, "D")) {
                prevActions.append('D');
                quitSetup = false;
                currentWorld.moveAvatar(1, 0);
                renderWorld(currentWorld);
            }
            i++;
        }
        while (!isGameOver) {
            if (shouldRenderNewFrame()) {
                updateBoard();
                renderWorld(currentWorld);
                renderHUD(avatarName.toString());
                runGame();
            }
        }
    }

    public String[] seedAndMoveExtractor() {
        if (Objects.equals(readSeedFromFile(), "No previous save file")) {
            return null;
        }
        String[] listSeedAndMoveFile= readSeedFromFile().split("");
        StringBuilder seedExtracted  = new StringBuilder();
        StringBuilder moveExtracted = new StringBuilder();
        String [] product = new String[2];
        int i = 1;
        if (listSeedAndMoveFile.length < 2) {
            return null;
        }
        while (!Objects.equals(listSeedAndMoveFile[i], "S")) {
            seedExtracted.append(listSeedAndMoveFile[i]);
            i++;
        }
        i++;
        while (i < listSeedAndMoveFile.length) {
            moveExtracted.append(listSeedAndMoveFile[i]);
            i++;
        }
        product[0] = seedExtracted.toString();
        product[1] = moveExtracted.toString();
        return product;
    }


    public void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char currentKey = StdDraw.nextKeyTyped();
            if (currentKey == 's' || currentKey == 'S') {
                prevActions.append('S');
                quitSetup = false;
                currentWorld.moveAvatar(0, -1);
                renderWorld(currentWorld);
                renderHUD(avatarName.toString());
            } else if (currentKey == 'w' || currentKey == 'W') {
                prevActions.append('W');
                quitSetup = false;
                currentWorld.moveAvatar(0, 1);
                renderWorld(currentWorld);
                renderHUD(avatarName.toString());
            } else if (currentKey == 'a' || currentKey == 'A') {
                prevActions.append('A');
                quitSetup = false;
                currentWorld.moveAvatar(-1, 0);
                renderWorld(currentWorld);
                renderHUD(avatarName.toString());
            } else if (currentKey == 'd' || currentKey == 'D') {
                prevActions.append('D');
                quitSetup = false;
                currentWorld.moveAvatar(1, 0);
                renderWorld(currentWorld);
                renderHUD(avatarName.toString());
            }
            if (currentKey == ':') {
                System.out.print("might quit");
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.textLeft(10, 20, "Are you sure you would like to quit? If so, press Q");
                StdDraw.show();
                StdDraw.pause(1500);
                quitSetup = true;
            }
            else if (currentKey == 'q' || currentKey == 'Q') {
                if (quitSetup) {
                    seed.insert(0, "N");
                    seed.append("S");
                    seed.append(prevActions);
                    storedPrevSeed.writeSeedToFile(seed);
                    System.exit(0);
                }
            }
        }
    }

    private final TERenderer ter = new TERenderer();


    public void renderWorld(World world) {
        ter.renderFrame(world.getTiles());

    }
    public boolean shouldRenderNewFrame() {
        if (frameDeltaTime() > 50) {
            resetFrameTimer();
            return true;
        }
        return false;
    }
    private long frameDeltaTime() {
        return System.currentTimeMillis() - prevFrameTimestamp;
    }

    /**
     * Resets the action timestamp to the current time in milliseconds.
     */
    private void resetFrameTimer() {
        prevFrameTimestamp = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        Main mainInstance = new Main();
        mainInstance.introScreen = World.main(new String[]{"0"});
        mainInstance.showIntro();
        mainInstance.runSeedIntake();
    }

}