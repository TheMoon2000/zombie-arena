package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.utils.NearTree;
import byow.utils.Point;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        ter.initialize(WIDTH, HEIGHT);
        InputSource source = new StringInputDevice(input);

        int seed = getSeed(source);

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }



    // Helper methods


    /** Gets the seed number from an input source
     * @param source The input source
     * */

    private int getSeed(InputSource source) {
        if (!source.possibleNextInput() || source.getNextKey() != 'N') {
            System.out.println("Invalid input, exiting...");
            return 0;
        }

        int seed = 0;

        while (source.possibleNextInput()) {
            char next = source.getNextKey();
            if (next != 'S') {
                seed = seed * 10 + Integer.parseInt(String.valueOf(next));
            }
        }

        return seed;
    }


    /** Generates a random point from the given rectangular region
     * @param x origin's x
     * @param y origin's y
     * @param dx width of rectangle
     * @param dy height of rectangle
     * @param r the random generator instance
     * */

    private static Point randomPoint(int x, int y, int dx, int dy, Random r) {
        return new Point(RandomUtils.uniform(r, x, x + dx + 1),
                      RandomUtils.uniform(r, y, y + dy + 1));
    }


    /**
     * Determine if at least c of the 8 neighbors of the given point is a tile of type p
     * @param x the x-coordinate of the given point
     * @param y the y-coordinate of the given point
     * @param p the tile pattern to look for
     * @param c the minimum number of surrounding tiles that are of type p
     * @param tiles the tile matrix
     * */

    private static boolean hasNearby(TETile[][] tiles, int x, int y, TETile p, int c) {

        int count = 0;

        // Check right
        if (x + 1 < WIDTH && tiles[x + 1][y] != null && tiles[x + 1][y].equals(p)) {
            count += 1;
        }

        // Check top right
        if (x + 1 < WIDTH && y + 1 < HEIGHT && tiles[x + 1][y + 1] != null
                && tiles[x + 1][y + 1].equals(p)) {
            count += 1;
        }

        // Check top
        if (y + 1 < HEIGHT && tiles[x][y + 1] != null && tiles[x][y + 1].equals(p)) {
            count += 1;
        }

        // Check top left
        if (x > 0 && y + 1 < HEIGHT && tiles[x - 1][y + 1] != null
                && tiles[x - 1][y + 1].equals(p)) {
            count += 1;
        }

        // Check left
        if (x > 0 && tiles[x - 1][y] != null && tiles[x - 1][y].equals(p)) {
            count += 1;
        }

        // Check bottom left
        if (x > 0 && y > 0 && tiles[x - 1][y - 1] != null
                && tiles[x - 1][y - 1].equals(p)) {
            count += 1;
        }

        // Check bottom
        if (y > 0 && tiles[x][y - 1] != null && tiles[x][y - 1].equals(p)) {
            count += 1;
        }

        // Check bottom right
        if (x + 1 < WIDTH && y > 0 && tiles[x + 1][y - 1] != null
                && tiles[x + 1][y - 1].equals(p)) {
            count += 1;
        }
        return count >= c;
    }
}
