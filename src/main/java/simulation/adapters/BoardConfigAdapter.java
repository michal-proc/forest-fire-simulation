package simulation.adapters;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import simulation.Directions;
import simulation.records.BoardConfig;
import java.io.IOException;

public class BoardConfigAdapter extends TypeAdapter<BoardConfig> {
    @Override
    public void write(JsonWriter out, BoardConfig config) throws IOException {
        out.beginObject();
        out.name("mapWidth").value(config.mapWidth());
        out.name("mapHeight").value(config.mapHeight());
        out.name("windVelocity").value(config.windVelocity());
        out.name("windDirection").value(config.windDirection().name());
        //out.name("mediumTreeAge").value(config.mediumTreeAge());
        //out.name("mediumTreeAgeVariance").value(config.mediumTreeAgeVariance());
        out.name("mediumMoisture").value(config.mediumMoisture());
        out.name("mediumMoistureVariance").value(config.mediumMoistureVariance());
        out.name("airTemperature").value(config.airTemperature());
        out.name("coniferousBurningTemperature").value(config.coniferousBurningTemperature());
        out.name("deciduousBurningTemperature").value(config.deciduousBurningTemperature());
        out.name("understoryBurningTemperature").value(config.understoryBurningTemperature());
        out.name("floorBurningTemperature").value(config.floorBurningTemperature());
        out.name("litterBurningTemperature").value(config.litterBurningTemperature());
        //out.name("overcast").value(config.overcast());
        //out.name("atmosphericPressure").value(config.atmosphericPressure());
        //out.name("maxFireTemperature").value(config.maxFireTemperature());
        out.name("size").value(config.size());
        out.name("pointPercentage").value(config.pointPercentage());
        out.endObject();
    }

    @Override
    public BoardConfig read(JsonReader in) throws IOException {
        int mapWidth = 0;
        int mapHeight = 0;
        double windVelocity = 0.0;
        Directions windDirection = null;
        double mediumTreeAge = 0.0;
        double mediumTreeAgeVariance = 0.0;
        double mediumMoisture = 0.0;
        double mediumMoistureVariance = 0.0;
        double airTemperature = 0.0;
        double coniferousBurningTemperature = 0.0;
        double deciduousBurningTemperature = 0.0;
        double understoryBurningTemperature = 0.0;
        double floorBurningTemperature = 0.0;
        double litterBurningTemperature = 0.0;
        double overcast = 0.0;
        double atmosphericPressure = 0.0;
        double maxFireTemperature = 0.0;
        int size = 0;
        double pointPercentage = 0.0;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "mapWidth" -> mapWidth = in.nextInt();
                case "mapHeight" -> mapHeight = in.nextInt();
                case "windVelocity" -> windVelocity = in.nextDouble();
                case "windDirection" -> windDirection = Directions.valueOf(in.nextString());
                case "mediumTreeAge" -> mediumTreeAge = in.nextDouble();
                case "mediumTreeAgeVariance" -> mediumTreeAgeVariance = in.nextDouble();
                case "mediumMoisture" -> mediumMoisture = in.nextDouble();
                case "mediumMoistureVariance" -> mediumMoistureVariance = in.nextDouble();
                case "airTemperature" -> airTemperature = in.nextDouble();
                case "coniferousBurningTemperature" -> coniferousBurningTemperature = in.nextDouble();
                case "deciduousBurningTemperature" -> deciduousBurningTemperature = in.nextDouble();
                case "understoryBurningTemperature" -> understoryBurningTemperature = in.nextDouble();
                case "floorBurningTemperature" -> floorBurningTemperature = in.nextDouble();
                case "litterBurningTemperature" -> litterBurningTemperature = in.nextDouble();
                case "overcast" -> overcast = in.nextDouble();
                case "atmosphericPressure" -> atmosphericPressure = in.nextDouble();
                case "maxFireTemperature" -> maxFireTemperature = in.nextDouble();
                case "size" -> size = in.nextInt();
                case "pointPercentage" -> pointPercentage = in.nextDouble();
            }
        }
        in.endObject();
        return new BoardConfig(
                mapWidth,
                mapHeight,
                windVelocity,
                windDirection,
                //mediumTreeAge,
                //mediumTreeAgeVariance,
                mediumMoisture,
                mediumMoistureVariance,
                airTemperature,
                coniferousBurningTemperature,
                deciduousBurningTemperature,
                understoryBurningTemperature,
                floorBurningTemperature,
                litterBurningTemperature,
                //overcast,
                //atmosphericPressure,
                //maxFireTemperature,
                size,
                pointPercentage
        );
    }
}

