package simulation.records;
import simulation.Directions;

public record  BoardConfig (
        int mapWidth,
        int mapHeight,
        double windVelocity,
        Directions windDirection,
        //double mediumTreeAge,
        //double mediumTreeAgeVariance,
        double mediumMoisture,
        double mediumMoistureVariance,
        double airTemperature,
        double coniferousBurningTemperature,
        double deciduousBurningTemperature,
        double understoryBurningTemperature,
        double floorBurningTemperature,
        double litterBurningTemperature,
        //double overcast,
        //double atmosphericPressure,
        //double maxFireTemperature,
        int size,
        double pointPercentage
) {
}
