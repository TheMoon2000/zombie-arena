package byow.gameplay;

public class Weapon implements ShopItem {

    private String name;
    private int damage;
    private int maxDistance;
    private int clip, ammo;
    private int clipCapacity, ammoCapacity;
    private int price;
    private int speed;

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
        pistol.damage = 15;
        pistol.maxDistance = 5;
        pistol.clip = 10;
        pistol.clipCapacity = 10;
        pistol.ammo = 15;
        pistol.ammoCapacity = 20;
        pistol.price = 500;
        pistol.speed = 1;
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
        sniperRifle.speed = 10;
        return sniperRifle;
    }

    static Weapon makeSword() {
        Weapon sword = new Weapon("Sword");
        sword.damage = 60;
        sword.maxDistance = 1;
        sword.speed = 1;
        return sword;
    }

    public int damage(int distance) {
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
        } else {
            return damage(distance);
        }
    }

    public int shoot() {
        if (name.equals("Sword")) {
            return 1;
        }
        if (clip > 0) {
            clip--;
        }
        return clip;
    }

    public boolean reload() {
        if (ammo == 0) {
            return false;
        }

        int refill = Math.min(ammo, clipCapacity - clip);
        ammo -= refill;
        clip += refill;
        return true;
    }

    public void refillAmmo() {
        ammo = ammoCapacity;
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
