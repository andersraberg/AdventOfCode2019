package se.anders_raberg.adventofcode2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {
    private static final Logger LOGGER = Logger.getLogger(Day4.class.getName());

    private Day4() {
    }

    public static void run() {
        List<List<Integer>> part1 = IntStream.rangeClosed(137683, 596253)
                .mapToObj(v -> Arrays.stream(Integer.toString(v).split("")) //
                        .map(Integer::parseInt) //
                        .collect(Collectors.toList()))
                .filter(Day4::isSorted) //
                .filter(Day4::hasConsecutive) //
                .collect(Collectors.toList());

        LOGGER.info(() -> String.format("Part 1 : No of Passwords %s", part1.size()));

        List<List<Integer>> part2 = part1.stream() //
                .filter(Day4::hasSequenceOfTwo) //
                .collect(Collectors.toList());

        LOGGER.info(() -> String.format("Part 2 : No of Passwords %s", part2.size()));
    }

    private static boolean isSorted(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < list.get(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasConsecutive(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).equals(list.get(i - 1))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSequenceOfTwo(List<Integer> list) {
        List<Integer> tmp = new ArrayList<>();
        int counter = 1;
        int last = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).equals(last)) {
                counter++;
            } else {
                tmp.add(counter);
                counter = 1;
                last = list.get(i);
            }
        }
        tmp.add(counter);
        return tmp.contains(2);
    }

}
