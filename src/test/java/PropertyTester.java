import OpenDataPhilly.data.PropertyReader;
import OpenDataPhilly.common.Property;

import java.io.IOException;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertyTester {
    public static void main(String[] args) {
        String fileName = "properties.csv";

        // 1. Create a Path object for the file using the current directory as the base
        Path currentDirectoryPath = Paths.get(fileName);

        // 2. Convert the relative path to an absolute path
        Path absolutePath = currentDirectoryPath.toAbsolutePath();

        String csvFilePath = absolutePath.toString();

        PropertyReader reader = new PropertyReader();
        //PropertyLoader reader = new PropertyLoader();
        try {
            List<Property> properties = reader.read(csvFilePath);

            //List<Property> properties = reader.load(csvFilePath);

            System.out.println("Total properties read: " + properties.size());
            System.out.println("Printing first 10 properties:");

            for (int i = 0; i < properties.size() && i < 10; i++) {
                System.out.println(properties.get(i));
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
        }
    }
}
