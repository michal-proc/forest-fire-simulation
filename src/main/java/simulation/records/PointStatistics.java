package simulation.records;

import java.util.List;

public record PointStatistics(
        int pointX,
        int pointY,
        int elevation,
        boolean litter,
        boolean floor,
        boolean understory,
        boolean coniferous,
        boolean deciduous,
        double height,
        boolean fireSource,
        List<Double> state,
        List<Double> temperature,
        double humidity,
        String currentState,
        List<Boolean> onFire
) {
}