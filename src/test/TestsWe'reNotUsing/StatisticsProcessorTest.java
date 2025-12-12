import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.common.Property;
import OpenDataPhilly.data.ParkingViolationLoader;
import OpenDataPhilly.data.PopulationLoader;
import OpenDataPhilly.data.PropertyReader;
import OpenDataPhilly.processor.StatisticsProcessor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsProcessorTest {

    static StatisticsProcessor processorCsv;
    static StatisticsProcessor processorJson;
    static List<Property> properties;

    @BeforeAll
    static void setup() throws IOException {
        Path csvFile = Paths.get("parking.csv").toAbsolutePath();
        Path jsonFile = Paths.get("parking.json").toAbsolutePath();
        Path propertiesFile = Paths.get("properties.csv").toAbsolutePath();
        Path populationFile = Paths.get("population.txt").toAbsolutePath();

        ParkingViolationLoader loader = new ParkingViolationLoader();
        List<ParkingViolation> violationsCsv = loader.load("csv", csvFile.toString());
        List<ParkingViolation> violationsJson = loader.load("json", jsonFile.toString());

        PropertyReader propertyReader = new PropertyReader();
        properties = propertyReader.read(propertiesFile.toString());

        PopulationLoader populationLoader = new PopulationLoader();
        List<Population> populations = populationLoader.load(populationFile.toString());

        processorCsv = new StatisticsProcessor(populations, properties, violationsCsv);
        processorJson = new StatisticsProcessor(populations, properties, violationsJson);
    }

    @Test
    void testTotalPopulation() {
        assertEquals(processorCsv.getTotalPopulation(), processorJson.getTotalPopulation(),
                "Total population should match for CSV and JSON");
    }

    @Test
    void testFinesPerCapita() {
        Map<String, Double> finesCsv = processorCsv.getFinesPerCapitaByZip();
        Map<String, Double> finesJson = processorJson.getFinesPerCapitaByZip();
        assertEquals(finesCsv, finesJson, "Fines per capita by ZIP should match for CSV and JSON");
    }

    @Test
    void testAverageMarketValueByZip() {
        int count = 0;
        for (Property p : properties) {
            if (count > 20) {
                break; //test the first 20
            }
            count++;
            String zip = p.getzipCode();
            assertEquals(processorCsv.getAverageMarketValue(zip), processorJson.getAverageMarketValue(zip),
                    "Average market value for ZIP " + zip + " should match");
        }
    }

    @Test
    void testAreaAndMarketValuePerCapitaByZip() {
        int count = 0;
        for (Property p : properties) {
            if (count > 20) {
                break; //test the first 20
            }
            count++;
            String zip = p.getzipCode();
            assertEquals(processorCsv.getAverageTotalLivableArea(zip), processorJson.getAverageTotalLivableArea(zip),
                    "Average livable area for ZIP " + zip + " should match");
            assertEquals(processorCsv.getResidentialMarketValuePerCapita(zip), processorJson.getResidentialMarketValuePerCapita(zip),
                    "Residential market value per capita for ZIP " + zip + " should match");
        }
    }
}
