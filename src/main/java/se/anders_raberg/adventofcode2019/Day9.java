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

public class Day9 {
    private static final Logger LOGGER = Logger.getLogger(Day9.class.getName());

    private Day9() {
    }

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input9.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        for (long i = 0; i < 10000; i++) {
            values.add((long) 0);
        }

        LOGGER.info(() -> String.format("Part 1 : %s", executeProgram(new ArrayList<>(values), 1)));
        LOGGER.info(() -> String.format("Part 2 : %s", executeProgram(new ArrayList<>(values), 2)));
    }

    private static String executeProgram(List<Long> program, long input) {
        StringBuilder output = new StringBuilder();
        long c = 0;
        long relativebase = 0;
        boolean done = false;
        while (!done) {
            long opCode = program.get((int) c) % 100;
            long val0;
            long val1;
            long pos;
            List<String> parameterModes = Arrays.stream(String.format("%03d", program.get((int) c) / 100).split(""))
                    .collect(Collectors.toList());

            Collections.reverse(parameterModes);
            switch ((int) opCode) {
            case 1:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                pos = getOutParameterPos(parameterModes.get(2), program, c + 3, relativebase);
                program.set((int) pos, val0 + val1);
                c += 4;
                break;

            case 2:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                pos = getOutParameterPos(parameterModes.get(2), program, c + 3, relativebase);
                program.set((int) pos, val0 * val1);
                c += 4;
                break;

            case 3:
                val0 = getOutParameterPos(parameterModes.get(0), program, c + 1, relativebase);
                program.set((int) val0, input);
                c += 2;
                break;

            case 4:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                output.append(val0).append(", ");
                c += 2;
                break;

            case 5:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                if (val0 != 0) {
                    c = val1;
                } else {
                    c += 3;
                }
                break;

            case 6:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                if (val0 == 0) {
                    c = val1;
                } else {
                    c += 3;
                }
                break;

            case 7:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                pos = getOutParameterPos(parameterModes.get(2), program, c + 3, relativebase);
                program.set((int) pos, (long) (val0 < val1 ? 1 : 0));
                c += 4;
                break;

            case 8:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                val1 = getInParameterVal(parameterModes.get(1), program, c + 2, relativebase);
                pos = getOutParameterPos(parameterModes.get(2), program, c + 3, relativebase);
                program.set((int) pos, (long) (val0 == val1 ? 1 : 0));
                c += 4;
                break;

            case 9:
                val0 = getInParameterVal(parameterModes.get(0), program, c + 1, relativebase);
                relativebase = relativebase + val0;
                c += 2;
                break;

            case 99:
                done = true;
                break;

            default:
                throw new IllegalArgumentException("Unknown opCode: " + opCode);
            }
        }
        return output.toString();
    }

    private static long getInParameterVal(String parameterMode, List<Long> program, long argOffset, long relativebase) {
        switch (parameterMode) {
        case "0":
            return program.get((int) (long) program.get((int) argOffset));

        case "1":
            return program.get((int) argOffset);

        case "2":
            return program.get((int) (long) program.get((int) argOffset) + (int) relativebase);

        default:
            throw new IllegalArgumentException();
        }
    }

    private static long getOutParameterPos(String parameterMode, List<Long> program, long argOffset,
            long relativebase) {
        switch (parameterMode) {
        case "0":
            return program.get((int) argOffset);

        case "2":
            return program.get((int) argOffset) + relativebase;

        default:
            throw new IllegalArgumentException();
        }
    }
}
