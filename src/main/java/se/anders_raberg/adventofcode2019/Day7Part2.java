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

import com.google.common.collect.Collections2;

public class Day7Part2 {
    private static final Logger LOGGER = Logger.getLogger(Day7Part2.class.getName());

    private Day7Part2() {
    }

    private static class Amplifier {
        private final List<Integer> _program;
        private final Queue<Integer> _input = new ArrayBlockingQueue<>(2);
        private int _c = 0;
        private List<Integer> _output = new ArrayList<>();

        public Amplifier(List<Integer> program, int phase) {
            _program = new ArrayList<>(program);
            _input.add(phase);
        }

        public List<Integer> getOutput() {
            List<Integer> tmp = new ArrayList<>(_output);
            _output.clear();
            return tmp;
        }

        private void executeProgram(List<Integer> input) {
            _input.addAll(input);
            while (true) {
                int opCode = _program.get(_c) % 100;
                int val0;
                int val1;
                List<Boolean> immediateMode = Arrays.stream(String.format("%03d", _program.get(_c) / 100).split(""))
                        .map(d -> d.equals("1")) //
                        .collect(Collectors.toList());
                Collections.reverse(immediateMode);

                switch (opCode) {
                case 1:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    _program.set(_program.get(_c + 3), val0 + val1);
                    _c += 4;
                    break;

                case 2:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    _program.set(_program.get(_c + 3), val0 * val1);
                    _c += 4;
                    break;

                case 3:
                    Integer inp = _input.poll();
                    if (inp == null) {
                        return;
                    }
                    _program.set(_program.get(_c + 1), inp);
                    _c += 2;
                    break;

                case 4:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    _c += 2;
                    _output.add(val0);
                    break;

                case 5:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    if (val0 != 0) {
                        _c = val1;
                    } else {
                        _c += 3;
                    }
                    break;

                case 6:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    if (val0 == 0) {
                        _c = val1;
                    } else {
                        _c += 3;
                    }
                    break;

                case 7:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    _program.set(_program.get(_c + 3), val0 < val1 ? 1 : 0);
                    _c += 4;
                    break;

                case 8:
                    val0 = immediateMode.get(0) ? _program.get(_c + 1) : _program.get(_program.get(_c + 1));
                    val1 = immediateMode.get(1) ? _program.get(_c + 2) : _program.get(_program.get(_c + 2));
                    _program.set(_program.get(_c + 3), val0 == val1 ? 1 : 0);
                    _c += 4;
                    break;

                case 99:
                    return;

                default:
                    throw new IllegalArgumentException("Unknown opCode: " + opCode);
                }
            }
        }
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input7.txt"))).trim().split(","))
                .map(Integer::valueOf).collect(Collectors.toList());

        int maxThruster = Collections2.permutations(List.of(5, 6, 7, 8, 9)).stream()
                .mapToInt(p -> thrusterValue(values, p)) //
                .max().orElseThrow();

        LOGGER.info(() -> String.format("Part 2 : %s", maxThruster));
    }

    private static int thrusterValue(List<Integer> program, List<Integer> phaseSettings) {
        Amplifier ampA = new Amplifier(program, phaseSettings.get(0));
        Amplifier ampB = new Amplifier(program, phaseSettings.get(1));
        Amplifier ampC = new Amplifier(program, phaseSettings.get(2));
        Amplifier ampD = new Amplifier(program, phaseSettings.get(3));
        Amplifier ampE = new Amplifier(program, phaseSettings.get(4));

        List<Integer> res = List.of(0);
        while (true) {
            ampA.executeProgram(res);
            List<Integer> output = ampA.getOutput();

            if (output.isEmpty()) {
                break;
            }

            res = output;

            ampB.executeProgram(res);
            res = ampB.getOutput();

            ampC.executeProgram(res);
            res = ampC.getOutput();

            ampD.executeProgram(res);
            res = ampD.getOutput();

            ampE.executeProgram(res);
            res = ampE.getOutput();
        }

        return res.get(0);
    }
}
