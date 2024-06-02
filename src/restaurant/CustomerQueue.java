package restaurant;

import java.util.LinkedList;
import java.util.Queue;

public class CustomerQueue {
    private Queue<Integer> queue;

    public CustomerQueue() {
        this.queue = new LinkedList<>();
    }

    public void addCustomer() {
        queue.add(1); // Dodajemy klienta do kolejki
        System.out.println("Dodano nowego klienta do kolejki. Liczba klientow w kolejce: " + getQueueSize());
    }

    public void removeCustomer() {
        if (!queue.isEmpty()) {
            queue.poll(); // Usuwamy klienta z kolejki
            System.out.println("Usunieto klienta z kolejki. Liczba klientow w kolejce: " + getQueueSize());
        } else {
            System.out.println("Brak klientow w kolejce.");
        }
    }

    public int getQueueSize() {
        return queue.size();
    }
}
