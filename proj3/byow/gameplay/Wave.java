package byow.gameplay;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Point;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;

public class Wave {

    private static final int MAX_WAVE = 10;

    private static int wave = 0;
    private static boolean waveStarted = false;
    private static Player player;
    private static TETile[][] tiles;
    private static Queue<Zombie> waveZombies;
    static Set<Zombie> aliveZombies;
    private static int preparation; // How many steps the player can make before wave starts

    public static void init(Player player, TETile[][] tiles) {
        Wave.player = player;
        Wave.tiles = tiles;
        aliveZombies = new HashSet<>();
        waveZombies = new ArrayDeque<>();
        waveStarted = true;
        update(player.location);
    }

    /**
     * The player takes a step
     */
    public static void update(Point location) {

        // Scenario 1: game is during preparation phase
        if (preparation > 1) {
            preparation--;
        } else if (zombiesRemaining() == 0 && waveStarted) {
            // Scenario 2: wave has just ended, begin preparation time
            preparation = 30;
            waveStarted = false;
            wave++;
        } else if (zombiesRemaining() == 0) {
             waveStarted = true;
            // Scenario 3: player's preparation time is over, begin wave

            // Add zombies here...
            for (int i = 0; i < 10 + currentWave() * 5; i++) {
                Zombie z = new Zombie(player, Engine.randomPlacement(tiles, player));
                waveZombies.add(z);
            }
            update(location);
        } else {
            // Scenario 4: game is ongoing

            while (!waveZombies.isEmpty() && aliveZombies.size() < 20) {
                Zombie z = waveZombies.remove();
                aliveZombies.add(z);
                // spawn the zombie 'z'
                tiles[z.location.getX()][z.location.getY()] = Tileset.ZOMBIE;
            }

            for (Zombie alive: aliveZombies) {
                alive.advance(location);
            }
        }
    }

    static boolean started() {
        return waveStarted;
    }

    static int currentWave() {
        return wave;
    }

    public static int stepsRemaining() {
        return preparation;
    }

    public static int zombiesRemaining() {
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
