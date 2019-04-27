package byow.gameplay;

import byow.Core.Engine;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.utils.InputHistory;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

class GameEndingMenu {

    private static final Color BGCOLOR = new Color(26, 26, 29);
    private static final Font CELL_FONT = new Font("Monaco", Font.PLAIN, 15);
    private static final Font CAPTION_FONT = new Font("Monaco", Font.PLAIN, 11);
    private static final double CELL_HEIGHT = 1.8;
    private static String[] rows = {"Replay Map (1)", "Back to Menu (2)"};
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color CAPTION_COLOR = new Color(190, 190, 190);

    private String titleText;

    private TERenderer renderer;

    GameEndingMenu(Player player, String myTitle) {
        renderer = player.ter;
        titleText = myTitle;
    }

    void open(InputSource source) {
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
                    break;
                case '2':
                    Engine engine = new Engine();
                    engine.interactWithKeyboard();
                default:
            }
        }
    }

    private void renderMenu(TERenderer ter) {

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

        double currentY = centerY + 8;
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
