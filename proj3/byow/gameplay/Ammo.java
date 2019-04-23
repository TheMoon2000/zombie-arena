package byow.gameplay;

public class Ammo implements ShopItem {

    private static final int PRICE = 750;

    @Override
    public String getName() {
        return "Refill Ammo";
    }

    @Override
    public int getPrice() {
        return PRICE;
    }

    @Override
    public String apply(Player player) {
        if (player.getPoints() >= PRICE) {
            player.deductPoints(PRICE);
            player.refillAmmo();
            return "Ammo refilled for all weapons.";
        }
        return "Not enough points to restore ammo!";
    }
}
