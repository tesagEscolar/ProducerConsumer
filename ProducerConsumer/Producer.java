import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<Cupcake> sharedQueue;
    private volatile boolean running = true;
    private int lastId = 1;
    private int Id = 1;
    private int producerDelay = 1000;
    private int consumerDelay = 1000;
    private boolean state= false;
    private ConveyorBeltAnimation animation;
    public Producer(BlockingQueue<Cupcake> sharedQueue, int id, ConveyorBeltAnimation animation) {
        this.sharedQueue = sharedQueue;
        this.Id = id;
        this.animation = animation;
    }

    public void setProducerDelay(int producerDelay) {
        this.producerDelay = producerDelay;
    }


    @Override
    public void run() {
        int cupcakeNumber = 1;

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Calculate cumulative delay
                int cumulativeDelay = 0;
                // Total delay for the new cupcake
                Cupcake cupcake = new Cupcake(cupcakeNumber++, producerDelay, cumulativeDelay + consumerDelay, 0, 70);
                animation.setProducerState(Id, true);
                Thread.sleep(producerDelay); //Deliver Delay

                sharedQueue.put(cupcake); // Block if queue is full
                System.out.println("Producer " + Id + ": " + cupcake.getNumber() + " (Delay: " + producerDelay + "ms)");

                animation.setProducerState(Id, false);
                Thread.sleep(1000); //Deliver Delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
