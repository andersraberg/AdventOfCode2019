package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day10 {
    private static final Logger LOGGER = Logger.getLogger(Day10.class.getName());

    private Day10() {
    }

    public static void run() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("inputs/input10.txt")).stream().collect(Collectors.toList());

        List<Pair<Double, Double>> coordinates = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String[] split = lines.get(i).trim().split("");
            for (int j = 0; j < split.length; j++) {
                if (split[j].equals("#")) {
                    coordinates.add(new Pair<>((double) j, (double) i));
                }
            }
        }

        DecimalFormat df = new DecimalFormat("###.####");

        Integer maxVisibleAsteroids = coordinates.stream() //
                .map(stationCoord -> coordinates.stream() //
                        .filter(c -> !c.equals(stationCoord)) //
                        .map(c -> azimuth(stationCoord, c)) //
                        .map(df::format) //
                        .distinct().collect(Collectors.toList()).size())
                .max(Integer::compareTo).orElseThrow();

        LOGGER.info(() -> String.format("Part 1 : Max visible asteroids : %s", maxVisibleAsteroids));
        
    }

    private static Double azimuth(Pair<Double, Double> a, Pair<Double, Double> b) {
        return Math.atan2(b.first() - a.first(), b.second() - a.second());
    }

}
