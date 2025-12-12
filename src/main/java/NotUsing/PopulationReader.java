package NotUsing;

import OpenDataPhilly.common.*;

import java.io.*;
import java.util.*;

public class PopulationReader {
    public List<Population> read(String filePath) throws IOException {  //this reads the file, given the filepath
        List<Population> population = new ArrayList<>();

        //check filepath
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path must not be null, or empty.");

        }

        //file exists and is readable
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist.");
        }
        if (!file.canRead()) {
            throw new IOException("File is not readable.");
        }

        //open up the file reader
        BufferedReader b = new BufferedReader(new FileReader(file));

        String row;
        while ((row = b.readLine()) != null) {
            String[] items = row.trim().split("\\s+"); //we are splitting by white space
            if (items.length < 2) {
                continue; //skip lines that are invalid length
            }

            String zipCode = items[0].trim();
            int populationVal = strToInt(items[1]);

            if (!zipCode.isEmpty() && populationVal > 0) { //if valid values for the items
                population.add(new Population(zipCode, populationVal));
            }
        }
        return population;
    }

    private int strToInt(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0;
        }
        try {
            int num = Integer.parseInt(str.trim());
            return Math.max(num, 0); //if the parsed int is positive
        } catch (NumberFormatException e) {
            return 0; // Ignore invalid numeric values
        }
    }
}
