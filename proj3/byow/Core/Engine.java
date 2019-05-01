package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.gameplay.Shop;
import byow.gameplay.Player;
import byow.gameplay.EndMenu;
import byow.gameplay.Wave;
import byow.gameplay.Bullet;
import byow.hw4.WeightedUndirectedGraph;
import byow.utils.Direction;
import byow.utils.NearTree;
import byow.utils.Point;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.Random;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Engine implements Serializable {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private TERenderer ter = new TERenderer();
    private Random r;
    private boolean kbInput = false;
    private long seed = 0;
    private TETile[][] tiles;
    private Queue<Point> toBeCleared;
    private Player player = null;
    private Wave wave;
    private WeightedUndirectedGraph arena;
    private boolean backToMenu = false;
    private StringBuilder history = new StringBuilder();


    /**
     * Generates a random point from the given rectangular region
     *
     * @param x  origin's x
     * @param y  origin's y
     * @param dx width of rectangle
     * @param dy height of rectangle
     */

    private Point randomPoint(int x, int y, int dx, int dy) {
        return new Point(RandomUtils.uniform(r, x, x + dx + 1),
                RandomUtils.uniform(r, y, y + dy + 1));
    }

    /**
     * Reads an existing Engine object from file
     * @return
     */

    private static Engine loadEngine() {
        File f = new File("./dump.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (Engine) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
            }
        }

        /* In the case no Editor has been saved yet, we return a new one. */
        return null;
    }

    public void save() {
        File f = new File("./dump.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(this);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource source = new KeyboardInputSource();

        kbInput = true;
        seed = 0;
        interact(source, false);

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

        InputSource source = new StringInputDevice(input.toUpperCase());
        kbInput = false;
        return interact(source, false);
    }


    /**
     * Reads an input source and do something about it
     * @param src The input source
     */

    public TETile[][] interact(InputSource src, boolean replay) {
        makeMenu(); seed = 0; boolean startReadingSeed = false; toBeCleared = new ArrayDeque<>();
        while (src.possibleNextInput()) {
            while (kbInput && !replay && !StdDraw.hasNextKeyTyped()) {
                sleep(10, false); renewDisplayBar();
            }
            char next = src.getNextKey(); history.append(next); renderRPG();
            switch (next) {
                case ':': // if :Q then save and quit
                    if (src.getNextKey() == 'Q') {
                        save(); // System.exit(0);
                    }
                    break;
                case 'N': // new world
                    if (player == null) {
                        startReadingSeed = true; drawSeedPrompt();
                    }
                    break;
                case 'S': // start game
                    if (player == null && startReadingSeed) {
                        startReadingSeed = false;
                        startNewWorld(src);
                        break;
                    } // fall through is 'S' refers to a direction
                case 'W': case 'A': case 'D':
                    if (player != null) {
                        player.move(Direction.parse(next)); renderGame(src);
                    }
                    break;
                case ' ': attemptFire(); renderGame(src); break;
                case 'R': reload(); renderGame(src); break;
                case 'T': wave.withPaths(ter, kbInput); break;
                case 'L':
                    Engine potential = loadEngine();
                    if (potential != null) {
                        this.tiles = potential.tiles; this.ter = potential.ter;
                        this.player = potential.player; this.wave = potential.wave;
                        this.r = potential.r; this.seed = potential.seed;
                        this.toBeCleared = potential.toBeCleared;
                        this.arena = potential.arena;
                        if (kbInput) {
                            ter.initialize(WIDTH, HEIGHT + 3);
                            renderGame(src); locate(); renewDisplayBar();
                        }
                        Direction.setArena(potential.arena);
                    }
                    history.deleteCharAt(history.length() - 1);
                    break;
                case 'B': //buy a weapon from the store
                    if (player != null && player.atShop()) {
                        String shopMsg = Shop.openMenu(this, src, history);
                        if (shopMsg == null) {
                            return tiles;
                        }
                        player.setMessage(shopMsg); renewDisplayBar();
                    }
                    break;
                default:
                    if ((next == '1' || next == '2') && player != null) {
                        player.switchWeapon(); renderGame(src);
                    } else if (startReadingSeed && validDigit(next)) {
                        seed = seed * 10 + Integer.parseInt(String.valueOf(next));
                        displaySeed(kbInput);
                    }
            }
            if (backToMenu) {
                backToMenu = false; return interact(src, false);
            }
            sleep(100, replay);
        }
        return tiles;
    }

    /**
     * Constructs a fresh world
     * @param src the input source that the world will use
     */

    public void startNewWorld(InputSource src) {
        r = new Random(seed);
        tiles = new TETile[WIDTH][HEIGHT]; generateWorld();
        player = new Player(randomPlacement(), this);
        if (kbInput) {
            ter.initialize(WIDTH, HEIGHT + 3); renderGame(src);
            locate();
        }

    }

    /**
     * Convenient helper function to reduce length of interact() function
     */

    private void attemptFire() {
        if (player != null) {
            player.fire();
        }
    }

    /**
     * Helper method that reloads the current weapon
     */
    private void reload() {
        if (player == null) {
            return;
        }
        if (player.currentWeapon().reload()) {
            wave.update(player.getLocation(), true, true);
            player.setMessage(player.currentWeapon().getName() + " is reloaded.");
        }
    }


    /**
     * Helper method that renders the game
     * Deletes bullet trails but doesn't render the frame yet!
     *
     */
    public void renderGame(InputSource src) {
        if (kbInput) {
            ter.renderFrame(tiles);
        }

        if (player.getHealth() == 0) {
            EndMenu menu = new EndMenu(player, "Game Over");
            menu.open(src);
            return;
        }

        for (Point p: toBeCleared) {
            if (tiles[p.getX()][p.getY()].equals(Tileset.FLOOR)) {
                tiles[p.getX()][p.getY()] = Tileset.FLOOR;
            }
        }

        toBeCleared.clear();
        renewDisplayBar();
    }

    /**
     * Pause for a moment
     *
     * @param n (in milliseconds) the time to wait
     */
    private static void sleep(int n, boolean replay) {
        try {
            if (n < 50 || replay) {
                TimeUnit.MILLISECONDS.sleep(n);
            }
        } catch (InterruptedException e) {
            System.out.print("\ndelay failed");
        }
    }


    /**
     * Whether a given character is a valid digit
     * @param c the char
     * @return Boolean. True if char is a digit
     */
    private boolean validDigit(char c) {
        try {
            Integer.parseInt(c + "");
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Helper method to return to previous tile state
     */

    private void renderRPG() {
        for (Point p: Bullet.getRpgExplosion().keySet()) {
            if (tiles[p.getX()][p.getY()] == null) {
                return;
            }
            if (tiles[p.getX()][p.getY()].description().equals("Flame")) {
                tiles[p.getX()][p.getY()] = Tileset.FLOOR;
            }
        }
        Bullet.getRpgExplosion().clear();
    }


    // User Interface

    /**
     * Helper method that generates a shop at a random position
     */

    private void generateShop() {
        ArrayList<Point> l1 = new ArrayList<>();
        ArrayList<Point> l2 = new ArrayList<>();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                if (tiles[x][y] == Tileset.FLOOR
                        && hasNearby(tiles, new Point(x, y), Tileset.FLOOR, 7)) {
                    if (r.nextDouble() < 0.5) { //construct a vertical shop
                        if (hasNearby(tiles, new Point(x, y - 1), Tileset.FLOOR, 8)) {
                            l1.add(new Point(x, y));
                            l2.add(new Point(x, y - 1));
                        }
                    } else { //construct a horizontal shop
                        if (hasNearby(tiles, new Point(x + 1, y), Tileset.FLOOR, 7)) {
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

    private void makeMenu() {
        if (!kbInput) {
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

    private void renewDisplayBar() {

        if (player == null || !kbInput) {
            return;
        }

        //get current mouse position to update tile information
        String tile = tileInfo();

        //cover previous display
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, HEIGHT + 2, WIDTH, 1);

        //tile information
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(6, HEIGHT + 2, tile);

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
        StdDraw.setPenColor(new Color(180, 70, 60));
        StdDraw.filledRectangle(36, HEIGHT + 2, 3, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(41, HEIGHT + 2, "Weapon");
        StdDraw.text(36, HEIGHT + 2, player.currentWeapon().getName());

        //Ammo information
        StdDraw.setPenColor(new Color(207, 127, 56));
        StdDraw.filledRectangle(49, HEIGHT + 2, 2.5, 1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(53, HEIGHT + 2, "Ammo");
        StdDraw.text(49, HEIGHT + 2, player.ammoDescription());

        //Message
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(57, HEIGHT + 2, player.getMessage());

        StdDraw.show();
    }

    private void drawSeedPrompt() {
        if (kbInput) {
            Font font3 = new Font("Times New Roman", Font.BOLD, 20);
            StdDraw.setFont(font3);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(0.8, 0.1, "Please enter random seed. Then press 'S'.");
            StdDraw.show();
        }
    }

    /**
     * Display the currently entered seed to the user in the main menu screen
     * @param keyboardInput the keyboard input
     */

    private void displaySeed(boolean keyboardInput) {
        if (keyboardInput) {
            Font font3 = new Font("Times New Roman", Font.BOLD, 20);
            StdDraw.setFont(font3);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledRectangle(0.8, 0.05, 1, 0.03);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(0.8, 0.05, " " + seed + " ");
            StdDraw.show();
        }
    }

    /**
     * Draw a ring around the player's spawn point
     */

    private void locate() {
        if (kbInput && player != null) {
            StdDraw.setPenColor(new Color(236, 96, 91));
            StdDraw.setPenRadius(0.01);
            StdDraw.circle(player.getLocation().getX() + 0.5,
                    player.getLocation().getY() + 0.5, 1.5);
            StdDraw.show();
        }
    }

    /**
     * Provides information about the tile at the given mouse position
     */

    private String tileInfo() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        if (y >= HEIGHT) {
            return "Void";
        }

        return tiles[x][y].description();
    }

    /**
     * Make a loading menu
     */

    private void loadingMenu(boolean replay) {
        if (!kbInput || replay) {
            return;
        }

        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.clear(StdDraw.BLACK);

        Font font = new Font("Arial", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.5, "Loading...");

        StdDraw.show();
    }




    // World generation


    private Point randomPlacement() {
        int randomX = r.nextInt(WIDTH - 1);
        int randomY = r.nextInt(HEIGHT - 1);
        while (!tiles[randomX][randomY].equals(Tileset.FLOOR)) {
            randomX = r.nextInt(WIDTH - 1);
            randomY = r.nextInt(HEIGHT - 1);
        }
        return new Point(randomX, randomY);
    }

    /**
     * Determine if at least c of the 8 neighbors of the given point is a tile of type p
     *
     * @param location A Point object containing the location to search
     * @param p        the tile pattern to look for
     * @param c        the minimum number of surrounding tiles that are of type p
     * @param tiles    the tile matrix
     */

    public static boolean hasNearby(TETile[][] tiles, Point location, TETile p, int c) {

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

    public static Point randomPlacement(TETile[][] tiles, Player player, Random r) {
        Point randomPoint = new Point(r.nextInt(WIDTH - 1), r.nextInt(HEIGHT - 1));
        while (!tiles[randomPoint.getX()][randomPoint.getY()].equals(Tileset.FLOOR)
                || hasNearby(tiles, randomPoint, player.getCurrentTile(), 1)) {
            randomPoint = new Point(r.nextInt(WIDTH - 1), r.nextInt(HEIGHT - 1));
        }
        return randomPoint;
    }


    /**
     * Generate a random world based on a seed
     */

    private void generateWorld() {
        final int numberOfRooms = r.nextInt(25) + 17;

        Map<Point, Point> originToSize = new HashMap<>();

        // Add some random rooms to the world

        while (originToSize.size() < numberOfRooms) {
            int dx = RandomUtils.uniform(r, 3, 10);
            int dy = RandomUtils.uniform(r, 3, 10);
            int x = r.nextInt(WIDTH - dx - 2) + 1;
            int y = r.nextInt(HEIGHT - dy - 2) + 1;

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

            Point s = randomPoint(p.getX(), p.getY(), pSize.getX(), pSize.getY());
            Point t = randomPoint(q.getX(), q.getY(), qSize.getX(), qSize.getY());

            int minX = Math.min(s.getX(), t.getX());
            int minY = Math.min(s.getY(), t.getY());
            int maxX = Math.max(s.getX(), t.getX());
            int maxY = Math.max(s.getY(), t.getY());

            if (r.nextBoolean()) {
                // First go vertically then horizontally
                fill(s.getX(), minY, 0, maxY - minY, tiles, Tileset.FLOOR);
                fill(minX, t.getY(), maxX - minX, 0, tiles, Tileset.FLOOR);
            } else {
                // First go horizontally then vertically
                fill(minX, s.getY(), maxX - minX, 0, tiles, Tileset.FLOOR);
                fill(t.getX(), minY, 0, maxY - minY, tiles, Tileset.FLOOR);

            }
        }

        fillTheRest();
        generateShop();
        this.arena = Direction.initPathFinder(tiles, randomPlacement());
    }

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
     * Fills up the empty space with appropriate tiles
     *
     */

    private void fillTheRest() {
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

    // Getter and setter methods

    public Random getR() {
        return r;
    }

    public boolean isKbInput() {
        return kbInput;
    }

    public Wave getWave() {
        return wave;
    }

    public void setWave(Wave w) {
        this.wave = w;
    }

    public Queue<Point> toBeCleared() {
        return toBeCleared;
    }

    public Player getPlayer() {
        return player;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public TERenderer getTer() {
        return ter;
    }

    public void setKbInput(boolean kb) {
        this.kbInput = kb;
    }

    public String getHistory() {
        return history.toString();
    }

    public void setBackToMenu() {
        backToMenu = true;
    }


}
