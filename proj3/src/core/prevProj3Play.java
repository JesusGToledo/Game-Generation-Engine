package core;

import java.io.*;
import java.util.Random;

public class prevProj3Play {

    public static void main(String[] args) {
        System.out.println("prevProj3Play main method ran");
    }
    // method to write the contents of the seed StringBuilder to a text file
    public void writeSeedToFile(StringBuilder seed) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("seedFile.txt"))) {
            writer.write(seed.toString());
            System.out.println("Seed written to file: seedFile.txt");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
    }

    public String readSeedFromFile() {
        File file = new File("seedFile.txt");
        if (!file.exists()) {
            return "No previous save file";
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("seedFile.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return content.toString();
    }
}