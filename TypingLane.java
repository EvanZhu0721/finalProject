import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TypingLane {
    static final String VERSION = "1.2.0";

    public static void main(String[] args) {
        if (args.length > 0 && "--smoke".equals(args[0])) {
            SmokeTest.run();
            return;
        }
        if (args.length > 0 && "--perf".equals(args[0])) {
            SmokeTest.runRenderBenchmark();
            return;
        }
        final boolean launchFullscreen = args.length > 0 && "--fullscreen".equals(args[0]);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Typing Lane " + VERSION);
                GamePanel panel = new GamePanel(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(panel);
                frame.pack();
                frame.setMinimumSize(new Dimension(GamePanel.MIN_WINDOW_WIDTH, GamePanel.MIN_WINDOW_HEIGHT));
                frame.setResizable(true);
                frame.setLocationRelativeTo(null);
                if (launchFullscreen) {
                    frame.setUndecorated(true);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    panel.setFullscreenState(true);
                }
                frame.setVisible(true);
                panel.requestFocusInWindow();
            }
        });
    }
}
