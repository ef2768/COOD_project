import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.processor.StatisticsProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class SPFinesPerCapitaTest {
    @Test
    void testFinesPerCapita() {
        List<Population> populations = List.of(new Population("12345", 50));
        List<ParkingViolation> violations = List.of(
                new ParkingViolation("2025-01-01T12:00:00Z", 50, "EXPIRED", "V1", "PA", "1", "12345"),
                new ParkingViolation("2025-01-02T12:00:00Z", 50, "OVER TIME LIMIT", "V2", "PA", "2", "12345")
        );

        StatisticsProcessor processor = new StatisticsProcessor(populations, List.of(), violations);
        Map<String, Double> fines = processor.getFinesPerCapitaByZip();

        assertEquals(1, fines.size());
        assertEquals(2.0, fines.get("12345")); //(50+50)/50
    }

    @Test
    void testZeroPopulationOnly() {
        //legit zip, population = 0
        List<Population> populations = List.of(
                new Population("12345", 0)
        );
        //Legitimate PA violation
        List<ParkingViolation> violations = List.of(
                new ParkingViolation("2025-01-01T12:00:00Z", 50, "EXPIRED", "V1", "PA", "1", "12345")
        );

        StatisticsProcessor processor = new StatisticsProcessor(populations, List.of(), violations);
        Map<String, Double> result = processor.getFinesPerCapitaByZip();
        // Because population = 0, ZIP must be omitted
        assertTrue(result.isEmpty(), "ZIP with population 0 should not appear in results");
    }

    @Test
    void testViolationFromOtherStateIgnored() {
        List<Population> populations = List.of(
                new Population("12345", 100)
        );

        // Violation from NJ!
        List<ParkingViolation> violations = List.of(
                new ParkingViolation("2025-01-01T12:00:00Z", 100, "EXPIRED", "V2", "NJ","1", "12345")
        );

        StatisticsProcessor processor = new StatisticsProcessor(populations, List.of(), violations);
        Map<String, Double> result = processor.getFinesPerCapitaByZip();
        // Because no PA (!!!) violations exist, zip doesnt appear
        assertTrue(result.isEmpty(), "Violations from non-PA states must be ignored");
    }

    @Test
    void testFinesPerCapitaEmpty() {
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), List.of(), List.of());
        assertTrue(processor.getFinesPerCapitaByZip().isEmpty());
    }

    //additional test to trigger a short zip code return
    @Test
    void testSub5NonNullZipCode() {
        //zip < 5 chars triggers return raw branch
        String shortZip = "123";

        List<Population> populations = List.of(new Population(shortZip, 100));
        List<ParkingViolation> violations = List.of(
                new ParkingViolation("2025-01-01T12:00:00Z", 50, "EXPIRED", "V1", "PA", "1", shortZip)
        );

        StatisticsProcessor processor = new StatisticsProcessor(populations, List.of(), violations);
        Map<String, Double> fines = processor.getFinesPerCapitaByZip();
        assertTrue(fines.containsKey(shortZip)); //make sure its loaded
        assertEquals(50.0 / 100, fines.get(shortZip)); //make sure the call works properly
    }

    //additional test for when i input a null field in constructor
    @Test
    void testConstructorThrowsOnNullInput() {
        assertThrows(IllegalArgumentException.class, () ->
                new StatisticsProcessor(null, List.of(), List.of())
        );
    }
}
