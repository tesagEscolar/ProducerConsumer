import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue<Cupcake> sharedQueue;
    private volatile boolean running = true;
    private int id;
    private int consumerDelay = 1000;
    private ConveyorBeltAnimation animation;
    public Consumer(int id, BlockingQueue<Cupcake> sharedQueue, ConveyorBeltAnimation animation) {
        this.id = id;
        this.sharedQueue = sharedQueue;
        this.animation = animation;
    }

    public void setConsumerDelay(int consumerDelay) {
        this.consumerDelay = consumerDelay;
    }


    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {

                if (sharedQueue.peek() != null) {
                    Thread.sleep(consumerDelay);
                    
                    animation.setConsumerColor(id);
                    Cupcake cupcake = sharedQueue.take(); // Block if queue is empty
                 
                    System.out.println("Consumer " +id+": " + cupcake.getNumber() + " after " + consumerDelay + "ms");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
