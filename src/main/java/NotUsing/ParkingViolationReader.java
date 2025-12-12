package NotUsing;
import OpenDataPhilly.common.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
import java.util.*;
public class ParkingViolationReader {
    public List<ParkingViolation> read(String fileType, String filePath) throws IOException {
        if (!"csv".equals(fileType) && !"json".equals(fileType)) {
            throw new IllegalArgumentException("Invalid file type (isn't csv or JSON).");
        } //check for good file type

        //file exists and is readable
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist.");
        }
        if (!file.canRead()) {
            throw new IOException("File is not readable.");
        }

        if ("csv".equals(fileType)) {
            return CSVreader(file);
        } else {
            return JSONreader(file);
        }

    }

    private List<ParkingViolation> CSVreader(File file) throws IOException {
        List<ParkingViolation> violations = new ArrayList<>();

        //open up the file reader
        BufferedReader b = new BufferedReader(new FileReader(file));

        String row;
        while ((row = b.readLine()) != null) {
            String[] items = row.trim().split(","); //we are splitting by comma
            if (items.length < 7) {
                continue; //skip lines that are invalid number of items
            }

            String timestamp = items[0].trim();
            double fine = strToDouble(items[1].trim()); // fines must be >= 0
            String description = items[2].trim();
            String vehicleID = items[3].trim();
            String state = items[4].trim();
            String violationID = items[5].trim();
            String zipCode = items[6].trim();

            if (zipCode.length() > 5) zipCode = zipCode.substring(0, 5);
            violations.add(new ParkingViolation(timestamp, fine, description, vehicleID, state, violationID, zipCode));

        }

        b.close();
        return violations;

    }

    private List<ParkingViolation> JSONreader(File file) throws IOException {
        List<ParkingViolation> violations = new ArrayList<>();
        // JSON.simple parser object
        JSONParser p = new JSONParser();

        try (FileReader f = new FileReader(file)) {
            Object obj = p.parse(f);
            if (!(obj instanceof JSONArray)) throw new IOException("Expected JSON Array");


            // Cast the Object to JSONArray since we know it's an array
            JSONArray array = (JSONArray) obj;
            for (Object o : array) {
                if (!(o instanceof JSONObject)) continue;
                JSONObject jsonObj = (JSONObject) o;

                // Extract items using helper methods
                String timestamp = getString(jsonObj, "timestamp");
                double fine = getDouble(jsonObj, "fine");
                String description = getString(jsonObj, "description");
                String vehicleID = getString(jsonObj, "vehicle_id");
                String state = getString(jsonObj, "state");
                String violationID = getString(jsonObj, "violation_id");
                String zipCode = getString(jsonObj, "zip_code");

                if (zipCode.length() > 5) zipCode = zipCode.substring(0, 5);

                violations.add(new ParkingViolation(timestamp, fine, description, vehicleID, state, violationID, zipCode));
            }
        } catch (org.json.simple.parser.ParseException e) {
            throw new IOException("Failed to parse JSON file: " + e.getMessage());
        }

        return violations;
    }

    private String getString(JSONObject obj, String key) {
        Object val = obj.get(key);
        if (val == null) {
            return "";
        } else {
            return val.toString().trim();
        }
    }

    private double getDouble(JSONObject obj, String key) {
        Object val = obj.get(key);
        if (val == null) {
            return 0;
        }
        try {
            double d = Double.parseDouble(val.toString().trim());
            return d >= 0 ? d : 0; //if d is pos, then return d, if not then 0
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    //helper method turning the raw strings into ints
    private double strToDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.;
        }
        try {
            double num = Double.parseDouble(str.trim());
            return Math.max(num, 0.); //if the parsed int is positive
        } catch (NumberFormatException e) {
            return 0.; // Non-numeric values are treated as 0!!!
        }
    }
}
