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
    private static int czasSymulacjiMs = 30000; // Czas trwania symulacji w milisekundach
    private static boolean simulationStopped = false; // Flaga do kontroli zakończenia symulacji

    // Dodane zmienne do kontroli czasu
    private static int czasSpedzonyPrzyStolikuMin = 5000;
    private static int czasSpedzonyPrzyStolikuMax = 10000;
    private static int czasDostawyZasobowMin = 5000; // Minimalny czas dostawy zasobów w ms
    private static int czasDostawyZasobowMax = 10000; // Maksymalny czas dostawy zasobów w ms
    private static int czasPojawieniaSieNowegoKlientaMin = 2000; // Minimalny czas pojawienia się nowego klienta w ms
    private static int czasPojawieniaSieNowegoKlientaMax = 5000; // Maksymalny czas pojawienia się nowego klienta w ms

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
        Manager manager = new Manager(1, "Jan", "Manager", getRandomFloat(-5, 5), listaPracownikow);
        Waiter waiter = new Waiter(2, "Anna", "Waiter", getRandomFloat(-5, 5), listaStolikow);
        Cook cook = new Cook(3, "Krzysztof", "Cook", getRandomFloat(-5, 5));

        listaPracownikow.add(manager);
        listaPracownikow.add(waiter);
        listaPracownikow.add(cook);

        // Dodawanie przykładowych stolików
        Table table1 = new Table(1, "wolny");
        Table table2 = new Table(2, "wolny");
        listaStolikow.add(table1);
        listaStolikow.add(table2);

        // Dodawanie przykładowego sprzętu
        Equipment oven = new Equipment(1, "Piecyk", "sprawny");
        Equipment fridge = new Equipment(2, "Lodowka", "sprawny");
        listaSprzetu.add(oven);
        listaSprzetu.add(fridge);

        System.out.println("Symulacja zostala zainicjowana.");
    }

    public static void startSimulation() {
        // Uruchomienie timera do obsługi nowych klientów i dodawania zapasów
        timer.schedule(new RestaurantTimerTask(new RestaurantSimulation()), 0, getRandomCustomerInterval());
        timer.schedule(new SupplyTimerTask(), 0, getRandomSupplyInterval());

        // Uruchomienie timera, który zakończy symulację po określonym czasie
        simulationEndTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopSimulation();
            }
        }, czasSymulacjiMs);

        System.out.println("Symulacja restauracji zostala uruchomiona.");
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
            System.out.println("Kuchnia nie działa z powodu awarii sprzetu lub symulacja zostala zatrzymana.");
            return;
        }

        int tableIndex = -1;
        for (int i = 0; i < listaStolikow.size(); i++) {
            if ("wolny".equals(listaStolikow.get(i).getStatus())) {
                tableIndex = i;
                break;
            }
        }

        customerQueue.addCustomer();
        System.out.println("Liczba klientow w kolejce: " + customerQueue.getQueueSize());

        if (tableIndex != -1 && customerQueue.getQueueSize() > 0) {
            customerQueue.removeCustomer();

            Table table = listaStolikow.get(tableIndex);
            table.assignGuest();
            int dishType = random.nextInt(3) + 1;
            Order order = new Order(listaZamowien.size() + 1, generateRandomOrder(dishType), "nowe");
            listaZamowien.add(order);
            System.out.println("Nowe zamowienie przy stoliku " + table.getNumerStolika());

            // Przypisanie zamówienia pracownikowi do wykonania
            for (Employee pracownik : listaPracownikow) {
                pracownik.przygotujZamowienie(order);
            }

            // Monitorowanie zużycia zapasów
            boolean suppliesConsumed = listaZapasow.consumeSupplies(dishType);
            if (suppliesConsumed) {
                float totalEfficiency = getTotalEmployeeEfficiency();
                float efficiencyImpact = 1 - (totalEfficiency / 100);
                int czasSpedzonyPrzyStoliku = (int)((czasSpedzonyPrzyStolikuMin + random.nextInt(czasSpedzonyPrzyStolikuMax - czasSpedzonyPrzyStolikuMin)) * efficiencyImpact);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        table.releaseTable();
                        order.setStatus("gotowe"); // Zmiana statusu zamówienia na "gotowe"
                        System.out.println("Stolik " + table.getNumerStolika() + " zostal zwolniony.");
                    }
                }, czasSpedzonyPrzyStoliku);
            } else {
                order.setStatus("oczekujace");
                System.out.println("Klient czeka na dostawe zapasow.");
            }
        } else {
            System.out.println("Brak wolnych stolikow dla nowych klientow.");
        }

        // Symulacja upływu czasu i awarie sprzętu
        czasSymulacji += getRandomCustomerInterval();
        if (random.nextFloat() < wspolczynnikAwarii) {
            Equipment equipment = listaSprzetu.get(random.nextInt(listaSprzetu.size()));
            equipment.setStatus("uszkodzony");
            System.out.println("Sprzet " + equipment.getTyp() + " ulegl awarii.");
            repairEquipment(equipment);
        }
    }

    private void repairEquipment(Equipment equipment) {
        kitchenOperational = false;
        System.out.println("Naprawa sprzetu " + equipment.getTyp() + " rozpoczeta.");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                equipment.repair();
                kitchenOperational = true;
                System.out.println("Naprawa sprzetu " + equipment.getTyp() + " zakonczona. Kuchnia ponownie dziala.");
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
        System.out.println("Symulacja zakonczona.");
        System.exit(0);
    }

    public static void generateReport() {
        System.out.println("=== Raport z symulacji restauracji ===");
        System.out.println("Stan zapasow:");
        listaZapasow.checkStock();

        System.out.println("Statusy zamówien:");
        for (Order zamowienie : listaZamowien) {
            System.out.println(" - Zamowienie " + zamowienie.getIdZamowienia() + ": " + zamowienie.getStatus());
        }

        System.out.println("Statusy stolikow:");
        for (Table stolik : listaStolikow) {
            System.out.println(" - Stolik " + stolik.getNumerStolika() + ": " + stolik.getStatus());
        }

        System.out.println("Statusy sprzetu:");
        for (Equipment sprzet : listaSprzetu) {
            System.out.println(" - Sprzet " + sprzet.getTyp() + ": " + sprzet.getStatus());
        }

        System.out.println("Efektywnosc pracownikow:");
        for (Employee pracownik : listaPracownikow) {
            System.out.println(" - " + pracownik.getImie() + " (" + pracownik.getStanowisko() + "): Efektywnosc pracy = " + pracownik.getEfektywnosc());
        }

        System.out.println("Czas trwania symulacji: " + czasSymulacji + " ms");
        System.out.println("Liczba klientow w kolejce na koniec symulacji: " + customerQueue.getQueueSize());
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

    private static long getRandomSupplyInterval() {
        return czasDostawyZasobowMin + random.nextInt(czasDostawyZasobowMax - czasDostawyZasobowMin);
    }

    public static void addSupplies(int amount) {
        listaZapasow.addSupplies(amount);
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
