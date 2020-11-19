package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day14 {
    private static final Logger LOGGER = Logger.getLogger(Day14.class.getName());
    private static final Pattern PATTERN_1 = Pattern.compile("(.+?) => (\\d+) ([A-Z]+)");
    private static final Pattern PATTERN_2 = Pattern.compile(" *(\\d+) ([A-Z]+),* *");
    private static final Map<Pair<String, Integer>, List<Pair<String, Integer>>> REACTIONS = new LinkedHashMap<>();
    private static final Map<String, List<String>> REACTION_TYPES = new LinkedHashMap<>();

    private static Map<String, Integer> storedQuantities;

    private Day14() {
    }

    public static void run() throws IOException {

        Files.readAllLines(Paths.get("inputs/input14x.txt")).stream().forEach(line -> {
            Matcher m1 = PATTERN_1.matcher(line);
            if (m1.matches()) {
                String outputType = m1.group(3);
                Pair<String, Integer> output = new Pair<>(outputType, Integer.parseInt(m1.group(2)));
                Matcher m2 = PATTERN_2.matcher(m1.group(1));
                List<String> inputTypes = new ArrayList<>();
                List<Pair<String, Integer>> inputs = new ArrayList<>();
                while (m2.find()) {
                    String inputType = m2.group(2);
                    inputTypes.add(inputType);
                    inputs.add(new Pair<>(inputType, Integer.parseInt(m2.group(1))));
                }
                REACTION_TYPES.put(outputType, inputTypes);
                REACTIONS.put(output, inputs);
            }
        });

        System.out.println(REACTION_TYPES);
        System.out.println(REACTIONS);

        storedQuantities = Collections
                .synchronizedMap(REACTION_TYPES.keySet().stream().collect(Collectors.toMap(k -> k, k -> 0)));

        storedQuantities.put("ORE", Integer.MAX_VALUE);

        while (storedQuantities.get("FUEL") == 0) {
            tryPerformReaction2(new Pair<>("FUEL", 1));
        }
        LOGGER.info("Part 1 : Consumed ore quantity = " + (Integer.MAX_VALUE - storedQuantities.get("ORE")));
        long qqq = (long) Integer.MAX_VALUE - storedQuantities.get("ORE");

        System.out.println(qqq);
        // long ffff = 1_000_000_000_000L / qqq;
        long qqq2 = 1_000_000_000_000L - 75_110_000 * qqq;
        System.out.println(qqq2);

    }

//    private static void apa(String chemical) {
//        List<Pair<String, Integer>> reactionForChemical = getReactionForChemical2(chemical);
//        while (storedQuantities.get(chemical)).
//        
//        
//        
//        while (storedQuantities.get(wantedOutput.first()) < wantedOutput.second()) {
//            List<Pair<String, Integer>> list = REACTIONS.get(getReactionForChemical(wantedOutput.first()));
//            for (Pair<String, Integer> pair : list) {
//                apa(pair);
//            }
//        }
//        
//        
//        List<Pair<String, Integer>> list = REACTIONS.get(wantedOutput);
//        for (Pair<String, Integer> pair : list) {
//            while (storedQuantities.get(pair.first()) < pair.second()) {
//                tryPerformReaction(getReactionForChemical(pair.first()));
//            }
//        }
//        boolean allMatch = REACTIONS.get(wantedOutput).stream()
//                .allMatch(a -> storedQuantities.get(a.first()) >= a.second());
//        if (allMatch) {
//            REACTIONS.get(wantedOutput).stream()
//                    .forEach(a -> storedQuantities.compute(a.first(), (k, v) -> v - a.second()));
//            storedQuantities.compute(wantedOutput.first(), (k, v) -> v + wantedOutput.second());
//        }
//
//    }

    private static void tryPerformReaction2(Pair<String, Integer> wantedOutput) {
        List<Pair<String, Integer>> list = REACTIONS.get(wantedOutput);
        for (Pair<String, Integer> pair : list) {
            while (storedQuantities.get(pair.first()) < pair.second()) {
                tryPerformReaction2(getReactionForChemical(pair.first()));
            }
        }
        boolean allMatch = REACTIONS.get(wantedOutput).stream()
                .allMatch(a -> storedQuantities.get(a.first()) >= a.second());
        if (allMatch) {
            REACTIONS.get(wantedOutput).stream()
                    .forEach(a -> storedQuantities.compute(a.first(), (k, v) -> v - a.second()));
            storedQuantities.compute(wantedOutput.first(), (k, v) -> v + wantedOutput.second());
        }

    }

    private static Pair<String, Integer> getReactionForChemical(String chemical) {
        return REACTIONS.keySet().stream().filter(q -> q.first().equals(chemical)).findFirst().orElseThrow();
    }

    private static List<Pair<String, Integer>> getReactionForChemical2(String chemical) {
        return REACTIONS.entrySet().stream().filter(e -> e.getKey().first().equals(chemical)).map(Entry::getValue).findFirst().orElseThrow();
    }

}
