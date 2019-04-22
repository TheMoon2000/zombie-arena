package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.Tileset;
import byow.gameplay.Player;
import byow.gameplay.Shop;
import byow.utils.Direction;
import byow.utils.NearTree;
import byow.utils.Point;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

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

        InputSource source = new StringInputDevice(input);

        return interact(source, false);
    }



    // Helper methods


    /**
     * Reads an input source and do something about it
     * @param source The input source
     * */

    private TETile[][] interact(InputSource source, boolean keyBoardInput) {

        //create menu if keyboardInput
        menu(keyBoardInput);

        boolean startReadingSeed = false;
        boolean colon = false;

        long seed = 0;
        Random r;

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        Player player = null;
        Shop shop = null;

        System.out.println("Capturing input source:");

        while (source.possibleNextInput()) {

            //display mouse cursor's tile information if game has started
            //update display bar whenever user doesn't input anything
            while (!StdDraw.hasNextKeyTyped()) {
                renewDisplayBar(shop, player, keyBoardInput);
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.print("\ndelay failed");
                }

            }

            char next = source.getNextKey();
            System.out.println(next);
            if (next != 'Q') {
                colon = false;
            }

            switch (next) {
                case 'Q': // if :Q then save and quit
                    if (colon) {
                        System.out.println("\nSaving...");
                        return tiles;
                    }
                    continue;
                case 'N': // new world
                    if (player != null) {continue;}
                    startReadingSeed = true;
                    if (keyBoardInput) {
                        Font font3 = new Font("Times New Roman", Font.BOLD, 20);
                        StdDraw.setFont(font3);
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.text(0.8, 0.1, "Please enter random seed. Then press 'S'.");
                        StdDraw.show();
                    }
                    continue;
                case 'S': // start game
                    if (player == null && startReadingSeed) {
                        r = new Random(seed);
                        generateWorld(tiles, seed);
                        fillTheRest(tiles, new Random(seed));
                        startReadingSeed = false;

                        // Randomly place the player on a floor tile
                        int randomX = r.nextInt(WIDTH - 1);
                        int randomY = r.nextInt(HEIGHT - 1);
                        while (!tiles[randomX][randomY].equals(Tileset.FLOOR)) {
                            randomX = r.nextInt(WIDTH - 1);
                            randomY = r.nextInt(HEIGHT - 1);
                        }
                        player = new Player(tiles, new Point(randomX, randomY));

                        //randomly generate two shops
                        generateShop(player,seed);
                        shop = new Shop();

                        if (keyBoardInput) {
                            ter.initialize(WIDTH, HEIGHT + 3);
                            ter.renderFrame(tiles);

                            //Tell User where he is at the beginning
                            StdDraw.setPenColor(new Color(236, 96, 91));
                            StdDraw.setPenRadius(0.01);
                            StdDraw.circle(player.getLocation().getX() + 0.5,player.getLocation().getY() + 0.5,1);
                        }
                    } else if (player != null) {
                        player.move(Direction.South);
                        //if there is a shop nearby, display the shop's message first
                        if (hasNearby(player.tiles,player.getLocation().getX(),player.getLocation().getY(),Tileset.LOCKED_DOOR,2)) {
                            player.setMessage(shop.displayMessage());
                        }
                        if (keyBoardInput) {
                            ter.renderFrame(tiles);
                        }
                    }
                    continue;
                case 'W':
                    if (player != null) {
                        player.move(Direction.North);
                        //if there is a shop nearby, display the shop's message first
                        if (hasNearby(player.tiles,player.getLocation().getX(),player.getLocation().getY(),Tileset.LOCKED_DOOR,2)) {
                            player.setMessage(shop.displayMessage());
                        }
                        if (keyBoardInput) {
                            ter.renderFrame(tiles);
                        }
                    }
                    continue;
                case 'A':
                    if (player != null) {
                        player.move(Direction.West);
                        //if there is a shop nearby, display the shop's message first
                        if (hasNearby(player.tiles,player.getLocation().getX(),player.getLocation().getY(),Tileset.LOCKED_DOOR,2)) {
                            player.setMessage(shop.displayMessage());
                        }
                        if (keyBoardInput) {
                            ter.renderFrame(tiles);
                        }
                    }
                    continue;
                case 'D':
                    if (player != null) {
                        player.move(Direction.East);
                        //if there is a shop nearby, display the shop's message first
                        if (hasNearby(player.tiles,player.getLocation().getX(),player.getLocation().getY(),Tileset.LOCKED_DOOR,2)) {
                            player.setMessage(shop.displayMessage());
                        }
                        if (keyBoardInput) {
                            ter.renderFrame(tiles);
                        }
                    }
                    continue;
                case ' ':

                    continue;
                case 'L':
                    if (player == null) {
                        System.out.print("\nLoad saved world (unimplemented)");
                    }
                    continue;
                case 'B': //buy a weapon from the store
                    if (player != null) {
                        if (hasNearby(player.tiles,player.getLocation().getX(),player.getLocation().getY(),Tileset.LOCKED_DOOR,2)) {
                            player.setMessage(shop.buy(player));
                        } else {
                            player.setMessage("Buy new weapons at the shop.");
                        }
                    }
                    continue;
                case ':':
                    colon = true; continue;

                //The below four inputs must be placed before default,
                //such that the user can use the number keys to both switch weapons and enter the seed
                case '1': //switch to first weapon
                    if (player != null) {
                        player.switchWeapon(1);
                        continue; //continue placed inside for loop such that user can enter the seed if at the start menu
                    }
                case '2': //switch to second weapon
                    if (player != null) {
                        player.switchWeapon(2);
                        continue;
                    }
                case '3': //switch to second weapon
                    if (player != null) {
                        player.switchWeapon(3);
                        continue;
                    }
                case '4': //switch to second weapon
                    if (player != null) {
                        player.switchWeapon(4);
                        continue;
                    }
                default:
                    if (startReadingSeed) {
                        seed = seed * 10 + Integer.parseInt(String.valueOf(next));
                        if (!keyBoardInput) {continue;}
                        Font font3 = new Font("Times New Roman", Font.BOLD, 20);
                        StdDraw.setFont(font3);
                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.filledRectangle(0.8,0.05,1,0.03);
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.text(0.8, 0.05, " " + seed + " ");
                        StdDraw.show();
                    }
            }
        }


        return tiles;
    }

    /**
     * Helper method that generates a shop at a random position
     */
    private void generateShop(Player player, long seed) {
        TETile[][] tiles = player.tiles;
        ArrayList<Point> l1 = new ArrayList<>();
        ArrayList<Point> l2 = new ArrayList<>();
        Random r = new Random(seed);
        for (int x = 0; x < tiles.length; x ++) {
            for (int y = 0; y < tiles[x].length; y ++) {
                if (tiles[x][y] == Tileset.FLOOR && hasNearby(tiles,x,y,Tileset.FLOOR,8)) {
                        if (r.nextDouble() < 0.5) { //construct a vertical shop
                            if (hasNearby(tiles,x,y - 1,Tileset.FLOOR,8)) {
                                l1.add(new Point(x,y));
                                l2.add(new Point(x,y - 1));
                            }
                        } else { //construct a horizontal shop
                            if (hasNearby(tiles,x + 1,y,Tileset.FLOOR,8)) {
                                l1.add(new Point(x,y));
                                l2.add(new Point(x + 1,y));
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


        while (Math.abs(shop1Point1.getX() - shop2Point1.getX()) <= 8 ||
                Math.abs(shop1Point1.getY() - shop2Point1.getY()) <= 8) {
            index2 = r.nextInt(l1.size());
            shop2Point1 = l1.get(index2);
            shop2Point2 = l2.get(index2);
        }

        tiles[shop1Point1.getX()][shop1Point1.getY()] = Tileset.LOCKED_DOOR;
        tiles[shop1Point2.getX()][shop1Point2.getY()] = Tileset.LOCKED_DOOR;
        tiles[shop2Point1.getX()][shop2Point1.getY()] = Tileset.LOCKED_DOOR;
        tiles[shop2Point2.getX()][shop2Point2.getY()] = Tileset.LOCKED_DOOR;
    }

    /**
     * Helper method that generates the main menu
     */
    private void menu(boolean keyBoardInput) {
        if (keyBoardInput) {
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
    }


    /**
     * Helper method that creates an display bar on top
     * Should be called every time a key is pressed or a state is updated
     * Fields include tile information, health, points, current weapon, weapon ammo, wave number.
     */

    private void renewDisplayBar(Shop shop, Player player, boolean keyboardInput) {

        if (player == null || !keyboardInput) {
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

    /**
     * Provides information about the tile at the given mouse position
     */

    private String tileInfo(Player player) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        if (y >= HEIGHT) {return "Void";}
        if ((new Point(x, y)).equals(player.getLocation())) {return "Player";}
        if (player.tiles[x][y].equals(Tileset.FLOOR)) {return "Floor";}
        if (player.tiles[x][y].equals(Tileset.NOTHING)) {return "Void";}
        if (player.tiles[x][y].equals(Tileset.LOCKED_DOOR)) {return "Shop";}

        return "Wall";
    }


    /**
     * Generate a random world based on a seed
     * @param tiles the tiles matrix
     * @param seed the seed for the random generator instance
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
