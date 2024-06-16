package restaurant;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Manager extends Employee {
    private List<Employee> listaPracownikow;
    private Timer manageSuppliesTimer;
    private Timer monitorEquipmentTimer;
    private Timer motivateEmployeesTimer;

    public Manager(int idPracownika, String imie, String stanowisko, float efektywnosc, List<Employee> listaPracownikow) {
        super(idPracownika, imie, stanowisko, efektywnosc);
        this.listaPracownikow = listaPracownikow;
    }

    @Override
    public void wykonajZadanie() {
        // Sprawdź, czy timery są już uruchomione
        if (manageSuppliesTimer == null) {
            manageSuppliesTimer = new Timer();
            manageSuppliesTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    zarzadzajZapasami();
                }
            }, 0, 10000); // Zarządzanie zapasami co 10 sekund
        }

        if (monitorEquipmentTimer == null) {
            monitorEquipmentTimer = new Timer();
            monitorEquipmentTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    monitorujSprzet();
                }
            }, 0, 20000); // Monitorowanie sprzętu co 20 sekund
        }

        if (motivateEmployeesTimer == null) {
            motivateEmployeesTimer = new Timer();
            motivateEmployeesTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    motywujPracownikow();
                }
            }, 0, 30000); // Motywowanie pracowników co 30 sekund
        }
    }

    private void zarzadzajZapasami() {
        System.out.println(imie + " sprawdza zapasy.");
        if (RestaurantSimulation.getInventory().getTotalSupplies() < 10) {
            System.out.println(imie + " zamawia nowe zapasy.");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Nowe zapasy zostały dostarczone.");
                    RestaurantSimulation.getInventory().addSupplies(10); // Zamawiane jest 10 zasobów
                }
            }, 10000); // Ustawienie czasu dostawy na 10 sekund dla testowania
        }
    }

    private void monitorujSprzet() {
        System.out.println(imie + " sprawdza stan sprzętu.");
        List<Equipment> equipmentList = RestaurantSimulation.getEquipment();
        for (Equipment equipment : equipmentList) {
            if (equipment.isBroken()) {
                System.out.println(imie + " zleca naprawę sprzętu.");
                try {
                    long sleepTime = Math.max((long) (700 / efektywnosc), 100);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid sleep time calculated: " + e.getMessage());
                }
                equipment.repair();
            }
        }
    }

    private void motywujPracownikow() {
        System.out.println(imie + " motywuje pracowników.");
        for (Employee employee : listaPracownikow) {
            if (!employee.isZmotywowany()) {
                if (Math.random() < 0.3) { // 30% szansa na zmotywowanie pracownika
                    employee.setZmotywowany(true);
                    System.out.println(employee.getImie() + " jest zmotywowany! Efektywność: " + employee.getEfektywnosc());
                }
            }
        }
    }
}
