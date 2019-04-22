package byow.gameplay;

import java.util.ArrayList;

public class Shop {

    private ArrayList<Weapon> listOfAvailableWeapons = new ArrayList<>(); //list of items that can be purchased
    private int index = 0; //index pointing to the next item that can be purchased

    public Shop(){
        listOfAvailableWeapons.add(Weapon.makeShotgun());
        listOfAvailableWeapons.add(Weapon.sniperRifle());
    }

    public String buy(Player player) {
        if (index >= listOfAvailableWeapons.size()) {
            return "All available weapons bought.";
        }
        if (player.getPoints() >= listOfAvailableWeapons.get(index).getPrice()) {
            player.deductPoints(listOfAvailableWeapons.get(index).getPrice());
            player.addWeapon(listOfAvailableWeapons.get(index));
            index ++;
            return "You bought a " + listOfAvailableWeapons.get(index - 1).getName() + " .";
        } else {
            return "You do not have enough points";
        }
    }

    public String displayMessage() {
        if (index >= listOfAvailableWeapons.size()) {
            return "All available weapons bought.";
        } else {
            return "Press B to buy a " + listOfAvailableWeapons.get(index).getName() + " for " + listOfAvailableWeapons.get(index).getPrice();
        }
    }

}
