package OpenDataPhilly.data;

import OpenDataPhilly.common.ParkingViolation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingViolationLoader {

    public List<ParkingViolation> load(String format, String filename) throws IOException {
        if ("csv".equals(format)) {
            return loadFromCsv(filename);
        } else if ("json".equals(format)) {
            try {
                return loadFromJson(filename);
            } catch (ParseException e) {
                throw new IOException("Error parsing JSON file: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Format must be 'csv' or 'json'");
        }
    }

    private List<ParkingViolation> loadFromCsv(String filename) throws IOException {
        List<ParkingViolation> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                String timestamp = parts[0].trim();
                double fine;
                try {
                    fine = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String description = parts[2].trim();
                String vehicleId = parts[3].trim();
                String state = parts[4].trim();
                String violationId = parts[5].trim();
                String zip = normalizeZip(parts[6].trim());

                ParkingViolation v = new ParkingViolation(
                        timestamp, fine, description, vehicleId, state, violationId, zip
                );
                result.add(v);
            }
        }

        return result;
    }

    private List<ParkingViolation> loadFromJson(String filename) throws IOException, ParseException {
        List<ParkingViolation> result = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filename)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;

                String timestamp = (String) json.get("timestamp");
                double fine;
                try {
                    fine = Double.parseDouble(json.get("fine").toString());
                } catch (NumberFormatException | NullPointerException e) {
                    continue;
                }
                String description = (String) json.get("description");
                String vehicleId = (String) json.get("vehicleID");
                String state = (String) json.get("state");
                String violationId = (String) json.get("violationID");
                String zip = json.get("zipCode") == null ? "" : normalizeZip(json.get("zipCode").toString());

                ParkingViolation v = new ParkingViolation(
                        timestamp, fine, description, vehicleId, state, violationId, zip
                );
                result.add(v);
            }
        }

        return result;
    }

    private String normalizeZip(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.length() >= 5) return raw.substring(0, 5);
        return raw;
    }
}