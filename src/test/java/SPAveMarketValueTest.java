import OpenDataPhilly.common.Property;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.processor.StatisticsProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SPAveMarketValueTest {
    @Test
    void testAveMarketValue() {
        List<Property> properties = List.of(
                new Property("12345", 100000, 1000),
                new Property("12345", 200000, 1200)
        );
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), properties, List.of());
        assertEquals(150000, processor.getAverageMarketValue("12345")); //(100000+20000)/2
    }

    @Test
    void testEmptyAverMarketValue() {
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), List.of(), List.of());
        assertEquals(0, processor.getAverageMarketValue("12345"));
    }

    @Test
    void testAverMarketValueSecondTime() {
        //just take the value from cache
        List<Property> properties = List.of(
                new Property("12345", 100000, 1000),
                new Property("12345", 200000, 1200)
        );

        StatisticsProcessor processor = new StatisticsProcessor(List.of(), properties, List.of());

        //firstcall
        double first = processor.getAverageMarketValue("12345");
        //secondcall
        double second = processor.getAverageMarketValue("12345");

        assertEquals(150000, first); //make sure calc is correct
        assertEquals(first, second);//cover the cache return cell
    }
}
