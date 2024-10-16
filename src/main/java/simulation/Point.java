package simulation;

import simulation.records.BoardConfig;
import simulation.records.PointJson;
import simulation.records.PointStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

public class Point {
    public static PointStates[] types = PointStates.values();
    public static final int LEVELS = 10;
    private int elevation;
    public boolean litter;
    public boolean floor;
    public boolean understory;
    public boolean coniferous;
    public boolean deciduous;
    private double height;
    public boolean fireSource;
    public List<Double> state;
    protected List<Double> temperature;
    private List<Point> neighbors;
    private List<Double> nextState;
    protected Double StandardHumidity = 0.5;
    protected Double humidity = 0.5;
    private List<Double> nextTemperature;
    public PointStates currentState;
    static Random RND = new Random();
    private double mediumConiferousHeight = 25;
    private double burningTemperature;
    private double mediumDeciduousHeight = 35;
    private double mediumUnderstoryHeight = 5.0;
    private double mediumFloorHeight = 0.05;
    private double mediumConiferousHeightVariance = 25;
    private double mediumDeciduousHeightVariance = 25;
    private double mediumUnderstoryHeightVariance = 0.001;
    private double mediumFloorHeightVariance = 0.001;
    private double mediumLitterHeightVariance = 0.001;
    private double mediumLitterHeight = 0.5;
    double w = 0.2;
    double p = 0.1;
    double distance = 1.0;
    public List<Boolean> onFire;
    private double fireGrowthRate = 0.2;
    private BoardConfig conf;
    int count = 0;

    public Point(int x, int y, BoardConfig conf) {
        this.conf = conf;
        this.neighbors = new ArrayList<Point>();
        elevation = max(x + y - 700, 0);
        initializeEmpty();
    }

    public void initializeEmpty() {
        fireSource = false;

        litter = false;
        floor = false;
        understory = false;
        coniferous = false;
        deciduous = false;

        currentState = PointStates.NO_FIRE;
        state = new ArrayList<>();
        humidity = RND.nextGaussian(conf.mediumMoisture(), Math.sqrt(conf.mediumMoistureVariance()));
        temperature = new ArrayList<>();
        nextState = new ArrayList<>();
        nextTemperature = new ArrayList<>();
        onFire = new ArrayList<>();

        for (int i = 0; i < LEVELS; i++) {
            state.add(1.0);
            temperature.add(30.0);
            nextState.add(1.0);
            nextTemperature.add(30.0);
            onFire.add(Boolean.FALSE);
        }
    }

    public void updateFromPointJson(PointJson pointJson) {
        this.elevation = pointJson.elevation();
        this.height = pointJson.height();
        this.currentState = pointJson.currentState();

        if (this.currentState == PointStates.LITTER) {
            this.initializeLitter();
        }
        if (this.currentState == PointStates.FLOOR) {
            this.initializeFloor();
        }
        if (this.currentState == PointStates.UNDERSTORY) {
            this.initializeUnderstory();
        }
        if (this.currentState == PointStates.CONIFEROUS) {
            this.initializeConiferous();
        }
        if (this.currentState == PointStates.DECIDUOUS) {
            this.initializeDeciduous();
        }
    }

    public void addFireSource() {
        temperature.set(0, 600.0);
        fireSource = true;
        onFire.set(0, Boolean.TRUE);
    }

    public void initializeLitter() {
        initializeEmpty();
        litter = true;
        currentState = PointStates.LITTER;
        burningTemperature = conf.litterBurningTemperature();

        height = RND.nextGaussian(mediumLitterHeight, Math.sqrt(mediumLitterHeightVariance));
    }

    public void initializeFloor() {
        initializeEmpty();
        floor = true;
        currentState = PointStates.FLOOR;
        burningTemperature = conf.floorBurningTemperature();

        height = RND.nextGaussian(mediumFloorHeight, Math.sqrt(mediumFloorHeightVariance));
    }

    public void initializeUnderstory() {
        initializeEmpty();
        understory = true;
        currentState = PointStates.UNDERSTORY;
        burningTemperature = conf.understoryBurningTemperature();

        height = RND.nextGaussian(mediumUnderstoryHeight, Math.sqrt(mediumUnderstoryHeightVariance));
    }

    public void initializeConiferous() {
        initializeEmpty();
        coniferous = true;
        currentState = PointStates.CONIFEROUS;
        burningTemperature = conf.coniferousBurningTemperature();

        height = RND.nextGaussian(mediumConiferousHeight, Math.sqrt(mediumConiferousHeightVariance));
    }

    public void initializeDeciduous() {
        initializeEmpty();
        deciduous = true;
        currentState = PointStates.DECIDUOUS;
        burningTemperature = conf.deciduousBurningTemperature();

        height = RND.nextGaussian(mediumDeciduousHeight, Math.sqrt(mediumDeciduousHeightVariance));
    }

    public PointStates getState() {
        return currentState;
    }

    public void update() {
        double actualBurnTemp = actualBurningTemperature();

        for (int i = 0; i < LEVELS; i++) {
            state.set(i, nextState.get(i));
            temperature.set(i, nextTemperature.get(i));
            if (temperature.get(i) >= actualBurnTemp) {
                onFire.set(i, Boolean.TRUE);
            } else {
                onFire.set(i, Boolean.FALSE);
            }
        }
    }

    private double actualBurningTemperature() {
        double k = 0.2;
        return burningTemperature * humidity / StandardHumidity;
    }

    private void burn() {
        double actualBurnTemp = actualBurningTemperature();

        for (int i = 0; i < LEVELS; i++) {
            nextState.set(i, state.get(i));
            if (onFire.get(i)) {
                nextState.set(i, state.get(i) * (1 - 0.005 * temperature.get(i) / actualBurnTemp));
                nextTemperature.set(i, temperature.get(i) * (1 + fireGrowthRate) * nextState.get(i));
            } else {
                nextTemperature.set(i, max(conf.airTemperature(), temperature.get(i) * (1 - fireGrowthRate)));
            }
        }
    }

    public void calculateNewState(double windVelocity, Directions dir) {
        if (currentState == PointStates.NO_FIRE) {
            return;
        }

        burn();

        for (int j = 0; j < neighbors.size(); j++) {
            Point neighbor = neighbors.get(j);

            if (neighbor.temperature.get(0) >= actualBurningTemperature()) {
                double elevationDifference = Math.sqrt(Math.pow((elevation - neighbor.elevation), 2) + Math.pow(distance, 2));
                double necessaryProb = 0.1 / (1 + elevationDifference * 1);
                if (j >= 4) {
                    necessaryProb /= Math.sqrt(2);
                }
                if (RND.nextDouble() < necessaryProb) {
                    nextTemperature.set(0, neighbor.temperature.get(0));
                }
            }
        }

        // Spreading fire up and down
        for (int i = 0; i < LEVELS - 1; i++) {
            if (onFire.get(i) && !onFire.get(i + 1)) {
                if (RND.nextDouble() < 0.2) {
                    nextTemperature.set(i + 1, temperature.get(i));
                }
            }
        }

        for (int i = LEVELS - 1; i > 0; i--) {
            if (onFire.get(i) && !onFire.get(i - 1)) {
                if (RND.nextDouble() < 0.05) {
                    nextTemperature.set(i - 1, temperature.get(i));
                }
            }
        }

        int direction = switch (dir) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            case NORTHWEST -> 4;
            case NORTHEAST -> 5;
            case SOUTHEAST -> 6;
            case SOUTHWEST -> 7;
        };

        //System.out.println(direction);

        double angle = calculateFireAngle(windVelocity, w);

        if (neighbors.size() > direction) {
            for (int k = LEVELS - 1; k >= 0; k--) {
                double newElevation = elevation + height * ((double) k / (LEVELS - 1)) * Math.sin(Math.toRadians(angle));

                //System.out.println(newElevation);
                int i = (int) ((newElevation - neighbors.get(direction).elevation) * LEVELS / neighbors.get(direction).height / ((double) k / (LEVELS - 1)));
                if (i >= 0 && i < LEVELS) {
                    double multiplier = 1;
                    if (direction > 3) {
                        multiplier = Math.sqrt(2);
                    }
                    //if(height * Math.cos(Math.toRadians(angle)) > distance / 2 / multiplier) {
                    if (RND.nextDouble() < p * multiplier) {
                        neighbors.get(direction).temperature.set(i, temperature.get(k));
                        neighbors.get(direction).nextTemperature.set(i, temperature.get(k));
                    }
                    //}
                }
            }
        }
    }

    public int getElevation() {
        return elevation;
    }

    public double getHeight() {
        return elevation;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double calculateFireAngle(double windVelocity, double w) {
        //System.out.println(90 * (2 - 2*sigmoid(w * windVelocity)));
        return 90 * (1 - sigmoid(w * windVelocity));
    }

    public void addNeighbor(Point nei) {
        neighbors.add(nei);
    }

    @Override
    public String toString() {
        return "Elevation:" + this.elevation + "\n"
                + "Current State:" + this.currentState + "\n"
                + "Has litter:" + this.litter + "\n"
                + "Has floor:" + this.floor + "\n"
                + "Has understory: " + this.understory + "\n"
                + "Is coniferous: " + this.coniferous + "\n"
                + "Is deciduous: " + this.deciduous + "\n"
                + "Height:" + this.height + "\n"
                + "States:" + this.state + "\n"
                + "Temperature:" + this.temperature + "\n";
    }

    public PointStatistics toPointStatistics(int x, int y) {
        return new PointStatistics(
                x,
                y,
                this.elevation,
                this.litter,
                this.floor,
                this.understory,
                this.coniferous,
                this.deciduous,
                this.height,
                this.fireSource,
                List.copyOf(this.state),
                List.copyOf(this.temperature),
                this.humidity,
                this.currentState.toString(),
                List.copyOf(this.onFire)
        );
    }
}
