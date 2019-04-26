package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

public class Bullet {

    private Weapon weapon;
    Point location; //current location of the bullet
    private TETile[][] tiles;
    private int speed;
    private int damage;
    private int distanceTravelled = 0; //how many blocks traveled
    private Player player;
    private Direction orientation;

    Bullet(Player player) {
        this.weapon = player.currentWeapon();
        this.tiles = player.tiles;
        this.location = player.getLocation();
        this.player = player;
        this.orientation = player.getOrientation();
        this.speed = weapon.getSpeed();
        this.damage = 0;
    }

    public boolean advance() {
        switch (orientation) {
            case North: return advanceHelper(1, 0);
            case South: return advanceHelper(-1, 0);
            case East: return advanceHelper(0, 1);
            case West: return advanceHelper(0, -1);
            default: return false;
        }
    }

    private TETile bulletTile() {
        return weapon.bulletTile(orientation, distanceTravelled);
    }

    int currentDamage() {
        return weapon.calculateDamage(distanceTravelled);
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
                    zombie.reduceHealth(currentDamage(), true);
                    player.setMessage("You dealt " + currentDamage() + " damage to a zombie.");
                }

            }
            return true;
        }


        if (distanceTravelled > 0) { // if bullet has started to travel,
                                     // first change its current location to floor
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
        }

        if (distanceTravelled > weapon.getMaxDistance()) {
            return true;
        }

        for (int i = 1; i <= speed; i++) { // bullet may travel multiple tiles at once
            distanceTravelled++;
            if (tiles[location.getX() + dx * i][location.getY() + dy * i]
                    .description().toLowerCase().contains("zombie")) {
                for (Zombie z : Wave.aliveZombies) {
                    if (z.location.equals(new Point(location.getX() + dx * i,
                            location.getY() + dy * i))) {
                        z.reduceHealth(currentDamage(), true); //deal damage to the zombie
                        player.setMessage("You dealt " + currentDamage()
                                + " damage to zombie.");
                        return true;
                    }
                }
            }
            //if bullet doesn't hit a floor or a zombie, it has hit an obstacle.
            if (tiles[location.getX() + dx * i][location.getY() + dy * i] != Tileset.FLOOR) {
                return true;
            }
        }
        //if bullet hasn't hit anything, show its new position, return false to keep track of it
        tiles[location.getX() + dx * speed][location.getY() + dy * speed] = bulletTile();
        location = new Point(location.getX() + dx * speed, location.getY() + dy * speed);

        return false;
    }

}
