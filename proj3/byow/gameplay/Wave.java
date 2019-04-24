package byow.gameplay;

import byow.Core.Engine;
import byow.TileEngine.TETile;

import java.util.HashSet;
import java.util.Set;

public class Wave {

    private static final int MAX_WAVE = 10;

    private static int wave;
    private static boolean waveStarted = false;
    private static Player player;
    private static TETile[][] tiles;
    static Set<Zombie> zombies;
    private static int preparation; // How many steps the player can make before wave starts

    public static void init(Player player, TETile[][] tiles) {
        Wave.player = player;
        Wave.tiles = tiles;
        zombies = new HashSet<>();
        preparation = 30;
        wave = 1;
    }

    /**
     * The player takes a step
     */
    public static void update() {

        // Scenario 1: game is during preparation phase
        if (preparation > 1) {
            preparation--;
        } else if (zombies.isEmpty() && waveStarted) {
            // Scenario 2: wave has just ended, begin preparation time
            preparation = 30;
            waveStarted = false;
        } else if (zombies.isEmpty()) {
             waveStarted = true;
            // Scenario 3: player's preparation time is over, begin wave

            // Add zombies here...
            Zombie z = new Zombie(player, Engine.randomPlacement(tiles));
            zombies.add(z);
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

    static String message() {
        if (zombies.isEmpty()) {
            return "Get ready for wave #" + wave + "! " + preparation + " steps remaining...";
        } else {
            String z = zombies.size() == 1 ? " zombie" : " zombies";
            return "Wave #" + wave + " in progress..." + zombies.size() + z + " remaining!";
        }
    }
}
