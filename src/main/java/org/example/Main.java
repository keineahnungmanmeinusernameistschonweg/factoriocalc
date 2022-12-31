package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class Main {
    private static final HashMap<String, Component> components = new HashMap<>();
    private static final HashMap<String, Double> billOfMaterials = new HashMap<>();
    private static int assemblers;
    private static String componentName;

    public static void main(String[] args) throws IOException {
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
            var component = components.get(input.toLowerCase());
            componentName = component.name;

            //Get amount
            System.out.print("Please enter the number of assemblers: ");
            assemblers = scanner.nextInt();

            deconstruct(component, assemblers);
            printResults();
            cleanup();
        }
    }

    private static void deconstruct(Component component, double cAssemblers) {
        var cProduceTime = component.time;

        for (var subComponent : component.subComponents.entrySet()) {
            var scName = subComponent.getKey();
            if (components.containsKey(scName)) {
                var scObject = components.get(scName);

                double scProduceTime = scObject.time;
                int scPerC = subComponent.getValue();
                double aggregateDemand = scPerC * cAssemblers;
                double scAssemblerAmount = (aggregateDemand * cProduceTime) / scProduceTime;

                if (billOfMaterials.containsKey(scName)) {
                    var cumulativeAssemblerCount = billOfMaterials.get(scName) + scAssemblerAmount;
                    billOfMaterials.put(scName, cumulativeAssemblerCount);
                } else {
                    billOfMaterials.put(scName, scAssemblerAmount);
                }

                deconstruct(scObject, scAssemblerAmount);
            }
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
        System.out.format("For %d %s assembler(s):\n", assemblers, componentName);
        System.out.println("--------------------------------------------------");
        for (var a : billOfMaterials.entrySet()) {
            int temp = (int)(a.getValue()*100.0);
            int roundedAssemblers = (int) Math.ceil(((double)temp)/100.0);
            System.out.format("%-27s %-3.1f ~ %d assembler(s)\n", a.getKey(), a.getValue(), roundedAssemblers);
        }
        System.out.println("--------------------------------------------------");
    }

    private static void cleanup() {
        billOfMaterials.clear();
    }

    private static void initialize() throws IOException {
        var filePath = Path.of("src/main/resources/test.json");
        var jsonString = Files.readString(filePath);
        var a = new JSONObject(jsonString);

        var b = a.getJSONArray("arr");

        for (int i = 0; i < b.length(); i++) {
            var key = b.getJSONObject(i).names().toString();
            key = key.substring(2, key.length() - 2);

            var component = new Component(key, -1d);

            var c = b.getJSONObject(i).getJSONArray(key);
            for (int j = 0; j < c.length(); j++) {
                int amount = c.getJSONObject(j).getInt("amount");
                String name = c.getJSONObject(j).getString("name");
                component = component.addSubcomponent(name, amount);


            }
            components.put(key, component);
        }

        System.out.println();
        for (var x :
             components.entrySet()) {
            System.out.println(x.toString());
        }

        /*
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



         */
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