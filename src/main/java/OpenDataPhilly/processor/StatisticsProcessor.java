package OpenDataPhilly.processor;

import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.common.Property;

import java.util.*;

public class StatisticsProcessor {

    private final Map<String, Integer> populationByZip = new HashMap<>();
    private final Map<String, List<Property>> propertiesByZip = new HashMap<>();
    private final List<ParkingViolation> violations;

    private final Map<String, Integer> avgMarketValueCache = new HashMap<>();
    private final Map<String, Integer> avgAreaCache = new HashMap<>();

    public StatisticsProcessor(List<Population> populations,
                               List<Property> properties,
                               List<ParkingViolation> violations) {
        for (Population p : populations) {
            populationByZip.put(p.getZipCode(), p.getPopulation());
        }

        for (Property prop : properties) {
            propertiesByZip
                    .computeIfAbsent(prop.getzipCode(), z -> new ArrayList<>())
                    .add(prop);
        }

        this.violations = violations;
    }

    public long getTotalPopulation() {
        long sum = 0;
        for (int value : populationByZip.values()) {
            sum += value;
        }
        return sum;
    }

    public Map<String, Double> getFinesPerCapitaByZip() {
        Map<String, Double> totalFinesByZip = new HashMap<>();

        for (ParkingViolation v : violations) {
            String zip = normalizeZip(v.getZipCode());
            if (zip.isEmpty()) continue;
            if (!"PA".equals(v.getState())) continue;

            totalFinesByZip.merge(zip, v.getFine(), Double::sum);
        }

        Map<String, Double> result = new TreeMap<>();
        for (Map.Entry<String, Double> e : totalFinesByZip.entrySet()) {
            String zip = e.getKey();
            double totalFines = e.getValue();
            Integer pop = populationByZip.get(zip);

            if (pop == null || pop == 0) continue;
            if (totalFines == 0.0) continue;

            double perCapita = totalFines / pop;
            result.put(zip, perCapita);
        }

        return result;
    }

    public double getAverageMarketValue(String zip) {
        zip = normalizeZip(zip);

        if (avgMarketValueCache.containsKey(zip)) {
            return avgMarketValueCache.get(zip);
        }

        List<Property> props = propertiesByZip.get(zip);
        if (props == null || props.isEmpty()) {
            avgMarketValueCache.put(zip, 0);
            return 0;
        }

        long sum = 0;
        int count = 0;
        for (Property p : props) {
            double mv = p.getMarketValue();
            if (mv > 0) {
                sum += mv;
                count++;
            }
        }

        int result = (count == 0) ? 0 : (int) Math.round((double) sum / count);
        avgMarketValueCache.put(zip, result);
        return result;
    }

    public int getAverageTotalLivableArea(String zip) {
        zip = normalizeZip(zip);

        if (avgAreaCache.containsKey(zip)) {
            return avgAreaCache.get(zip);
        }

        List<Property> props = propertiesByZip.get(zip);
        if (props == null || props.isEmpty()) {
            avgAreaCache.put(zip, 0);
            return 0;
        }

        long sum = 0;
        int count = 0;
        for (Property p : props) {
            double area = p.getTotalLivableArea();
            if (area > 0) {
                sum += area;
                count++;
            }
        }

        int result = (count == 0) ? 0 : (int) Math.round((double) sum / count);
        avgAreaCache.put(zip, result);
        return result;
    }

    public double getResidentialMarketValuePerCapita(String zip) {
        zip = normalizeZip(zip);

        List<Property> props = propertiesByZip.get(zip);
        Integer pop = populationByZip.get(zip);

        if (props == null || props.isEmpty() || pop == null || pop == 0) {
            return 0;
        }

        long sumMarket = 0;
        for (Property p : props) {
            double mv = p.getMarketValue();
            if (mv > 0) {
                sumMarket += mv;
            }
        }

        if (sumMarket == 0) return 0;

        return (int) Math.round((double) sumMarket / pop);
    }

    private String normalizeZip(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.length() >= 5) return raw.substring(0, 5);
        return raw;
    }
}