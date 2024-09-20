import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final List<Producer> producers = new ArrayList<>();
    private static final List<Consumer> consumers = new ArrayList<>();
    private static final List<Thread> producerThreads = new ArrayList<>();
    private static final List<Thread> consumerThreads = new ArrayList<>();
    private static ConveyorBeltAnimation animation;
    private static int producerDelay = 1000; // Initial producer delay
    private static int consumerDelay = 1000; // Initial consumer delay

    public static void main(String[] args) {
        int queueCapacity = 5;
        BlockingQueue<Cupcake> sharedQueue = new LinkedBlockingQueue<>(queueCapacity);
        animation = new ConveyorBeltAnimation(sharedQueue);

        JFrame frame = new JFrame("Producer-Consumer Conveyor Belt");
        frame.setLayout(new BorderLayout());
        frame.add(animation, BorderLayout.CENTER);

        // Labels to show the number of cupcakes, producers, and consumers
        JLabel cupcakeCountLabel = new JLabel("Cupcakes on belt: 0");
        JLabel producerCountLabel = new JLabel("Producers: 0");
        JLabel consumerCountLabel = new JLabel("Consumers: 0");

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, 3));
        labelPanel.add(cupcakeCountLabel);
        labelPanel.add(producerCountLabel);
        labelPanel.add(consumerCountLabel);
        frame.add(labelPanel, BorderLayout.NORTH);

        // Add producer and consumer dynamically
        JButton addProducerButton = new JButton("Add Producer");
        JButton removeProducerButton = new JButton("Remove Producer");
        JButton addConsumerButton = new JButton("Add Consumer");
        JButton removeConsumerButton = new JButton("Remove Consumer");

        JPanel controlPanel = new JPanel();
        controlPanel.add(addProducerButton);
        controlPanel.add(removeProducerButton);
        controlPanel.add(addConsumerButton);
        controlPanel.add(removeConsumerButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Delay control sliders
        JPanel sliderPanel = new JPanel(new GridLayout(2, 1));
        JSlider producerDelaySlider = new JSlider(JSlider.HORIZONTAL, 500, 3000, producerDelay);
        JSlider consumerDelaySlider = new JSlider(JSlider.HORIZONTAL, 500, 3000, consumerDelay);

        producerDelaySlider.setMajorTickSpacing(500);
        producerDelaySlider.setMinorTickSpacing(100);
        producerDelaySlider.setPaintTicks(true);
        producerDelaySlider.setPaintLabels(true);
        producerDelaySlider.setBorder(BorderFactory.createTitledBorder("Producer Delay"));

        consumerDelaySlider.setMajorTickSpacing(500);
        consumerDelaySlider.setMinorTickSpacing(100);
        consumerDelaySlider.setPaintTicks(true);
        consumerDelaySlider.setPaintLabels(true);
        consumerDelaySlider.setBorder(BorderFactory.createTitledBorder("Consumer Delay"));

        sliderPanel.add(producerDelaySlider);
        sliderPanel.add(consumerDelaySlider);
        frame.add(sliderPanel, BorderLayout.EAST);

        // Add first producer and consumer at the beginning
        addProducer(sharedQueue, producerCountLabel);
        addConsumer(sharedQueue, animation, consumerCountLabel);

        // Action listeners for buttons
        addProducerButton.addActionListener(e -> addProducer(sharedQueue, producerCountLabel));
        removeProducerButton.addActionListener(e -> removeProducer(producerCountLabel));
        addConsumerButton.addActionListener(e -> addConsumer(sharedQueue, animation, consumerCountLabel));
        removeConsumerButton.addActionListener(e -> removeConsumer(consumerCountLabel));

        // Update the cupcake count label dynamically
        new Thread(() -> {
            while (true) {
                try {
                    SwingUtilities
                            .invokeLater(() -> cupcakeCountLabel.setText("Cupcakes on belt: " + sharedQueue.size()));
                    Thread.sleep(100); // Update the label every 100ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        // Add change listeners for sliders to update delays in real-time
        producerDelaySlider.addChangeListener(e -> {
            producerDelay = producerDelaySlider.getValue();
            updateProducerDelays();
        });
        consumerDelaySlider.addChangeListener(e -> {
            consumerDelay = consumerDelaySlider.getValue();
            updateConsumerDelays();
        });

        frame.setSize(900, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add a shutdown hook to gracefully stop the threads when the program is
        // interrupted
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Thread producerThread : producerThreads) {
                producerThread.interrupt();
            }
            for (Thread consumerThread : consumerThreads) {
                consumerThread.interrupt();
            }
        }));
    }

    // Method to add a new producer
    private static void addProducer(BlockingQueue<Cupcake> sharedQueue, JLabel producerCountLabel) {
        Producer producer = new Producer(sharedQueue, producers.size(), animation);
        Thread producerThread = new Thread(producer);
        producers.add(producer);
        producerThreads.add(producerThread);
        producerThread.start();
        updateLabel(producerCountLabel, "Producers", producerThreads.size());
        animation.addProducer();
    }

    // Method to remove a producer
    private static void removeProducer(JLabel producerCountLabel) {
        if (!producerThreads.isEmpty()) {
            Thread producerThread = producerThreads.remove(producerThreads.size() - 1);
            Producer producer = producers.remove(producers.size() - 1);
            producerThread.interrupt();
            animation.removeProducer();
            updateLabel(producerCountLabel, "Producers", producerThreads.size());
        }
    }

    // Method to add a new consumer
    private static void addConsumer(BlockingQueue<Cupcake> sharedQueue, ConveyorBeltAnimation animation,
        JLabel consumerCountLabel) {
        Consumer consumer = new Consumer(consumerThreads.size(),  sharedQueue, animation);
        Thread consumerThread = new Thread(consumer);
        consumers.add(consumer);
        consumerThreads.add(consumerThread);
        consumerThread.start();
        animation.setNumberOfConsumers(consumers.size());
        updateLabel(consumerCountLabel, "Consumers", consumerThreads.size());
    }

    // Method to remove a consumer
    private static void removeConsumer(JLabel consumerCountLabel) {
        if (!consumerThreads.isEmpty()) {
            Thread consumerThread = consumerThreads.remove(consumerThreads.size() - 1);
            Consumer consumer = consumers.remove(consumers.size() - 1);
            consumerThread.interrupt();
            animation.setNumberOfConsumers(consumers.size());
            updateLabel(consumerCountLabel, "Consumers", consumerThreads.size());
        }
    }

    // Utility to update the number of producers or consumers
    private static void updateLabel(JLabel label, String role, int count) {
        label.setText(role + ": " + count);
    }

    // Update the delay for all running producers
    private static void updateProducerDelays() {
        for (Producer producer : producers) {
            producer.setProducerDelay(producerDelay);
        }
    }

    // Update the delay for all running consumers
    private static void updateConsumerDelays() {
        for (Consumer consumer : consumers) {
            consumer.setConsumerDelay(consumerDelay);
        }
    }
}
