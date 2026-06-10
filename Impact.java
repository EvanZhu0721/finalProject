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
    final double driftX;
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
        int seed = (int) Math.round(x * 13.0 + y * 7.0 + damage * 31.0);
        this.driftX = GamePanel.worldAmount(((seed & 7) - 3.5) * 2.4);
        this.popHeight = GamePanel.worldAmount(38.0 + ((seed >>> 3) & 7) * 2.5);
    }
}
