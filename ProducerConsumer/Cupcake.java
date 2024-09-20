import java.awt.*;

public class Cupcake {
    public int x;
    public int y; // Added y coordinate
    private int number;
    private int baseDelay; // How long it takes to consume this cupcake
    private int cumulativeDelay = 1000; // How long it takes to consume this cupcake
    private static final int FIXED_POINT_SCALE = 1000;
    private int producerId;

    public Cupcake(int number, int baseDelay, int cumulativeDelay, int x, int y) {
        this.number = number;
        this.x = x; // Start position on x-axis
        this.y = y; // Position on y-axis, passed from the producer or animation logic
        this.baseDelay = baseDelay;
        this.cumulativeDelay = cumulativeDelay;
    }

    public int getNumber() {
        return number;
    }

    public int getBaseDelay() {
        return baseDelay;
    }

    public int getProducerId(){
        return producerId;
    }

    public int getCumulativeDelay() {
        return cumulativeDelay;
    }

    public void move(int mouthX) {
        x += calculateSpeed(mouthX);
    }

    public int getX() {
        return x / FIXED_POINT_SCALE;
    }

    private int calculateSpeed(int mouthX) {
        return ((mouthX * FIXED_POINT_SCALE) / (cumulativeDelay / 20)); // Adjust speed based on delay
    }

    public void draw(Graphics g) {
        // Draw base
        int xPos = getX();
        g.setColor(Color.ORANGE);
        g.fillRect(xPos, y + 30, 40, 20);

        // Draw top (half-circle)
        g.setColor(Color.PINK);
        g.fillArc(xPos, y, 40, 60, 0, 180);

        // Draw cherry
        g.setColor(Color.RED);
        g.fillOval(xPos + 15, y - 5, 10, 10);
    }
}
