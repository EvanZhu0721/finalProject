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
    final boolean particleTrail;
    final int maxLifeTicks;
    final double[] shapeAngles;
    final double[] shapeRadii;
    double shapeRotation;
    double shapeSpin;
    List<Target> hitTargets;
    double x;
    double previousX;
    double y;
    double previousY;
    double vx;
    double vy;
    double launchX;
    double launchY;
    int pierceLeft;
    int hits;
    int lifeTicks;
    boolean beamResolved;

    Bullet(double x, int lane, int damage, int pierceLeft, BulletKind kind) {
        this(x, lane, damage, pierceLeft, kind, true);
    }

    Bullet(double x, int lane, int damage, int pierceLeft, BulletKind kind, boolean particleTrail) {
        this.x = x;
        this.previousX = x;
        this.y = GamePanel.LANE_Y[lane];
        this.previousY = this.y;
        this.launchX = x;
        this.launchY = this.y;
        this.lane = lane;
        this.damage = damage;
        this.pierceLeft = pierceLeft;
        this.kind = kind;
        this.particleTrail = particleTrail;
        if (kind == BulletKind.DRY_ICE) {
            this.speed = 20;
            this.radius = GamePanel.worldAmount(22);
        } else if (kind == BulletKind.HOMING_SHOT) {
            this.speed = 25;
            this.radius = GamePanel.worldAmount(5.5);
        } else if (kind == BulletKind.PIERCE) {
            this.speed = 21;
            this.radius = GamePanel.worldAmount(10);
        } else if (kind == BulletKind.BURST) {
            this.speed = 24;
            this.radius = GamePanel.worldAmount(10);
        } else if (kind == BulletKind.CONTINUOUS_SURGE) {
            this.speed = 30;
            this.radius = GamePanel.worldAmount(9);
        } else if (kind == BulletKind.CONTINUOUS) {
            this.speed = 28;
            this.radius = GamePanel.worldAmount(4);
        } else {
            this.speed = 18;
            this.radius = GamePanel.worldAmount(8);
        }
        this.vx = this.speed;
        this.vy = 0.0;
        if (kind == BulletKind.DRY_ICE) {
            this.shapeAngles = new double[5];
            this.shapeRadii = new double[5];
            int seed = 0x5f3759df ^ (int) Math.round(x * 31) ^ (lane * 131) ^ (damage * 17);
            seed ^= seed << 13;
            seed ^= seed >>> 17;
            seed ^= seed << 5;
            this.shapeRotation = (seed & 0x3ff) / 1024.0 * Math.PI * 2.0;
            seed ^= seed << 13;
            seed ^= seed >>> 17;
            seed ^= seed << 5;
            double spinDirection = (seed & 1) == 0 ? -1.0 : 1.0;
            this.shapeSpin = spinDirection * (0.13 + ((seed >>> 1) & 0xff) / 255.0 * 0.08);
            for (int i = 0; i < shapeAngles.length; i++) {
                seed ^= seed << 13;
                seed ^= seed >>> 17;
                seed ^= seed << 5;
                double angleJitter = ((seed & 0xff) / 255.0 - 0.5) * 0.34;
                shapeAngles[i] = -Math.PI / 2.0 + i * Math.PI * 2.0 / 5.0 + angleJitter;
                seed ^= seed << 13;
                seed ^= seed >>> 17;
                seed ^= seed << 5;
                shapeRadii[i] = radius * (0.72 + (seed & 0xff) / 255.0 * 0.46);
            }
        } else {
            this.shapeAngles = null;
            this.shapeRadii = null;
            this.shapeRotation = 0.0;
            this.shapeSpin = 0.0;
        }
        if (kind == BulletKind.PIERCE) {
            this.lifeTicks = GamePanel.logicTicks(18);
        } else if (kind == BulletKind.HOMING_SHOT) {
            this.lifeTicks = fullLaneLifeTicks() + GamePanel.logicTicks(44);
        } else {
            this.lifeTicks = fullLaneLifeTicks();
        }
        this.maxLifeTicks = this.lifeTicks;
    }

    private int fullLaneLifeTicks() {
        double distance = Math.max(0.0, GamePanel.LANE_RIGHT_X - x + radius);
        return GamePanel.logicTicks(Math.max(1,
                (int) Math.ceil(distance / GamePanel.worldAmount(speed)) + 2));
    }
}
