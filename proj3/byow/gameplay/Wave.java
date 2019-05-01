package byow.gameplay;

import byow.Core.Engine;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

import java.io.Serializable;
import java.util.Random;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Wave implements Serializable {

    private static final int MAX_WAVE = 12;

    private int wave;
    private Random r;
    private boolean waveStarted;
    private Player player;
    private TETile[][] tiles;
    private Queue<Zombie> waveZombies;
    Set<Zombie> aliveZombies;
    private int preparation; // How many steps the player can make before wave starts
    List<Bullet> bullets = new ArrayList<>();
    private boolean pathEnabled = false;

    public Wave(Player myPlayer, TETile[][] myTiles, Random random) {
        wave = 0;
        player = myPlayer;
        tiles = myTiles;
        aliveZombies = new HashSet<>();
        waveZombies = new ArrayDeque<>();
        waveStarted = true;
        r = random;
        update(player.location, true, true);
        player.setMessage("Welcome to Zombie arena!");
    }



    /**
     * The player takes a step
     * @param location Where the zombies think the player is at
     */
    public void update(Point location, boolean updateBullets, boolean updateZombies) {
        pathEnabled = false;
        String oldMessage = player.getMessage();
        if (updateBullets) {
            //first deal with all the bullets
            ArrayList<Bullet> removeList = new ArrayList<>();
            for (Bullet b: bullets) {
                if (b.advance()) {
                    removeList.add(b);
                }
            }
            for (Bullet b : removeList) {
                bullets.remove(b);
            }
            player.waitTimeUpdate();
            if (player.getMessage() == null || player.getMessage().equals(oldMessage)) {
                player.setMessage(message());
            }
        }

        if (updateZombies) {
            // Scenario 1: game is during preparation phase
            if (preparation > 1) {
                preparation--;
            } else if (zombiesRemaining() == 0 && waveStarted) {
                // Scenario 2: wave has just ended, begin preparation time
                waveStarted = false;
                if (wave < MAX_WAVE) {
                    wave++; preparation = wave == 1 ? 50 : 60;
                } else {
                    // Ends game, player wins
                    EndMenu menu = new EndMenu(player, "You Win!");
                    menu.open(new KeyboardInputSource());
                }
            } else if (zombiesRemaining() == 0) {
                waveStarted = true;
                // Scenario 3: player's preparation time is over, begin wave
                for (int i = 0; i < 15 + currentWave() * 5; i++) {
                    Zombie z = new Zombie(player, Engine.randomPlacement(tiles, player, r));
                    z.explosive = r.nextDouble() > 0.9;
                    waveZombies.add(z);
                }
                update(location, false, true);
            } else {
                // Scenario 4: game is ongoing
                Queue<Zombie> toBeRemoved = new ArrayDeque<>();

                for (Zombie alive: aliveZombies) {
                    alive.advance(location);
                    if (alive.getHealth() == 0) {
                        toBeRemoved.add(alive);
                    }
                }

                for (Zombie deadZombie: toBeRemoved) {
                    aliveZombies.remove(deadZombie);
                }
                for (Point exp: Bullet.getRpgExplosion().keySet()) {
                    if (tiles[exp.getX()][exp.getY()].equals(Tileset.FLOOR)
                        || tiles[exp.getX()][exp.getY()].description().contains("RPG")) {
                        tiles[exp.getX()][exp.getY()] = Bullet.getRpgExplosion().get(exp);
                    }
                }
                while (!waveZombies.isEmpty() && aliveZombies.size() < 20) {
                    Zombie z = waveZombies.remove(); // dequeue a zombie from the waiting list
                    aliveZombies.add(z);
                    // spawn the zombie 'z'
                    tiles[z.location.getX()][z.location.getY()] = z.tile();
                }
            }

            if (player.getMessage() == null || player.getMessage().equals(oldMessage)) {
                player.setMessage(message());
            }
        }
    }

    int currentWave() {
        return wave;
    }

    private int zombiesRemaining() {
        return aliveZombies.size() + waveZombies.size();
    }

    private String message() {
        if (aliveZombies.isEmpty()) {
            return "Get ready for wave #" + wave + "! " + preparation + " steps remaining...";
        } else {
            String z = zombiesRemaining() == 1 ? " zombie" : " zombies";
            return "Wave #" + wave + " in progress..." + zombiesRemaining() + z + " remaining!";
        }
    }

    public void withPaths(TERenderer ter, boolean keyboard) {

        if (player == null) {
            return;
        }

        if (pathEnabled && keyboard) {
            ter.renderFrame(tiles);
            pathEnabled = false;
            return;
        }

        pathEnabled = true;

        // Create a new copy of tiles so that the original world won't be overwritten
        TETile[][] tilesCopy = TETile.copyOf(tiles);

        for (Zombie z: aliveZombies) {
            TETile pathTile = new TETile('·', z.tile().getTextColor(), Tileset.FLOOR_COLOR,
                    "Path");
            List<Point> path = Direction.shortestPath(z.location, player.location);
            path.remove(path.size() - 1);
            for (Point p: path) {
                if (tilesCopy[p.getX()][p.getY()].equals(pathTile)) {
                    continue; // redrawing paths do nothing
                }
                if (tilesCopy[p.getX()][p.getY()].equals(Tileset.FLOOR)) {
                    tilesCopy[p.getX()][p.getY()] = pathTile;
                }
            }
        }
        if (keyboard) {
            ter.renderFrame(tilesCopy);
        }
    }
}
