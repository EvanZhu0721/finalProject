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


final class Bullet {
    final int lane;
    final int damage;
    final double radius;
    final double speed;
    final BulletKind kind;
    List<Target> hitTargets;
    double x;
    double previousX;
	        int pierceLeft;
	        int hits;
	        int lifeTicks;

    Bullet(double x, int lane, int damage, int pierceLeft, BulletKind kind) {
        this.x = x;
        this.previousX = x;
        this.lane = lane;
        this.damage = damage;
        this.pierceLeft = pierceLeft;
        this.kind = kind;
	            if (kind == BulletKind.PIERCE) {
	                this.speed = 21;
	                this.radius = 10;
	            } else if (kind == BulletKind.BURST) {
	                this.speed = 24;
	                this.radius = 10;
	            } else if (kind == BulletKind.CONTINUOUS_SURGE) {
	                this.speed = 30;
	                this.radius = 9;
	            } else if (kind == BulletKind.CONTINUOUS) {
	                this.speed = 28;
	                this.radius = 4;
	            } else {
	                this.speed = 18;
	                this.radius = 8;
	            }
	            this.lifeTicks = fullLaneLifeTicks();
	        }

	        private int fullLaneLifeTicks() {
	            double distance = Math.max(0.0, GamePanel.LANE_RIGHT_X - x + radius);
	            return Math.max(1, (int) Math.ceil(distance / speed) + 2);
	        }
	    }
