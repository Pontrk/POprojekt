package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class RestaurantSimulation {
    private static List<Order> listaZamowien;
    private static List<Employee> listaPracownikow;
    private static Inventory listaZapasow;
    private static List<Table> listaStolikow;
    private static float wspolczynnikAwarii;
    private static List<Equipment> listaSprzetu;
    private static Random random;
    private static Timer timer;
    private static Timer simulationEndTimer;
    private static int czasSymulacji;
    private static CustomerQueue customerQueue;
    private static boolean kitchenOperational = true;
    private static int czasSymulacjiMs = 100000; // Czas trwania symulacji w milisekundach
    private static boolean simulationStopped = false; // Flaga do kontroli zakończenia symulacji

    // Dodane zmienne do kontroli czasu
    private static int czasSpedzonyPrzyStolikuMin = 5000;
    private static int czasSpedzonyPrzyStolikuMax = 10000;
    private static int czasDostawyZasobowMin = 10000; // Minimalny czas dostawy zasobów w ms
    private static int czasDostawyZasobowMax = 30000; // Maksymalny czas dostawy zasobów w ms
    private static int czasPojawieniaSieNowegoKlientaMin = 2000; // Minimalny czas pojawienia się nowego klienta w ms
    private static int czasPojawieniaSieNowegoKlientaMax = 3000; // Maksymalny czas pojawienia się nowego klienta w ms

    public static void initSimulation() {
        listaZamowien = new ArrayList<>();
        listaPracownikow = new ArrayList<>();
        listaStolikow = new ArrayList<>();
        listaSprzetu = new ArrayList<>();
        customerQueue = new CustomerQueue();
        
        wspolczynnikAwarii = 0.05f;
        random = new Random();
        timer = new Timer();
        simulationEndTimer = new Timer();
        czasSymulacji = 0;

        // Generowanie początkowej liczby zapasów
        int initialSupplies = random.nextInt(10) + 20;
        listaZapasow = new Inventory(initialSupplies);

        // Dodawanie przykładowych pracowników
        Manager manager = new Manager(1, "Jan", "Manager", getRandomFloat(1, 3), listaPracownikow);
        Waiter waiter = new Waiter(2, "Anna", "Waiter", getRandomFloat(1, 3), listaStolikow);
        Cook cook = new Cook(3, "Krzysztof", "Cook", getRandomFloat(1, 3));

        listaPracownikow.add(manager);
        listaPracownikow.add(waiter);
        listaPracownikow.add(cook);

        // Dodawanie przykładowych stolików
        Table table1 = new Table(1);
        Table table2 = new Table(2);
        listaStolikow.add(table1);
        listaStolikow.add(table2);

        // Dodawanie przykładowego sprzętu
        Equipment oven = new Equipment(1, "Piecyk", "sprawny");
        Equipment fridge = new Equipment(2, "Lodówka", "sprawny");
        listaSprzetu.add(oven);
        listaSprzetu.add(fridge);

        System.out.println("Symulacja została zainicjowana.");
    }

    public static void startSimulation() {
        // Uruchomienie timera do obsługi nowych klientów
        timer.schedule(new RestaurantTimerTask(new RestaurantSimulation()), 0, getRandomCustomerInterval());

        // Uruchomienie timera dla pracowników
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Employee pracownik : listaPracownikow) {
                    pracownik.wykonajZadanie();
                }
            }
        }, 0, 5000); // Pracownicy wykonują swoje zadania co 5 sekund

        // Uruchomienie timera, który zakończy symulację po określonym czasie
        simulationEndTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopSimulation();
            }
        }, czasSymulacjiMs);

        System.out.println("Symulacja restauracji została uruchomiona.");
    }

    private static float getTotalEmployeeEfficiency() {
        float totalEfficiency = 0;
        for (Employee pracownik : listaPracownikow) {
            totalEfficiency += pracownik.getEfektywnosc();
        }
        return totalEfficiency;
    }

    public void handleNewCustomer() {
        if (!kitchenOperational || simulationStopped) {
            System.out.println("Kuchnia nie działa z powodu awarii sprzętu lub symulacja została zatrzymana.");
            return;
        }

        int tableIndex = -1;
        for (int i = 0; i < listaStolikow.size(); i++) {
            if (!listaStolikow.get(i).isOccupied()) {
                tableIndex = i;
                break;
            }
        }

        customerQueue.addCustomer();
        System.out.println("Liczba klientów w kolejce: " + customerQueue.getQueueSize());

        if (tableIndex != -1 && customerQueue.getQueueSize() > 0) {
            customerQueue.removeCustomer();

            Table table = listaStolikow.get(tableIndex);
            table.assignGuest();
            int dishType = random.nextInt(3) + 1;
            Order order = new Order(listaZamowien.size() + 1, generateRandomOrder(dishType), "nowe");
            listaZamowien.add(order);
            System.out.println("Nowe zamówienie przy stoliku " + table.getTableId());

            // Przypisanie zamówienia pierwszemu dostępnemu pracownikowi do wykonania
            if (!listaPracownikow.isEmpty()) {
                Employee pracownik = listaPracownikow.get(0); // Pobierz pierwszego pracownika z listy
                pracownik.przygotujZamowienie(order);
            }

            // Monitorowanie zużycia zapasów
            boolean suppliesConsumed = listaZapasow.consumeSupplies(dishType);
            if (suppliesConsumed) {
                float totalEfficiency = getTotalEmployeeEfficiency();
                float efficiencyImpact = 1 - (totalEfficiency / 100);
                int czasSpedzonyPrzyStoliku = (int)((czasSpedzonyPrzyStolikuMin + random.nextInt(czasSpedzonyPrzyStolikuMax - czasSpedzonyPrzyStolikuMin)) * efficiencyImpact);

                // Zaplanowanie zwolnienia stolika
                table.scheduleRelease(czasSpedzonyPrzyStoliku);
                order.setStatus("gotowe"); // Zmiana statusu zamówienia na "gotowe"
            } else {
                order.setStatus("oczekujące");
                System.out.println("Klient czeka na dostawę zapasów.");
            }
        } else {
            System.out.println("Brak wolnych stolików dla nowych klientów.");
        }

        // Symulacja upływu czasu i awarie sprzętu
        czasSymulacji += getRandomCustomerInterval();
        if (random.nextFloat() < wspolczynnikAwarii) {
            Equipment equipment = listaSprzetu.get(random.nextInt(listaSprzetu.size()));
            equipment.setStatus("uszkodzony");
            System.out.println("Sprzęt " + equipment.getTyp() + " uległ awarii.");
            repairEquipment(equipment);
        }
    }

    private void repairEquipment(Equipment equipment) {
        kitchenOperational = false;
        System.out.println("Naprawa sprzętu " + equipment.getTyp() + " rozpoczęta.");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                equipment.repair();
                kitchenOperational = true;
                System.out.println("Naprawa sprzętu " + equipment.getTyp() + " zakończona. Kuchnia ponownie działa.");
            }
        }, 5000); // Symulacja czasu naprawy (5 sekund)
    }

    public static synchronized void stopSimulation() {
        if (simulationStopped) {
            return;
        }
        simulationStopped = true;
        timer.cancel();
        simulationEndTimer.cancel();
        generateReport();
        System.out.println("Symulacja zakończona.");
        System.exit(0);
    }

    public static void generateReport() {
        System.out.println("=== Raport z symulacji restauracji ===");
        System.out.println("Stan zapasów:");
        listaZapasow.checkStock();

        System.out.println("Statusy zamówień:");
        for (Order zamowienie : listaZamowien) {
            System.out.println(" - Zamówienie " + zamowienie.getIdZamowienia() + ": " + zamowienie.getStatus());
        }

        System.out.println("Statusy stolików:");
        for (Table stolik : listaStolikow) {
            System.out.println(" - Stolik " + stolik.getTableId() + ": " + stolik.isOccupied());
        }

        System.out.println("Statusy sprzętu:");
        for (Equipment sprzet : listaSprzetu) {
            System.out.println(" - Sprzęt " + sprzet.getTyp() + ": " + sprzet.getStatus());
        }

        System.out.println("Efektywność pracowników:");
        for (Employee pracownik : listaPracownikow) {
            System.out.println(" - " + pracownik.getImie() + " (" + pracownik.getStanowisko() + "): Efektywność pracy = " + pracownik.getEfektywnosc());
            if (pracownik instanceof Waiter) {
                System.out.println("    Napiwki: " + ((Waiter) pracownik).getNapiwki());
            }
        }

        System.out.println("Czas trwania symulacji: " + czasSymulacji + " ms");
        System.out.println("Liczba klientów w kolejce na koniec symulacji: " + customerQueue.getQueueSize());
    }

    private static float getRandomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static List<String> generateRandomOrder(int dishType) {
        List<String> dania = new ArrayList<>();
        dania.add("Danie " + dishType);
        return dania;
    }

    private static long getRandomCustomerInterval() {
        if (czasPojawieniaSieNowegoKlientaMax <= czasPojawieniaSieNowegoKlientaMin) {
            return czasPojawieniaSieNowegoKlientaMin;
        }
        return czasPojawieniaSieNowegoKlientaMin + random.nextInt(czasPojawieniaSieNowegoKlientaMax - czasPojawieniaSieNowegoKlientaMin);
    }

    public static long getRandomSupplyInterval() {
        return czasDostawyZasobowMin + random.nextInt(czasDostawyZasobowMax - czasDostawyZasobowMin);
    }

    public static Inventory getInventory() {
        return listaZapasow;
    }

    public static List<Table> getTables() {
        return listaStolikow;
    }

    public static List<Equipment> getEquipment() {
        return listaSprzetu;
    }

    public static List<Order> getOrders() {
        return listaZamowien;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("=== Menu ===");
            System.out.println("1. Uruchom symulacje");
            System.out.println("2. Zmien czas trwania symulacji (ms)");
            System.out.println("3. Zmien minimalny czas spedzony przy stoliku (ms)");
            System.out.println("4. Zmien maksymalny czas spedzony przy stoliku (ms)");
            System.out.println("5. Zmien wspolczynnik awarii");
            System.out.println("6. Zmien minimalny czas dostawy zasobow (ms)");
            System.out.println("7. Zmien maksymalny czas dostawy zasobow (ms)");
            System.out.println("8. Zmien minimalny czas pojawienia się nowego klienta (ms)");
            System.out.println("9. Zmien maksymalny czas pojawienia się nowego klienta (ms)");
            System.out.println("10. Wyjdz");
            System.out.print("Wybierz opcje: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    initSimulation();
                    startSimulation();
                    break;
                case 2:
                    System.out.print("Podaj nowy czas trwania symulacji (ms): ");
                    czasSymulacjiMs = scanner.nextInt();
                    break;
                case 3:
                    System.out.print("Podaj nowy minimalny czas spedzony przy stoliku (ms): ");
                    czasSpedzonyPrzyStolikuMin = scanner.nextInt();
                    break;
                case 4:
                    System.out.print("Podaj nowy maksymalny czas spedzony przy stoliku (ms): ");
                    czasSpedzonyPrzyStolikuMax = scanner.nextInt();
                    break;
                case 5:
                    System.out.print("Podaj nowy wspolczynnik awarii: ");
                    wspolczynnikAwarii = scanner.nextFloat();
                    break;
                case 6:
                    System.out.print("Podaj nowy minimalny czas dostawy zasobow (ms): ");
                    czasDostawyZasobowMin = scanner.nextInt();
                    break;
                case 7:
                    System.out.print("Podaj nowy maksymalny czas dostawy zasobow (ms): ");
                    czasDostawyZasobowMax = scanner.nextInt();
                    break;
                case 8:
                    System.out.print("Podaj nowy minimalny czas pojawienia sie nowego klienta (ms): ");
                    czasPojawieniaSieNowegoKlientaMin = scanner.nextInt();
                    break;
                case 9:
                    System.out.print("Podaj nowy maksymalny czas pojawienia sie nowego klienta (ms): ");
                    czasPojawieniaSieNowegoKlientaMax = scanner.nextInt();
                    break;
                case 10:
                    exit = true;
                    System.out.println("Wyjscie z programu.");
                    break;
                default:
                    System.out.println("Nieprawidlowa opcja. Sprobuj ponownie.");
            }
        }

        scanner.close();
    }
}
