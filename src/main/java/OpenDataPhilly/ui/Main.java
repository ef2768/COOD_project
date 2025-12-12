package OpenDataPhilly.ui;

import OpenDataPhilly.common.ParkingViolation;
import OpenDataPhilly.common.Population;
import OpenDataPhilly.common.Property;
import OpenDataPhilly.data.ParkingViolationLoader;
import OpenDataPhilly.data.PopulationLoader;
import OpenDataPhilly.data.PropertyLoader;
import OpenDataPhilly.processor.StatisticsProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Error: expected 4 runtime arguments:");
            System.out.println("<csv|json> <parking file> <properties file> <population file>");
            return;
        }

        String format = args[0];
        if (!"csv".equals(format) && !"json".equals(format)) {
            System.out.println("Error: first argument must be 'csv' or 'json'");
            return;
        }

        String parkingFile = args[1];
        String propertiesFile = args[2];
        String populationFile = args[3];

        if (!canRead(parkingFile) || !canRead(propertiesFile) || !canRead(populationFile)) {
            System.out.println("Error: one or more input files cannot be opened for reading.");
            return;
        }

        try {
            ParkingViolationLoader pvl = new ParkingViolationLoader();
            PropertyLoader propertyLoader = new PropertyLoader();
            PopulationLoader populationLoader = new PopulationLoader();

            List<ParkingViolation> violations = pvl.load(format, parkingFile);
            List<Property> properties = propertyLoader.load(propertiesFile);
            List<Population> populations = populationLoader.load(populationFile);

            StatisticsProcessor processor = new StatisticsProcessor(populations, properties, violations);

            runMenu(processor);

        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    private static boolean canRead(String filename) {
        File f = new File(filename);
        return f.exists() && f.canRead();
    }

    private static void runMenu(StatisticsProcessor processor) {
        Scanner scanner = new Scanner(System.in);
        Locale.setDefault(Locale.US);

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Goodbye!");
                    return;
                case 1:
                    long totalPop = processor.getTotalPopulation();
                    System.out.println(totalPop);
                    break;
                case 2:
                    Map<String, Double> finesPerCapita = processor.getFinesPerCapitaByZip();
                    for (Map.Entry<String, Double> e : finesPerCapita.entrySet()) {
                        System.out.printf("%s %.4f%n", e.getKey(), e.getValue());
                    }
                    break;
                case 3:
                    System.out.print("Enter ZIP Code: ");
                    String zip3 = scanner.nextLine().trim();
                    double avgMv = processor.getAverageMarketValue(zip3);
                    System.out.println(avgMv);
                    break;
                case 4:
                    System.out.print("Enter ZIP Code: ");
                    String zip4 = scanner.nextLine().trim();
                    int avgArea = processor.getAverageTotalLivableArea(zip4);
                    System.out.println(avgArea);
                    break;
                case 5:
                    System.out.print("Enter ZIP Code: ");
                    String zip5 = scanner.nextLine().trim();
                    double mvPerCapita = processor.getResidentialMarketValuePerCapita(zip5);
                    System.out.println(mvPerCapita);
                    break;
                default:
                    // ignore invalid
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Main Menu");
        System.out.println("1. Total population for all ZIP Codes");
        System.out.println("2. Fines per capita for each ZIP Code");
        System.out.println("3. Average residential market value for a ZIP Code");
        System.out.println("4. Average residential total livable area for a ZIP Code");
        System.out.println("5. Residential market value per capita for a ZIP Code");
        System.out.println("0. Quit");
        System.out.print("Enter selection: ");
    }
}