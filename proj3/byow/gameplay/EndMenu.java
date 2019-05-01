package byow.gameplay;

import byow.Core.Engine;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.utils.InputHistory;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;

public class EndMenu {

    private static final Color BGCOLOR = new Color(26, 26, 29);
    private static final Font CELL_FONT = new Font("Monaco", Font.PLAIN, 15);
    private static final double CELL_HEIGHT = 1.8;
    private static String[] rows = {"Restart Map (1)", "Replay (2)", "Back to Menu (3)"};
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);

    private String titleText;
    private boolean keyboard;
    private TERenderer renderer;

    private static boolean reset = false;
    private static boolean replay = false;
    private Engine engine;

    public EndMenu(Player player, String myTitle) {
        renderer = player.engine.getTer();
        titleText = myTitle;
        keyboard = player.engine.isKbInput();
        this.engine = player.engine;
        engine.save();
        InputHistory.save();
        InputHistory.clear();
    }

    public void open(InputSource source) {
        renderMenu(renderer);
        while (source.possibleNextInput()) {
            renderMenu(renderer);
            char next = source.getNextKey();
            InputHistory.addInputChar(next);
            System.out.println(next);
            switch (next) {
                case ':': // if :Q then save and quit
                    if (source.getNextKey() == 'Q') {
                        InputHistory.save(); System.exit(0);
                    }
                    break;
                case '1':
                    // Restart the world
                    engine.startNewWorld(source);
                    InputHistory.createNewFile();
                    return;
                case '2':
                    if (keyboard) {
                        replay = true;
                        StdDraw.clear(StdDraw.BLACK);
                        StdDraw.show();
                    }
                    InputHistory.setReloaded(false);
                    return;
                case '3':
                    InputHistory.createNewFile();
                    engine.setBackToMenu();
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

    public static boolean replay() {
        return replay;
    }

    public static boolean resetReplay() {
        boolean tmp = replay;
        replay = false;
        return tmp;
    }

    public static boolean resetReset() {
        boolean tmp = reset;
        reset = false;
        return tmp;
    }

    public static boolean reset() {
        return reset;
    }
}
