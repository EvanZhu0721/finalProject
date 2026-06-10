import java.awt.Color;

final class BreakParticle {
    final int maxLife;
    final Color color;
    double x;
    double y;
    double previousX;
    double previousY;
    double vx;
    double vy;
    double size;
    int life;

    BreakParticle(double x, double y, double vx, double vy, double size, int life, Color color) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.color = color;
    }

    void step() {
        previousX = x;
        previousY = y;
        x += vx;
        y += vy;
        vx *= 0.94;
        vy = vy * 0.94 + 0.12;
        life--;
    }
}
