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


final class XpOrb {
    final int lane;
    final int value;
    final double speed = 1.35;
    double x;
    double previousX;
    double y;
    double previousY;
    boolean attracted;
    int pulse;

    XpOrb(double x, int lane, int value) {
        this.x = x;
        this.previousX = x;
        this.y = GamePanel.LANE_Y[lane];
        this.previousY = this.y;
        this.lane = lane;
        this.value = value;
    }
}
