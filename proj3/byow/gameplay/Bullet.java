package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

public class Bullet {

    private Weapon weapon;
    private Point location; //current location of the bullet
    private TETile[][] tiles;
    private int speed;
    private int damage;
    private int distanceTravelled = 0; //how many blocks traveled
    private Player player;
    private Direction orientation;
    private boolean discard = false; //if true, that means the bullet has hit something, or the bullet is invalid because out of ammo

    public Bullet(Player player) {
        this.weapon = player.currentWeapon();
        this.tiles = player.tiles;
        this.location = player.getLocation();
        this.player = player;
        this.orientation = player.getOrientation();

        this.speed = weapon.getSpeed();
        this.damage = 0;

        if (weapon.shoot() == 0) {//if clip empty, discard the bullet
            discard = true;
        }
    }

    public boolean advance() {
        if (orientation == Direction.North) {
            return advanceHelper(1,0);
        } else if (orientation == Direction.South) {
            return advanceHelper(-1,0);
        } else if (orientation == Direction.East) {
            return advanceHelper(0,1);
        } else {
            return advanceHelper(0,-1);
        }
    }

    //this is a boolean method. It returns true if the bullet should be removed from the bullet list after advancing this step.
    public boolean advanceHelper(int northOrSouth, int eastOrWest) {

        if (weapon.getName().equals("Sword")) {
            if (tiles[location.getX() + eastOrWest * 1][location.getY() + northOrSouth * 1] == Tileset.ZOMBIE) {
                Zombie zombie = null;
                for (Zombie z : Wave.aliveZombies) {
                    if (z.location.equals(new Point(location.getX() + eastOrWest * 1, location.getY() + northOrSouth * 1))) {
                        zombie = z; //locate the zombie at a particular tile
                    }
                }
                if (zombie != null) {
                    damage = weapon.fire(distanceTravelled);
                    zombie.reduceHealth(damage);
                    player.setMessage("You dealt " + weapon.fire(distanceTravelled) + " damage to zombie.");
                }

            }
            return true;
        }


        if (discard) { //if discard the bullet, simply return true so the bullet is removed from tracking list
            return true;
        }


        if (distanceTravelled > 0) { //if bullet has started to travel, first change its current location to floor
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
        }

        if (distanceTravelled > weapon.getMaxDistance()) { //if bullet has exceeded the maximum distance 
            return true;
        }

        for (int i = 1; i <= speed; i++) { //bullet may travel multiple tiles at once, so we need to find first tile where there is a zombie
            distanceTravelled++;
            if (tiles[location.getX() + eastOrWest * i][location.getY() + northOrSouth * i] == Tileset.ZOMBIE) {
                for (Zombie z : Wave.aliveZombies) {
                    if (z.location.equals(new Point(location.getX() + eastOrWest * i, location.getY() + northOrSouth * i))) {
                        damage = weapon.fire(distanceTravelled);
                        z.reduceHealth(damage); //deal damage to the zombie
                        player.setMessage("You dealt " + weapon.fire(distanceTravelled) + " damage to zombie.");
                        return true;
                    }
                }
            }
            //if bullet doesn't hit a floor or a zombie, it has hit an obstacle.
            if (tiles[location.getX() + eastOrWest * i][location.getY() + northOrSouth * i] != Tileset.FLOOR) {
                return true;
            }
        }
        //if bullet hasn't hit anything, show its new position, return false to keep track of it
        tiles[location.getX() + eastOrWest * speed][location.getY() + northOrSouth * speed] = Tileset.MBULLET;
        location = new Point(location.getX() + eastOrWest * speed, location.getY() + northOrSouth * speed);
        return false;
    }

}
