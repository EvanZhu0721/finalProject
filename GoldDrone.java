import java.awt.Color;

final class GoldDrone {
    final Color color;
    double x;
    double y;
    double previousX;
    double previousY;
    double laserStartX;
    double laserStartY;
    double laserEndX;
    double laserEndY;
    double waveOffset;
    int lifeTicks;
    int cooldownTicks;
    int laserTicks;

    GoldDrone(double x, double y, Color color, int lifeTicks, int cooldownTicks, double waveOffset) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
        this.color = color;
        this.lifeTicks = lifeTicks;
        this.cooldownTicks = cooldownTicks;
        this.waveOffset = waveOffset;
    }
}
