package simulation;

import simulation.records.BoardConfig;
import simulation.records.BoardStatistics;
import simulation.records.PointJson;
import simulation.records.TrackedPoint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * simulation.Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points = new Point[0][0];
    public PointStates editType = PointStates.NO_FIRE;

    private BoardConfig boardConfig;

    private final GUI gui;
    private TrackedPoint trackedPoint = null;

    private final int length;
    private final int height;

    public int componentResizedNumber = 0;

    public Board(GUI gui, int length, int height, BoardConfig boardConfig) {
        this.gui = gui;
        this.boardConfig = boardConfig;
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
        this.boardConfig = boardConfig;

        this.length = length;
        this.height = height;
        initialize(length, height);
    }

    public Point[][] getPoints() {
        return points;
    }

    public void updatePointsFromJson(PointJson[] pointJsons) {
        clear();

        for (PointJson pointJson : pointJsons) {
            points[pointJson.x()][pointJson.y()].updateFromPointJson(pointJson);
        }

        this.repaint();
    }

    // single iteration
    public void iteration() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].calculateNewState(boardConfig.windVelocity(), boardConfig.windDirection());

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].update();

        if (trackedPoint != null) {
            gui.pointStatsChanged(trackedPoint.point(), trackedPoint.pointX(), trackedPoint.pointY());
        }

        gui.boardStatsChanged(this.toBoardStatistics());

        this.repaint();
    }

    // clearing board
    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].initializeEmpty();
            }
        this.repaint();
    }

    private void initializeBoard(int width, int height) {
        for (int x = 8; x < 8 + width; ++x) {
            for (int y = 5; y < 5 + height; ++y) {
                Random rnd = new Random();
                switch (rnd.nextInt(5)) {
                    case 0:
                        points[x][y].initializeLitter();
                        break;
                    case 1:
                        points[x][y].initializeFloor();
                        break;
                    case 2:
                        points[x][y].initializeUnderstory();
                        break;
                    case 3:
                        points[x][y].initializeConiferous();
                        break;
                    case 4:
                        points[x][y].initializeDeciduous();
                        break;
                }
            }
        }
    }

    public void regenerateMap() {
        clear();
        initialize(this.length, this.height);
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y] = new Point(x, y, boardConfig);

                if (Math.random() < boardConfig.pointPercentage()) {
                    points[x][y].currentState = PointStates.NO_FIRE;
                }
            }
        }
        initializeBoard(boardConfig.mapWidth(), boardConfig.mapHeight());

        for (int x = 1; x < points.length - 1; ++x) {
            for (int y = 1; y < points[x].length - 1; ++y) {
                // Moore'a Neighbourhood
                points[x][y].addNeighbor(points[x][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y]);
                points[x][y].addNeighbor(points[x][y + 1]);
                points[x][y].addNeighbor(points[x - 1][y]);
                points[x][y].addNeighbor(points[x - 1][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y - 1]);
                points[x][y].addNeighbor(points[x + 1][y + 1]);
                points[x][y].addNeighbor(points[x - 1][y + 1]);
            }
        }
    }

    // Paint background and separators between cells
    protected void paintComponent(Graphics g) {
        gui.boardStatsChanged(this.toBoardStatistics());
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
        drawNetting(g, boardConfig.size());
    }

    // draws the background netting
    private void drawNetting(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }

        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {
                if (points[x][y].getState() != PointStates.NO_FIRE) {
                    switch (points[x][y].getState()) {
                        case LITTER:
                            g.setColor(new Color(0x59350e));

                            break;
                        case FLOOR:
                            g.setColor(new Color(0x9c6427));

                            break;
                        case UNDERSTORY:
                            g.setColor(new Color(0xadd962));

                            break;
                        case CONIFEROUS:
                            g.setColor(new Color(0x364f0d));

                            break;
                        case DECIDUOUS:
                            g.setColor(new Color(0x6aa60a));

                            break;
                    }
                    for (int i = 0; i < Point.LEVELS; i++) {
                        if (points[x][y].state.get(i) < 1) {
                            g.setColor(new Color(0x333333));
                        }
                        if (points[x][y].onFire.get(i) == Boolean.TRUE) {
                            if (points[x][y].temperature.get(i) < 1000)
                                g.setColor(new Color(0xff0000));
                            else if (points[x][y].temperature.get(i) < 1200)
                                g.setColor(new Color(0xffa500));
                            else
                                g.setColor(new Color(0xffd700));
                            break;
                        }
                    }

                    g.fillRect((x * boardConfig.size()) + 1, (y * boardConfig.size()) + 1, (boardConfig.size() - 1), (boardConfig.size() - 1));
                }
            }
        }
    }


    public BoardStatistics toBoardStatistics() {
        int allFieldsCount = 0;
        int burntFieldsCount = 0;
        int fireFieldsCount = 0;
        int litterFieldsCount = 0;
        int floorFieldsCount = 0;
        int understoryFieldsCount = 0;
        int coniferousFieldsCount = 0;
        int deciduousFieldsCount = 0;

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                Point point = points[x][y];
                if (point.currentState != PointStates.NO_FIRE) {
                    boolean burntStatement = true;
                    boolean fireStatement = true;
                    for (int i = 0; i < Point.LEVELS; i++) {
                        if (points[x][y].state.get(i) < 1 && burntStatement) {
                            burntStatement = false;
                            burntFieldsCount++;
                        }
                        if (points[x][y].onFire.get(i) == Boolean.TRUE && fireStatement) {
                            fireStatement = false;
                            fireFieldsCount++;
                        }
                    }

                    allFieldsCount++;

                    if (point.litter && burntStatement) litterFieldsCount++;
                    if (point.floor && burntStatement) floorFieldsCount++;
                    if (point.understory && burntStatement) understoryFieldsCount++;
                    if (point.coniferous && burntStatement) coniferousFieldsCount++;
                    if (point.deciduous && burntStatement) deciduousFieldsCount++;
                }
            }
        }

        return new BoardStatistics(
                allFieldsCount,
                burntFieldsCount,
                fireFieldsCount,
                litterFieldsCount,
                floorFieldsCount,
                understoryFieldsCount,
                coniferousFieldsCount,
                deciduousFieldsCount
        );
    }


    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / boardConfig.size();
        int y = e.getY() / boardConfig.size();
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == PointStates.LITTER) {
                points[x][y].initializeLitter();
            } else if (editType == PointStates.FLOOR) {
                points[x][y].initializeFloor();
            } else if (editType == PointStates.UNDERSTORY) {
                points[x][y].initializeUnderstory();
            } else if (editType == PointStates.CONIFEROUS) {
                points[x][y].initializeConiferous();
            } else if (editType == PointStates.DECIDUOUS) {
                points[x][y].initializeDeciduous();
            } else if (editType == PointStates.FIRE) {
                points[x][y].addFireSource();
            } else {
                points[x][y].initializeEmpty();
            }
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
            int width = (this.getWidth() / boardConfig.size()) + 1;
            int height = (this.getHeight() / boardConfig.size()) + 1;
            initialize(width, height);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / boardConfig.size();
        int y = e.getY() / boardConfig.size();
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == PointStates.LITTER) {
                points[x][y].initializeLitter();
            } else if (editType == PointStates.FLOOR) {
                points[x][y].initializeFloor();
            } else if (editType == PointStates.UNDERSTORY) {
                points[x][y].initializeUnderstory();
            } else if (editType == PointStates.CONIFEROUS) {
                points[x][y].initializeConiferous();
            } else if (editType == PointStates.DECIDUOUS) {
                points[x][y].initializeDeciduous();
            } else if (editType == PointStates.FIRE) {
                points[x][y].addFireSource();
            } else {
                points[x][y].initializeEmpty();
            }
            this.repaint();
        }
    }

    public void mouseExited(MouseEvent e) {
        gui.showInitialMessage();
        trackedPoint = null;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / boardConfig.size();
        int y = e.getY() / boardConfig.size();
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            gui.pointStatsChanged(points[x][y], x, y);
            trackedPoint = new TrackedPoint(points[x][y], x, y);
        } else {
            gui.showInitialMessage();
            trackedPoint = null;
        }
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

}
