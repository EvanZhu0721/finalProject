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


enum Difficulty {
    EASY("Easy", 25, 0.90),
    NORMAL("Normal", 40, 1.00),
    HARD("Hard", 60, 1.12);

    final String label;
    final int typingSpeedWpm;
    final double speedMultiplier;

    Difficulty(String label, int typingSpeedWpm, double speedMultiplier) {
        this.label = label;
        this.typingSpeedWpm = typingSpeedWpm;
        this.speedMultiplier = speedMultiplier;
    }
}
