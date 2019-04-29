package byow.gameplay;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.hw4.ShortestPathsSolver;
import byow.utils.Direction;
import byow.utils.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Bullet {

    private Weapon weapon;
    Point location; //current location of the bullet
    private TETile[][] tiles;
    private int speed;
    private int distanceTravelled = 0; //how many blocks traveled
    private Player player;
    private Direction orientation;
    private int zombiesHarmed = 0;
    public static Queue<Point> toBeCleared = new ArrayDeque<>();

    Bullet(Player player, Point start, Weapon w) {
        this.weapon = w;
        this.tiles = player.tiles;
        this.location = start;
        this.player = player;
        this.orientation = player.getOrientation();
        this.speed = weapon.getSpeed();
    }

    boolean advance() {
        switch (orientation) {
            case North: return advanceHelper(1, 0);
            case South: return advanceHelper(-1, 0);
            case East: return advanceHelper(0, 1);
            case West: return advanceHelper(0, -1);
            default: return false;
        }
    }

    TETile bulletTile() {
        return weapon.bulletTile(orientation, distanceTravelled);
    }

    int currentDamage() {
        return weapon.calculateDamage(distanceTravelled, zombiesHarmed);
    }

    /** Helper function to help bullets advance
     * @param dy the vertical displacement of the bullet
     * @param dx the horizontal displacement of the bullet
     * @return Whether the bullet should be removed from the map after this step
     */
    public boolean advanceHelper(int dy, int dx) {
        if (weapon.getName().equals("Sword")) {
            if (tiles[location.getX() + dx][location.getY() + dy]
                    .description().toLowerCase().contains("zombie")) {
                Zombie zombie = null;
                for (Zombie z : Wave.aliveZombies) {
                    if (z.location.equals(new Point(location.getX()
                        + dx, location.getY() + dy))) {
                        zombie = z; //locate the zombie at a particular tile
                    }
                }
                if (zombie != null) {
                    zombie.reduceHealth(currentDamage());
                    player.setMessage("You dealt " + currentDamage() + " damage to a zombie.");
                }

            }
            return true;
        }


        if (tiles[location.getX()][location.getY()].equals(Tileset.WEAPON_BOX)
            || tiles[location.getX()][location.getY()].equals(Tileset.WALL)) {
            return true;
        }

        if (distanceTravelled > 0) { // if bullet has started to travel,
                                     // first change its current location to floor
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
        }


        if (distanceTravelled > weapon.getMaxDistance()) {
            return true;
        }
        /*else if (location.getX() < 0 || location.getX() >= Engine.WIDTH) {
            return true;
        } else if (location.getY() < 0 || location.getY() >= Engine.HEIGHT) {
            return true;
        }*/

        TETile trail = weapon.trailTiles[orientation.vertical() ? 1 : 0];

        int killCount = 0;

        for (int i = 1; i <= speed; i++) { // bullet may travel multiple tiles at once
            distanceTravelled++;
            int targetX = location.getX() + dx * i, targetY = location.getY() + dy * i;
            if (tiles[targetX][targetY].description().toLowerCase().contains("zombie")) {
                List<Zombie> toBeDeleted = new ArrayList<>();
                for (Zombie z : Wave.aliveZombies) {
                    if (z.location.equals(new Point(targetX, targetY))) {
                        z.reduceHealth(currentDamage());
                        zombiesHarmed++;
                        System.out.println("You dealt " + currentDamage()  + " damage!");
                        if (z.getHealth() == 0) {
                            toBeDeleted.add(z);
                            killCount++;
                        }
                    }
                }

                for (Zombie dead: toBeDeleted) {
                    Wave.aliveZombies.remove(dead);
                }
            }

            if (tiles[targetX][targetY].equals(Tileset.FLOOR)) {
                /*
                int nextX = location.getX() + dx * (i + 1);
                int nextY = location.getY() + dy * (i + 1);
                if (tiles[nextX][nextY].equals(Tileset.WALL) ||
                    tiles[nextX][nextY].equals(Tileset.WEAPON_BOX)) {
                    tiles[targetX][targetY] = bulletTile();
                } else {
                    tiles[targetX][targetY] = trail;
                }
                */
                tiles[targetX][targetY] = trail;
                toBeCleared.add(new Point(targetX, targetY));
            } else if (tiles[targetX][targetY].equals(Tileset.WEAPON_BOX)
                    || tiles[targetX][targetY].equals(Tileset.WALL)) {
                return true;
            }
        }

        if (killCount > 1) {
            player.addPoints(50);
            player.setMessage("Multikill! You get 50 points!");
        }

        //if bullet hasn't hit anything, show its new position, return false to keep track of it
        int endX = location.getX() + dx * speed, endY = location.getY() + dy * speed;
        if (tiles[endX][endY].equals(Tileset.FLOOR)
                || tiles[endX][endY].description().equals("Flame")) {
            tiles[endX][endY] = bulletTile();
        }
        location = new Point(endX, endY);

        return false;
    }

}
