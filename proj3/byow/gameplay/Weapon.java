package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;

import java.awt.*;

public class Weapon implements ShopItem {

    private String name;
    private int damage;
    private int maxDistance;
    private int clip, ammo;
    private int clipCapacity, ammoCapacity;
    private int price;
    private int speed;
    private int waitTime; // how long the play needs to wait before shooting again
    int currentWaitTime; // how many more rounds the player needs to wait

    private Weapon(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public String apply(Player player) {
        if (player.getPoints() >= price) {
            player.replaceWeapon(this);
            player.deductPoints(price);
            return "You just bought a new " + name + "!";
        }
        return "Not enough points to buy " + name + "!";
    }

    public int getSpeed() {
        return this.speed;
    }

    static Weapon makePistol() {
        Weapon pistol = new Weapon("Pistol");
        pistol.damage = 20;
        pistol.maxDistance = 8;
        pistol.clip = 10;
        pistol.clipCapacity = 10;
        pistol.ammo = 15;
        pistol.ammoCapacity = 20;
        pistol.price = 500;
        pistol.speed = 1;
        pistol.waitTime = 3;
        return pistol;
    }

    static Weapon makeShotgun() {
        Weapon shotgun = new Weapon("Shotgun");
        shotgun.damage = 50;
        shotgun.maxDistance = 6;
        shotgun.clip = 6;
        shotgun.clipCapacity = 6;
        shotgun.ammo = 12;
        shotgun.ammoCapacity = 18;
        shotgun.price = 1200;
        shotgun.speed = 2;
        shotgun.waitTime = 3;
        return shotgun;
    }

    static Weapon makeSniperRifle() {
        Weapon sniperRifle = new Weapon("Sniper rifle");
        sniperRifle.damage = 80;
        sniperRifle.maxDistance = 100;
        sniperRifle.clip = 5;
        sniperRifle.clipCapacity = 5;
        sniperRifle.ammo = 15;
        sniperRifle.ammoCapacity = 20;
        sniperRifle.price = 1500;
        sniperRifle.speed = 12;
        sniperRifle.waitTime = 4;
        return sniperRifle;
    }

    static Weapon makeMachineGun() {
        Weapon machineGun = new Weapon("Machine gun");
        machineGun.damage = 40;
        machineGun.maxDistance = 25;
        machineGun.clip = 20;
        machineGun.clipCapacity = 30;
        machineGun.ammo = 40;
        machineGun.ammoCapacity = 60;
        machineGun.price = 2000;
        machineGun.speed = 2;
        machineGun.waitTime = 1;
        return machineGun;
    }

    static Weapon makeSword() {
        Weapon sword = new Weapon("Sword");
        sword.damage = 45;
        sword.maxDistance = 1;
        sword.speed = 1;
        sword.waitTime = 1;
        return sword;
    }

    public int damage(int distance) {
        if (maxDistance < distance) {
            return 0;
        } else if (name.equals("Shotgun")) {
            return distance > 5 ? 0 : damage / distance;
        } else {
            return Math.max(0, damage - distance);
        }
    }

    public int calculateDamage(int distance) {
        if (name.equals("Sword")) {
            return damage;
        } else {
            return damage(distance);
        }
    }

    public boolean shoot() {
        if (name.equals("Sword") && currentWaitTime == 0) {
            currentWaitTime = waitTime;
            return true;
        }
        if (clip > 0 && currentWaitTime == 0) {
            clip--;
            currentWaitTime = waitTime;
            return true;
        }
        return false;
    }

    void reduceWaitTime() {
        currentWaitTime = Math.max(0, currentWaitTime - 1);
    }

    public boolean reload() {
        if (ammo == 0 || clip == clipCapacity) {
            return false;
        }

        int refill = Math.min(ammo, clipCapacity - clip);
        ammo -= refill;
        clip += refill;
        return true;
    }

    void refillAmmo() {
        ammo = ammoCapacity;
    }

    TETile bulletTile(Direction orientation, int distanceTravelled) {
        switch (name) {
            case "Pistol":
                return new TETile('·', Color.white, Tileset.FLOOR_COLOR,
                        "Pistol bullet");
            case "Machine gun":
                char c = orientation.vertical() ? '⋮' : '⋯';
                return new TETile(c, new Color(236, 229, 179),
                        Tileset.FLOOR_COLOR, "Machine gun bullet");
            case "Sniper rifle":
                return new TETile('•', new Color(190, 230, 206),
                        Tileset.FLOOR_COLOR, "Sniper rifle bullet");
            case "Shotgun":
                return new TETile('⦿', new Color(242, 205, 143),
                        Tileset.FLOOR_COLOR, "Shotgun bullet");
            default:
                throw new RuntimeException(name + " tile is not considered");
        }
    }

    public String ammoDescription() {
        return clip + " / " + ammo;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Weapon other = (Weapon) obj;
            return this.getName().equals(other.getName());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }
}
