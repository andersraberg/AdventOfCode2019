package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day6 {
    private static final String COM = "COM";
    private static final String SAN = "SAN";
    private static final String YOU = "YOU";
    private static final Logger LOGGER = Logger.getLogger(Day6.class.getName());

    private Day6() {
    }

    public static void run() throws IOException {
        Map<String, String> orbitMapping = Files.readAllLines(Paths.get("inputs/input6.txt")).stream() //
                .map(Day6::parseOrbit) //
                .collect(Collectors.toMap(Pair::second, Pair::first));

        LOGGER.info(() -> String.format("Part 1 : Orbits: %s",
                orbitMapping.keySet().stream().mapToInt(o -> getPath(orbitMapping, o, COM).size()).sum()));

        List<String> sanToComPath = getPath(orbitMapping, SAN, COM);
        List<String> youToComPath = getPath(orbitMapping, YOU, COM);
        Set<String> commonOrbits = new HashSet<>(sanToComPath);
        commonOrbits.retainAll(youToComPath);

        int min = commonOrbits.stream()
                .mapToInt(o -> getPath(orbitMapping, SAN, o).size() + getPath(orbitMapping, YOU, o).size()).min()
                .getAsInt();

        LOGGER.info(() -> String.format("Part 2 : Orbit path length: %s", min - 2));

    }

    private static Pair<String, String> parseOrbit(String orbit) {
        String[] split = orbit.split("\\)");
        return new Pair<>(split[0], split[1]);
    }

    private static List<String> getPath(Map<String, String> mapping, String start, String end) {
        List<String> path = new ArrayList<>();
        String obj = start;
        while (!obj.equals(end)) {
            obj = mapping.get(obj);
            path.add(obj);
        }
        return path;
    }
}
