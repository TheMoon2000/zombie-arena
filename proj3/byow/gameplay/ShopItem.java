package byow.gameplay;

public interface ShopItem {
    int getPrice(); // Price of the upgrade item
    String getName(); // Name of the upgrade item
    String apply(Player player); // How the player uses the upgrade, return a message
}
