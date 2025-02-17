import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n5643591630121615871swwaawd");
        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(20000);
    }

    @Test
    public void basicInteractivityTest() {
        String input = "n5643591630121615871wwwddd";
        TETile[][] initialTiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(initialTiles.length, initialTiles[0].length);
        ter.renderFrame(initialTiles);
        StdDraw.pause(2000);
    }
        // TODO: write a test that uses an input like "n123swasdwasd"

    @Test
    public void emptyStringTest() {
        String input = "n20000s";
        TETile[][] initialTiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(initialTiles.length, initialTiles[0].length);
        ter.renderFrame(initialTiles);
        StdDraw.pause(20000);
    }

    @Test
    public void testCompareSameWorld() {
        String input = "n1392967723524655428sddsaawwsaddw";
        TETile[][] initialTiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(initialTiles.length, initialTiles[0].length);
        ter.renderFrame(initialTiles);
        StdDraw.pause(20000);
    }

    @Test
    public void testCompareSameWorld2() {
        String input = "n139296772352465542sddsaawws:qladdw";
        TETile[][] initialTiles = AutograderBuddy.getWorldFromInput(input);
        TERenderer ter = new TERenderer();
        ter.initialize(initialTiles.length, initialTiles[0].length);
        ter.renderFrame(initialTiles);
        StdDraw.pause(20000);
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }
}
