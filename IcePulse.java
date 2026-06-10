final class IcePulse {
    final double x;
    final double y;
    final double radius;
    final int maxTicks = GamePanel.logicTicks(18);
    int ticks = maxTicks;

    IcePulse(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}
