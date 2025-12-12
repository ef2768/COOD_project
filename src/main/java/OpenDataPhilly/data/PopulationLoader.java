package OpenDataPhilly.data;

import OpenDataPhilly.common.Population;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopulationLoader {

    public List<Population> load(String filename) throws IOException {
        List<Population> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                String zip = normalizeZip(parts[0]);
                int pop;
                try {
                    pop = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    continue;
                }

                result.add(new Population(zip, pop));
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