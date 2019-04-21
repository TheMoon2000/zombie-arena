package byow.gameplay;

import byow.TileEngine.TETile;

public class GameCharacter {

    int health;
    TETile[][] tiles;

    public GameCharacter(TETile[][] tiles) {
        health = 0;
        this.tiles = tiles;
    }

    public int addHealth(int health) {
        this.health = Math.min(100, this.health + health);
        return this.health;
    }

    public int reduceHealth(int health) {
        this.health = Math.max(0, this.health - health);
        return this.health;
    }

    public int getHealth() {
        return health;
    }
}
