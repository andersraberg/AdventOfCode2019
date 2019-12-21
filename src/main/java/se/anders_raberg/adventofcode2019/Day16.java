package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 {
    private static final Logger LOGGER = Logger.getLogger(Day16.class.getName());
    private static final List<Integer> BASE_PATTERN = List.of(0, 1, 0, -1);

    private Day16() {
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input16.txt"))).trim().split(""))
                .map(Integer::valueOf).collect(Collectors.toList());

        List<Integer> output = values;
        for (int i = 0; i < 100; i++) {
            output = runPhase(output);
        }
        LOGGER.info("Part 1 : " + output.subList(0, 8));
    }

    private static List<Integer> runPhase(List<Integer> input) {
        List<Integer> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            final int a = i + 1;
            List<Integer> pattern = IntStream.rangeClosed(0, 3)
                    .mapToObj(x -> Collections.nCopies(a, BASE_PATTERN.get(x))) //
                    .flatMap(List::stream) //
                    .collect(Collectors.toList());
            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += input.get(j) * pattern.get((j + 1) % pattern.size());
            }
            output.add(Math.abs(sum) % 10);
        }
        return output;

    }

}
