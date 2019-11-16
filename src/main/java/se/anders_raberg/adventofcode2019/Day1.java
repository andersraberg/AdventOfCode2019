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
        List<Integer> changes = Files.readAllLines(Paths.get("inputs/input1.txt")).stream() //
                .map(Integer::valueOf) //
                .collect(Collectors.toList());

        // Part 1
        int sum = changes.stream().reduce(0, Integer::sum);

        // Results
        LOGGER.info(() -> String.format("Part 1: Sum: %s", sum));
    }
}
