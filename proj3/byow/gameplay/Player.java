package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Point;
import byow.utils.Direction;

public class Player extends GameCharacter {

    private Weapon[] weapons = new Weapon[2]; // Player has the ability to use weapons
    private int currentWeapon = 0;
    private Direction orientation;
    private int points;

    private static final int PREP_STEPS = 30;
    static final int MAX_HEALTH = 100;
    private String message;

    public Player(TETile[][] tiles, Point location) {
        super(tiles);
        this.addHealth(MAX_HEALTH);
        points = 1200;
        weapons[0] = Weapon.makePistol();
        weapons[1] = Weapon.makeSword();
        Wave.init(this, tiles);
        message = Wave.message();

        // Make default orientation North
        this.location = location;
        tiles[location.getX()][location.getY()] = Tileset.PLAYER_NORTH;
        orientation = Direction.North;
    }

    public Player move(Direction direction) {
        int x = location.getX(), y = location.getY();
        if (!Wave.started() && direction.equals(orientation)) {
            Wave.update();
        }
        message = Wave.message();

        switch (direction) {
            case North:
                if (!orientation.equals(Direction.North)) {
                    orientation = Direction.North;
                    tiles[x][y] = Tileset.PLAYER_NORTH;
                    return this;
                } else if (y + 1 < tiles[0].length && tiles[x][y + 1].equals(Tileset.FLOOR)) {
                    tiles[x][y + 1] = Tileset.PLAYER_NORTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y + 1);
                    return this;
                }
                return null;
            case South:
                if (!orientation.equals(Direction.South)) {
                    orientation = Direction.South;
                    tiles[x][y] = Tileset.PLAYER_SOUTH;
                    return this;
                } else if (y > 0 && tiles[x][y - 1].equals(Tileset.FLOOR)) {
                    tiles[x][y - 1] = Tileset.PLAYER_SOUTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y - 1);
                    return this;
                }
                return null;
            case West:
                if (!orientation.equals(Direction.West)) {
                    orientation = Direction.West;
                    tiles[x][y] = Tileset.PLAYER_WEST;
                    return this;
                } else if (x > 0 && tiles[x - 1][y].equals(Tileset.FLOOR)) {
                    tiles[x - 1][y] = Tileset.PLAYER_WEST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x - 1, y);
                    return this;
                }
                return null;
            case East:
                if (!orientation.equals(Direction.East)) {
                    orientation = Direction.East;
                    tiles[x][y] = Tileset.PLAYER_EAST;
                    return this;
                } else if (x + 1 < tiles.length && tiles[x + 1][y].equals(Tileset.FLOOR)) {
                    tiles[x + 1][y] = Tileset.PLAYER_EAST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x + 1, y);
                    return this;
                }
                return null;
            default:
                throw new IllegalArgumentException("Illegal movement direction: " + direction);
        }

    }

    public Direction getOrientation() {
        return orientation;
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

    public void replaceWeapon(Weapon weapon) {
        weapons[currentWeapon] = weapon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
