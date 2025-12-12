

import OpenDataPhilly.common.Population;
import OpenDataPhilly.data.PopulationLoader;
import NotUsing.PopulationReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PopulationComparisonTester {

    public static void main(String[] args) {
        String fileName = "population.txt";

        // 1. Create a Path object for the file using the current directory as the base
        Path currentDirectoryPath = Paths.get(fileName);

        // 2. Convert the relative path to an absolute path
        Path absolutePath = currentDirectoryPath.toAbsolutePath();

        String filePath = absolutePath.toString();


        PopulationLoader loader = new PopulationLoader();
        PopulationReader reader = new PopulationReader();

        try {
            List<Population> list1 = loader.load(filePath);
            List<Population> list2 = reader.read(filePath);

            System.out.println("PopulationLoader produced " + list1.size() + " entries.");
            System.out.println("PopulationReader produced " + list2.size() + " entries.");

            boolean allMatch = true;

            // Compare entry by entry
            int minSize = Math.min(list1.size(), list2.size());
            for (int i = 0; i < minSize; i++) {
                Population p1 = list1.get(i);
                Population p2 = list2.get(i);

                if (!p1.getZipCode().equals(p2.getZipCode()) || p1.getPopulation() != p2.getPopulation()) {
                    allMatch = false;
                    System.out.println("Mismatch at index " + i + ":");
                    System.out.println("  Loader: " + p1);
                    System.out.println("  Reader: " + p2);
                }
            }

            if (list1.size() != list2.size()) {
                allMatch = false;
                System.out.println("Sizes differ: Loader=" + list1.size() + ", Reader=" + list2.size());
            }

            if (allMatch) {
                System.out.println("Both implementations produce identical results!");
            } else {
                System.out.println("There are differences between the two implementations.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
