package restaurant;

import java.util.TimerTask;

public class RestaurantTimerTask extends TimerTask {
    private RestaurantSimulation simulation;

    public RestaurantTimerTask(RestaurantSimulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        simulation.handleNewCustomer();
    }
}
