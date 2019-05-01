package byow.gameplay;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Bullet implements Serializable {

    private Weapon weapon;
    Point location; //current location of the bullet
    private TETile[][] tiles;
    private int speed;
    private int distanceTravelled = 0; //how many blocks traveled
    private Player player;
    private Direction orientation;
    int zombiesHarmed = 0;
    static HashMap<Point, TETile> rpgExplosion = new HashMap<>();
    private Engine engine;

    Bullet(Player player, Point start, Weapon w) {
        this.weapon = w;
        this.tiles = player.tiles;
        this.location = start;
        this.player = player;
        this.orientation = player.getOrientation();
        this.speed = weapon.getSpeed();
        this.engine = player.engine;
    }

    boolean advance() {
        switch (orientation) {
            case North: return advanceHelper(1, 0);
            case South: return advanceHelper(-1, 0);
            case East: return advanceHelper(0, 1);
            case West: return advanceHelper(0, -1);
            default: return true;
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
    private boolean advanceHelper(int dy, int dx) {
        if (weapon.getName().equals("Sword")) {
            if (tiles[location.getX() + dx][location.getY() + dy]
                    .description().toLowerCase().contains("zombie")) {
                for (Zombie z: engine.getWave().aliveZombies) {
                    if (z.location.equals(new Point(location.getX()
                        + dx, location.getY() + dy))) {
                        z.reduceHealth(currentDamage());
                    }
                }
            }
            return true;
        }
        if (distanceTravelled > 0
                && tiles[location.getX()][location.getY()].equals(bulletTile())) {
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
        }
        if (tiles[location.getX()][location.getY()].equals(Tileset.WEAPON_BOX)
            || tiles[location.getX()][location.getY()].equals(Tileset.WALL)
            || distanceTravelled > weapon.getMaxDistance()) {
            return true;
        }

        TETile trail = weapon.trailTiles[orientation.vertical() ? 1 : 0];
        int kills = 0;

        for (int i = 1; i <= speed; i++) { // bullet may travel multiple tiles at once
            distanceTravelled++;
            int targetX = location.getX() + dx * i, targetY = location.getY() + dy * i;
            if (tiles[targetX][targetY].description().toLowerCase().contains("zombie")) {
                if (this.weapon.getName().equals("RPG")) {
                    handleRPGCase(targetX, targetY, true); return true;
                } else {
                    List<Zombie> toBeDeleted = new ArrayList<>();
                    for (Zombie z: engine.getWave().aliveZombies) {
                        if (z.location.equals(new Point(targetX, targetY))) {
                            z.reduceHealth(currentDamage()); zombiesHarmed++;
                            if (z.getHealth() == 0) {
                                toBeDeleted.add(z);
                                kills++;
                            }
                        }
                    }
                    for (Zombie dead : toBeDeleted) {
                        engine.getWave().aliveZombies.remove(dead);
                    }
                }
            }

            if (tiles[targetX][targetY].equals(Tileset.FLOOR)) {
                tiles[targetX][targetY] = trail;
                engine.toBeCleared().add(new Point(targetX, targetY));
            } else if (tiles[targetX][targetY].equals(Tileset.WEAPON_BOX)
                    || tiles[targetX][targetY].equals(Tileset.WALL)) {
                if (this.weapon.getName().equals("RPG")) {
                    handleRPGCase(targetX, targetY, true);
                }

                if (kills > 1) {
                    player.addPoints(50);
                    player.setMessage("Multikill! You get 50 points!");
                }
                return true;
            }
        }

        if (kills > 1) {
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

    static int computeRPGDamage(Point source, Point target, double damage) {
        if (source.equals(target)) {
            return (int) damage;
        } else if (Point.distance(source, target) < 6) {
            return (int) (damage / Math.pow(Point.distance(source, target), 1.3));
        } else {
            return 0;
        }
    }

    void handleRPGCase(int targetX, int targetY, boolean clearDead) {
        Point source = new Point(targetX, targetY); // center of explosion
        List<Zombie> toBeDeleted = new ArrayList<>();
        for (Zombie z: engine.getWave().aliveZombies) {
            int damage = computeRPGDamage(source, z.location, currentDamage());
            if (damage > 0) {
                zombiesHarmed++; z.reduceHealth(damage);
            }
            if (z.getHealth() == 0) {
                toBeDeleted.add(z);
            }
        }

        if (toBeDeleted.size() > 1) {
            player.addPoints(50); player.setMessage("Multikill! You get 50 points!");
        }

        if (clearDead) {
            for (Zombie dead: toBeDeleted) {
                engine.getWave().aliveZombies.remove(dead);
            }
        }

        for (int i = targetX - 2; i <= targetX + 2; i++) {
            for (int j = targetY - 2; j <= targetY + 2; j++) {
                Point current = new Point(i, j);
                if (!((i >= 0 && i < tiles.length) && (j >= 0 && j < tiles[i].length))) {
                    continue;
                }
                if ((tiles[i][j].equals(Tileset.FLOOR)) || (i == targetX && j == targetY
                        && !tiles[i][j].description().equals("Wall"))) {
                    double distance = Point.distance(source, current);
                    int red = (int) (255.0 / Math.pow(1.4, distance));
                    int green = (int) (120.0 / Math.pow(1.35, distance));
                    int blue = (int) (80.0 / Math.pow(1.35, distance));
                    tiles[i][j] = new TETile('✦', new Color(red, green, blue),
                            Tileset.FLOOR_COLOR, "Flame");
                    rpgExplosion.put(current, tiles[i][j]);
                }
            }
        }
    }

    public static HashMap<Point, TETile> getRpgExplosion() {
        return rpgExplosion;
    }
}
