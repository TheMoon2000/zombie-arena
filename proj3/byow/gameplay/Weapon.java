package byow.gameplay;

public class Weapon {

    private String name;
    private int damage;
    private int maxDistance;
    private int clip, ammo;
    private int clipCapacity, ammoCapacity;
    private int reloadDuration;


    private Weapon(String name) {}

    static Weapon makePistol() {
        Weapon pistol = new Weapon("Pistol");
        pistol.damage = 15;
        pistol.maxDistance = 5;
        pistol.clip = 10;
        pistol.clipCapacity = 10;
        pistol.ammo = 20;
        pistol.ammoCapacity = 20;
        pistol.reloadDuration = 4;
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
        shotgun.reloadDuration = 5;
        return shotgun;
    }

    static Weapon sniperRifle() {
        Weapon sniperRifle = new Weapon("Sniper rifle");
        sniperRifle.damage = 80;
        sniperRifle.maxDistance = 100;
        sniperRifle.clip = 5;
        sniperRifle.clipCapacity = 5;
        sniperRifle.ammo = 15;
        sniperRifle.ammoCapacity = 20;
        sniperRifle.reloadDuration = 5;
        return sniperRifle;
    }

    static Weapon makeSword() {
        Weapon sword = new Weapon("Sword");
        sword.damage = 60;
        sword.maxDistance = 1;
        return sword;
    }

    int damage(int distance) {
        if (maxDistance < distance) {
            return 0;
        } else if (name.equals("Shotgun")) {
            return distance > 5 ? 0 : damage / distance;
        }

        return damage;
    }

    public int fire(int distance) {
        if (name.equals("Sword")) {
            return damage;
        } else if (clip > 0) {
            clip--;
            return damage(distance);
        }

        return -1;
    }

    public int reload() {
        if (ammo == 0) {
            return 0;
        }

        int refill = Math.min(ammo, clipCapacity - clip);
        ammo -= refill;
        clip += refill;
        return reloadDuration;
    }

    public void refillAmmo() {
        ammo = ammoCapacity;
    }
}
