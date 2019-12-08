package se.anders_raberg.adventofcode2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class Day8 {
    private static final Logger LOGGER = Logger.getLogger(Day8.class.getName());
    private static final int IMAGE_WIDTH = 25;
    private static final int IMAGE_HEIGHT = 6;
    private static final int LAYER_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT;

    private Day8() {
    }

    public static void run() throws IOException {
        List<Integer> values = Arrays
                .stream(new String(Files.readAllBytes(Paths.get("inputs/input8.txt"))).trim().split(""))
                .map(Integer::valueOf) //
                .collect(Collectors.toList());

        List<List<Integer>> layers = Lists.partition(values, LAYER_SIZE);

        // Part 1
        List<Integer> layerMostZeros = layers.stream() //
                .sorted(Day8::compareNoOfZeros) //
                .findFirst().orElseThrow();

        LOGGER.info(() -> String.format("Part 1: %s",
                numberofDigits(layerMostZeros, 1) * numberofDigits(layerMostZeros, 2)));

        // Part 2
        Map<Integer, List<Integer>> image = new HashMap<>();
        layers.stream().forEach(layer -> {
            for (int i = 0; i < layer.size(); i++) {
                image.computeIfAbsent(i, k -> new ArrayList<>()).add(layer.get(i));
            }
        });

        List<Integer> collect = image.entrySet().stream() //
                .sorted((a, b) -> a.getKey() - b.getKey()) //
                .map(p -> pixelColor(p.getValue())) //
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collect.size(); i++) {
            sb.append(collect.get(i) == 1 ? "#" : " ");
            if ((i + 1) % IMAGE_WIDTH == 0) {
                sb.append("\n");
            }
        }

        LOGGER.info(() -> String.format("Part 2: %n%s", sb));
    }

    private static int numberofDigits(List<Integer> list, int digit) {
        return (int) list.stream().filter(d -> d == digit).count();
    }

    private static int compareNoOfZeros(List<Integer> lista, List<Integer> listb) {
        return numberofDigits(lista, 0) - numberofDigits(listb, 0);
    }

    private static int pixelColor(List<Integer> pixelLayers) {
        return pixelLayers.stream() //
                .filter(p -> p == 0 || p == 1) //
                .findFirst().orElseThrow();
    }
}
