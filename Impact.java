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


final class Impact {
    final double x;
    final double y;
    final int damage;
    final Color damageColor;
    final double startOffsetX;
    final double startOffsetY;
    final double travelX;
    final double travelY;
    final double popHeight;
    final int maxTicks = GamePanel.logicTicks(34);
    int ticks = maxTicks;

    Impact(double x, double y, int damage) {
        this(x, y, damage, GamePanel.COLOR_IMPACT_CORE);
    }

    Impact(double x, double y, int damage, Color damageColor) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.damageColor = damageColor;
        double offsetAngle = Math.random() * Math.PI * 2.0;
        double offsetDistance = GamePanel.worldAmount(4.0 + Math.random() * 20.0);
        this.startOffsetX = Math.cos(offsetAngle) * offsetDistance;
        this.startOffsetY = Math.sin(offsetAngle) * offsetDistance * 0.72;

        double travelAngle = Math.random() * Math.PI * 2.0;
        double travelDistance = GamePanel.worldAmount(42.0 + Math.random() * 62.0);
        double nextTravelX = Math.cos(travelAngle) * travelDistance;
        double nextTravelY = Math.sin(travelAngle) * travelDistance * 0.62 - GamePanel.worldAmount(18.0);
        double minTravel = GamePanel.worldAmount(28.0);
        double travelLength = Math.hypot(nextTravelX, nextTravelY);
        if (travelLength < minTravel) {
            double scale = minTravel / Math.max(0.001, travelLength);
            nextTravelX *= scale;
            nextTravelY *= scale;
        }
        this.travelX = nextTravelX;
        this.travelY = nextTravelY;
        this.popHeight = GamePanel.worldAmount(24.0 + Math.random() * 24.0);
    }
}
