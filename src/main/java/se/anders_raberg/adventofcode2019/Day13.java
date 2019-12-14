package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day13 {
    private static final Logger LOGGER = Logger.getLogger(Day13.class.getName());

    private Day13() {
    }

    public static void run() throws IOException {
        List<Long> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input13.txt"))).trim().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        for (long i = 0; i < 1000; i++) {
            values.add((long) 0);
        }

        // Part 1
        Computer computerPart1 = new Computer(new ArrayList<>(values));
        computerPart1.executeProgram(List.of());
        List<Long> outPut = computerPart1.getOutput();

        Map<Pair<Long, Long>, Long> tileMap = new HashMap<>();
        Lists.partition(outPut, 3).forEach(p -> tileMap.put(new Pair<>(p.get(0), p.get(1)), p.get(2)));
        LOGGER.info(String.format("Part 1 : Block tiles %s",
                tileMap.values().stream().filter(t -> t.equals((long) 2)).count()));

        // Part 2

        Pair<Long, Long> ball = new Pair<>(0L, 0L);
        Pair<Long, Long> paddle = new Pair<>(0L, 0L);
        long score = 0;

        List<Long> program = new ArrayList<>(values);
        program.set(0, (long) 2);

        Computer computerPart2 = new Computer(program);
        computerPart2.executeProgram(List.of());
        outPut = computerPart2.getOutput();

        while (!outPut.isEmpty()) {
            for (List<Long> instr : Lists.partition(outPut, 3)) {
                if (instr.get(0).equals(-1L) && instr.get(1).equals(0L)) {
                    score = instr.get(2);
                } else {
                    switch (instr.get(2).intValue()) {
                    case 3:
                        paddle = new Pair<>(instr.get(0), instr.get(1));
                        break;

                    case 4:
                        ball = new Pair<>(instr.get(0), instr.get(1));
                        break;
                    default:
                        break;
                    }

                }
            }
            long joystick = Integer.compare(ball.first().intValue(), paddle.first().intValue());
            computerPart2.executeProgram(List.of(joystick));
            outPut = computerPart2.getOutput();
        }

        LOGGER.info(String.format("Part 2 : Score %s", score));
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