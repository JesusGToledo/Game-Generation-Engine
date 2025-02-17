package core;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.
        WeightedQuickUnionUF;
import org.w3c.dom.stylesheets.LinkStyle;

import tileengine.TERenderer;

import tileengine.TETile;

import tileengine.Tileset;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Random;


// disjoijtset of size of how many rooms we are creating, union and connect rooms like this
//

/**
 * Draws a world consisting of knight-move holes.
 */
public class World {
    // should be 3 to the right and one up before next insert
    private WeightedQuickUnionUF roomRelations;
    private TETile[][] tiles;
    private Integer[][] roomInfo;
    int width;
    boolean isGameOver = false;
    int height;
    int numOfRooms;
    int pointTracker;
    int numOfTreats;
    int healthHearts;
    boolean regainedHealth;


    // NEW
    boolean hasAvatar = false;



    int avatarY;
    int avatarX;
    String avatarName;
    /*
    Outside space (in other words space that is not being used as part of the world
    is going to be denoted by nothing.
     */
    // TODO: Add additional instance variables here

    public World(int width, int height, long seed) {
        // NEW
        Random randomSeed = new Random(seed);
        this.width = width;
        this.height = height - 5;
        tiles = new TETile[this.width][this.height];
        tileFiller(randomSeed);
        // NEW
        renderAvatar();
        renderHUD(avatarName);
        // TODO: Fill in this constructor and class, adding helper methods and/or classes as necessary to draw the
        //  specified pattern of the given hole size for a window of size width x height. If you're stuck on how to
        //  begin, look at the provided demo code!
    }
    public void avatarNameSaver (String givenAvatarName) {
        avatarName = givenAvatarName;
    }

    public void tileFiller(Random randomSeed) {
        numOfRooms = 0;
        pointTracker = 0;
        healthHearts = 3;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int desiredNumOfRooms = randomSeed.nextInt(15, 20); //
        roomInfo = new Integer[desiredNumOfRooms][5];
        while (numOfRooms < desiredNumOfRooms) {
            roomMaker(randomSeed);
        }
        hallwayMaker(randomSeed);
        randomTreatPlacer(randomSeed);
    }

    public void roomMaker(Random randomSeed) {
        int coordinateX = randomSeed.nextInt(0, width - 1);
        int coordinateY = randomSeed.nextInt(0, height - 1);
        int roomWidth = randomSeed.nextInt(7, 12); // actually between 3-10 due to edges being turned into borders
        int roomHeight = randomSeed.nextInt(7, 12); // actually between 3-10 due to edges being turned into borders
        if (spaceMaker(coordinateX, coordinateY, roomWidth, roomHeight, Tileset.FLOOR, new boolean[2])) {
            numOfRooms += 1; // called here since this means we have created a random room
            roomInfo[numOfRooms - 1][0] = numOfRooms - 1; //roomID #
            roomInfo[numOfRooms - 1][1] = coordinateX;
            roomInfo[numOfRooms - 1][2] = coordinateY;
            roomInfo[numOfRooms - 1][3] = roomWidth;
            roomInfo[numOfRooms - 1][4] = roomHeight;
            System.out.println(roomInfo[numOfRooms - 1][0]);
        }
    }

    public void hallwayMaker(Random randomSeed) {
        roomRelations = new WeightedQuickUnionUF(numOfRooms + 1); //after all rooms created so numOfRooms = desiredNumOfRooms
        for (Integer[] i : roomInfo) {
            boolean[] isHallway = {true, true};
            //first for x+
            int startX = i[1] + i[3] - 1;
            //int startX = new Random().nextInt(i[1] + 1, i[1] + i[3] - 1); //i[1] + 1 so that you don't get a belted room [-]
            int startY = randomSeed.nextInt(i[2], i[2] + i[4] - 3);
            int bumpedWall = 0;
            int currPos = startX;
            for (currPos = startX; currPos < this.width - 1 && bumpedWall < 2; currPos++) {
                if (tiles[currPos][startY] == Tileset.WALL) {
                    bumpedWall += 1;
                } else if (tiles[currPos][startY] == Tileset.FLOOR) {
                    bumpedWall += 3;
                }
            }
            if (bumpedWall == 2 && !roomRelations.connected(i[0], roomFinder(currPos, startY))) {
                System.out.println("hallway x+ created and roomfinder(currPos, startY) equals: " + roomFinder(currPos, startY) + " which is not equal to numOfRooms: " + numOfRooms);
                spaceMaker(startX, startY, currPos - startX, 3, Tileset.FLOOR, isHallway);
                currPos -= 1;
                if (tiles[currPos + 1][startY + 1] == Tileset.WALL && roomFinder(currPos, startY) == roomFinder(currPos + 1, startY + 1)) {
                    tiles[currPos][startY] = Tileset.FLOOR;
                    //tiles[currPos + 1][startY + 2] = Tileset.WALL;
                    //tiles[currPos - 1][startY - 1] = Tileset.WALL;
                } else if (tiles[currPos + 1][startY + 1] == Tileset.NOTHING) {
                    tiles[currPos][startY] = Tileset.FLOOR;
                    tiles[currPos][startY - 1] = Tileset.FLOOR;
                    tiles[currPos + 1][startY + 1] = Tileset.WALL;
                    tiles[currPos - 1][startY - 1] = Tileset.WALL;
                    //tiles[currPos + 1][startY + 2] = Tileset.WALL;
                    //tiles[currPos - 1][startY - 2] = Tileset.WALL;
                }
                if (roomFinder(currPos, startY) != numOfRooms) {
                    roomRelations.union(i[0], roomFinder(currPos, startY));
                }
                System.out.println("helloX");
            }
            //second for x-
            startX = i[1];
            //startX = new Random().nextInt(i[1], i[1] + i[3] - 2); // - 2 so that you don't get a belted room [-]
            startY = randomSeed.nextInt(i[2], i[2] + i[4] - 3);
            bumpedWall = 0;
            currPos = startX;
            for (currPos = startX; currPos > 0 && bumpedWall < 2; currPos--) {
                if (tiles[currPos][startY] == Tileset.WALL) {
                    bumpedWall += 1;
                }
            }
            if (bumpedWall == 2 && !roomRelations.connected(i[0], roomFinder(currPos, startY))) {
                System.out.println("hallway x- created and roomfinder(currPos, startY) equals: " + roomFinder(currPos, startY) + " which is not equal to numOfRooms: " + numOfRooms);
                spaceMaker(currPos + 1, startY, startX - currPos, 3, Tileset.FLOOR, isHallway);
                currPos += 1;
                if (tiles[currPos - 1][startY + 1] == Tileset.WALL && roomFinder(currPos, startY) == roomFinder(currPos - 1, startY + 1)) {
                    tiles[currPos][startY] = Tileset.FLOOR;
                    //tiles[currPos - 1][startY + 2] = Tileset.WALL;
                    //tiles[currPos + 1][startY - 1] = Tileset.WALL;
                } else if (tiles[currPos - 1][startY + 1] == Tileset.NOTHING) {
                    tiles[currPos][startY] = Tileset.FLOOR;
                    tiles[currPos][startY - 1] = Tileset.FLOOR;
                    tiles[currPos + 1][startY - 1] = Tileset.WALL;
                    //tiles[currPos + 1][startY - 2] = Tileset.WALL;
                    tiles[currPos - 1][startY + 1] = Tileset.WALL;
                    //tiles[currPos - 1][startY + 2] = Tileset.WALL;
                }
                if (roomFinder(currPos, startY) != numOfRooms) {
                    roomRelations.union(i[0], roomFinder(currPos, startY));
                }
                System.out.println("hiiX");
            }
            //changing the isHallway to represent vertical:
            isHallway[1] = false;
            //third for y+
            startX = randomSeed.nextInt(i[1], i[1] + i[3] - 3);
            startY = i[2] + i[4] - 1;
            bumpedWall = 0;
            currPos = startY;
            for (currPos = startY; currPos < this.height - 1 && bumpedWall < 2; currPos++) {
                if (tiles[startX][currPos] == Tileset.WALL) {
                    bumpedWall += 1;
                }
            }
            if (bumpedWall == 2 && !roomRelations.connected(i[0], roomFinder(startX, currPos))) {
                System.out.println("hallway y+ created and roomfinder(startX, currPos) equals: " + roomFinder(startX, currPos) + " which is not equal to numOfRooms: " + numOfRooms);
                spaceMaker(startX, startY, 3, currPos - startY, Tileset.FLOOR, isHallway);
                currPos -= 1;
                if (tiles[startX + 1][currPos + 1] == Tileset.WALL && roomFinder(startX, currPos) == roomFinder(startX + 1, currPos + 1)) {
                    System.out.println("startX = " + startX);
                    System.out.println("currPos = " + currPos);
                    tiles[startX][currPos] = Tileset.FLOOR;
                    //tiles[startX + 2][currPos + 1] = Tileset.WALL;
                    //tiles[startX - 1][currPos - 1] = Tileset.WALL;
                } else if (tiles[startX + 1][currPos + 1] == Tileset.NOTHING) {
                    System.out.println("startX = " + startX);
                    System.out.println("currPos = " + currPos);
                    tiles[startX][currPos] = Tileset.FLOOR;
                    tiles[startX][currPos + 1] = Tileset.FLOOR;
                    tiles[startX + 1][currPos + 1] = Tileset.WALL;
                    //tiles[startX - 1][currPos - 1] = Tileset.WALL;
                    //tiles[startX + 1][currPos + 2] = Tileset.WALL;
                    //tiles[startX + 2][currPos + 1] = Tileset.WALL;

                }
                if (roomFinder(startX, currPos) != numOfRooms) {
                    roomRelations.union(i[0], roomFinder(startX, currPos));
                }
                System.out.println("helloY");
            }
            //fourth for y-
            startX = randomSeed.nextInt(i[1], i[1] + i[3] - 3);
            startY = i[2];
            bumpedWall = 0;
            currPos = startY;
            for (currPos = startY; currPos > 0 && bumpedWall < 2; currPos--) {
                if (tiles[startX][currPos] == Tileset.WALL) {
                    bumpedWall += 1;
                }
            }
            if (bumpedWall == 2 && !roomRelations.connected(i[0], roomFinder(startX, currPos))) {
                System.out.println("hallway y- created and roomfinder(startX, currPos) equals: " + roomFinder(startX, currPos) + " which is not equal to numOfRooms: " + numOfRooms);
                spaceMaker(startX, currPos + 1, 3, startY - currPos, Tileset.FLOOR, isHallway);
                currPos += 1;
                if (tiles[startX + 1][currPos - 1] == Tileset.WALL && roomFinder(startX, currPos) == roomFinder(startX + 1, currPos - 1)) {
                    System.out.println("startX = " + startX);
                    System.out.println("currPos = " + currPos);
                    tiles[startX][currPos] = Tileset.FLOOR;
                    //tiles[startX - 1][currPos + 1] = Tileset.WALL;
                    //tiles[startX + 2][currPos - 1] = Tileset.WALL;
                } else if (tiles[startX + 1][currPos - 1] == Tileset.NOTHING) {
                    System.out.println("startX = " + startX);
                    System.out.println("currPos = " + currPos);
                    tiles[startX][currPos] = Tileset.FLOOR;
                    tiles[startX - 1][currPos] = Tileset.FLOOR;
                    tiles[startX + 1][currPos - 1] = Tileset.WALL;
                    //tiles[startX + 2][currPos - 1] = Tileset.WALL;
                    tiles[startX - 1][currPos + 1] = Tileset.WALL;
                    //tiles[startX - 2][currPos + 1] = Tileset.WALL;
                }
                if (roomFinder(startX, currPos) != numOfRooms) {
                    roomRelations.union(i[0], roomFinder(startX, currPos));
                }
                System.out.println("hiiY");
            }
        }
//        roomRelations.union(numOfRooms, 0);
    }

    //when given a point, finds the ID of the room that that point lies in, given the points X & Y
    public int roomFinder(int coordinateX, int coordinateY) {
        int roomID = numOfRooms;
        for (Integer[] i : roomInfo) {
            if (i[1] <= coordinateX && coordinateX <= (i[1] + i[3] - 1) && i[2] <= coordinateY && coordinateY <= (i[2] + i[4] - 1)) {
                roomID = i[0];
            }
        }
        return roomID;
    }

    // Creates borders for objects
    public void edgeCreater(int x, int y, int objectWidth, int objectHeight, boolean[] isHallway) {
        for (int i = 0; i < objectWidth; i++) {
            for (int j = 0; j < objectHeight; j++) {
                if (!isHallway[0] || tiles[x][y + j] != Tileset.FLOOR) {
                    tiles[x][y + j] = Tileset.WALL; //Left border
                }
                if (!isHallway[0] || tiles[x + objectWidth - 1][y + j] != Tileset.FLOOR) {
                    tiles[x + objectWidth - 1][y + j] = Tileset.WALL; // Right Border
                }
                if (!isHallway[0] || tiles[x + i][y] != Tileset.FLOOR) {
                    tiles[x + i][y] = Tileset.WALL; //Bottom border
                }
                if (!isHallway[0] || tiles[x + i][y + objectHeight - 1] != Tileset.FLOOR) {
                    tiles[x + i][y + objectHeight - 1] = Tileset.WALL; //Top border
                }
            }
        }
        if (isHallway[0]) {
            edgeRemover(x, y, objectWidth, objectHeight, isHallway);
        }
    }

    // Removes entrance edges/ ends of hallways
    public void edgeRemover(int x, int y, int objectWidth, int objectHeight, boolean[] isHallway) {
        for (int i = 0; i < objectWidth; i++) {
            for (int j = 0; j < objectHeight; j++) {
                tiles[x][y + j] = Tileset.WALL; //Left border
                tiles[x + objectWidth - 1][y + j] = Tileset.WALL; // Right Border
                tiles[x + i][y] = Tileset.WALL; //Bottom border
                tiles[x + i][y + objectHeight - 1] = Tileset.WALL; //Top border
            }
            if (isHallway[0]) {
                if (!isHallway[1]) {
                    tiles[x + objectWidth - 2][y + objectHeight - 1] = Tileset.FLOOR; // removes top wall
                    tiles[x + objectWidth - 2][y] = Tileset.FLOOR; // removes bottom wall
                }
                if (isHallway[1]) {
                    tiles[x + objectWidth - 1][y + 1] = Tileset.FLOOR; // removes right most wall
                    tiles[x][y + 1] = Tileset.FLOOR; // removes leftmost wall
                }
                 // If it happens to be a 3x3
                if (objectHeight == 3 && objectWidth == 3) {
                    if (tiles[x + objectWidth][y + 1] != Tileset.NOTHING || tiles[x - 1][y + 1] != Tileset.NOTHING) {
                        tiles[x + objectWidth - 1][y + 1] = Tileset.FLOOR; // removes right most wall
                        tiles[x][y + 1] = Tileset.FLOOR; // removes leftmost wall
                    }
                    else if (tiles[x + objectWidth - 2 ][y + objectHeight] != Tileset.NOTHING || tiles[x + objectWidth - 2][y-1] != Tileset.NOTHING) {

                        tiles[x + objectWidth - 2][y + objectHeight - 1] = Tileset.FLOOR; // removes top wall                 tiles[x + objectWidth - 2][y] = Tileset.FLOOR; // removes bottom wall

                    }
               }
            }
        }
    }

    // creates actual area of objects
    public boolean spaceMaker(int x, int y, int objectWidth, int objectHeight, TETile tileType, boolean[] isHallway) {
        if (overlapChecker(x, y, objectWidth, objectHeight, isHallway)) {
            for (int i = 0; i < objectWidth; i++) {
                for (int j = 0; j < objectHeight; j++) {
                    if (x + i < this.width && y + j < this.height) {
                        tiles[x + i][y + j] = tileType;
                    }
                }
            }
            edgeCreater(x, y, objectWidth, objectHeight, isHallway);
            return true;
        }
        return false;
    }

    public void randomTreatPlacer(Random randomSeed) {
        numOfTreats = randomSeed.nextInt(10, 15);
        int randomSpacer = randomSeed.nextInt(10, 15);
        int tracker = 0;
        boolean hasMet = false;
        System.out.print(numOfTreats);
        while (!hasMet) {
            randomSpacer = randomSeed.nextInt(10, 15);
            for (int i = 0; i < this.width && !hasMet; i += randomSpacer) {
                for (int j = 0; j < this.height && !hasMet; j += randomSpacer) {
                    if (tiles[i][j] == Tileset.FLOOR) {
                        tiles[i][j] = Tileset.TREE;
                        tracker += 1;
                        if (tracker == numOfTreats) {
                            hasMet = true; // ends game (all trees ate)
                        }
                    }
                }
            }
        }
    }
    // NEW

    public void renderHUD(String avatarName) {
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, height + 3, "HUD:");
        StdDraw.setPenColor(Color.RED);
        StdDraw.textLeft(6, height + 3, " PLAYER: " + avatarName);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text((double) width / 2 + 15, (double) height + 3, "TILE: " + mousePad());
        StdDraw.setPenColor(Color.CYAN);
        StdDraw.textLeft((double) width - 50,  height + 3, "POINTS: " + pointTracker + "/" + numOfTreats);

    }
    public void renderHealthBarHUD () {
        String facePrint = "";
        Color healthColor = Color.WHITE;
            if (healthHearts == 3) {
                facePrint = ":}";
                healthColor = Color.GREEN;
            }
            else if (healthHearts == 2) {
                facePrint = ":|";
                healthColor = Color.YELLOW;
                }
            else if (healthHearts == 1) {
                facePrint = ":{";
                healthColor = Color.RED;
            }
            else if (healthHearts < 1) {
                endGameDisplay();
            }
        StdDraw.setPenColor(healthColor);
        StdDraw.textRight(width - 2, height + 3, "HEALTH: " + facePrint);
    }
    public void winGame () {
        if (pointTracker >= numOfTreats) {
            StdDraw.clear(Color.black);
            Font font = new Font("Arial", Font.PLAIN, 100);
            StdDraw.setFont(font);
            StdDraw.setPenColor(0, 255, 0);
            StdDraw.text((double) width / 2, (double) height / 2, "| YOU WON"
                    + " |");
            isGameOver = true;
            StdDraw.show();
            StdDraw.pause(1500);
            System.exit(0);
        }
    }

    public String mousePad() {
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        if (mouseX < this.width && mouseY < this.height) {
            TETile checkingTile = tiles[(int) mouseX][(int) mouseY];
            return checkingTile.description();
        }
        return "Out of Bounds";
    }
    public void endGameDisplay () {
        if (healthHearts < 1) {
            StdDraw.clear(Color.black);
            Font font = new Font("Arial", Font.BOLD, 100);
            StdDraw.setFont(font);
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.text((double) width / 2, (double) height / 2, "| GAMEOVER"
                    + " |");
            isGameOver = true;
            StdDraw.show();
            StdDraw.pause(1500);
            System.exit(0);
        }
    }

    public void regainHealthHUD() {
        if (pointTracker != 0 && pointTracker % 5 == 0 && healthHearts < 3 && !regainedHealth) {
            healthHearts += 1;
            regainedHealth = true;
        } else if (pointTracker != 0 && pointTracker % 5 != 0) {
            regainedHealth = false;
        }
    }

    public boolean tryMove(int dx, int dy) { // Checks if position we are attempting to move to is valid
        if (avatarX + dx < this.width && avatarY + dy < this.height) {
            if (tiles[avatarX + dx][avatarY + dy] == Tileset.FLOOR) {
                return true;
            } else if (tiles[avatarX + dx][avatarY + dy] == Tileset.TREE) {
                pointTracker += 1;
                winGame();
                regainHealthHUD(); // checks if valid time to regain health
                return true;
            } else if (tiles[avatarX + dx][avatarY + dy] == Tileset.WALL) {
                reduceHealth();
                return false;
            }
        }
        return false;
    }

    public void reduceHealth() {
        healthHearts -= 1;
    }


    public void moveAvatar(int dx, int dy) { // moves avatar
        if (tryMove(dx, dy)) {
            tiles[avatarX][avatarY] = Tileset.FLOOR;
            avatarY += dy;
            avatarX += dx;
            tiles[avatarX][avatarY] = Tileset.AVATAR;
        }
    }
    // NEW
    public void renderAvatar() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (tiles[i][j] == Tileset.FLOOR && !hasAvatar) {
                    hasAvatar = true;
                    avatarX = i;
                    avatarY = j;
                    tiles[i][j] = Tileset.AVATAR;
                }
            }
        }
    }

    // NEW DONE
    //checks if an objects desired location is a valid place (i.e. no overlap with other objects and is within bounds.
    public boolean overlapChecker(int x, int y, int objectWidth, int objectHeight, boolean[] isHallway) {
        for (int i = 0; i < objectWidth + 1; i++) {
            for (int j = 0; j < objectHeight + 1; j++) {
                if ((x + i) > this.width || (y + j) > this.height) {
                    return false;
                }
                if ((x + i) < this.width && (y + j) < this.height) {
                    if (tiles[x + i][y + j] != Tileset.NOTHING && tiles[x + i][y + j] != Tileset.SAND && !isHallway[0]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the tiles associated with this KnightWorld.
     */
    public TETile[][] getTiles() {
        return tiles;
    }

    // NEW
    public static World main(String[] args) {
        StringBuilder stringSeed = new StringBuilder();
        long longseed = 5643591630821615871L;
        int width = 80;
        int height = 43;
        // Change these parameters as necessary
        for (String arg : args) {
            stringSeed.append(arg);
        }
        System.out.print(stringSeed);
        longseed = Long.parseLong(stringSeed.toString()); // converts seed passed in by user into usable seed
        World WorldTrail = new World(width, height, longseed); // generates world with user given seed
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        ter.renderFrame(WorldTrail.getTiles());
        //NEW
        return WorldTrail;
    }
}