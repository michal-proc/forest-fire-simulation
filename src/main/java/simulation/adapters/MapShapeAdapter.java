package simulation.adapters;

import com.google.gson.stream.JsonWriter;
import simulation.Point;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class MapShapeAdapter {

    public static void saveMap(Point[][] points, String filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath);
             JsonWriter writer = new JsonWriter(fileWriter)) {
            writer.setIndent("  ");
            writer.beginArray();
            for (int x = 0; x < points.length; x++) {
                for (int y = 0; y < points[x].length; y++) {
                    Point point = points[x][y];
                    writer.beginObject();
                    writer.name("x").value(x);
                    writer.name("y").value(y);
                    writer.name("currentState").value(point.currentState.toString());
                    writer.name("elevation").value(point.getElevation());
                    writer.name("height").value(point.getHeight());
                    writer.endObject();
                }
            }
            writer.endArray();
            JOptionPane.showMessageDialog(null, "Map saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
