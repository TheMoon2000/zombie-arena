package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Point;
import byow.utils.Direction;

public class Player extends GameCharacter {

    public Weapon[] weapon; // Player has the ability to use weapons
    private Point location;
    private Direction orientation;
    private int points;
    private int wave;
    private static final int MAX_WAVE = 10;

    public Player(TETile[][] tiles, Point location) {
        super(tiles);
        this.health = 100;
        points = 0;
        wave = 1;
        weapon = new Weapon[] {Weapon.makePistol(), Weapon.makeSword()};

        // Make default orientation North
        this.location = location;
        tiles[location.getX()][location.getY()] = Tileset.PLAYER_NORTH;
        orientation = Direction.North;
    }

    public Point move(Direction direction) {
        int x = location.getX(), y = location.getY();

        switch (direction) {
            case North:
                if (!orientation.equals(Direction.North)) {
                    orientation = Direction.North;
                    tiles[x][y] = Tileset.PLAYER_NORTH;
                    return location;
                } else if (y + 1 < tiles[0].length && tiles[x][y + 1].equals(Tileset.FLOOR)) {
                    tiles[x][y + 1] = Tileset.PLAYER_NORTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y + 1);
                    return location;
                }
                return null;
            case South:
                if (!orientation.equals(Direction.South)) {
                    orientation = Direction.South;
                    tiles[x][y] = Tileset.PLAYER_SOUTH;
                    return location;
                } else if (y > 0 && tiles[x][y - 1].equals(Tileset.FLOOR)) {
                    tiles[x][y - 1] = Tileset.PLAYER_SOUTH;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x, y - 1);
                    return location;
                }
                return null;
            case West:
                if (!orientation.equals(Direction.West)) {
                    orientation = Direction.West;
                    tiles[x][y] = Tileset.PLAYER_WEST;
                    return location;
                } else if (x > 0 && tiles[x - 1][y].equals(Tileset.FLOOR)) {
                    tiles[x - 1][y] = Tileset.PLAYER_WEST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x - 1, y);
                    return location;
                }
                return null;
            case East:
                if (!orientation.equals(Direction.East)) {
                    orientation = Direction.East;
                    tiles[x][y] = Tileset.PLAYER_EAST;
                    return location;
                } else if (x + 1 < tiles.length && tiles[x + 1][y].equals(Tileset.FLOOR)) {
                    tiles[x + 1][y] = Tileset.PLAYER_EAST;
                    tiles[x][y] = Tileset.FLOOR;
                    location = new Point(x + 1, y);
                    return location;
                }
                return null;
            default:
                throw new IllegalArgumentException("Illegal movement direction: " + direction);
        }

    }

    public Direction getOrientation() {
        return orientation;
    }

    public Point getLocation() {
        return location;
    }

    public int getPoints() {
        return points;
    }

    public Player addPoints(int p) {
        points += p;
        return this;
    }

    public Player deductPoints(int p) {
        if (points < p) {
            return null;
        }
        points -= p;
        return this;
    }

    public Player nextWave() {
        if (wave == MAX_WAVE) {
            return null;
        }
        wave += 1;
        return this;
    }

    public int currentWave() {
        return wave;
    }
}
