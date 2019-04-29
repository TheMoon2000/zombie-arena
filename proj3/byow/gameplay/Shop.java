package byow.gameplay;

import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.utils.InputHistory;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class Shop {

    private static final ShopItem[] UPGRADES_LIST = new ShopItem[] {
        Health.of(Player.MAX_HEALTH),
        new Ammo(),
        Weapon.makeSword(),
        Weapon.makePistol(),
        Weapon.makeShotgun(),
        Weapon.makeMachineGun(),
        new RandomWeapon()
    };

    private static final Color BGCOLOR = new Color(26, 26, 29);
    private static final Font CELL_FONT = new Font("Monaco", Font.PLAIN, 15);
    private static final Font CAPTION_FONT = new Font("Monaco", Font.PLAIN, 11);
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color CAPTION_COLOR = new Color(190, 190, 190);

    public static String openMenu(Player player, InputSource source, boolean kb, Random r) {

        int selection = -1;
        ((RandomWeapon) UPGRADES_LIST[UPGRADES_LIST.length - 1]).random = r;

        Wave.update(player.location, true, true);

        while (source.possibleNextInput()) {

            renderMenu(selection, player.ter, kb);
            char next = source.getNextKey();
            InputHistory.addInputChar(next);

            switch (next) {
                case ':': // if :Q then save and quit
                    if (source.getNextKey() == 'Q') {
                        InputHistory.save(); return null;
                    }
                    break;
                case ' ':
                    selection = (selection + 1) % UPGRADES_LIST.length;
                    renderMenu(selection, player.ter, kb);
                    break;
                case 'P':
                    if (selection == -1) {
                        break;
                    } else {
                        String returnValue = UPGRADES_LIST[selection].apply(player);
                        if (kb) {
                            player.ter.renderFrame(player.tiles);
                        }
                        return returnValue;
                    }
                case 'B':
                    if (kb) {
                        player.ter.renderFrame(player.tiles);
                    }
                    return "You did not buy anything :("; // Exit the shop
                default:
            }
        }

        return "Source interrupted";
    }

    /**
     * Specifies what the player sees when approaching a shop
     * @return The description string that will be displayed in the menu bar
     */

    public static String displayMessage() {
        return "Press B to open shop menu.";
    }

    private static void renderMenu(int selection, TERenderer ter, boolean keyboard) {

        if (!keyboard) {
            return;
        }

        double centerX = ((double) ter.getWidth()) / 2.0;
        double centerY = ((double) ter.getHeight()) / 2.0;

        StdDraw.clear(BGCOLOR);
        StdDraw.setPenColor(Color.darkGray);
        StdDraw.setPenRadius(0.005);
        StdDraw.rectangle(centerX, centerY, 21, 18);

        Font title = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.text(centerX, centerY + 14, "Upgrades");

        double currentY = centerY + 10;
        StdDraw.setFont(CELL_FONT);

        for (int i = 0; i < UPGRADES_LIST.length; i++) {

            // Draw the cell rectangle
            StdDraw.setPenColor(new Color(41, 42, 43));
            if (i == selection) {
                StdDraw.setPenColor(new Color(50, 50, 52));
            }
            StdDraw.filledRectangle(centerX, currentY, 19, 1.6);

            // Draw the text
            StdDraw.setPenColor(CELL_TEXT_COLOR);
            StdDraw.textLeft(centerX - 18, currentY - 0.02, UPGRADES_LIST[i].getName());
            StdDraw.textRight(centerX + 18, currentY - 0.02, "" + UPGRADES_LIST[i].getPrice());

            currentY -= 3.5;
        }

        // Draw the caption
        StdDraw.setFont(CAPTION_FONT);
        StdDraw.setPenColor(CAPTION_COLOR);
        StdDraw.text(centerX, centerY - 16.9,
                "Press 'P' to purchase, SPACE to select, 'B' to exit shop.");

        StdDraw.show();
    }
}
