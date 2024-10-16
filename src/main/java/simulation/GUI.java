package simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import simulation.adapters.MapShapeAdapter;
import simulation.components.TextAreaRenderer;
import simulation.records.BoardConfig;
import simulation.records.BoardStatistics;
import simulation.records.PointStatistics;
import simulation.records.PointJson;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class containing simulation.GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 1L;
    private Timer timer;
    private Board board;

    private JButton restart;
    private JButton regenerateMap;
    private JButton loadMap;
    private JButton saveMap;
    private JButton start;
    private JButton clear;

    private JSlider pred;
    private JFrame frame;
    private JComboBox<PointStates> drawType;
    private int iterNum = 0;
    private final int maxDelay = 500;
    private final int initDelay = 100;
    private boolean running = false;

    private Font statsFont = new Font("SansSerif", Font.PLAIN, 12);
    private DecimalFormat decimalFormat = new DecimalFormat("#.####");
    private JLabel initialMessageLabel;

    private JPanel pointStatsPanel;
    private JPanel worldStatsPanel;
    private JTable worldStatsTable;
    private DefaultTableModel worldStatsModel;

    private XYSeries burntFieldsSeries;
    private XYSeries burningFieldsSeries;
    private XYSeries unaffectedFieldsSeries;

    public GUI(JFrame jf) {
        frame = jf;
        timer = new Timer(initDelay, this);
        timer.stop();
    }

    /**
     * @param container to which simulation.GUI and board is added
     */
    public void initialize(Container container, BoardConfig boardConfig) {
        // simulation.Board
        container.setLayout(new BorderLayout());

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();

        restart = new JButton("Restart");
        restart.setActionCommand("restart");
        restart.setToolTipText("Restart simulation with new config");
        restart.addActionListener(this);

        regenerateMap = new JButton("Regenerate Map");
        regenerateMap.setActionCommand("regenerateMap");
        regenerateMap.setToolTipText("Generate new random map");
        regenerateMap.addActionListener(this);

        loadMap = new JButton("Load Map");
        loadMap.setActionCommand("loadMap");
        loadMap.setToolTipText("Load map from *.json file");
        loadMap.addActionListener(this);

        saveMap = new JButton("Save Map");
        saveMap.setActionCommand("saveMap");
        saveMap.setToolTipText("Save map from *.json file");
        saveMap.addActionListener(this);

        start = new JButton("Start");
        start.setActionCommand("Start");
        start.setToolTipText("Starts clock");
        start.addActionListener(this);

        clear = new JButton("Clear");
        clear.setActionCommand("clear");
        clear.setToolTipText("Clears the board");
        clear.addActionListener(this);

        pred = new JSlider();
        pred.setMinimum(0);
        pred.setMaximum(maxDelay);
        pred.setToolTipText("Time speed");
        pred.addChangeListener(this);
        pred.setValue(maxDelay - timer.getDelay());

        drawType = new JComboBox<PointStates>(Point.types);
        drawType.addActionListener(this);
        drawType.setActionCommand("drawType");

        buttonPanel.add(restart);
        buttonPanel.add(regenerateMap);
        buttonPanel.add(loadMap);
        buttonPanel.add(saveMap);
        buttonPanel.add(start);
        buttonPanel.add(clear);
        buttonPanel.add(pred);
        buttonPanel.add(drawType);

        // Point Stats Panel
        pointStatsPanel = new JPanel();
        pointStatsPanel.setPreferredSize(new Dimension(200, 600));
        pointStatsPanel.setLayout(new BorderLayout());
        initialMessageLabel = new JLabel("<html><div style='text-align: center;'>Move the mouse over the board to see stats</div></html>", SwingConstants.CENTER);
        initialMessageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        initialMessageLabel.setVerticalAlignment(SwingConstants.CENTER);
        pointStatsPanel.add(initialMessageLabel, BorderLayout.CENTER);

        board = new Board(this, 1400, 1200 - buttonPanel.getHeight(), boardConfig);
        container.add(board, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        container.add(pointStatsPanel, BorderLayout.WEST);

        // Board Stats Panel
        worldStatsPanel = new JPanel();
        worldStatsPanel.setPreferredSize(new Dimension(200, 600));
        worldStatsPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Property", "Value"};
        Object[][] initialData = {
                {"All Fields", 0},
                {"Burnt Fields", 0},
                {"Fire Fields", 0},
                {"Litter Fields", 0},
                {"Floor Fields", 0},
                {"Understory Fields", 0},
                {"Coniferous Fields", 0},
                {"Deciduous Fields", 0}
        };
        worldStatsModel = new DefaultTableModel(initialData, columnNames);
        worldStatsTable = new JTable(worldStatsModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                return super.prepareRenderer(renderer, row, column);
            }
        };
        worldStatsTable.setFont(statsFont);
        worldStatsTable.setFillsViewportHeight(true);
        worldStatsTable.getColumnModel().getColumn(0).setCellRenderer(new TextAreaRenderer());
        worldStatsTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        adjustRowHeights(worldStatsTable);

        JScrollPane scrollPane = new JScrollPane(worldStatsTable);
        worldStatsPanel.add(scrollPane, BorderLayout.CENTER);

        container.add(worldStatsPanel, BorderLayout.EAST);


        burntFieldsSeries = new XYSeries("Burnt Fields");
        burningFieldsSeries = new XYSeries("Burning Fields");
        unaffectedFieldsSeries = new XYSeries("Unaffected Fields");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(burntFieldsSeries);
        dataset.addSeries(burningFieldsSeries);
        dataset.addSeries(unaffectedFieldsSeries);


        JFreeChart chart = ChartFactory.createXYLineChart(
                "Simulation Statistics",
                "Iteration",
                "Number of Fields",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));


        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.add(chartPanel, BorderLayout.CENTER);


        container.add(chartContainer, BorderLayout.NORTH);

        frame.setSize(new Dimension(1200, 1080));
        frame.setLocationRelativeTo(null); // Center the window
    }

    /**
     * handles clicking on each button
     *
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(timer)) {
            iterNum++;
            frame.setTitle("Forest Fire Simulation (" + Integer.toString(iterNum) + " iteration)");
            board.iteration();
            boardStatsChanged(board.toBoardStatistics());
        } else {
            String command = e.getActionCommand();
            JFileChooser fileChooser = new JFileChooser();
            switch (command) {
                case "Start":
                    if (!running) {
                        timer.start();
                        start.setText("Pause");
                    } else {
                        timer.stop();
                        start.setText("Start");
                    }
                    running = !running;
                    clear.setEnabled(true);
                    break;

                case "clear":
                    iterNum = 0;
                    timer.stop();
                    start.setEnabled(true);
                    board.clear();
                    frame.setTitle("Cellular Automata Toolbox");
                    break;

                case "restart":
                    new StartScreen();
                    frame.dispose();
                    break;

                case "drawType":
                    board.editType = PointStates.fromDescription(drawType.getSelectedItem().toString());
                    break;

                case "regenerateMap":
                    board.regenerateMap();
                    break;

                case "loadMap":
                    int option = fileChooser.showOpenDialog(this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try (Reader reader = new FileReader(file)) {
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(PointJson.class, createPointJsonDeserializer())
                                    .create();
                            PointJson[] pointJsons = gson.fromJson(reader, PointJson[].class);
                            board.updatePointsFromJson(pointJsons);

                            JOptionPane.showMessageDialog(this, "Map loaded successfully.");
                        } catch (IOException exception) {
                            JOptionPane.showMessageDialog(this, "Error loading map: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case "saveMap":
                    Point[][] points = board.getPoints();
                    fileChooser.setDialogTitle("Save Map File");
                    fileChooser.setAcceptAllFileFilterUsed(false);

                    int userSelection = fileChooser.showSaveDialog(null);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        if (!fileToSave.getAbsolutePath().endsWith(".json")) {
                            fileToSave = new File(fileToSave + ".json");
                        }
                        MapShapeAdapter.saveMap(points, fileToSave.getAbsolutePath());
                    }
                    break;
            }
        }
    }

    private JsonDeserializer<PointJson> createPointJsonDeserializer() {
        return (json, typeOfT, context) -> new PointJson(
                json.getAsJsonObject().get("x").getAsInt(),
                json.getAsJsonObject().get("y").getAsInt(),
                json.getAsJsonObject().get("elevation").getAsInt(),
                json.getAsJsonObject().get("height").getAsFloat(),
                PointStates.fromDescription(json.getAsJsonObject().get("currentState").getAsString())
        );
    }

    /**
     * slider to control simulation speed
     *
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        timer.setDelay(maxDelay - pred.getValue());
    }

    public void pointStatsChanged(Point point, int x, int y) {
        pointStatsPanel.removeAll();

        PointStatistics stats = point.toPointStatistics(x, y);

        String[] columnNames = {"Property", "Value"};
        Object[][] data = {
                {"Coordinates", "(" + stats.pointX() + ", " + stats.pointY() + ")"},
                {"Elevation", stats.elevation()},
                {"Litter", stats.litter()},
                {"Floor", stats.floor()},
                {"Understory", stats.understory()},
                {"Coniferous", stats.coniferous()},
                {"Deciduous", stats.deciduous()},
                {"Height", formatDecimal(stats.height())},
                {"Fire Source", stats.fireSource()},
                {"Humidity", formatDecimal(stats.humidity())},
                {"Current State", stats.currentState()},
                {"States", listToString(stats.state())},
                {"Temperatures", listToString(stats.temperature())},
                {"On Fire", listToString(stats.onFire())}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable statsTable = new JTable(model) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                return super.prepareRenderer(renderer, row, column);
            }
        };
        statsTable.setFont(statsFont);
        statsTable.setFillsViewportHeight(true);

        statsTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());

        adjustRowHeights(statsTable);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        pointStatsPanel.add(scrollPane, BorderLayout.CENTER);

        pointStatsPanel.revalidate();
        pointStatsPanel.repaint();
    }

    public void boardStatsChanged(BoardStatistics stats) {
        worldStatsModel.setValueAt(stats.allFields(), 0, 1);
        worldStatsModel.setValueAt(stats.burntFields(), 1, 1);
        worldStatsModel.setValueAt(stats.fireFields(), 2, 1);
        worldStatsModel.setValueAt(stats.litterFields(), 3, 1);
        worldStatsModel.setValueAt(stats.floorFields(), 4, 1);
        worldStatsModel.setValueAt(stats.understoryFields(), 5, 1);
        worldStatsModel.setValueAt(stats.coniferousFields(), 6, 1);
        worldStatsModel.setValueAt(stats.deciduousFields(), 7, 1);

        int currentIteration = iterNum;
        burntFieldsSeries.add(currentIteration, stats.burntFields());
        burningFieldsSeries.add(currentIteration, stats.fireFields());
        unaffectedFieldsSeries.add(currentIteration, stats.allFields() - stats.burntFields());

        adjustRowHeights(worldStatsTable);
    }

    public void showInitialMessage() {
        pointStatsPanel.removeAll();
        pointStatsPanel.add(initialMessageLabel, BorderLayout.CENTER);
        pointStatsPanel.revalidate();
        pointStatsPanel.repaint();
    }

    private void adjustRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            Component comp = table.prepareRenderer(table.getCellRenderer(row, 1), row, 1);
            rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            table.setRowHeight(row, rowHeight);
        }
    }

    private String formatDecimal(double value) {
        return decimalFormat.format(value);
    }

    private String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            sb.append(item.toString()).append("\n");
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
