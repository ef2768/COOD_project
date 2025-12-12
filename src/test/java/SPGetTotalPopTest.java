import OpenDataPhilly.common.Population;
import OpenDataPhilly.processor.StatisticsProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SPGetTotalPopTest {

    @Test
    void testTotalPopulation() {
        List<Population> populations = List.of(
                new Population("19104", 1000),
                new Population("19103", 500)
        );
        StatisticsProcessor processor = new StatisticsProcessor(populations, List.of(), List.of());

        long totalPop = processor.getTotalPopulation();
        assertEquals(1500, totalPop);
    }

    @Test
    void testEmptyPopulation() {
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), List.of(), List.of());
        assertEquals(0, processor.getTotalPopulation());
    }

}
