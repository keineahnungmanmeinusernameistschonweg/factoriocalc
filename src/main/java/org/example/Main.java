package org.example;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Main {
    private static final HashMap<String, Component> components = new HashMap<>();
    private static final HashMap<String, Double> billOfMaterials = new HashMap<>();

    public static void main(String[] args) {
        initialize();
        run();
    }

    private static void run() {
        var scanner = new Scanner(System.in);
        String input;
        while (true) {

            //Get component
            System.out.print("Enter component name or type exit: ");
            input = scanner.next();
            if (input.equals("exit")) return;
            while (!components.containsKey(input)) {
                System.out.println("Component could not be found, please enter valid component name:");
                input = scanner.next();
            }
            var component = components.get(input);

            //Get amount
            System.out.print("\nPlease enter the number of assemblers: ");
            var assemblers = scanner.nextInt();


            deconstruct(component, assemblers);
            printResults();
        }

    }

    private static void deconstruct(Component component, double amount) {
        var cProduceTime = component.time;

        for (var subComponent : component.subComponents.entrySet()) {
            var scName = subComponent.getKey();
            var scObject = components.get(scName);

            double scProduceTime = scObject.time;
            int scAmount = subComponent.getValue();
            double aggregateDemand = scAmount * amount;
            double amountOfAssemblers = (aggregateDemand * cProduceTime) / scProduceTime;

            if (billOfMaterials.containsKey(scName)) {
                var cumulativeAssemblerCount = billOfMaterials.get(scName) + amountOfAssemblers;
                billOfMaterials.put(scName, cumulativeAssemblerCount);
            } else {
                billOfMaterials.put(scName, amountOfAssemblers);
            }

            deconstruct(scObject, amountOfAssemblers);
        }
    }

    private static void printResults() {
        System.out.print(" _______                                 __    __              \n" +
                "/       \\                               /  |  /  |             \n" +
                "$$$$$$$  |  ______    _______  __    __ $$ | _$$ |_    _______ \n" +
                "$$ |__$$ | /      \\  /       |/  |  /  |$$ |/ $$   |  /       |\n" +
                "$$    $$< /$$$$$$  |/$$$$$$$/ $$ |  $$ |$$ |$$$$$$/  /$$$$$$$/ \n" +
                "$$$$$$$  |$$    $$ |$$      \\ $$ |  $$ |$$ |  $$ | __$$      \\ \n" +
                "$$ |  $$ |$$$$$$$$/  $$$$$$  |$$ \\__$$ |$$ |  $$ |/  |$$$$$$  |\n" +
                "$$ |  $$ |$$       |/     $$/ $$    $$/ $$ |  $$  $$//     $$/ \n" +
                "$$/   $$/  $$$$$$$/ $$$$$$$/   $$$$$$/  $$/    $$$$/ $$$$$$$/  \n" +
                "                                                               \n" +
                "                                                               \n");
        System.out.println("--------------------------------------------------------------");
        for (var a : billOfMaterials.entrySet()) {
            System.out.format("%-25s %-12.10f ~ %d Assembler\n", a.getKey(), a.getValue(), (int) Math.ceil(a.getValue()));
        }
        System.out.println("--------------------------------------------------------------");
    }

    private static void initialize() {
        components.put(
                "red_science",
                new Component("red_science", r(5.0d))
                        .addSubcomponent("gear", 1)
        );

        components.put(
                "gear",
                new Component("gear", r(0.5d))
        );

        components.put(
                "green_science",
                new Component("green_science", r(5.0d))
                        .addSubcomponent("transport_belt", 2)
                        .addSubcomponent("inserter", 1)
        );
        components.put(
                "inserter",
                new Component("inserter", r(0.5d))
                        .addSubcomponent("small_electric_motor", 1)
                        .addSubcomponent("burner_inserter", 1)
        );
        components.put(
                "small_electric_motor",
                new Component("small_electric_motor", r(0.8d))
                        .addSubcomponent("gear", 1)
                        .addSubcomponent("copper_cable", 6)
        );
        components.put(
                "copper_cable",
                new Component("copper_cable", r(0.25d))
        );
        components.put(
                "burner_inserter", new Component("burner_inserter", r(0.5d))
                        .addSubcomponent("iron_stick", 2)
                        .addSubcomponent("single_cylinder_engine", 1)
        );
        components.put(
                "iron_stick", new Component("iron_stick", r(0.25d))
        );
        components.put(
                "single_cylinder_engine", new Component("single_cylinder_engine", r(0.6d))
                        .addSubcomponent("gear", 1)
        );
        components.put(
                "transport_belt",
                new Component("transport_belt", r(0.25d))
                        .addSubcomponent("single_cylinder_engine", 1)
        );


        System.out.println("Initialized!");
    }

    /**
     * Turns time per component into components per time
     *
     * @param input time per component
     * @return components per time
     */
    private static double r(double input) {
        return 1.0d / input;
    }
}