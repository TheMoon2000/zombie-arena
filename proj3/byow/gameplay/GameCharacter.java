package byow.gameplay;

import byow.TileEngine.TETile;

public class GameCharacter {

    private int health;
    TETile[][] tiles;

    GameCharacter(TETile[][] tiles) {
        health = 0;
        this.tiles = tiles;
    }

    int addHealth(int amount) {
        this.health = Math.min(100, this.health + amount);
        return this.health;
    }

    int reduceHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
        return this.health;
    }

    public int getHealth() {
        return health;
    }

    public TETile[][] getTiles() {
        return tiles;
    }
}
