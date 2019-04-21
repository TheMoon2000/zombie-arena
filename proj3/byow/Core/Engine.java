package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.Tileset;
import byow.utils.NearTree;
import byow.utils.Point;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.introcs.StdDraw;

import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInputSource();
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setFont(StdDraw.getFont());
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.text(0.5,0.5,"wow");
        StdDraw.show();
        interact(inputSource);

        System.exit(0);
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

        return interact(source);
    }



    // Helper methods


    /**
     * Reads an input source and do something about it
     * @param source The input source
     * */

    private TETile[][] interact(InputSource source) {

        boolean startReadingSeed = false;
        boolean colon = false;
        boolean loadedWorld = false;

        int seed = 0;
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        System.out.println("Capturing input source:");

        while (source.possibleNextInput()) {
            char next = source.getNextKey();
            System.out.print(next);
            if (next != 'Q') {
                colon = false;
            }
            switch (next) {
                case 'Q': // if :Q then save and quit
                    if (colon) {
                        System.out.println("\nSaving...");
                        return finalWorldFrame;
                    }
                    continue;
                case 'N': // new world
                    if (loadedWorld) {
                        System.out.print("\nInvalid argument");
                    } else {
                        startReadingSeed = true;
                    }
                    continue;
                case 'S': // start game
                    if (!loadedWorld && startReadingSeed) {
                        startReadingSeed = false;
                        generateWorld(finalWorldFrame, seed);
                        fillTheRest(finalWorldFrame, new Random(seed));
                        ter.renderFrame(finalWorldFrame);
                        loadedWorld = true;
                    } else if (loadedWorld) {
                        System.out.print("\n[Move back]");
                    }
                    continue;
                case 'W':
                    if (loadedWorld) {
                        System.out.print("\n[Move forward]");
                    }
                    continue;
                case 'A':
                    if (loadedWorld) {
                        System.out.print("\n[Move left]");
                    }
                    continue;
                case 'D':
                    if (loadedWorld) {
                        System.out.print("\n[Move right]");
                    }
                    continue;
                case ' ':
                    if (loadedWorld) {
                        System.out.print("\n[space]");
                    }
                    continue;
                case 'L':
                    if (!loadedWorld) {
                        System.out.print("\nLoad saved world (unimplemented)");
                    }
                case ':':
                    colon = true; continue;
                default:
                    if (startReadingSeed) {
                        seed = seed * 10 + Integer.parseInt(String.valueOf(next));
                    }
            }
        }


        return finalWorldFrame;
    }

    /**
     * Generate a random world based on a seed
     * @param tiles the tiles matrix
     * @param seed the seed for the random generator instance
     */

    private void generateWorld(TETile[][] tiles, int seed) {
        Random random = new Random(seed);
        final int numberOfRooms = random.nextInt(25) + 12;

        Map<Point, Point> originToSize = new HashMap<>();

        // Add some random rooms to the world

        while (originToSize.size() < numberOfRooms) {
            int dx = RandomUtils.uniform(random, 2, 8);
            int dy = RandomUtils.uniform(random, 2, 8);
            int x = random.nextInt(WIDTH - dx - 2) + 1;
            int y = random.nextInt(HEIGHT - dy - 2) + 1;

            if (!hasNearby(tiles, x, y, Tileset.FLOOR, 1)
                    && !hasNearby(tiles, x + dx, y + dy, Tileset.FLOOR, 1)
                    && !hasNearby(tiles, x, y + dy, Tileset.FLOOR, 1)
                    && !hasNearby(tiles, x + dx, y, Tileset.FLOOR, 1)) {
                fill(x, y, dx, dy, tiles, Tileset.FLOOR);
                Point startPoint = new Point(x, y);
                originToSize.put(startPoint, new Point(dx, dy));
            }
        }

        NearTree points = new NearTree(originToSize.keySet());


        // Connect the rooms together

        for (Point p: originToSize.keySet()) {
            // q will always be a point that p isn't connected with
            Point q = points.nearest(p.getX(), p.getY());
            if (p.equals(q)) {
                throw new RuntimeException("p = q!");
            }
            Point pSize = originToSize.get(p);
            Point qSize = originToSize.get(q);

            Point s = randomPoint(p.getX(), p.getY(), pSize.getX(), pSize.getY(), random);
            Point t = randomPoint(q.getX(), q.getY(), qSize.getX(), qSize.getY(), random);

            int minX = Math.min(s.getX(), t.getX());
            int minY = Math.min(s.getY(), t.getY());
            int maxX = Math.max(s.getX(), t.getX());
            int maxY = Math.max(s.getY(), t.getY());

            if (random.nextBoolean()) {
                // First go vertically then horizontally
                fill(s.getX(), minY, 0, maxY - minY, tiles, Tileset.FLOOR);
                fill(minX, t.getY(), maxX - minX, 0, tiles, Tileset.FLOOR);
            } else {
                // First go horizontally then vertically
                fill(minX, s.getY(), maxX - minX, 0, tiles, Tileset.FLOOR);
                fill(t.getX(), minY, 0, maxY - minY, tiles, Tileset.FLOOR);

            }


        }
    }


    /**
     * Fill up a rectangular region in the given tiles matrix
     * @param x x-coordinate of the origin
     * @param y y-coordinate of the origin
     * @param dx the
     * @param dy
     * @param tiles
     * @param p
     */

    private static void fill(int x, int y, int dx, int dy, TETile[][] tiles, TETile p) {
        for (int i = x; i <= x + dx; i++) {
            for (int j = y; j <= y + dy; j++) {
                tiles[i][j] = p;
            }
        }
    }

    /**
     * Generates a random point from the given rectangular region
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
     * Fills up the empty space with appropriate tiles
     * @param tiles the tiles to fill up
     * */

    private static void fillTheRest(TETile[][] tiles, Random r) {
        for (int w = 0; w < WIDTH; w++) {
            for (int h = 0; h < HEIGHT; h++) {
                if (hasNearby(tiles, w, h, Tileset.FLOOR, 8) && tiles[w][h] == null) {
                    tiles[w][h] = Tileset.FLOOR;
                } else if (tiles[w][h] == null && hasNearby(tiles, w, h, Tileset.FLOOR, 1)) {
                    tiles[w][h] = TETile.colorVariant(Tileset.WALL, 20, 20, 30, r);
                } else if (tiles[w][h] == null) {
                    tiles[w][h] = Tileset.NOTHING;
                }
            }
        }
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
