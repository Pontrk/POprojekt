package restaurant;

import java.util.Timer;
import java.util.TimerTask;

public class Table {
    private int idStolika;
    private boolean occupied;
    private Timer releaseTimer;

    public Table(int idStolika) {
        this.idStolika = idStolika;
        this.occupied = false;
        this.releaseTimer = new Timer();
    }

    public int getTableId() {
        return idStolika;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void assignGuest() {
        occupied = true;
        System.out.println("Stolik " + idStolika + " został przypisany do gościa.");
    }

    public void releaseTable() {
        occupied = false;
        System.out.println("Stolik " + idStolika + " został zwolniony.");
    }

    public void scheduleRelease(long delay) {
        releaseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                releaseTable();
            }
        }, delay);
    }
}
