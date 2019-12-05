package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day5 {
    private static final Logger LOGGER = Logger.getLogger(Day5.class.getName());
    private static final Integer INPUT_PART_1 = 1;
    private static final Integer INPUT_PART_2 = 5;

    private Day5() {
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input5.txt"))).trim().split(","))
                .map(Integer::valueOf).collect(Collectors.toList());

        LOGGER.info(() -> String.format("Part 1 : %s",
                executeProgram(new ArrayList<>(values), Set.of(1, 2, 3, 4, 99), INPUT_PART_1)));

        LOGGER.info(() -> String.format("Part 2 : %s",
                executeProgram(new ArrayList<>(values), Set.of(1, 2, 3, 4, 5, 6, 7, 8, 99), INPUT_PART_2)));
    }

    private static String executeProgram(List<Integer> program, Set<Integer> enabledOpcodes, int input) {
        StringBuilder output = new StringBuilder();
        int c = 0;
        boolean done = false;
        while (!done) {
            int opCode = program.get(c) % 100;
            int val0;
            int val1;
            List<Boolean> immediateMode = Arrays.stream(String.format("%03d", program.get(c) / 100).split(""))
                    .map(d -> d.equals("1")) //
                    .collect(Collectors.toList());
            Collections.reverse(immediateMode);

            if (enabledOpcodes.contains(opCode)) {
                switch (opCode) {
                case 1:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    program.set(program.get(c + 3), val0 + val1);
                    c += 4;
                    break;

                case 2:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    program.set(program.get(c + 3), val0 * val1);
                    c += 4;
                    break;

                case 3:
                    program.set(program.get(c + 1), input);
                    c += 2;
                    break;

                case 4:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    output.append(val0).append(", ");
                    c += 2;
                    break;

                case 5:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    if (val0 != 0) {
                        c = val1;
                    } else {
                        c += 3;
                    }
                    break;

                case 6:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    if (val0 == 0) {
                        c = val1;
                    } else {
                        c += 3;
                    }
                    break;

                case 7:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    program.set(program.get(c + 3), val0 < val1 ? 1 : 0);
                    c += 4;
                    break;

                case 8:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    val1 = immediateMode.get(1) ? program.get(c + 2) : program.get(program.get(c + 2));
                    program.set(program.get(c + 3), val0 == val1 ? 1 : 0);
                    c += 4;
                    break;

                case 99:
                    done = true;
                    break;

                default:
                    throw new IllegalArgumentException("Unknown opCode: " + opCode);
                }
            }
        }
        return output.toString();
    }
}
