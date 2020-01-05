package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day15 {
    private static final Logger LOGGER = Logger.getLogger(Day15.class.getName());

    private Day15() {
    }

    private enum Direction {
        NORTH(1), SOUTH(2), WEST(3), EAST(4);

        private final long _value;

        private Direction(int value) {
            _value = value;
        }

        public long value() {
            return _value;
        }

        public Pair<Integer, Integer> nextPos(Pair<Integer, Integer> pos) {
            switch (this) {
            case NORTH:
                return new Pair<>(pos.first() - 1, pos.second());
            case SOUTH:
                return new Pair<>(pos.first() + 1, pos.second());
            case WEST:
                return new Pair<>(pos.first(), pos.second() - 1);
            case EAST:
                return new Pair<>(pos.first(), pos.second() + 1);
            default:
                throw new IllegalStateException();
            }
        }

    }

    private static final Set<Pair<Integer, Integer>> VISITED_PART_1 = new HashSet<>();
    private static final Set<Integer> PATH_LENGTHS = new HashSet<>();
    private static final Set<Pair<Integer, Integer>> VISITED_PART_2 = new HashSet<>();
    private static final Map<Pair<Integer, Integer>, String> LOCATION_MAP = new HashMap<>();
    private static Pair<Integer, Integer> oxygenPos;

    private static int maxPathLength = 0;

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input15.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        // Part 1
        findPossiblePaths(new Pair<>(0, 0), 0, new Computer(values));
        LOGGER.info(() -> "Part 1: " + PATH_LENGTHS.stream().min(Integer::compareTo).orElseThrow() + "\n"
                + toString(LOCATION_MAP));

        // Part 2
        fillOxygen(oxygenPos, 0);
        LOGGER.info(() -> "Part 2: Number of minutes: " + maxPathLength);
    }

    private static void findPossiblePaths(Pair<Integer, Integer> currentPos, int currentPathLength, Computer a) {
        VISITED_PART_1.add(currentPos);

        for (Direction dir : Direction.values()) {
            Computer dirComputer = new Computer(a);
            dirComputer.executeProgram(List.of(dir.value()));
            List<Long> output = dirComputer.getOutput();
            switch (output.get(0).intValue()) {
            case 0:
                LOCATION_MAP.put(dir.nextPos(currentPos), "#");
                break;

            case 1:
                Pair<Integer, Integer> nextPos = dir.nextPos(currentPos);
                if (!VISITED_PART_1.contains(nextPos)) {
                    LOCATION_MAP.put(nextPos, ".");
                    findPossiblePaths(nextPos, currentPathLength + 1, dirComputer);
                }
                break;

            case 2:
                PATH_LENGTHS.add(currentPathLength + 1);
                Pair<Integer, Integer> nextPos2 = dir.nextPos(currentPos);
                if (!VISITED_PART_1.contains(nextPos2)) {
                    LOCATION_MAP.put(nextPos2, "O");
                    oxygenPos = nextPos2;
                    findPossiblePaths(nextPos2, currentPathLength + 1, dirComputer);
                }
                break;

            default:
                throw new IllegalArgumentException();
            }

        }
    }

    private static void fillOxygen(Pair<Integer, Integer> currentPos, int currentPathLength) {
        VISITED_PART_2.add(currentPos);
        maxPathLength = Math.max(maxPathLength, currentPathLength);
        for (Direction dir : Direction.values()) {
            String posType = LOCATION_MAP.getOrDefault(dir.nextPos(currentPos), "#");

            switch (posType) {
            case "#":
                break;

            case "O":
            case ".":
                Pair<Integer, Integer> nextPos = dir.nextPos(currentPos);
                if (!VISITED_PART_2.contains(nextPos)) {
                    LOCATION_MAP.put(nextPos, "O");
                    fillOxygen(nextPos, currentPathLength + 1);
                }
                break;

            default:
                throw new IllegalArgumentException();
            }

        }

    }

    private static String toString(Map<Pair<Integer, Integer>, String> map) {
        StringBuilder sb = new StringBuilder();
        int minX = map.keySet().stream().mapToInt(Pair::first).min().orElseThrow();
        int maxX = map.keySet().stream().mapToInt(Pair::first).max().orElseThrow();
        int minY = map.keySet().stream().mapToInt(Pair::second).min().orElseThrow();
        int maxY = map.keySet().stream().mapToInt(Pair::second).max().orElseThrow();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                sb.append(map.getOrDefault((new Pair<>(x, y)), " "));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static class Computer {
        private final List<Long> _program;
        private final Queue<Long> _input = new ArrayBlockingQueue<>(2);
        private long _c = 0;
        private long _relativebase = 0;
        private List<Long> _output = new ArrayList<>();

        public Computer(List<Long> program) {
            _program = new ArrayList<>(program);
        }

        public Computer(Computer computer) {
            _program = new ArrayList<>(computer._program);
            _c = computer._c;
            _relativebase = computer._relativebase;
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
                    return;

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