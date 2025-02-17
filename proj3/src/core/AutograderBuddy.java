package core;

//import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Objects;

public class AutograderBuddy {

    /**
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public static TETile[][] getWorldFromInput(String input) {
        char[] charArray = input.toCharArray();
        StringBuilder sbSeed = new StringBuilder();
        for (char character : charArray) {
            if (Character.isDigit(character)) {
                sbSeed.append(character);
            }
        }
        String strSeed = sbSeed.toString();
        if (Objects.equals(strSeed, "")) {
            strSeed = "5643591630121615871";
        }
        long value = Long.parseLong(strSeed);
        World world = new World(90, 50, value);
        for (char character : charArray) {
            if (character == 's' || character == 'S') {
                world.moveAvatar(0, -1);
            } else if (character == 'w' || character == 'W') {
                world.moveAvatar(0, 1);
            } else if (character == 'a' || character == 'A') {
                world.moveAvatar(-1, 0);
            } else if (character == 'd' || character == 'D') {
                world.moveAvatar(1, 0);
            }
        }
        return world.getTiles();
    }

    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character() ||
                t.character() == Tileset.AVATAR.character() ||
                t.character() == Tileset.FLOWER.character();
    }
    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */

    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character() ||
                t.character() == Tileset.LOCKED_DOOR.character() ||
                t.character() == Tileset.UNLOCKED_DOOR.character();
    }
    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */

}
