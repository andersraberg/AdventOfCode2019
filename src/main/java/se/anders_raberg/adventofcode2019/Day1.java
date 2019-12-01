package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day1 {
    private static final Logger LOGGER = Logger.getLogger(Day1.class.getName());

    private Day1() {
    }

    public static void run() throws IOException {
        List<Integer> modules = Files.readAllLines(Paths.get("inputs/input1.txt")).stream() //
                .map(Integer::valueOf) //
                .collect(Collectors.toList());

        // Part 1
        LOGGER.info(() -> String.format("Part 1: Fuel consumption: %s",
                modules.stream().mapToInt(i -> (int) (i / 3.0) - 2).sum()));

        // Part 2
        LOGGER.info(() -> String.format("Part 2: Fuel consumption: %s",
                modules.stream().mapToInt(Day1::recursiveConsumption).sum()));
    }

    private static int recursiveConsumption(int unit) {
        int consumption = (int) (unit / 3.0) - 2;
        return consumption < 0 ? 0 : consumption + recursiveConsumption(consumption);
    }
}
