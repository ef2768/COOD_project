package OpenDataPhilly.data;

import OpenDataPhilly.common.Property;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyReader {

    private static final PropertyReader INSTANCE = new PropertyReader();

    private PropertyReader() {}

    public static PropertyReader getInstance() {
        return INSTANCE;
    }

    public List<Property> read(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path must not be null or blank.");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Property file does not exist: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("Property file is not readable: " + filePath);
        }

        List<Property> properties = new ArrayList<>();
        BufferedReader b = new BufferedReader(new FileReader(file));

        String header = b.readLine();
        if (header == null) {
            b.close();
            throw new IOException("CSV file is empty.");
        }

        String[] fields = header.split(",");
        int zip_index = -1;
        int val_index = -1;
        int area_index = -1;

        for (int i = 0; i < fields.length; i++) {
            String f = fields[i].trim().toLowerCase();
            if (f.equals("zip_code")) zip_index = i;
            else if (f.equals("market_value")) val_index = i;
            else if (f.equals("total_livable_area")) area_index = i;
        }

        if (zip_index == -1 || val_index == -1 || area_index == -1) {
            b.close();
            throw new IOException("CSV header missing required fields.");
        }

        String row;
        while ((row = b.readLine()) != null) {
            String[] parts = row.split(",");
            if (parts.length < fields.length) continue;

            String zip = normalizeZip(parts[zip_index].trim());
            double mv = strToDouble(parts[val_index].trim());
            double area = strToDouble(parts[area_index].trim());

            if (!zip.isEmpty()) {
                properties.add(new Property(zip, mv, area));
            }
        }

        b.close();
        return properties;
    }

    private String normalizeZip(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        return raw.length() >= 5 ? raw.substring(0, 5) : raw;
    }

    private double strToDouble(String str) {
        if (str == null || str.isBlank()) return 0;
        try {
            double d = Double.parseDouble(str.trim());
            return Math.max(d, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}