package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day19 {
    private static final int SIZE_PART_1 = 50;
    private static final int SIZE_PART_2 = 100;
    private static final Logger LOGGER = Logger.getLogger(Day19.class.getName());

    private Day19() {
    }

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input19.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        for (long i = 0; i < 10000; i++) {
            values.add((long) 0);
        }

        // Part 1
        //
        String[][] area = new String[SIZE_PART_1][SIZE_PART_1];
        List<Long> totalOutput = new ArrayList<>();
        for (long x = 0; x < SIZE_PART_1; x++) {
            for (long y = 0; y < SIZE_PART_1; y++) {
                Long output = runComputer(x, y, values);
                totalOutput.add(output);
                area[(int) x][(int) y] = output.equals(1L) ? "#" : ".";
            }
        }

        LOGGER.info(() -> "Part 1 : Points affected: " + totalOutput.stream().filter(e -> e.equals(1L)).count());

        // Part 2
        //
        long xPos = 0;
        long yPos = 5;

        while (true) {
            yPos++;
            while (true) {
                Long output = runComputer(xPos, yPos, values);
                if (output.equals(1L)) {
                    break;
                }
                xPos++;
            }

            int distance = SIZE_PART_2 - 1;
            if (runComputer(xPos + distance, yPos - distance, values).equals(1L)) {
                LOGGER.info("Part 2 : Answer :" + (xPos * 10_000 + yPos - 99));
                break;
            }

        }

    }

    private static Long runComputer(long x, long y, List<Long> program) {
        Computer computer = new Computer(program);
        computer.executeProgram(List.of(x, y));
        return computer.getOutput().get(0);

    }

    private static class Computer {
        private final List<Long> _program;
        private final Queue<Long> _input = new ArrayBlockingQueue<>(100);
        private long _c = 0;
        private long _relativebase = 0;
        private List<Long> _output = new ArrayList<>();

        public Computer(List<Long> program) {
            _program = new ArrayList<>(program);
        }

        public List<Long> getOutput() {
            List<Long> tmp = new ArrayList<>(_output);
            _output.clear();
            return tmp;
        }

        private void executeProgram(List<Long> input) {
            _input.addAll(input);
            while (true) {
                long opCode = _program.get((int) _c) % 100;
                long val0;
                long val1;
                long pos;
                List<String> parameterModes = Arrays
                        .stream(String.format("%03d", _program.get((int) _c) / 100).split(""))
                        .collect(Collectors.toList());

                Collections.reverse(parameterModes);
                switch ((int) opCode) {
                case 1:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    pos = getOutParameterPos(parameterModes.get(2), _program, _c + 3, _relativebase);
                    _program.set((int) pos, val0 + val1);
                    _c += 4;
                    break;

                case 2:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    pos = getOutParameterPos(parameterModes.get(2), _program, _c + 3, _relativebase);
                    _program.set((int) pos, val0 * val1);
                    _c += 4;
                    break;

                case 3:
                    Long inp = _input.poll();
                    if (inp == null) {
                        return;
                    }

                    val0 = getOutParameterPos(parameterModes.get(0), _program, _c + 1, _relativebase);
                    _program.set((int) val0, inp);
                    _c += 2;
                    break;

                case 4:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    _output.add(val0);
                    _c += 2;
                    break;

                case 5:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    if (val0 != 0) {
                        _c = val1;
                    } else {
                        _c += 3;
                    }
                    break;

                case 6:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    if (val0 == 0) {
                        _c = val1;
                    } else {
                        _c += 3;
                    }
                    break;

                case 7:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    pos = getOutParameterPos(parameterModes.get(2), _program, _c + 3, _relativebase);
                    _program.set((int) pos, (long) (val0 < val1 ? 1 : 0));
                    _c += 4;
                    break;

                case 8:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    val1 = getInParameterVal(parameterModes.get(1), _program, _c + 2, _relativebase);
                    pos = getOutParameterPos(parameterModes.get(2), _program, _c + 3, _relativebase);
                    _program.set((int) pos, (long) (val0 == val1 ? 1 : 0));
                    _c += 4;
                    break;

                case 9:
                    val0 = getInParameterVal(parameterModes.get(0), _program, _c + 1, _relativebase);
                    _relativebase = _relativebase + val0;
                    _c += 2;
                    break;

                case 99:
                    return;

                default:
                    throw new IllegalArgumentException("Unknown opCode: " + opCode);
                }
            }
        }

        private static long getInParameterVal(String parameterMode, List<Long> program, long argOffset,
                long relativebase) {
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

}