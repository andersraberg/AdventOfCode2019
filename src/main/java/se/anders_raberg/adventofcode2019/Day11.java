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

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day11 {
    private static final Logger LOGGER = Logger.getLogger(Day11.class.getName());

    private Day11() {
    }

    private enum Direction {
        DOWN, LEFT, UP, RIGHT;

        private static Map<Direction, Direction> turnRight = Map.of(DOWN, LEFT, LEFT, UP, UP, RIGHT, RIGHT, DOWN);
        private static Map<Direction, Direction> turnLeft = Map.of(DOWN, RIGHT, RIGHT, UP, UP, LEFT, LEFT, DOWN);
    }

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input11.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        for (long i = 0; i < 1000; i++) {
            values.add((long) 0);
        }

        runRobot(1, 0, new ArrayList<>(values));
        runRobot(2, 1, new ArrayList<>(values));
    }

    private static void runRobot(int part, int startColor, List<Long> program) {
        long[][] map = new long[100][100];

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                map[x][y] = 0;
            }
        }

        int currentX = 50;
        int currentY = 50;
        Direction currentDir = Direction.UP;
        long currentPanelColor = startColor;

        Computer a = new Computer(program);
        Set<Pair<Integer, Integer>> paintedPanels = new HashSet<>();

        while (true) {
            a.executeProgram(List.of(currentPanelColor));
            List<Long> outPut = a.getOutput();
            if (outPut.size() < 2) {
                break;
            }
            map[currentX][currentY] = outPut.get(0);
            paintedPanels.add(new Pair<>(currentX, currentY));
            currentDir = outPut.get(1) == 0 ? Direction.turnLeft.get(currentDir) : Direction.turnRight.get(currentDir);
            switch (currentDir) {
            case DOWN:
                currentY--;
                break;
            case LEFT:
                currentX--;
                break;
            case RIGHT:
                currentX++;
                break;
            case UP:
                currentY++;
                break;
            default:
                throw new IllegalArgumentException();
            }
            currentPanelColor = map[currentX][currentY];
        }

        LOGGER.info(() -> String.format("Part %s : Number of panels = %s", part, paintedPanels.size()));

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                sb.append(map[x][y] == 1 ? "#" : " ");
            }
            sb.append("\n");
        }
        
        LOGGER.info(() -> String.format("Part %s : Image %n %s", part, sb.toString()));

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