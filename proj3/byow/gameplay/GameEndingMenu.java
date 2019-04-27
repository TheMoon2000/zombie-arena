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
    private static final Font CAPTION_FONT = new Font("Monaco", Font.PLAIN, 11);
    private static final double CELL_HEIGHT = 1.8;
    private static String[] rows = {"Replay Map (1)", "Back to Menu (2)"};
    private static final Color CELL_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color CAPTION_COLOR = new Color(190, 190, 190);

    private String titleText;

    private TERenderer renderer;

    public static boolean reset = false;

    GameEndingMenu(Player player, String myTitle) {
        renderer = player.ter;
        titleText = myTitle;
    }

    void open(InputSource source) {
        renderMenu(renderer);
        boolean replay = false;
        while (source.possibleNextInput() && !replay) {
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
                    String newWorld = "" + "N" + Engine.seed + "SL";
                    try {
                        //create an empty new file that replaces the old one
                        new PrintWriter("SaveFile.txt", StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        System.out.println("Unable to create SaveFile.txt!");
                    }
                    try {
                        FileWriter fw = new FileWriter("SaveFile.txt");
                        fw.write(newWorld);
                        fw.close();
                    } catch (IOException e) {
                        System.out.println("Unable to write to disk");
                    }
                    replay = true;
                    reset = true;
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
