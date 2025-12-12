package OpenDataPhilly.processor;

import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.common.Property;

import java.util.*;

public class StatisticsProcessor {

    private final Map<String, Integer> populationByZip = new HashMap<>();
    private final Map<String, List<Property>> propertiesByZip = new HashMap<>();
    private final List<ParkingViolation> violations;

    private final Map<String, Integer> avgMVCache = new HashMap<>();
    private final Map<String, Integer> avgAreaCache = new HashMap<>();

    public StatisticsProcessor(List<Population> populations,
                               List<Property> properties,
                               List<ParkingViolation> violations) {

        if (populations == null || properties == null || violations == null) {
            throw new IllegalArgumentException("Null list passed to StatisticsProcessor");
        }

        for (Population p : populations) {
            populationByZip.put(p.getZipCode(), p.getPopulation());
        }

        for (Property prop : properties) {
            propertiesByZip.computeIfAbsent(prop.getzipCode(), z -> new ArrayList<>()).add(prop);
        }

        this.violations = violations;
    }

    public Iterator<Map.Entry<String, Integer>> populationIterator() {
        return new PopulationIterator();
    }

    private class PopulationIterator implements Iterator<Map.Entry<String, Integer>> {
        private final Iterator<Map.Entry<String, Integer>> it = populationByZip.entrySet().iterator();
        @Override public boolean hasNext() { return it.hasNext(); }
        @Override public Map.Entry<String, Integer> next() { return it.next(); }
    }

    public double getAverageMarketValue(String zip) {
        zip = normalizeZip(zip);

        if (avgMVCache.containsKey(zip)) {
            return avgMVCache.get(zip);
        }

        List<Property> props = propertiesByZip.get(zip);
        if (props == null) {
            avgMVCache.put(zip, 0);
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

        int avg = (count == 0) ? 0 : (int) Math.round((double) sum / count);
        avgMVCache.put(zip, avg);
        return avg;
    }

    public int getAverageTotalLivableArea(String zip) {
        zip = normalizeZip(zip);

        if (avgAreaCache.containsKey(zip)) {
            return avgAreaCache.get(zip);
        }

        List<Property> props = propertiesByZip.get(zip);
        if (props == null) {
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

        int avg = (count == 0) ? 0 : (int) Math.round((double) sum / count);
        avgAreaCache.put(zip, avg);
        return avg;
    }

    public long getTotalPopulation() {
        long sum = 0;
        Iterator<Map.Entry<String, Integer>> it = populationIterator();
        while (it.hasNext()) {
            sum += it.next().getValue();
        }
        return sum;
    }

    public Map<String, Double> getFinesPerCapitaByZip() {
        Map<String, Double> totalFines = new HashMap<>();

        for (ParkingViolation v : violations) {
            String zip = normalizeZip(v.getZipCode());
            if (zip.isEmpty()) continue;
            if (!"PA".equals(v.getState())) continue;

            totalFines.merge(zip, v.getFine(), Double::sum); // lambda feature
        }

        Map<String, Double> result = new TreeMap<>();
        for (String zip : totalFines.keySet()) {
            Integer pop = populationByZip.get(zip);
            if (pop == null || pop == 0) continue;

            double perCapita = totalFines.get(zip) / pop;
            if (perCapita > 0) result.put(zip, perCapita);
        }

        return result;
    }

    public double getResidentialMarketValuePerCapita(String zip) {
        zip = normalizeZip(zip);

        List<Property> props = propertiesByZip.get(zip);
        Integer pop = populationByZip.get(zip);

        if (props == null || pop == null || pop == 0) return 0;

        long sumMarket = 0;
        for (Property p : props) {
            if (p.getMarketValue() > 0) {
                sumMarket += p.getMarketValue();
            }
        }

        return (sumMarket == 0) ? 0 : (int) Math.round((double) sumMarket / pop);
    }

    private String normalizeZip(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        return (raw.length() >= 5) ? raw.substring(0, 5) : raw;
    }
}