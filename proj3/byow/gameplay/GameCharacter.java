package byow.gameplay;

import byow.TileEngine.TETile;
import byow.utils.Point;

import java.io.Serializable;

public class GameCharacter implements Serializable {

    private int health;
    Point location;
    TETile[][] tiles;

    GameCharacter() {

    }

    GameCharacter(TETile[][] tiles) {
        this.tiles = tiles;
    }

    void addHealth(int amount) {
        this.health = Math.min(100, this.health + amount);
    }

    void reduceHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public int getHealth() {
        return health;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public Point getLocation() {
        return location;
    }
}
