import OpenDataPhilly.common.Property;
import OpenDataPhilly.processor.StatisticsProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SPAveLivableAreaTest {
    @Test
    void testAveTotalLivableArea() {
        List<Property> props = List.of(
                new Property("12345", 100000, 1000),
                new Property("12345", 200000, 1200)
        );
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), props, List.of());
        assertEquals(1100, processor.getAverageTotalLivableArea("12345")); //(1000+1200)/2
    }

    @Test
    void testEmptyAveTotalLivableArea() {
        StatisticsProcessor processor = new StatisticsProcessor(List.of(), List.of(), List.of());
        assertEquals(0, processor.getAverageTotalLivableArea("12345"));
    }

    @Test
    void testAveLivableAreaSecondTime() {
        //just take the value from cache
        List<Property> properties = List.of(
                new Property("12345", 100000, 1000),
                new Property("12345", 200000, 1200)
        );

        StatisticsProcessor processor = new StatisticsProcessor(List.of(), properties, List.of());

        //firstcall
        double first = processor.getAverageTotalLivableArea("12345");
        //secondcall
        double second = processor.getAverageTotalLivableArea("12345");

        assertEquals(1100, first); //make sure calc is correct
        assertEquals(first, second);//cover the cache return cell
    }
}
