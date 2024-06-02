package restaurant;

import java.util.TimerTask;
import java.util.Random;

public class SupplyTimerTask extends TimerTask {
    private Random random = new Random();

    @Override
    public void run() {
        int amount = random.nextInt(2) + 1; // Dodajemy od 1 do 2 jednostek zapasów
        RestaurantSimulation.addSupplies(amount);
    }
}
