final class BossProjectile {
    final int lane;
    final int damage;
    final double radius = GamePanel.worldAmount(14.0);
    final double speed;
    double x;
    double previousX;
    double y;
    double previousY;
    double waveOffset;
    int lifeTicks;

    BossProjectile(double x, int lane, int damage, double speed) {
        this.x = x;
        this.previousX = x;
        this.lane = lane;
        this.damage = damage;
        this.speed = speed;
        this.y = GamePanel.LANE_Y[lane];
        this.previousY = this.y;
        this.lifeTicks = GamePanel.logicTicks(230);
    }
}
