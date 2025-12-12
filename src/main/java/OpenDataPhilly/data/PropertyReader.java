package OpenDataPhilly.data;

import OpenDataPhilly.common.*;

import java.io.*;
import java.util.*;

/**
 * this needs to read the CSV, and also dynamically identify what the columns are
 * it also needs to validate the data (defensive programming)
 */

public class PropertyReader {
    public List<Property> read(String filePath) throws IOException {
        List<Property> properties = new ArrayList<>(); //List to hold the valid properties

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

        //we read the header row to dynamically assign
        String header = b.readLine();
        if(header == null) {
            b.close();
            throw new IOException("CSV file empty.");

        }

        //splitstring so we get the fields
        String[] fields = header.split(",");
        int zip_index = -1;
        int val_index = -1;
        int area_index = -1;

        //loop through the fields to find match
        for (int i=0; i<fields.length; i++) {
            String field = fields[i].trim().toLowerCase();
            if (field.equals("total_livable_area")) {
                area_index = i;
            } else if (field.equals("market_value")){
                val_index = i;
            } else if (field.equals("zip_code")){
                zip_index = i;
            }
        }

        //check that all the field entries we want are actually in the header
        if (zip_index == -1 || val_index == -1 || area_index == -1) {
            b.close();
            throw new IOException("CSV header missing required field(s)");
        }

        String row;
        //now read row by row
        while ((row = b.readLine()) != null) {
            String[] items = row.split(",");
            if (items.length <= fields.length) {
                continue; //this is when the row entry doesnt have enough items
            }
            //zipCode
            String zipCode = fields[zip_index].trim();
            if (zipCode.length() > 5) {
                zipCode = zipCode.substring(0, 5); //only take first five digits
            }
            //market value
            String marketValueRaw = fields[val_index].trim();
            int marketValue = strToInt(marketValueRaw); //use helper function to handle invalid numeric fields
            // "your program should ignore market values and total livable areas that are missing, non-numeric, negative, or zero, but should include “good” data even when the rest of the data for that property is bad."
            // --> in the data tier, we just have to make sure we are not counting that row out
            // Simply register the invalid entry as 0. processor tier will deal with the rest

            //Liveable area
            String totLivableAreaRaw = fields[area_index].trim();
            int totLivableArea = strToInt(totLivableAreaRaw);

            //if all the items turn out to be non-trivial values, we add them as a valid property
            if (!zipCode.isEmpty()) {
                properties.add(new Property(zipCode, marketValue, totLivableArea));
            }
        }

        b.close();
        return properties;

    }

    //helper method turning the raw strings into ints
    private int strToInt(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0;
        }
        try {
            int num = Integer.parseInt(str.trim());
            return Math.max(num, 0); //if the parsed int is positive
        } catch (NumberFormatException e) {
            return 0; // Non-numeric values are treated as 0!!! E.g. total livable area = "dog"
        }
    }
}
