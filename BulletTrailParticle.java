import java.awt.Color;

final class BulletTrailParticle {
    final int maxLife;
    final Color color;
    final int sides;
    double rotation;
    double spin;
    double x;
    double y;
    double previousX;
    double previousY;
    double vx;
    double vy;
    double size;
    int life;

    BulletTrailParticle(double x, double y, double vx, double vy, double size, int life, Color color) {
        this(x, y, vx, vy, size, life, color, 0, 0.0, 0.0);
    }

    BulletTrailParticle(double x, double y, double vx, double vy, double size, int life, Color color,
            int sides, double rotation, double spin) {
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
        this.sides = sides;
        this.rotation = rotation;
        this.spin = spin;
    }

    void step() {
        previousX = x;
        previousY = y;
        x += GamePanel.gameplayStep(vx);
        y += GamePanel.gameplayStep(vy);
        double damping = Math.pow(0.82, GamePanel.GAMEPLAY_STEP_SCALE);
        vx *= damping;
        vy *= damping;
        rotation += GamePanel.gameplayStep(spin);
        spin *= Math.pow(0.88, GamePanel.GAMEPLAY_STEP_SCALE);
        life--;
    }
}
