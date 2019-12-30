package se.anders_raberg.adventofcode2019;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.math.LongMath;

import se.anders_raberg.adventofcode2019.utilities.Pair;

public class Day12 {
    private static final Logger LOGGER = Logger.getLogger(Day12.class.getName());

    private Day12() {
    }

    private static class Moon {
        private final int _xPos;
        private final int _yPos;
        private final int _zPos;
        private final int _xVel;
        private final int _yVel;
        private final int _zVel;

        public Moon(int xPos, int yPos, int zPos, int xVel, int yVel, int zVel) {
            _xPos = xPos;
            _yPos = yPos;
            _zPos = zPos;
            _xVel = xVel;
            _yVel = yVel;
            _zVel = zVel;
        }

        public int xPos() {
            return _xPos;
        }

        public int yPos() {
            return _yPos;
        }

        public int zPos() {
            return _zPos;
        }

        public int xVel() {
            return _xVel;
        }

        public int yVel() {
            return _yVel;
        }

        public int zVel() {
            return _zVel;
        }

        public int getEnergy() {
            int potentialEnergy = Math.abs(_xPos) + Math.abs(_yPos) + Math.abs(_zPos);
            int kinecticEnergy = Math.abs(_xVel) + Math.abs(_yVel) + Math.abs(_zVel);
            return potentialEnergy * kinecticEnergy;
        }

        @Override
        public String toString() {
            return String.format("pos=<x=%3d, y=%3d, z=%3d>, vel=<x=%3d, y=%3d, z=%3d>", //
                    _xPos, _yPos, _zPos, _xVel, _yVel, _zVel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_xPos, _xVel, _yPos, _yVel, _zPos, _zVel);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Moon other = (Moon) obj;
            return _xPos == other._xPos && _xVel == other._xVel && _yPos == other._yPos && _yVel == other._yVel
                    && _zPos == other._zPos && _zVel == other._zVel;
        }

    }

    private static final Moon IO = new Moon(-6, -5, -8, 0, 0, 0);
    private static final Moon GANYMEDE = new Moon(0, -3, -13, 0, 0, 0);
    private static final Moon EUROPA = new Moon(-15, 10, -11, 0, 0, 0);
    private static final Moon CALLISTO = new Moon(-3, -8, 3, 0, 0, 0);

    public static void run() {
        List<Moon> moons = List.of(IO, GANYMEDE, EUROPA, CALLISTO);

        for (int i = 0; i < 1000; i++) {
            moons = performStep(moons);
        }

        LOGGER.info(String.format("Part 1 : Energy : %s", moons.stream().mapToInt(Moon::getEnergy).sum()));

        moons = List.of(IO, GANYMEDE, EUROPA, CALLISTO);
        Optional<Integer> xCycle = Optional.empty();
        Optional<Integer> yCycle = Optional.empty();
        Optional<Integer> zCycle = Optional.empty();

        Set<List<Pair<Integer, Integer>>> xSet = new HashSet<>();
        Set<List<Pair<Integer, Integer>>> ySet = new HashSet<>();
        Set<List<Pair<Integer, Integer>>> zSet = new HashSet<>();

        int steps = 0;
        while (xCycle.isEmpty() || yCycle.isEmpty() || zCycle.isEmpty()) {
            moons = performStep(moons);

            if (!xSet.add(getXCoords(moons)) && xCycle.isEmpty()) {
                xCycle = Optional.of(steps);
            }

            if (!ySet.add(getYCoords(moons)) && yCycle.isEmpty()) {
                yCycle = Optional.of(steps);
            }

            if (!zSet.add(getZCoords(moons)) && zCycle.isEmpty()) {
                zCycle = Optional.of(steps);
            }

            steps++;
        }

        LOGGER.info("Part 2: Steps : " + lcm(lcm(xCycle.get(), yCycle.get()), zCycle.get()));
    }

    private static List<Pair<Integer, Integer>> getXCoords(List<Moon> moons) {
        return moons.stream().map(m -> new Pair<>(m.xPos(), m.xVel())).collect(Collectors.toList());
    }

    private static List<Pair<Integer, Integer>> getYCoords(List<Moon> moons) {
        return moons.stream().map(m -> new Pair<>(m.yPos(), m.yVel())).collect(Collectors.toList());
    }

    private static List<Pair<Integer, Integer>> getZCoords(List<Moon> moons) {
        return moons.stream().map(m -> new Pair<>(m.zPos(), m.zVel())).collect(Collectors.toList());
    }

    private static long lcm(long n1, long n2) {
        return (n1 == 0 || n2 == 0) ? 0 : Math.abs(n1 * n2) / LongMath.gcd(n1, n2);
    }

    private static List<Moon> performStep(List<Moon> moons) {
        return moons.stream() //
                .map(m -> applyGravityFromOthers(m, moons)) //
                .map(Day12::applyVelocity) //
                .collect(Collectors.toList()); //
    }

    private static Moon applyGravityFromOthers(Moon moon, List<Moon> allMoons) {
        Moon result = moon;
        for (Moon other : allMoons) {
            result = applyGravity(result, other);
        }
        return result;
    }

    private static Moon applyVelocity(Moon moon) {
        return new Moon(moon.xPos() + moon.xVel(), //
                moon.yPos() + moon.yVel(), //
                moon.zPos() + moon.zVel(), //
                moon.xVel(), moon.yVel(), moon.zVel());
    }

    private static Moon applyGravity(Moon affectee, Moon affector) {
        return new Moon(affectee.xPos(), affectee.yPos(), affectee.zPos(), //
                affectee.xVel() + Integer.compare(affector.xPos(), affectee.xPos()), //
                affectee.yVel() + Integer.compare(affector.yPos(), affectee.yPos()), //
                affectee.zVel() + Integer.compare(affector.zPos(), affectee.zPos()));
    }
}
