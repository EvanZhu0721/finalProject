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


final class Target {
    final TargetKind kind;
    int maxHp;
    int hp;
    int lane;
    double x;
    double previousX;
    double speed;
    boolean dead;
    boolean counted;
    int hitFlash;
    int phaseTick;
    int slowTicks;
    int markTicks;

    private Target(int lane, TargetKind kind, int hp, double speed) {
        this.lane = lane;
        this.kind = kind;
        this.hp = hp;
        this.maxHp = hp;
        this.speed = speed;
	            this.x = GamePanel.targetSpawnX(kind);
        this.previousX = this.x;
    }

    static Target enemy(int lane, TargetKind kind) {
        if (kind == TargetKind.FAST) {
            return new Target(lane, kind, GamePanel.scaleEnemyHp(3), 2.65);
        }
        if (kind == TargetKind.TANK) {
            return new Target(lane, kind, GamePanel.scaleEnemyHp(9), 1.25);
        }
        if (kind == TargetKind.SWITCHER) {
            return new Target(lane, kind, GamePanel.scaleEnemyHp(5), 1.75);
        }
        return new Target(lane, TargetKind.NORMAL, GamePanel.scaleEnemyHp(4), 1.85);
    }

    static Target upgrade(int lane) {
        return new Target(lane, TargetKind.UPGRADE, GamePanel.scaleEnemyHp(4), 1.45);
    }

    static Target boss(int lane, int level) {
        return new Target(lane, TargetKind.BOSS, GamePanel.scaleBossHp(18 + level * 8), 0.55);
    }
}
