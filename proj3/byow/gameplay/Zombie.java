package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

import java.util.List;

class Zombie extends GameCharacter {

    private Player player;

    Zombie(Player player, Point location) {
        super(player.tiles);
        this.location = location;
        this.player = player;

        addHealth(25 + Wave.currentWave() * 5);
    }

    public void advance(Point target) {
        List<Point> sPath = Direction.shortestPath(this.location, target);
        Point destination = sPath.get(1);
        TETile desTile = tiles[destination.getX()][destination.getY()];

        if (desTile.equals(Tileset.FLOOR))  {
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
            tiles[destination.getX()][destination.getY()] = Tileset.ZOMBIE;
            this.location = destination;
        } else if (destination.equals(player.location)) {
            attack();
        }
    }

    private void attack() {
        player.reduceHealth(15 + Wave.currentWave() * 3);
    }

    @Override
    void reduceHealth(int amount) {
        super.reduceHealth(amount);
        if (this.getHealth() <= 0) {
            Wave.aliveZombies.remove(this);
        }
    }
}
