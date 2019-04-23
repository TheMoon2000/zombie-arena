package byow.gameplay;

public class Health implements ShopItem {

    private int healPower;
    private static final int PRICE = 500;

    private Health(int healPower) {
        this.healPower = healPower;
    }

    public static Health of(int healPower) {
        return new Health(healPower);
    }

    @Override
    public String getName() {
        return "Restore Health";
    }

    @Override
    public int getPrice() {
        return PRICE;
    }

    @Override
    public String apply(Player player) {
        if (player.getPoints() >= PRICE) {
            player.addHealth(healPower);
            player.deductPoints(PRICE);
            return "Restored health to 100%.";
        }
        return "Not enough points to buy health!";
    }
}
