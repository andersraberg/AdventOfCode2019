package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day3 {
    private static final Logger LOGGER = Logger.getLogger(Day3.class.getName());

    private Day3() {
    }

    private enum Direction {
        R, L, U, D;
    }

    private static class Movement {
        private final Direction _direction;
        private final Integer _distance;

        public Movement(Direction direction, Integer distance) {
            _direction = direction;
            _distance = distance;
        }

        public Direction direction() {
            return _direction;
        }

        public Integer distance() {
            return _distance;
        }

        public static Movement parse(String str) {
            return new Movement(Direction.valueOf(str.substring(0, 1)),
                    Integer.valueOf(str.substring(1, str.length())));
        }

        @Override
        public String toString() {
            return _direction + ":" + _distance;
        }
    }

    public static void run() throws IOException {
        List<List<Movement>> wires = Files.readAllLines(Paths.get("inputs/input3.txt")).stream() //
                .map(m -> Arrays.stream(m.split(",")) //
                        .map(q -> Movement.parse(q)) //
                        .collect(Collectors.toList())) //
                .collect(Collectors.toList());

        List<Pair<Integer, Integer>> path0 = walkPath(wires.get(0));
        List<Pair<Integer, Integer>> path1 = walkPath(wires.get(1));

        Set<Pair<Integer, Integer>> pointSet0 = new HashSet<>(path0);
        Set<Pair<Integer, Integer>> pointSet1 = new HashSet<>(path1);

        Set<Pair<Integer, Integer>> intersections = pointSet0.stream().filter(pointSet1::contains)
                .collect(Collectors.toSet());

        // Part 1
        int minDist = intersections.stream().mapToInt(p -> Math.abs(p.first()) + Math.abs(p.second()))
                .filter(v -> v > 0).min().getAsInt();
        LOGGER.info(() -> String.format("Part 1 : Min distance %s", minDist));

        // Part 2
        int minSteps = intersections.stream().mapToInt(p -> path0.indexOf(p) + path1.indexOf(p)).filter(v -> v > 0)
                .min().getAsInt();
        LOGGER.info(() -> String.format("Part 2 : Min combined steps %s", minSteps));
    }

    private static List<Pair<Integer, Integer>> walkPath(List<Movement> movements) {
        List<Pair<Integer, Integer>> path = new ArrayList<>();
        int xPos = 0;
        int yPos = 0;
        for (Movement move : movements) {
            int currentXPos = xPos;
            int currentYPos = yPos;
            switch (move.direction()) {
            case D:
                while (yPos < currentYPos + move.distance()) {
                    path.add(new Pair<>(xPos, yPos++));
                }
                break;
            case U:
                while (yPos > currentYPos - move.distance()) {
                    path.add(new Pair<>(xPos, yPos--));
                }
                break;
            case R:
                while (xPos < currentXPos + move.distance()) {
                    path.add(new Pair<>(xPos++, yPos));
                }
                break;
            case L:
                while (xPos > currentXPos - move.distance()) {
                    path.add(new Pair<>(xPos--, yPos));
                }
                break;
            }
        }
        return path;
    }

}
