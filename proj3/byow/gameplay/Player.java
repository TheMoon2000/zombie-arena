package byow.gameplay;

import byow.Core.Engine;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Point;
import byow.utils.Direction;

import java.util.Random;

public class Player extends GameCharacter {

    private Weapon[] weapons = new Weapon[2]; // Player has the ability to use weapons
    private int currentWeapon = 0;
    private Direction orientation;
    private int points;
    TERenderer ter;
    boolean keyboardInput;
    static final int MAX_HEALTH = 100;
    private String message;

    public Player(TETile[][] tiles, Point location, TERenderer renderer, Random r, boolean kb) {
        super(tiles);
        this.addHealth(MAX_HEALTH);
        points = 10000;
        weapons[0] = Weapon.makePistol();
        weapons[1] = Weapon.makeSword();
        Wave.init(this, tiles, r);
        message = Wave.message();
        ter = renderer;
        keyboardInput = kb;

        // Make default orientation North
        this.location = location;
        tiles[location.getX()][location.getY()] = Tileset.PLAYER_NORTH;
        this.orientation = Direction.North;
    }

    public void move(Direction direction) {
        int x = location.getX(), y = location.getY();
        Point previousLocation = location;
        Wave.update(location, true, false);
        switch (direction) {
            case North:
                if (!orientation.equals(Direction.North)) {
                    orientation = Direction.North;
                    tiles[x][y] = Tileset.PLAYER_NORTH;
                } else if (y + 1 < tiles[0].length && tiles[x][y + 1].equals(Tileset.FLOOR)) {
                    tiles[x][y + 1] = Tileset.PLAYER_NORTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y + 1);
                }
                break;
            case South:
                if (!orientation.equals(Direction.South)) {
                    orientation = Direction.South;
                    tiles[x][y] = Tileset.PLAYER_SOUTH;
                } else if (y > 0 && tiles[x][y - 1].equals(Tileset.FLOOR)) {
                    tiles[x][y - 1] = Tileset.PLAYER_SOUTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y - 1);
                }
                break;
            case West:
                if (!orientation.equals(Direction.West)) {
                    orientation = Direction.West;
                    tiles[x][y] = Tileset.PLAYER_WEST;
                } else if (x > 0 && tiles[x - 1][y].equals(Tileset.FLOOR)) {
                    tiles[x - 1][y] = Tileset.PLAYER_WEST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x - 1, y);
                }
                break;
            case East:
                if (!orientation.equals(Direction.East)) {
                    orientation = Direction.East;
                    tiles[x][y] = Tileset.PLAYER_EAST;
                } else if (x + 1 < tiles.length && tiles[x + 1][y].equals(Tileset.FLOOR)) {
                    tiles[x + 1][y] = Tileset.PLAYER_EAST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x + 1, y);
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal movement direction: " + direction);
        }

        Wave.update(previousLocation, false, true);

        if (atShop()) {
            message = Shop.displayMessage();
        }
    }

    public boolean atShop() {
        return Engine.hasNearby(tiles, location, Tileset.WEAPON_BOX, 1);
    }

    public TETile getCurrentTile() {
        return tiles[location.getX()][location.getY()];
    }

    // Points

    public int getPoints() {
        return points;
    }

    public void addPoints(int p) {
        points += p;
    }

    public void deductPoints(int p) {
        if (points < p) {
            return;
        }
        points -= p;
    }

    // Weapons

    public void switchWeapon() {
        currentWeapon = 1 - currentWeapon;
        if (weapons[currentWeapon] == null) {
            this.message = "Empty weapon slot.";
        } else {
            this.message = "Current weapon: " + currentWeapon().getName() + "";
        }

        // Unfortunately, switching weapon counts as one step
        Wave.update(location, true, true);
    }

    public String ammoDescription() {
        return weapons[currentWeapon].ammoDescription();
    }

    void refillAmmo() {
        for (Weapon w: weapons) {
            if (w != null) {
                w.refillAmmo();
            }
        }
    }

    public Weapon currentWeapon() {
        return weapons[currentWeapon];
    }
    private Weapon otherWeapon() {
        return weapons[1 - currentWeapon];
    }

    void replaceWeapon(Weapon weapon) {
        weapons[currentWeapon] = weapon;
    }

    public void fire() {
        if (currentWeapon().shoot()) { // Whether the weapon can shoot
            Wave.bullets.add(new Bullet(this));
        }
        Wave.update(location, true, true);
    }

    void waitTimeUpdate() {
        currentWeapon().reduceWaitTime();
        otherWeapon().reduceWaitTime();
    }

    // Auxiliary

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    Direction getOrientation () {return this.orientation;}

    @Override
    void reduceHealth(int amount) {
        super.reduceHealth(amount);
        System.out.println("Player health reduced by " + amount + " to " + getHealth() + ".");
        if (getHealth() == 0) {
            EndMenu menu = new EndMenu(this, "Game Over", keyboardInput);
            menu.open(new KeyboardInputSource());
            System.out.println("menu closed");
        }
    }
}
