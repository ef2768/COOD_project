package OpenDataPhilly.data;

import OpenDataPhilly.common.Property;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PropertyLoader {

    public List<Property> load(String filename) throws IOException {
        List<Property> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine();
            if (header == null) return result;

            String[] labels = header.split(",", -1);
            int zipIdx = -1, marketIdx = -1, areaIdx = -1;

            for (int i = 0; i < labels.length; i++) {
                String lbl = labels[i].trim();
                if ("zip_code".equals(lbl)) zipIdx = i;
                else if ("market_value".equals(lbl)) marketIdx = i;
                else if ("total_livable_area".equals(lbl)) areaIdx = i;
            }

            if (zipIdx == -1 || marketIdx == -1 || areaIdx == -1) {
                throw new IOException("Header row missing required fields");
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length <= Math.max(zipIdx, Math.max(marketIdx, areaIdx))) {
                    continue;
                }

                String zip = normalizeZip(parts[zipIdx]);

                int marketValue = parsePositiveInt(parts[marketIdx]);
                int area = parsePositiveInt(parts[areaIdx]);

                Property p = new Property(zip, marketValue, area);
                result.add(p);
            }
        }

        return result;
    }

    private int parsePositiveInt(String s) {
        try {
            int v = Integer.parseInt(s.trim());
            if (v <= 0) return -1;
            return v;
        } catch (Exception e) {
            return -1;
        }
    }

    private String normalizeZip(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.length() >= 5) return raw.substring(0, 5);
        return raw;
    }
}