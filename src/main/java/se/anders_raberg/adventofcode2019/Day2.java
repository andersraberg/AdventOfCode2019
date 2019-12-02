package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day2 {
    private static final Logger LOGGER = Logger.getLogger(Day2.class.getName());

    private Day2() {
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input2.txt"))).trim().split(","))
                .map(Integer::valueOf).collect(Collectors.toList());

        // Part 1
        LOGGER.info(() -> "Part 1 : " + executeProgram(new ArrayList<>(values), 12, 2));

        // Part 2
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                if (executeProgram(new ArrayList<>(values), noun, verb) == 19690720) {
                    final int result = 100 * noun + verb;
                    LOGGER.info(() -> String.format("Part 2 : %s", result));
                }
            }
        }
    }

    private static int executeProgram(List<Integer> program, int noun, int verb) {
        program.set(1, noun);
        program.set(2, verb);

        int c = 0;
        boolean done = false;
        while (!done) {
            switch (program.get(c)) {
            case 1:
                program.set(program.get(c + 3), program.get(program.get(c + 1)) + program.get(program.get(c + 2)));
                break;

            case 2:
                program.set(program.get(c + 3), program.get(program.get(c + 1)) * program.get(program.get(c + 2)));
                break;

            case 99:
                done = true;
                break;

            default:
                throw new IllegalArgumentException();
            }
            c += 4;
        }

        return program.get(0);
    }
}
