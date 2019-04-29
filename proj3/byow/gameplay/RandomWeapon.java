package byow.gameplay;

import java.util.Random;

public class RandomWeapon implements ShopItem {

    final static int PRICE = 1500;
    Random random;

    RandomWeapon() {}

    @Override
    public int getPrice() {
        return 1500;
    }

    @Override
    public String getName() {
        return "Roll Random Weapon!";
    }

    @Override
    public String apply(Player player) {
        if (random == null) {
            throw new RuntimeException("Random is not defined in shop!");
        }
        Weapon[] list = {
                Weapon.makeSword(),
                Weapon.makePistol(),
                Weapon.makeShotgun(),
                Weapon.makeSniperRifle(),
                Weapon.makeMachineGun(),
                Weapon.makeMachinePistol(),
                Weapon.makeMinigun(),
                Weapon.makeSubmachineGun(),
                Weapon.flamethrower()
        };

        if (player.getPoints() >= PRICE) {
            player.deductPoints(PRICE);
            player.replaceWeapon(list[random.nextInt(list.length)]);
        }
        return "You rolled a " + player.currentWeapon().getName() + "!";
    }
}
