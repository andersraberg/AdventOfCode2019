package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        Pair<Pair<Double, Double>, Integer> maxVisible = coordinates.stream() //
                .map(stationCoord -> new Pair<>(stationCoord, coordinates.stream() //
                        .filter(c -> !c.equals(stationCoord)) //
                        .map(c -> azimuth(stationCoord, c)) //
                        .distinct().collect(Collectors.toList()).size()))
                .sorted((a, b) -> b.second() - a.second()).findFirst().orElseThrow();

        LOGGER.info(() -> String.format("Part 1 : Max visible asteroids for %s is %s", maxVisible.first(),
                maxVisible.second()));

        Pair<Double, Double> laserOrigin = maxVisible.first();

        List<Pair<Double, Double>> allSortedByAzimuth = coordinates.stream().filter(c -> !c.equals(laserOrigin))
                .sorted((a, b) -> compareTo(a, b, laserOrigin)).collect(Collectors.toList());

        List<Double> azimuths = allSortedByAzimuth.stream().map(c -> azimuth(laserOrigin, c)).sorted().distinct()
                .collect(Collectors.toList());

        Collections.reverse(azimuths);

        int currentIndex = azimuths.indexOf(azimuths.stream().filter(a -> a <= 180.0).findFirst().orElseThrow());
        int count = 0;

        Pair<Double, Double> lastDestroyed = null;
        while (count < 200) {
            final int index = currentIndex;
            Optional<Pair<Double, Double>> destroyed = allSortedByAzimuth.stream()
                    .filter(c -> azimuth(laserOrigin, c).equals(azimuths.get(index))).findFirst();
            if (destroyed.isPresent()) {
                lastDestroyed = destroyed.get();
                allSortedByAzimuth.remove(lastDestroyed);
                count++;
            }
            currentIndex = (currentIndex + 1) % azimuths.size();
        }

        LOGGER.info(String.format("Part 2 : Last destroyed %s", lastDestroyed));
    }

    private static int compareTo(Pair<Double, Double> a, Pair<Double, Double> b, Pair<Double, Double> origo) {
        if (Math.round(azimuth(origo, b) - azimuth(origo, a)) == 0) {
            return (int) Math.round(distance(origo, a) - distance(origo, b));
        }

        return (int) Math.round(azimuth(origo, b) - azimuth(origo, a));
    }

    private static Double azimuth(Pair<Double, Double> a, Pair<Double, Double> b) {
        return Math.toDegrees(normalize(Math.atan2(b.first() - a.first(), b.second() - a.second())));
    }

    private static Double distance(Pair<Double, Double> a, Pair<Double, Double> b) {
        double dx = b.first() - a.first();
        double dy = b.second() - a.second();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double normalize(double angle) {
        return (angle >= 0 ? angle : ((2 * Math.PI) - ((-angle) % (2 * Math.PI)))) % (2 * Math.PI);
    }

}
