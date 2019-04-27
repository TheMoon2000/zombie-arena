package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

class Zombie extends GameCharacter {

    private Player player;
    private Random r;
    private boolean isHurt = false;
    boolean explosive = false;
    static boolean recalculatePath = true;

    Zombie(Player player, Point location, Random random) {
        super(player.tiles);
        this.location = location;
        this.player = player;
        r = random;

        addHealth(fullHealth());
    }

    private int fullHealth() {
        return 25 + Wave.currentWave() * 5;
    }

    public boolean advance(Point target) {
        List<Point> sPath = Direction.shortestPath(this.location, target, r);
        Point destination = sPath.get(1);
        TETile desTile = tiles[destination.getX()][destination.getY()];

        if (desTile.equals(Tileset.FLOOR) && !isHurt)  {
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
            tiles[destination.getX()][destination.getY()] = tile();
            this.location = destination;
        } else if (destination.equals(player.location)) {
            attack();
        } else if (desTile.description().toLowerCase().contains("bullet") && !isHurt) {
            Bullet damageSource = null;
            for (Bullet b: Wave.bullets) {
                if (b.location.equals(destination)) {
                    damageSource = b;
                }
            }

            if (damageSource != null) {
                tiles[location.getX()][location.getY()] = Tileset.FLOOR;
                tiles[destination.getX()][destination.getY()] = tile();
                Wave.bullets.remove(damageSource);
                this.location = destination;
                reduceHealth(damageSource.currentDamage(), false, Wave.aliveZombies);
                return getHealth() == 0;
            } else {
                throw new RuntimeException("Internal inconsistency with bullet locations");
            }
        } else {
            isHurt = false;
        }
        return false;
    }

    private void attack() {
        player.reduceHealth(Math.min(10 + Wave.currentWave() * 3, getHealth()));
    }

    void reduceHealth(int amount, boolean clearIfNeeded, Set<Zombie> aliveZombies) {
        super.reduceHealth(amount);
        isHurt = true;
        if (this.getHealth() <= 0) {
            if (r.nextDouble() < 0.15) {
                player.addHealth((int) (r.nextDouble() * 10));
            }
            if (clearIfNeeded) {
                Wave.aliveZombies.remove(this);
            }
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
            player.addPoints(100);
            if (explosive && Math.abs(location.getX() - player.getLocation().getX()) <= 1
                && Math.abs(location.getY() - player.getLocation().getY()) <= 1) {
                //find adjacent zombies first
                ArrayList<Zombie> surroundingZombies = new ArrayList<>();
                for (Zombie z : aliveZombies) {
                    if (Math.abs(location.getX() - z.getLocation().getX()) <= 1
                            && Math.abs(location.getY() - z.getLocation().getY()) <= 1) {
                        surroundingZombies.add(z);
                    }
                }
                for (Zombie z: surroundingZombies) {
                    z.reduceHealth(20);
                }
                player.reduceHealth(19);
                player.setMessage("Ouch! That was an explosive zombie!");
            }
        } else {
            player.addPoints(10);
            tiles[location.getX()][location.getY()] = tile();
        }
    }

    TETile tile() {
        double healthPercentage = (double) getHealth() / (double) fullHealth();
        int red = (int) Math.round(200.0 * healthPercentage) + 50;
        double greenBase = explosive ? 150.0 : 30.0;
        int greenMin = explosive ? 40 : 10;
        String desc = (explosive ? "Explosive zombie" : "Zombie") + " (" + getHealth() + ")";
        int green = (int) Math.round(greenBase * healthPercentage) + greenMin;
        int blue = (int) Math.round(30.0 * healthPercentage) + 10;

        return new TETile('x', new Color(red, green, blue), Tileset.FLOOR_COLOR, desc);
    }
}
