import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConveyorBeltAnimation extends JPanel implements Runnable {
    private final BlockingQueue<Cupcake> sharedQueue;
    private final List<Cupcake> cupcakes = new CopyOnWriteArrayList<>();
    private int mouthX = 480;
    private int mouthY = 100;
    private int numberOfProducers = 0; // Keep track of the number of producers
    private int numberOfConsumers = 1; // Keep track of the number of producers
    private List<Boolean> producerStates = new ArrayList<>();
    private Color consumerColor;

    public ConveyorBeltAnimation(BlockingQueue<Cupcake> sharedQueue) {
        this.sharedQueue = sharedQueue;
        new Thread(this).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawConveyorBelt(g);
        drawCupcakes(g);
        drawFace(g);
        drawChefs(g);
    }

    private void drawConveyorBelt(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, mouthY, getWidth(), 20);
    }

    private void drawCupcakes(Graphics g) {
        for (Cupcake cupcake : cupcakes) {
            cupcake.draw(g); // Use the draw method of the Cupcake class with new y position
        }
    }

    private void drawChefs(Graphics g) {
        
        for (int i = 0; i < numberOfProducers; i++) {
            drawChef(g, 50 + i * 80, mouthY - 60, producerStates.get(i)); // Spacing the chefs along the belt     
        }
    }

    private void drawChef(Graphics g, int x, int y, boolean isProducing) {
        g.setColor(isProducing ? Color.YELLOW : Color.WHITE); // Yellow if producing
        g.fillOval(x, y, 40, 20); // Top part of the hat

        g.fillRect(x + 10, y + 10, 20, 20); // Bottom part of the hat

        g.setColor(Color.BLACK);
        g.drawRect(x + 10, y + 10, 20, 20); // Outline for bottom part
        g.drawOval(x, y, 40, 20); // Outline for top part
    }

    public void setNumberOfConsumers(int numberOfConsumers) {
        this.numberOfConsumers = numberOfConsumers;
        repaint();
    }

    public void setProducerState(int id, boolean state) {
        System.out.println(""+ id + state  + producerStates.size());
        producerStates.set(id, state);
        repaint();
    }

    public void setConsumerColor(int consumerId){
        consumerColor = switch (consumerId) {
            case 0 -> Color.RED;
            case 1 -> Color.GREEN;
            case 2 -> Color.BLUE;
            case 3 -> Color.CYAN;
            case 4 -> Color.ORANGE;
            default -> Color.MAGENTA;
        };
        repaint();
    }




    public void addProducer() {
        this.producerStates.add(false);
        this.numberOfProducers++;
        repaint(); // Redraw the conveyor belt with new producers
    }

    public void removeProducer() {
        this.producerStates.remove(producerStates.size() - 1);
        this.numberOfProducers--;
        repaint(); // Redraw the conveyor belt with new producers
    }
    

    private void drawFace(Graphics g) {
        g.setColor(consumerColor);
        g.fillArc(mouthX, mouthY - (25 * numberOfConsumers) + 20, 50 * numberOfConsumers, 50 * numberOfConsumers, 0,
                -180);

        g.setColor(Color.BLACK);
        g.fillOval(mouthX + (32*numberOfConsumers) + 2, mouthY - 5, 40, 40);

        g.setColor(Color.WHITE);
        g.fillOval(mouthX + (32*numberOfConsumers) +5, mouthY, 14, 14);
    }

    @Override
    public void run() {
        while (true) {
            syncWithQueue(); // Sync cupcakes with the sharedQueue
            moveCupcakes(); // Move them according to the state and delays
            repaint(); // Refresh the screen with updated positions

            try {
                Thread.sleep(20); // Polling frequency for real-time animation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void syncWithQueue() {
        cupcakes.clear(); // Clear current cupcakes on the belt
        cupcakes.addAll(sharedQueue); // Sync with the actual queue state
    }

    private void moveCupcakes() {
        for (Cupcake cupcake : cupcakes) {

            // If the cupcake reaches the mouth and is consumed, remove it
            if (cupcake.getX() <= mouthX + 25) {
                cupcake.move(mouthX);
            }

        }
    }

}
