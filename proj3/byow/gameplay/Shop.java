package byow.gameplay;

import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

public class Shop {

    private static final ShopItem[] UPGRADES_LIST = new ShopItem[] {
        Health.of(Player.MAX_HEALTH),
        new Ammo(),
        Weapon.makeSword(),
        Weapon.makePistol(),
        Weapon.makeShotgun(),
        Weapon.makeSniperRifle()
    };

    private static final Color BGCOLOR = new Color(26, 26, 29);
    private static final Font CELL_FONT = new Font("Monaco", Font.PLAIN, 15);
    private static final Font CAPTION_FONT = new Font("Monaco", Font.PLAIN, 11);
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color CAPTION_COLOR = new Color(190, 190, 190);

    public static String openMenu(Player player, TERenderer ter, InputSource source, boolean kb) {

        int selection = -1;
        Wave.update();

        while (source.possibleNextInput()) {

            renderMenu(selection, ter, kb);
            char next = source.getNextKey();

            switch (next) {
                case ' ':
                    selection = (selection + 1) % UPGRADES_LIST.length;
                    renderMenu(selection, ter, kb);
                    break;
                case 'P':
                    if (selection == -1) {
                        break;
                    } else {
                        return UPGRADES_LIST[selection].apply(player);
                    }
                case 'B':
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
        StdDraw.rectangle(centerX, centerY, 20, 16);

        Font title = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.text(centerX, centerY + 12.5, "Upgrades");

        double currentY = centerY + 8.5;
        StdDraw.setFont(CELL_FONT);

        for (int i = 0; i < UPGRADES_LIST.length; i++) {

            // Draw the cell rectangle
            StdDraw.setPenColor(new Color(41, 42, 43));
            if (i == selection) {
                StdDraw.setPenColor(new Color(50, 50, 52));
            }
            StdDraw.filledRectangle(centerX, currentY, 19, 1.8);

            // Draw the text
            StdDraw.setPenColor(CELL_TEXT_COLOR);
            StdDraw.textLeft(centerX - 18, currentY - 0.02, UPGRADES_LIST[i].getName());
            StdDraw.textRight(centerX + 18, currentY - 0.02, "" + UPGRADES_LIST[i].getPrice());

            currentY -= 4;
        }

        // Draw the caption
        StdDraw.setFont(CAPTION_FONT);
        StdDraw.setPenColor(CAPTION_COLOR);
        StdDraw.text(centerX, centerY - 15,
                "Press 'P' to purchase, SPACE to select, 'B' to exit shop.");

        StdDraw.show();
    }


    /**
     * Pause for a moment
     * @param n (in milliseconds) the time to wait
     */
    private static void sleep(int n) {
        try {
            TimeUnit.MILLISECONDS.sleep(n);
        } catch (InterruptedException e) {
            System.out.print("\ndelay failed");
        }
    }
}
