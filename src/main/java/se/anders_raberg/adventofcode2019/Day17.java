package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.primitives.Bytes;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day17 {
    private static final Logger LOGGER = Logger.getLogger(Day17.class.getName());

    private Day17() {
    }

    private enum Direction {
        NORTH, SOUTH, WEST, EAST;

        public Pair<Integer, Integer> nextPos(Pair<Integer, Integer> pos) {
            switch (this) {
            case NORTH:
                return new Pair<>(pos.first(), pos.second() - 1);
            case SOUTH:
                return new Pair<>(pos.first(), pos.second() + 1);
            case WEST:
                return new Pair<>(pos.first() - 1, pos.second());
            case EAST:
                return new Pair<>(pos.first() + 1, pos.second());
            default:
                throw new IllegalStateException();
            }
        }

        private static Map<Direction, Direction> turnRight = Map.of(SOUTH, WEST, WEST, NORTH, NORTH, EAST, EAST, SOUTH);
        private static Map<Direction, Direction> turnLeft = Map.of(SOUTH, EAST, EAST, NORTH, NORTH, WEST, WEST, SOUTH);
    }

    private static final Set<Pair<Integer, Integer>> VISITED_PART_1 = new HashSet<>();

    private static final Set<Pair<Integer, Integer>> SCAFFOLDING = new HashSet<>();

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input17.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        for (long i = 0; i < 10000; i++) {
            values.add((long) 0);
        }

        // Part 1
        Computer computerPart1 = new Computer(values);
        computerPart1.executeProgram(Collections.emptyList());

        int x = 0;
        int y = 0;
        Pair<Integer, Integer> robotPos = null;
        Direction robotDirection = null;
        for (Long point : computerPart1.getOutput()) {
            switch (point.intValue()) {
            case 94:
                robotPos = new Pair<>(x, y);
                robotDirection = Direction.NORTH;
                SCAFFOLDING.add(new Pair<>(x, y));
                x++;
                break;

            case 35:
                SCAFFOLDING.add(new Pair<>(x, y));
                x++;
                break;

            case 46:
                x++;
                break;

            case 10:
                y++;
                x = 0;
                break;

            default:
                throw new IllegalArgumentException("Value:" + point.intValue());
            }
        }

        Set<Pair<Integer, Integer>> intersections = new HashSet<>();
        StringBuilder robotMovements = new StringBuilder();
        int stepsInCurrentDirection = 0;
        while (true) {
            boolean added = VISITED_PART_1.add(robotPos);
            if (!added) {
                intersections.add(robotPos);
            }
            Pair<Integer, Integer> posInFront = robotDirection.nextPos(robotPos);
            Direction afterLeftTurn = Direction.turnLeft.get(robotDirection);
            Direction afterRightTurn = Direction.turnRight.get(robotDirection);
            Pair<Integer, Integer> posToLeft = afterLeftTurn.nextPos(robotPos);
            Pair<Integer, Integer> posToRight = afterRightTurn.nextPos(robotPos);

            if (SCAFFOLDING.contains(posInFront)) {
                robotPos = posInFront;
                stepsInCurrentDirection++;
            } else {
                if (stepsInCurrentDirection > 0) {
                    robotMovements.append(stepsInCurrentDirection).append(",");
                }
                if (SCAFFOLDING.contains(posToLeft)) {
                    robotMovements.append("L,");
                    robotPos = posToLeft;
                    robotDirection = afterLeftTurn;
                    stepsInCurrentDirection = 1;
                } else if (SCAFFOLDING.contains(posToRight)) {
                    robotMovements.append("R,");
                    robotPos = posToRight;
                    robotDirection = afterRightTurn;
                    stepsInCurrentDirection = 1;
                } else {
                    break;
                }
            }
        }

        LOGGER.info("Part 1: Sum of alignment parameters: "
                + intersections.stream().mapToInt(i -> i.first() * i.second()).sum()
                + "\n              Robot movements: " + robotMovements.toString() + "\n" + toString(SCAFFOLDING));

        // Manually split from the robot movements output in part 1.
        String funcA = "R,6,L,12,R,6\n";
        String funcB = "L,12,R,6,L,8,L,12\n";
        String funcC = "R,12,L,10,L,10\n";

        String mainRoutine = "A,A,B,C,B,C,B,C,B,A\n";
        String videoChoice = "n\n";

        values.set(0, (long) 2);
        Computer computerPart2 = new Computer(values);

        List<Long> input = new ArrayList<>();
        input.addAll(toLongList(mainRoutine));
        input.addAll(toLongList(funcA));
        input.addAll(toLongList(funcB));
        input.addAll(toLongList(funcC));
        input.addAll(toLongList(videoChoice));

        computerPart2.executeProgram(input);

        List<Long> output = computerPart2.getOutput();
        LOGGER.info("Part 2: Amount of dust: " + output.get(output.size() - 1));
    }

    private static List<Long> toLongList(String str) {
        return Bytes.asList(str.getBytes()).stream().map(Byte::longValue).collect(Collectors.toList());
    }

    private static String toString(Set<Pair<Integer, Integer>> set) {
        StringBuilder sb = new StringBuilder();
        int minX = set.stream().mapToInt(Pair::first).min().orElseThrow();
        int maxX = set.stream().mapToInt(Pair::first).max().orElseThrow();
        int minY = set.stream().mapToInt(Pair::second).min().orElseThrow();
        int maxY = set.stream().mapToInt(Pair::second).max().orElseThrow();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                sb.append(set.contains(new Pair<>(x, y)) ? "#" : ".");
            }
            sb.append("\n");
        }
        return sb.toString();
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