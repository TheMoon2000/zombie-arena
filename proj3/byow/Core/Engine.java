package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.gameplay.Player;
import byow.gameplay.Shop;
import byow.hw4.AStarSolver;
import byow.hw4.WeightedUndirectedGraph;
import byow.utils.Direction;
import byow.utils.NearTree;
import byow.utils.Point;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    TERenderer ter = new TERenderer();

    /**
     * Fill up a rectangular region in the given tiles matrix
     *
     * @param x     x-coordinate of the origin
     * @param y     y-coordinate of the origin
     * @param dx    the width
     * @param dy    the height
     * @param tiles the tiles matrix
     * @param p     the tile pattern to fill
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
     *
     * @param x  origin's x
     * @param y  origin's y
     * @param dx width of rectangle
     * @param dy height of rectangle
     * @param r  the random generator instance
     */

    private static Point randomPoint(int x, int y, int dx, int dy, Random r) {
        return new Point(RandomUtils.uniform(r, x, x + dx + 1),
                RandomUtils.uniform(r, y, y + dy + 1));
    }


    // Helper methods

    /**
     * Fills up the empty space with appropriate tiles
     *
     * @param tiles the tiles to fill up
     */

    private static void fillTheRest(TETile[][] tiles, Random r) {
        for (int w = 0; w < WIDTH; w++) {
            for (int h = 0; h < HEIGHT; h++) {
                if (hasNearby(tiles, new Point(w, h), Tileset.FLOOR, 8) && tiles[w][h] == null) {
                    tiles[w][h] = Tileset.FLOOR;
                } else if (tiles[w][h] == null
                        && hasNearby(tiles, new Point(w, h), Tileset.FLOOR, 1)) {
                    tiles[w][h] = TETile.colorVariant(Tileset.WALL, 20, 20, 30, r);
                } else if (tiles[w][h] == null) {
                    tiles[w][h] = Tileset.NOTHING;
                }
            }
        }
    }

    /**
     * Determine if at least c of the 8 neighbors of the given point is a tile of type p
     *
     * @param location A Point object containing the location to search
     * @param p        the tile pattern to look for
     * @param c        the minimum number of surrounding tiles that are of type p
     * @param tiles    the tile matrix
     */

    private static boolean hasNearby(TETile[][] tiles, Point location, TETile p, int c) {

        int count = 0;
        int x = location.getX(), y = location.getY();

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

    /**
     * Pause for a moment
     *
     * @param n (in milliseconds) the time to wait
     */
    private static void sleep(int n) {
        try {
            TimeUnit.MILLISECONDS.sleep(n);
        } catch (InterruptedException e) {
            System.out.print("\ndelay failed");
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInputSource();

        interact(inputSource, true);

        System.exit(0);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        InputSource source = new StringInputDevice(input);

        return interact(source, false);
    }

    /**
     * Reads an input source and do something about it
     *
     * @param source The input source
     */

    private TETile[][] interact(InputSource source, boolean keyBoardInput) {

        //create menu if keyboardInput
        menu(keyBoardInput);

        boolean startReadingSeed = false;
        boolean colon = false;

        long seed = 0; Random r;

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        Player player = null;

        //create new graph
        WeightedUndirectedGraph aStarGraph = new WeightedUndirectedGraph();

        while (source.possibleNextInput()) {

            //display mouse cursor's tile information if game has started
            //update display bar whenever user doesn't input anything
            while (keyBoardInput && !StdDraw.hasNextKeyTyped() && player != null) {
                sleep(10);
                renewDisplayBar(player);
            }

            char next = Character.toUpperCase(source.getNextKey());

            if (next != 'Q') {
                colon = false;
            }

            switch (next) {
                case 'Q': // if :Q then save and quit
                    if (colon) {
                        return tiles;
                    }
                    break;
                case 'N': // new world
                    if (player != null) {
                        continue;
                    }
                    startReadingSeed = true;
                    drawSeedPrompt(keyBoardInput);
                    break;
                case 'S': // start game
                    if (player == null && startReadingSeed) {
                        r = new Random(seed);
                        generateWorld(tiles, seed);
                        fillTheRest(tiles, new Random(seed));
                        startReadingSeed = false;

                        // Randomly place the player on a floor tile
                        player = new Player(tiles, randomPlacement(r, tiles));
                        //randomly generate two shops
                        generateShop(player, seed);
                        //collect all the floor tiles into a connected graph for A* algorithm
                        aStarCollect(aStarGraph, tiles, player.getLocation());

                        if (keyBoardInput) {
                            ter.initialize(WIDTH, HEIGHT + 3);
                            ter.renderFrame(tiles);
                            locate(player);
                        }
                        break;
                    } else if (player == null) {
                        break;
                    }
                    // Otherwise, 'S' refers to a direction, so fall through...
                case 'W':
                case 'A':
                case 'D':
                    if (player != null) {
                        player.move(Direction.parse(next));
                        //if there is a shop nearby, display the shop's message first
                        if (hasNearby(player.getTiles(), player.getLocation(),
                                Tileset.WEAPON_BOX, 1)) {
                            player.setMessage(Shop.displayMessage());
                        } else {
                            player.setMessage(player.waveMessage());
                        }
                        if (keyBoardInput) {
                            ter.renderFrame(tiles);
                            renewDisplayBar(player);
                        }
                    }
                    break;
                case ' ': break;
                case 'L': break;
                case 'B': //buy a weapon from the store
                    if (player != null) {
                        if (hasNearby(player.getTiles(), player.getLocation(),
                                Tileset.WEAPON_BOX, 1)) {
                            player.setMessage(Shop.openMenu(player, ter, source, keyBoardInput));
                            ter.renderFrame(tiles);
                        } else {
                            player.setMessage("Buy new weapons at the shop.");
                        }
                    }
                    break;
                case ':':
                    colon = true; break;
                case 'T': //Press T every time to see a randomly generated shortest path
                    Random random = new Random();
                    int x1 = random.nextInt(WIDTH);
                    int y1 = random.nextInt(HEIGHT);
                    while (tiles[x1][y1] != Tileset.FLOOR) {
                        x1 = random.nextInt(WIDTH);
                        y1 = random.nextInt(HEIGHT);
                    }

                    int x2 = random.nextInt(WIDTH);
                    int y2 = random.nextInt(HEIGHT);
                    while (tiles[x2][y2] != Tileset.FLOOR) {
                        x2 = random.nextInt(WIDTH);
                        y2 = random.nextInt(HEIGHT);
                    }
                    TETile[][] tilesCopy = new TETile[WIDTH][HEIGHT];
                    for (int i = 0; i < WIDTH; i++) {
                        System.arraycopy(tiles[i], 0, tilesCopy[i], 0, HEIGHT);
                    }
                    AStarSolver<Point> solver = new AStarSolver<>(aStarGraph, new Point(x1, y1),
                            new Point(x2, y2), 1000);
                    List<Point> solution = solver.solution();
                    for (Point p : solution) {
                        tilesCopy[p.getX()][p.getY()] = Tileset.FLOWER;
                    }
                    tilesCopy[x1][y1] = Tileset.LOCKED_DOOR;
                    tilesCopy[x2][y2] = Tileset.LOCKED_DOOR;
                    if (keyBoardInput) {
                        ter.renderFrame(tilesCopy);
                        renewDisplayBar(player);
                    }
                    continue;
                // The below four inputs must be placed before default,
                // such that the user can use the number keys
                // to both switch weapons and enter the seed
                case '1':
                case '2':
                    if (player != null) {
                        player.switchWeapon(Integer.parseInt(String.valueOf(next)));
                        break;
                    }
                default:
                    if (startReadingSeed) {
                        seed = seed * 10 + Integer.parseInt(String.valueOf(next));
                        if (!keyBoardInput) {
                            continue;
                        }
                        displaySeed(seed);
                    }
            }
        }

        return tiles;
    }

    /**
     * Helper methods that takes all the floor tiles and add them into an A* graph
     */
    private void aStarCollect(WeightedUndirectedGraph aStarGraph, TETile[][] tiles, Point source) {
        HashMap<Point, Boolean> map = new HashMap<>();
        depthFirstSearch(aStarGraph, tiles, map, source);
    }

    private void depthFirstSearch(WeightedUndirectedGraph aStarGraph, TETile[][] tiles,
                                  HashMap<Point, Boolean> map, Point current) {
        map.put(current, true);
        List<Point> adjacent = new ArrayList<>();
        //check the four adjacent tiles to see if they are floors
        if (tiles[current.getX() + 1][current.getY()].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(current.getX() + 1, current.getY()));
        }
        if (tiles[current.getX() - 1][current.getY()].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(current.getX() - 1, current.getY()));
        }
        if (tiles[current.getX()][current.getY() + 1].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(current.getX(), current.getY() + 1));
        }
        if (tiles[current.getX()][current.getY() - 1].equals(Tileset.FLOOR)) {
            adjacent.add(new Point(current.getX(), current.getY() - 1));
        }
        for (Point p : adjacent) {
            aStarGraph.addEdge(current, p);
            if (!map.containsKey(p)) {

                depthFirstSearch(aStarGraph, tiles, map, p);
            }
        }
    }

    /**
     * Helper method that generates a shop at a random position
     */
    private void generateShop(Player player, long seed) {
        TETile[][] tiles = player.getTiles();
        ArrayList<Point> l1 = new ArrayList<>();
        ArrayList<Point> l2 = new ArrayList<>();
        Random r = new Random(seed);
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                if (tiles[x][y] == Tileset.FLOOR
                        && hasNearby(tiles, new Point(x, y), Tileset.FLOOR, 8)) {
                    if (r.nextDouble() < 0.5) { //construct a vertical shop
                        if (hasNearby(tiles, new Point(x, y - 1), Tileset.FLOOR, 8)) {
                            l1.add(new Point(x, y));
                            l2.add(new Point(x, y - 1));
                        }
                    } else { //construct a horizontal shop
                        if (hasNearby(tiles, new Point(x + 1, y), Tileset.FLOOR, 8)) {
                            l1.add(new Point(x, y));
                            l2.add(new Point(x + 1, y));
                        }
                    }
                }
            }
        }
        int index1 = r.nextInt(l1.size());
        int index2 = r.nextInt(l1.size());
        Point shop1Point1 = l1.get(index1);
        Point shop1Point2 = l2.get(index1);
        Point shop2Point1 = l1.get(index2);
        Point shop2Point2 = l2.get(index2);


        while (Math.abs(shop1Point1.getX() - shop2Point1.getX()) <= 8
                || Math.abs(shop1Point1.getY() - shop2Point1.getY()) <= 8) {
            index2 = r.nextInt(l1.size());
            shop2Point1 = l1.get(index2);
            shop2Point2 = l2.get(index2);
        }

        tiles[shop1Point1.getX()][shop1Point1.getY()] = Tileset.WEAPON_BOX;
        tiles[shop1Point2.getX()][shop1Point2.getY()] = Tileset.WEAPON_BOX;
        tiles[shop2Point1.getX()][shop2Point1.getY()] = Tileset.WEAPON_BOX;
        tiles[shop2Point2.getX()][shop2Point2.getY()] = Tileset.WEAPON_BOX;
    }

    /**
     * Helper method that generates the main menu
     */
    private void menu(boolean keyBoardInput) {
        if (!keyBoardInput) {
            return;
        }

        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.clear(StdDraw.BLACK);

        Font font1 = new Font("Arial", Font.BOLD, 60);
        Font font2 = new Font("Helvetica", Font.ITALIC, 40);

        StdDraw.setFont(font1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.7, "ZOMBIE ARENA");

        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.4, "NEW GAME (N)");

        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.3, "LOAD GAME (L)");

        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.2, "QUIT (Q)");

        StdDraw.show();
    }

    /**
     * Helper method that creates an display bar on top
     * Should be called every time a key is pressed or a state is updated
     * Fields include tile information, health, points, current weapon, weapon ammo, wave number.
     */

    private void renewDisplayBar(Player player) {

        if (player == null) {
            return;
        }

        //get current mouse position to update tile information
        String tile = tileInfo(player);

        //cover previous display
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, HEIGHT + 2, WIDTH, 1);

        //tile information
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(5, HEIGHT + 2, tile);

        //health information
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(13, HEIGHT + 2, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(16, HEIGHT + 2, "Health");
        StdDraw.text(13, HEIGHT + 2, Integer.toString(player.getHealth()));

        //point information
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledRectangle(24, HEIGHT + 2, 2, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(28, HEIGHT + 2, "Points");
        StdDraw.text(24, HEIGHT + 2, Integer.toString(player.getPoints()));

        //weapon information
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.filledRectangle(36, HEIGHT + 2, 3, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(41, HEIGHT + 2, "Weapon");
        StdDraw.text(36, HEIGHT + 2, player.currentWeapon().getName());

        //Ammo information
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        StdDraw.filledRectangle(49, HEIGHT + 2, 2, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(52.5, HEIGHT + 2, "Ammo");
        StdDraw.text(49, HEIGHT + 2, player.ammoDescription());

        //Wave information
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(57, HEIGHT + 2, "Wave:");
        StdDraw.text(59, HEIGHT + 2, Integer.toString(player.currentWave()));

        //Message
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(70, HEIGHT + 2, player.getMessage());

        StdDraw.show();
    }

    private static void drawSeedPrompt(boolean keyBoardInput) {
        if (keyBoardInput) {
            Font font3 = new Font("Times New Roman", Font.BOLD, 20);
            StdDraw.setFont(font3);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(0.8, 0.1, "Please enter random seed. Then press 'S'.");
            StdDraw.show();
        }
    }

    private static Point randomPlacement(Random r, TETile[][] tiles) {
        int randomX = r.nextInt(WIDTH - 1);
        int randomY = r.nextInt(HEIGHT - 1);
        while (!tiles[randomX][randomY].equals(Tileset.FLOOR)) {
            randomX = r.nextInt(WIDTH - 1);
            randomY = r.nextInt(HEIGHT - 1);
        }
        return new Point(randomX, randomY);
    }

    /**
     * Draw a ring around the player's spawn point
     * @param player the player
     */

    private static void locate(Player player) {
        StdDraw.setPenColor(new Color(236, 96, 91));
        StdDraw.setPenRadius(0.01);
        StdDraw.circle(player.getLocation().getX() + 0.5, player.getLocation().getY() + 0.5, 1);
    }

    /**
     * Display the currently entered seed to the user in the main menu screen
     * @param seed the random generator's seed
     */

    private static void displaySeed(long seed) {
        Font font3 = new Font("Times New Roman", Font.BOLD, 20);
        StdDraw.setFont(font3);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0.8, 0.05, 1, 0.03);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.8, 0.05, " " + seed + " ");
        StdDraw.show();
    }

    /**
     * Provides information about the tile at the given mouse position
     */

    private String tileInfo(Player player) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        if (y >= HEIGHT) {
            return "Void";
        }
        if ((new Point(x, y)).equals(player.getLocation())) {
            return "Player";
        }
        if (player.getTiles()[x][y].equals(Tileset.FLOOR)) {
            return "Floor";
        }
        if (player.getTiles()[x][y].equals(Tileset.NOTHING)) {
            return "Void";
        }
        if (player.getTiles()[x][y].equals(Tileset.WEAPON_BOX)) {
            return "Shop";
        }

        return "Wall";
    }

    /**
     * Generate a random world based on a seed
     *
     * @param tiles the tiles matrix
     * @param seed  the seed for the random generator instance
     */

    private void generateWorld(TETile[][] tiles, long seed) {
        Random random = new Random(seed);
        final int numberOfRooms = random.nextInt(25) + 12;

        Map<Point, Point> originToSize = new HashMap<>();

        // Add some random rooms to the world

        while (originToSize.size() < numberOfRooms) {
            int dx = RandomUtils.uniform(random, 2, 8);
            int dy = RandomUtils.uniform(random, 2, 8);
            int x = random.nextInt(WIDTH - dx - 2) + 1;
            int y = random.nextInt(HEIGHT - dy - 2) + 1;

            if (!hasNearby(tiles, new Point(x, y), Tileset.FLOOR, 1)
                    && !hasNearby(tiles, new Point(x + dx, y + dy), Tileset.FLOOR, 1)
                    && !hasNearby(tiles, new Point(x, y + dy), Tileset.FLOOR, 1)
                    && !hasNearby(tiles, new Point(x + dx, y), Tileset.FLOOR, 1)) {
                fill(x, y, dx, dy, tiles, Tileset.FLOOR);
                Point startPoint = new Point(x, y);
                originToSize.put(startPoint, new Point(dx, dy));
            }
        }

        NearTree points = new NearTree(originToSize.keySet());


        // Connect the rooms together

        for (Point p : originToSize.keySet()) {
            // q will always be a point that p isn't connected with
            Point q = points.nearest(p.getX(), p.getY());
            if (p.equals(q)) {
                continue;
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
}
