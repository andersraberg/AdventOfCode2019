package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;

public class Day7Part1 {
    private static final Logger LOGGER = Logger.getLogger(Day7Part1.class.getName());

    private Day7Part1() {
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input7.txt"))).trim().split(","))
                .map(Integer::valueOf).collect(Collectors.toList());

        Collection<List<Integer>> permutations = Collections2.permutations(List.of(0, 1, 2, 3, 4));

        LOGGER.info(() -> String.format("Part 1 : %s", permutations.stream() //
                .mapToInt(p -> executeSequence(new ArrayList<>(values), p)) //
                .max().getAsInt()));
    }

    private static Integer executeSequence(List<Integer> program, List<Integer> phaseSeq) {
        int output = 0;
        for (int i = 0; i < phaseSeq.size(); i++) {
            output = executeProgram(new ArrayList<>(program), Set.of(1, 2, 3, 4, 5, 6, 7, 8, 99),
                    List.of(phaseSeq.get(i), output));
        }
        return output;
    }

    private static Integer executeProgram(List<Integer> program, Set<Integer> enabledOpcodes, List<Integer> inputs) {
        int inputCounter = 0;
        List<Integer> output = new ArrayList<>();
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
                    program.set(program.get(c + 1), inputs.get(inputCounter++));
                    c += 2;
                    break;

                case 4:
                    val0 = immediateMode.get(0) ? program.get(c + 1) : program.get(program.get(c + 1));
                    output.add(val0);
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
        return output.get(output.size() - 1);
    }
}
