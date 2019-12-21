package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day14 {
    private static final Logger LOGGER = Logger.getLogger(Day14.class.getName());
    private static final Pattern PATTERN_1 = Pattern.compile("(.+?) => (\\d+) ([A-Z]+)");
    private static final Pattern PATTERN_2 = Pattern.compile(" *(\\d+) ([A-Z]+),* *");

    private Day14() {
    }

    public static void run() throws IOException {
        Map<Pair<String, Integer>, List<Pair<String, Integer>>> reactions = new LinkedHashMap<>();

        Files.readAllLines(Paths.get("inputs/input14.txt")).stream().forEach(line -> {
            Matcher m1 = PATTERN_1.matcher(line);
            if (m1.matches()) {
                Pair<String, Integer> output = new Pair<>(m1.group(3), Integer.parseInt(m1.group(2)));
                Matcher m2 = PATTERN_2.matcher(m1.group(1));
                List<Pair<String, Integer>> inputs = new ArrayList<>();
                while (m2.find()) {
                    inputs.add(new Pair<>(m2.group(2), Integer.parseInt(m2.group(1))));
                }
                reactions.put(output, inputs);
            }
        });

        Map<String, Integer> storedQuantities = reactions.keySet().stream()
                .collect(Collectors.toMap(Pair::first, k -> 0));

        storedQuantities.put("ORE", Integer.MAX_VALUE);

        while (storedQuantities.get("FUEL") == 0) {
            tryPerformReaction2(reactions, new Pair<>("FUEL", 1), storedQuantities);
        }
        LOGGER.info("Part 1 : Consumed ore quantity = " + (Integer.MAX_VALUE - storedQuantities.get("ORE")));

    }

    private static void tryPerformReaction2(Map<Pair<String, Integer>, List<Pair<String, Integer>>> reactionMap,
            Pair<String, Integer> wantedOutput, Map<String, Integer> store) {
        List<Pair<String, Integer>> list = reactionMap.get(wantedOutput);
        for (Pair<String, Integer> pair : list) {
            if (store.get(pair.first()) < pair.second()) {
                tryPerformReaction2(reactionMap, getReactionForChemical(reactionMap, pair.first()), store);
            }
        }
        boolean allMatch = reactionMap.get(wantedOutput).stream().allMatch(a -> store.get(a.first()) >= a.second());
        if (allMatch) {
            reactionMap.get(wantedOutput).stream().forEach(a -> store.compute(a.first(), (k, v) -> v - a.second()));
            store.compute(wantedOutput.first(), (k, v) -> v + wantedOutput.second());
        }

    }

    private static Pair<String, Integer> getReactionForChemical(
            Map<Pair<String, Integer>, List<Pair<String, Integer>>> reactionMap, String chemical) {
        return reactionMap.keySet().stream().filter(q -> q.first().equals(chemical)).findFirst().orElseThrow();
    }

}
