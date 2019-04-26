package byow.gameplay;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Point;

import java.util.*;

public class Wave {

    private static final int MAX_WAVE = 10;

    private static int wave = 0;
    private static Random r;
    private static boolean waveStarted = false;
    private static Player player;
    private static TETile[][] tiles;
    private static Queue<Zombie> waveZombies;
    static Set<Zombie> aliveZombies;
    private static int preparation; // How many steps the player can make before wave starts

    static List<Bullet> bullets;

    public static void init(Player myPlayer, TETile[][] myTiles, Random random) {
        Wave.player = myPlayer;
        Wave.tiles = myTiles;
        aliveZombies = new HashSet<>();
        waveZombies = new ArrayDeque<>();
        waveStarted = true;
        bullets = new ArrayList<>();
        update(player.location, true, true);
        r = random;
    }

    /**
     * The player takes a step
     * @param location Where the zombies think the player is at
     */
    public static void update(Point location, boolean updateBullets, boolean updateZombies) {

        if (updateBullets) {
            //first deal with all the bullets
            ArrayList<Bullet> removeList = new ArrayList<>();
            for (Bullet b : bullets) {
                if (b.advance()) {
                    removeList.add(b);
                }
            }
            for (Bullet b : removeList) {
                bullets.remove(b);
            }

            player.waitTimeUpdate();
        }

        if (updateZombies) {
            // Scenario 1: game is during preparation phase
            if (preparation > 1) {
                preparation--;
            } else if (zombiesRemaining() == 0 && waveStarted) {
                // Scenario 2: wave has just ended, begin preparation time
                waveStarted = false;
                if (wave < MAX_WAVE) {
                    wave++;
                    preparation = wave == 1 ? 60 : 45;
                } else {
                    // Ends game, player wins
                }
            } else if (zombiesRemaining() == 0) {
                waveStarted = true;
                // Scenario 3: player's preparation time is over, begin wave

                // Add zombies here...
                for (int i = 0; i < 10 + currentWave() * 5; i++) {
                    Zombie z = new Zombie(player, Engine.randomPlacement(tiles, player), r);
                    z.explosive = r.nextDouble() > 0.9; // chance that the zombie is explosive
                    waveZombies.add(z);
                }
                update(location, false, true);
            } else {
                // Scenario 4: game is ongoing

                Queue<Zombie> toBeRemoved = new ArrayDeque<>();

                for (Zombie alive: aliveZombies) {
                    if (alive.advance(location)) {
                        toBeRemoved.add(alive);
                    }
                }

                for (Zombie deadZombie: toBeRemoved) {
                    aliveZombies.remove(deadZombie);
                }

                while (!waveZombies.isEmpty() && aliveZombies.size() < 20) {
                    Zombie z = waveZombies.remove(); // dequeue a zombie from the waiting list
                    aliveZombies.add(z);
                    // spawn the zombie 'z'
                    tiles[z.location.getX()][z.location.getY()] = z.tile();
                }

            }
        }

        player.setMessage(message());
    }

    static int currentWave() {
        return wave;
    }

    private static int zombiesRemaining() {
        return aliveZombies.size() + waveZombies.size();
    }

    static String message() {
        if (aliveZombies.isEmpty()) {
            return "Get ready for wave #" + wave + "! " + preparation + " steps remaining...";
        } else {
            String z = zombiesRemaining() == 1 ? " zombie" : " zombies";
            return "Wave #" + wave + " in progress..." + zombiesRemaining() + z + " remaining!";
        }
    }
}
