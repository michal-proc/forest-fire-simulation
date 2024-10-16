package simulation;

import simulation.records.BoardConfig;

import javax.swing.JFrame;
import java.io.Serial;

public class Program extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private GUI gof;

    public Program(BoardConfig boardConfig) {
        setTitle("Forest Fire Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gof = new GUI(this);
        gof.initialize(this.getContentPane(), boardConfig);

        this.setSize(1024, 768);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}
