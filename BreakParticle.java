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
        this.size = GamePanel.worldAmount(size);
        this.life = GamePanel.logicTicks(life);
        this.maxLife = this.life;
        this.color = color;
    }

    void step() {
        previousX = x;
        previousY = y;
        x += GamePanel.gameplayStep(vx);
        y += GamePanel.gameplayStep(vy);
        double damping = Math.pow(0.94, GamePanel.GAMEPLAY_STEP_SCALE);
        vx *= damping;
        vy = vy * damping + GamePanel.gameplayStep(0.12);
        life--;
    }
}
