package restaurant;

public class Inventory {
    private int supplies;

    public Inventory(int initialSupplies) {
        this.supplies = initialSupplies;
    }

    public void checkStock() {
        System.out.println("Stan zapasow: " + supplies);
    }

    public void addSupplies(int amount) {
        supplies += amount;
        System.out.println("Dodano " + amount + " jednostek zapasow. Obecny stan: " + supplies);
    }

    public boolean consumeSupplies(int amount) {
        if (supplies >= amount) {
            supplies -= amount;
            System.out.println("Zuzyto " + amount + " jednostek zapasow. Pozostalo: " + supplies);
            return true;
        } else {
            System.out.println("Brak wystarczajacych zapasow do zuzycia " + amount + " jednostek.");
            return false;
        }
    }

    public int getSupplies() {
        return supplies;
    }
}
