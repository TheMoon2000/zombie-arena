package byow.gameplay;

import byow.Core.Engine;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.utils.InputHistory;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class GameEndingMenu {

    private static final Color BGCOLOR = new Color(26, 26, 29);
    private static final Font CELL_FONT = new Font("Monaco", Font.PLAIN, 15);
    private static final double CELL_HEIGHT = 1.8;
    private static String[] rows = {"Restart Map (1)", "Replay (2)", "Back to Menu (3)"};
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);

    private String titleText;
    private boolean keyboard;
    private TERenderer renderer;

    public static boolean reset = false;
    public static boolean m = false;
    public static boolean replay = true;

    GameEndingMenu(Player player, String myTitle, boolean keyboardInput) {
        renderer = player.ter;
        titleText = myTitle;
        keyboard = keyboardInput;
    }

    void open(InputSource source) {
        System.out.print("User opened end of game menu, title='" + titleText + "'");
        renderMenu(renderer);
        while (source.possibleNextInput()) {
            renderMenu(renderer);
            char next = source.getNextKey();
            InputHistory.addInputChar(next);
            switch (next) {
                case ':': // if :Q then save and quit
                    if (source.getNextKey() == 'Q') {
                        InputHistory.save(); System.exit(0);
                    }
                    break;
                case '1':
                    // Restart the world
                    String newWorld = "N" + Engine.seed + "S";
                    InputHistory.createNewFile(newWorld);
                    reset = true;
                    return;
                case '2':
                    replay = true;
                    InputHistory.save();
                    return;
                case '3':
                    InputHistory.createNewFile();
                    reset = true;
                    m = true;
                    return;
                default:
            }
        }
    }

    private void renderMenu(TERenderer ter) {

        if (!keyboard) {
            return;
        }

        double centerX = ((double) ter.getWidth()) / 2.0;
        double centerY = ((double) ter.getHeight()) / 2.0;

        StdDraw.clear(BGCOLOR);
        StdDraw.setPenColor(Color.darkGray);
        StdDraw.setPenRadius(0.005);
        StdDraw.rectangle(centerX, centerY, 18, 15);

        Font title = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.text(centerX, centerY + 12, titleText);

        double currentY = centerY + 6;
        StdDraw.setFont(CELL_FONT);

        for (int i = 0; i < rows.length; i++) {

            // Draw the cell rectangle
            StdDraw.setPenColor(new Color(41, 42, 43));
            StdDraw.filledRectangle(centerX, currentY, 18, CELL_HEIGHT);

            // Draw the text
            StdDraw.setPenColor(CELL_TEXT_COLOR);
            StdDraw.text(centerX, currentY - 0.02, rows[i]);

            currentY -= CELL_HEIGHT * 2 + 0.2;
        }

        StdDraw.show();
    }
}
