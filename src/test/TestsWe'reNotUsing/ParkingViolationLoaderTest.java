import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.data.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ParkingViolationLoaderTest {

    @Test
    void testSpecificViolationByTimestamp() {
        ParkingViolationLoader loader = new ParkingViolationLoader();
        String fileName = "parking.csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();

        try {
            List<ParkingViolation> violations = loader.load("csv", filePath.toString());

            assertNotNull(violations, "Loader should not return null");

            // Find violation by timestamp
            String targetTimestamp = "2013-01-28T12:29:00Z";
            ParkingViolation targetViolation = violations.stream()
                    .filter(v -> v.getTimestamp().equals(targetTimestamp))
                    .findFirst()
                    .orElse(null);

            assertNotNull(targetViolation, "Violation with timestamp " + targetTimestamp + " should exist");

            // Test its fields
            assertEquals(41, targetViolation.getFine());
            assertEquals("EXPIRED INSPECTION", targetViolation.getDescription());
            assertEquals("1408768", targetViolation.getVehicleID());
            assertEquals("PA", targetViolation.getState());
            assertEquals("2905951", targetViolation.getViolationID());
            assertEquals("", targetViolation.getZipCode()); // empty zip in CSV snippet

        } catch (IOException e) {
            fail("IOException should not occur: " + e.getMessage());
        }
    }

    @Test
    void testSpecificViolationByTimestampJson() {
        ParkingViolationLoader loader = new ParkingViolationLoader();
        String fileName = "parking.json";
        Path filePath = Paths.get(fileName).toAbsolutePath();

        try {
            List<ParkingViolation> violations = loader.load("json", filePath.toString());

            assertNotNull(violations, "Loader should not return null");

            // Find violation by timestamp
            String targetTimestamp = "2013-01-28T12:29:00Z";
            ParkingViolation targetViolation = violations.stream()
                    .filter(v -> targetTimestamp.equals(v.getTimestamp())) // safer: call equals on constant
                    .findFirst()
                    .orElse(null);

            assertNotNull(targetViolation, "Violation with timestamp " + targetTimestamp + " should exist");

            // Test its fields
            assertEquals(41, targetViolation.getFine());
            assertEquals("EXPIRED INSPECTION", targetViolation.getDescription());
            assertEquals("1408768", targetViolation.getVehicleID());
            assertEquals("PA", targetViolation.getState());
            assertEquals("2905951", targetViolation.getViolationID());
            assertEquals("", targetViolation.getZipCode()); // empty zip in JSON snippet

        } catch (IOException e) {
            fail("IOException should not occur: " + e.getMessage());
        }
    }


}


