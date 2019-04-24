package byow.utils;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import edu.princeton.cs.introcs.In;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class InputHistory {

    private static StringBuilder input = new StringBuilder();
    public static boolean isReloading = false;

    public static void addInputChar(char c) {
        if (c != ':') {
            input.append(Character.toUpperCase(c));
        }
    }

    public static void save() {
        try {
            FileWriter fw = new FileWriter("SaveFile.txt",false);
            fw.write(input.toString() + "L");//appends the string to the file
            fw.close();
        } catch (IOException e) {
            System.out.println("Unable to write to disk");
        }
    }

    /**
     * Helper method that creates a new save file
     */
    public static void createNewFile() {
        if (isReloading) {
            return;
        }

        try {
            //create an empty new file that replaces the old one
            new PrintWriter("SaveFile.txt", StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Unable to create SaveFile.txt!");
        }
    }

    public static boolean hasValidInput() {
        In input = new In("SaveFile.txt");
        String all = input.readAll();
        return !all.isEmpty() && all.charAt(all.length() - 1) == 'L';
    }

    /**
     * Helper method that reads input string from file
     */
    public static InputSource source() {
        StringBuilder fileInput = new StringBuilder();
        In input = new In("SaveFile.txt");
        while (input.hasNextChar()) {
            fileInput.append(input.readChar());
        }
        System.out.println("\nRead the following string from SaveFile.txt:");
        System.out.println(fileInput.toString());
        return new StringInputDevice(fileInput.toString());
    }


}
