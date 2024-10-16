package simulation;
import simulation.adapters.BoardConfigAdapter;
import simulation.records.BoardConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class StartScreen extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private final JTextField mapWidthField;
    private final JTextField mapHeightField;
    private final JTextField windVelocityField;
    private final JComboBox<Directions> windDirectionField;
    //private final JTextField mediumTreeAgeField;
    //private final JTextField mediumTreeAgeVarianceField;
    private final JTextField mediumMoistureField;
    private final JTextField mediumMoistureVarianceField;
    private final JTextField airTemperatureField;
    private final JTextField coniferousBurningTemperatureField;
    private final JTextField deciduousBurningTemperatureField;
    private final JTextField understoryBurningTemperatureField;
    private final JTextField floorBurningTemperatureField;
    private final JTextField litterBurningTemperatureField;
    //private final JTextField overcastField;
    //private final JTextField atmosphericPressureField;
    //private final JTextField maxFireTemperatureField;
    private final JTextField sizeField;
    private final JTextField pointPercentageField;

    public StartScreen() {
        setTitle("Forest Fire Simulation - Start");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel headingLabel = new JLabel("Put start Forest parameters", SwingConstants.CENTER);
        headingLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(headingLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(18, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mapWidthField = createField(formPanel, "Map Width:", "60");
        mapHeightField = createField(formPanel, "Map Height:", "60");
        windVelocityField = createField(formPanel, "Wind Velocity (km/h):", "10.0");
        windDirectionField = new JComboBox<>(Directions.values());
        formPanel.add(new JLabel("Wind Direction:"));
        formPanel.add(windDirectionField);
        //mediumTreeAgeField = createField(formPanel, "Medium Tree Age:", "1.0");
        //mediumTreeAgeVarianceField = createField(formPanel, "Medium Tree Age Variance:", "1.0");
        mediumMoistureField = createField(formPanel, "Medium Moisture (0.0-1.0):", "0.4");
        mediumMoistureVarianceField = createField(formPanel, "Medium Moisture Variance (0.0-1.0):", "0.001");
        airTemperatureField = createField(formPanel, "Air temperature (C): ", "20.0");
        coniferousBurningTemperatureField = createField(formPanel, "Coniferous Tree Burning Temperature (C):", "260.0");
        deciduousBurningTemperatureField = createField(formPanel, "Deciduous Tree Burning Temperature (C):", "310.0");
        understoryBurningTemperatureField = createField(formPanel, "Understory Burning Temperature (C):", "300.0");
        floorBurningTemperatureField = createField(formPanel, "Floor Burning Temperature (C):", "275.0");
        litterBurningTemperatureField = createField(formPanel, "Litter Burning Temperature (C):", "215.0");
        //overcastField = createField(formPanel, "Overcast:", "1.0");
        //atmosphericPressureField = createField(formPanel, "Atmospheric Pressure:", "1.0");
        //maxFireTemperatureField = createField(formPanel, "Max Fire Temperature:", "1.0");
        sizeField = createField(formPanel, "Size (pixels):", "7");
        pointPercentageField = createField(formPanel, "Point Percentage:", "0.1");

        JButton startButton = createButton("Start Simulation", "SansSerif", 18, this::startSimulation);
        JButton saveButton = createButton("Save simulation parameters", "SansSerif", 18, this::save);
        JButton loadButton = createButton("Load simulation parameters", "SansSerif", 18, this::load);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.setSize(1024, 768);
        this.setLocationRelativeTo(null); // Center the window
        this.setVisible(true);
    }

    private JTextField createField(JPanel panel, String label, String defaultValue) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField(defaultValue);
        panel.add(textField);
        return textField;
    }

    private JButton createButton(String buttonName, String fontName, int fontSize, Runnable function) {
        JButton button = new JButton(buttonName);
        button.setFont(new Font(fontName, Font.BOLD, fontSize));
        button.addActionListener(e -> function.run());
        return button;
    }

    private void save() {
        BoardConfig config = createBoardConfigFromFields();
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }
            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(config, writer);
                JOptionPane.showMessageDialog(this, "Parameters saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving parameters: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void load() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Reader reader = new FileReader(file)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(BoardConfig.class, new BoardConfigAdapter())
                        .create();
                BoardConfig config = gson.fromJson(reader, BoardConfig.class);
                System.out.println("Loaded config: " + gson.toJson(config));
                setFieldsFromBoardConfig(config);
                JOptionPane.showMessageDialog(this, "Parameters loaded successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading parameters: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BoardConfig createBoardConfigFromFields() {
        int mapWidth = Integer.parseInt(mapWidthField.getText());
        int mapHeight = Integer.parseInt(mapHeightField.getText());
        double windVelocity = Double.parseDouble(windVelocityField.getText());
        Directions windDirection = (Directions) windDirectionField.getSelectedItem();
        //double mediumTreeAge = Double.parseDouble(mediumTreeAgeField.getText());
        //double mediumTreeAgeVariance = Double.parseDouble(mediumTreeAgeVarianceField.getText());
        double mediumMoisture = Double.parseDouble(mediumMoistureField.getText());
        double mediumMoistureVariance = Double.parseDouble(mediumMoistureVarianceField.getText());
        double airTemperature = Double.parseDouble(airTemperatureField.getText());
        double coniferousBurningTemperature = Double.parseDouble(coniferousBurningTemperatureField.getText());
        double deciduousBurningTemperature = Double.parseDouble(deciduousBurningTemperatureField.getText());
        double understoryBurningTemperature = Double.parseDouble(understoryBurningTemperatureField.getText());
        double floorBurningTemperature = Double.parseDouble(floorBurningTemperatureField.getText());
        double litterBurningTemperature = Double.parseDouble(litterBurningTemperatureField.getText());
        //double overcast = Double.parseDouble(overcastField.getText());
        //double atmosphericPressure = Double.parseDouble(atmosphericPressureField.getText());
        //double maxFireTemperature = Double.parseDouble(maxFireTemperatureField.getText());
        int size = Integer.parseInt(sizeField.getText());
        double pointPercentage = Double.parseDouble(pointPercentageField.getText());

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

    private void setFieldsFromBoardConfig(BoardConfig config) {
        mapWidthField.setText(String.valueOf(config.mapWidth()));
        mapHeightField.setText(String.valueOf(config.mapHeight()));
        windVelocityField.setText(String.valueOf(config.windVelocity()));
        windDirectionField.setSelectedItem(config.windDirection());
        //mediumTreeAgeField.setText(String.valueOf(config.mediumTreeAge()));
        //mediumTreeAgeVarianceField.setText(String.valueOf(config.mediumTreeAgeVariance()));
        mediumMoistureField.setText(String.valueOf(config.mediumMoisture()));
        mediumMoistureVarianceField.setText(String.valueOf(config.mediumMoistureVariance()));
        airTemperatureField.setText(String.valueOf(config.airTemperature()));
        coniferousBurningTemperatureField.setText(String.valueOf(config.coniferousBurningTemperature()));
        deciduousBurningTemperatureField.setText(String.valueOf(config.deciduousBurningTemperature()));
        understoryBurningTemperatureField.setText(String.valueOf(config.understoryBurningTemperature()));
        floorBurningTemperatureField.setText(String.valueOf(config.floorBurningTemperature()));
        litterBurningTemperatureField.setText(String.valueOf(config.litterBurningTemperature()));
        //overcastField.setText(String.valueOf(config.overcast()));
        //atmosphericPressureField.setText(String.valueOf(config.atmosphericPressure()));
        //maxFireTemperatureField.setText(String.valueOf(config.maxFireTemperature()));
        sizeField.setText(String.valueOf(config.size()));
        pointPercentageField.setText(String.valueOf(config.pointPercentage()));
    }

    private void startSimulation() {
        BoardConfig config = createBoardConfigFromFields();
        new Program(config);
        dispose();
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}
