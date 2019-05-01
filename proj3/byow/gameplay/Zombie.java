package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;
import byow.utils.Point;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

class Zombie extends GameCharacter {

    private Player player;
    private boolean isHurt = false;
    boolean explosive = false;
    private Wave wave;

    Zombie(Player player, Point location) {
        super(player.tiles);
        this.location = location;
        this.player = player;
        this.wave = player.engine.getWave();

        addHealth(fullHealth());
    }

    private int fullHealth() {
        return 25 + wave.currentWave() * 6;
    }

    public void advance(Point target) {
        List<Point> sPath = Direction.shortestPath(this.location, target);
        if (sPath.isEmpty() || getHealth() == 0) {
            return; // The zombie is already dead
        }
        Point destination = sPath.get(0);
        TETile desTile = tiles[destination.getX()][destination.getY()];

        if (desTile.equals(Tileset.FLOOR) && !isHurt)  {
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
            tiles[destination.getX()][destination.getY()] = tile();
            this.location = sPath.remove(0);
        } else if (destination.equals(player.location)) {
            attack();
        } else if ((desTile.description().toLowerCase().contains("bullet")
                || desTile.description().equals("Flame")) && !isHurt) {
            int damage = 0;
            Bullet needRemovalRPGBullet = null;
            for (Bullet b: wave.bullets) {
                if (b.location.equals(destination)) {
                    if (b.bulletTile().description().contains("RPG")) {
                        b.handleRPGCase(destination.getX(), destination.getY(), false);
                        needRemovalRPGBullet = b;
                    } else {
                        damage = b.currentDamage(); b.zombiesHarmed++;
                    }
                } else if (Bullet.getRpgExplosion().keySet().contains(destination)) {
                    damage = Bullet.computeRPGDamage(b.location, location, b.currentDamage());
                }
            }
            if (needRemovalRPGBullet != null) {
                wave.bullets.remove(needRemovalRPGBullet);
            }

            if (damage > 0) {
                tiles[location.getX()][location.getY()] = Tileset.FLOOR;
                tiles[destination.getX()][destination.getY()] = tile();
                refreshTile(location, tiles);
                this.location = sPath.remove(0);
                reduceHealth(damage);
                refreshTile(destination, tiles);
            }
        } else {
            isHurt = false;
        }
    }

    private void refreshTile(Point p, TETile[][] tiles) {
        if (!tiles[p.getX()][p.getY()].equals(Tileset.FLOOR)) {
            return;
        }
        for (Bullet bullet: wave.bullets) {
            if (bullet.location.equals(p)) {
                tiles[p.getX()][p.getY()] = bullet.bulletTile();
            }
        }
    }

    private void attack() {
        if (getHealth() == 0) {
            System.out.println("WARNING: A zombie has 0 health but is still alive!");
        }
        player.reduceHealth(Math.min(10 + wave.currentWave() * 3, getHealth() + 1));
    }

    @Override
    void reduceHealth(int amount) {
        super.reduceHealth(amount);
        isHurt = true;
        if (this.getHealth() == 0) {
            tiles[location.getX()][location.getY()] = Tileset.FLOOR;
            player.addPoints(80);
            if (explosive && Math.abs(location.getX() - player.getLocation().getX()) <= 1
                && Math.abs(location.getY() - player.getLocation().getY()) <= 1) {
                //find adjacent zombies first
                ArrayList<Zombie> surroundingZombies = new ArrayList<>();
                for (Zombie z: wave.aliveZombies) {
                    if (Math.abs(location.getX() - z.getLocation().getX()) <= 1
                            && Math.abs(location.getY() - z.getLocation().getY()) <= 1) {
                        surroundingZombies.add(z);
                    }
                }
                for (Zombie z: surroundingZombies) {
                    if (!z.equals(this)) {
                        z.reduceHealth(20);
                    }
                }
                player.reduceHealth(19);
                player.setMessage("Ouch! That was an explosive zombie!");
            }
        } else {
            player.addPoints(5);
            tiles[location.getX()][location.getY()] = tile();
        }
    }

    TETile tile() {
        double healthPercentage = (double) getHealth() / (double) fullHealth();
        int red = (int) Math.round(190.0 * healthPercentage) + 60;
        double greenBase = explosive ? 150.0 : 30.0;
        int greenMin = explosive ? 45 : 15;
        String desc = (explosive ? "Explosive zombie" : "Zombie") + " (" + getHealth() + ")";
        int green = (int) Math.round(greenBase * healthPercentage) + greenMin;
        int blue = (int) Math.round(30.0 * healthPercentage) + 12;

        return new TETile('x', new Color(red, green, blue), Tileset.FLOOR_COLOR, desc);
    }
}
