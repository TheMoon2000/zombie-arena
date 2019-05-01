package byow.gameplay;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.utils.Direction;

import java.awt.Color;
import java.io.Serializable;

public class Weapon implements ShopItem, Serializable {

    private String name;
    private int damage;
    private int maxDistance;
    private int clip, ammo;
    private int clipCapacity, ammoCapacity;
    private int price;
    private int speed; private int waitTime; // how long to wait before firing again
    private int currentWaitTime; // how many more rounds the player needs to wait
    private double penetration;
    private int reloadTime;
    TETile[] trailTiles = {Tileset.FLOOR, Tileset.FLOOR};

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

    int getSpeed() {
        return this.speed;
    }

    static Weapon makePistol() {
        Weapon pistol = new Weapon("H&K USP");
        pistol.damage = 22;
        pistol.maxDistance = 8;
        pistol.clip = 10;
        pistol.clipCapacity = 10;
        pistol.ammo = 20;
        pistol.ammoCapacity = 40;
        pistol.price = 500;
        pistol.speed = 1;
        pistol.waitTime = 3;
        pistol.penetration = 0.2;
        pistol.reloadTime = 2;
        return pistol;
    }

    static Weapon makeShotgun() {
        Weapon shotgun = new Weapon("Shotgun");
        shotgun.damage = 60;
        shotgun.maxDistance = 6;
        shotgun.clip = 6;
        shotgun.clipCapacity = 6;
        shotgun.ammo = 18;
        shotgun.ammoCapacity = 24;
        shotgun.price = 1500;
        shotgun.speed = 2;
        shotgun.waitTime = 2;
        shotgun.penetration = 0.65;
        shotgun.reloadTime = 2;
        shotgun.trailTiles = new TETile[] {
            new TETile('⋯', new Color(226, 193, 142), Tileset.FLOOR_COLOR, "Arena"),
            new TETile('⋮', new Color(226, 193, 142), Tileset.FLOOR_COLOR, "Arena")
        };
        return shotgun;
    }

    static Weapon makeSniperRifle() {
        Weapon sniperRifle = new Weapon("Barrett");
        sniperRifle.damage = 80;
        sniperRifle.maxDistance = 80;
        sniperRifle.clip = 5;
        sniperRifle.clipCapacity = 5;
        sniperRifle.ammo = 20;
        sniperRifle.ammoCapacity = 25;
        sniperRifle.price = 3000;
        sniperRifle.speed = 15;
        sniperRifle.waitTime = 3;
        sniperRifle.penetration = 0.85;
        sniperRifle.reloadTime = 2;
        sniperRifle.trailTiles = new TETile[] {
            new TETile('⋯', new Color(178, 215, 193), Tileset.FLOOR_COLOR, "Arena"),
            new TETile('⋮', new Color(178, 215, 193), Tileset.FLOOR_COLOR, "Arena")
        };
        return sniperRifle;
    }

    static Weapon makeMachineGun() {
        Weapon machineGun = new Weapon("AK-47");
        machineGun.damage = 42;
        machineGun.maxDistance = 25;
        machineGun.clip = 20;
        machineGun.clipCapacity = 30;
        machineGun.ammo = 80;
        machineGun.ammoCapacity = 120;
        machineGun.price = 2000;
        machineGun.speed = 3;
        machineGun.waitTime = 1;
        machineGun.penetration = 0.75;
        machineGun.reloadTime = 3;
        machineGun.trailTiles = new TETile[] {
            new TETile('⋯', new Color(220, 214, 167), Tileset.FLOOR_COLOR, "Arena"),
            new TETile('⋮', new Color(220, 214, 167), Tileset.FLOOR_COLOR, "Arena")
        };
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

    // Random weapon only:

    static Weapon makeMachinePistol() {
        Weapon machinePistol = new Weapon("Glock");
        machinePistol.damage = 30;
        machinePistol.maxDistance = 10;
        machinePistol.clip = 15;
        machinePistol.clipCapacity = 15;
        machinePistol.ammo = 60;
        machinePistol.ammoCapacity = 90;
        machinePistol.speed = 1;
        machinePistol.waitTime = 1;
        machinePistol.penetration = 0.35;
        machinePistol.reloadTime = 2;
        return machinePistol;
    }

    static Weapon makeMinigun() {
        Weapon minigun = new Weapon("Minigun");
        minigun.damage = 60;
        minigun.maxDistance = 15;
        minigun.clip = 100;
        minigun.clipCapacity = 100;
        minigun.ammo = 200;
        minigun.ammoCapacity = 300;
        minigun.speed = 3;
        minigun.waitTime = 1;
        minigun.penetration = 0.8;
        minigun.reloadTime = 6;
        minigun.trailTiles = new TETile[] {
            new TETile('⋯', new Color(226, 220, 214), Tileset.FLOOR_COLOR, "Arena"),
            new TETile('⋮', new Color(226, 220, 214), Tileset.FLOOR_COLOR, "Arena")
        };
        return minigun;
    }

    static Weapon makeSubmachineGun() {
        Weapon submachineGun = new Weapon("MP5");
        submachineGun.damage = 32;
        submachineGun.maxDistance = 12;
        submachineGun.clip = 20;
        submachineGun.clipCapacity = 20;
        submachineGun.ammo = 100;
        submachineGun.ammoCapacity = 160;
        submachineGun.speed = 2;
        submachineGun.waitTime = 1;
        submachineGun.penetration = 0.5;
        submachineGun.reloadTime = 1;
        return submachineGun;
    }

    static Weapon rocketLauncher() {
        Weapon rocketLauncher = new Weapon("RPG");
        rocketLauncher.damage = 100;
        rocketLauncher.maxDistance = 20;
        rocketLauncher.clip = 1;
        rocketLauncher.clipCapacity = 1;
        rocketLauncher.ammo = 10;
        rocketLauncher.ammoCapacity = 15;
        rocketLauncher.speed = 1;
        rocketLauncher.waitTime = 3;
        rocketLauncher.penetration = 1;
        rocketLauncher.reloadTime = 3;
        return rocketLauncher;
    }

    static Weapon flamethrower() {
        Weapon flamethrower = new Weapon("Flamethrower");
        flamethrower.damage = 75;
        flamethrower.maxDistance = 6;
        flamethrower.clip = 50;
        flamethrower.clipCapacity = 50;
        flamethrower.ammo = 200;
        flamethrower.ammoCapacity = 300;
        flamethrower.speed = 1;
        flamethrower.waitTime = 1;
        flamethrower.penetration = 0.5;
        flamethrower.reloadTime = 3;
        return flamethrower;
    }

    static Weapon flame() {
        Weapon flame = new Weapon("Flame");
        flame.damage = 45;
        flame.maxDistance = 5;
        flame.speed = 1;
        flame.penetration = 0.35;
        return flame;
    }

    private int damage(int distance, int z) {
        if (maxDistance < distance) {
            return 0;
        } else if (name.equals("Shotgun")) {
            return (int) (Math.max(0.0, damage - distance * 2) * Math.pow(penetration, z));
        } else if (name.contains("Flame")) {
            return Math.max(0, damage - distance * distance - z * 5);
        } else {
            return (int) (Math.max(0.0, damage - distance) * Math.pow(penetration, z));
        }
    }

    int calculateDamage(int distance, int zombiesHit) {
        if (name.equals("Sword")) {
            return damage;
        } else {
            return damage(distance, zombiesHit);
        }
    }

    boolean shoot() {
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

        // Reload time
        currentWaitTime = reloadTime;
        return true;
    }

    void refillAmmo() {
        ammo = ammoCapacity;
    }

    TETile bulletTile(Direction orientation, int distanceTravelled) {
        switch (name) {
            case "H&K USP":
                return new TETile('·', Color.white, Tileset.FLOOR_COLOR,
                        "USP.45 bullet");
            case "Glock":
                return new TETile('·', Color.white, Tileset.FLOOR_COLOR,
                        "Glock bullet");
            case "AK-47":
                return new TETile('∗', new Color(236, 229, 179),
                        Tileset.FLOOR_COLOR, "AK-47 bullet");
            case "Barrett":
                return new TETile('•', new Color(190, 230, 206),
                        Tileset.FLOOR_COLOR, "Barrett bullet");
            case "Shotgun":
//                char rocket = '⦿';
                return new TETile('⠶', new Color(242, 205, 143),
                        Tileset.FLOOR_COLOR, "Shotgun bullet");
            case "Minigun":
                return new TETile('•', new Color(200, 200, 204),
                        Tileset.FLOOR_COLOR, "Minigun bullet");
            case "MP5":
                return new TETile('⋆', new Color(226, 220, 214),
                        Tileset.FLOOR_COLOR, "MP5 bullet");
            case "Flamethrower":
                int red = (int) (255.0 / Math.pow(1.2, (double) distanceTravelled - 1));
                int green = (int) (120.0 / Math.pow(1.25, (double) distanceTravelled - 1));
                int blue = (int) (80.0 / Math.pow(1.3, (double) distanceTravelled - 1));
                return new TETile('✦', new Color(red, green, blue),
                        Tileset.FLOOR_COLOR, "Flame");
            case "Flame":
                red = (int) (190.0 / Math.pow(1.3, (double) distanceTravelled - 1)) + 5;
                green = (int) (80.0 / Math.pow(1.3, (double) distanceTravelled - 1)) + 1;
                blue = (int) (60.0 / Math.pow(1.35, (double) distanceTravelled - 1)) + 1;
                return new TETile('✧', new Color(red, green, blue),
                        Tileset.FLOOR_COLOR, "Flame");
            case "RPG":
                return new TETile('⊙', new Color(231, 86, 88), Tileset.FLOOR_COLOR, "RPG bullet");
            default:
                throw new RuntimeException(name + " tile is not considered");
        }
    }

    String ammoDescription() {
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

    int getMaxDistance() {
        return this.maxDistance;
    }
}
