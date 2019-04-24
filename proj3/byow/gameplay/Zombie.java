package byow.gameplay;

import byow.utils.Direction;
import byow.utils.Point;

import java.util.List;

public class Zombie extends GameCharacter {

    private Player player;

    public Zombie(Player player, Point location) {
        super(player.tiles);
        this.location = location;
        this.player = player;

        addHealth(25 + Wave.currentWave() * 5);
    }

    public Point advance() {
        List<Point> sPath = Direction.shortestPath(this.location, player.location);
        Point destination = sPath.get(1);
        if (destination.equals(player.location)) {
            System.out.println("danger!");
        }
        return destination;
    }

    private void attack() {
        player.reduceHealth(15 + Wave.currentWave() * 3);
    }

    @Override
    void reduceHealth(int amount) {
        super.reduceHealth(amount);
        if (this.getHealth() <= 0) {
            Wave.zombies.remove(this);
        }
    }
}
