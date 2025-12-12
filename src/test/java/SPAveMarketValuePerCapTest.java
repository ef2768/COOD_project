import OpenDataPhilly.common.Property;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.processor.StatisticsProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SPAveMarketValuePerCapTest {
    @Test
    void testMarketValuePerCapita() {
        List<Property> props = List.of(
                new Property("12345", 100000, 1000),
                new Property("12345", 200000, 1200)
        );
        List<Population> pops = List.of(new Population("12345", 100));
        StatisticsProcessor processor = new StatisticsProcessor(pops, props, List.of());
        assertEquals(3000, processor.getResidentialMarketValuePerCapita("12345")); //(100000+200000)/100
    }

    @Test
    void testMVPCwithZeroPop() {
        List<Property> properties = List.of(
                new Property("12345", 100000, 1000)
        );
        List<Population> populations = List.of(new Population("12345", 0));
        StatisticsProcessor processor = new StatisticsProcessor(populations, properties, List.of());
        assertEquals(0, processor.getResidentialMarketValuePerCapita("12345"));
    }

    void testEmptyMVPC() {
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), List.of(), List.of());
        assertEquals(0, processor.getResidentialMarketValuePerCapita("12345"));
    }
}
