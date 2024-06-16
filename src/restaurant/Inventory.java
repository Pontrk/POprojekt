package restaurant;

public class Inventory {
    private int supplies;

    public Inventory(int supplies) {
        this.supplies = supplies;
    }

    public int getTotalSupplies() {
        return supplies;
    }

    public boolean consumeSupplies(int amount) {
        if (supplies >= amount) {
            supplies -= amount;
            System.out.println("Zużyto " + amount + " jednostek zapasów. Pozostało: " + supplies);
            return true;
        } else {
            System.out.println("Niewystarczająca ilość zapasów.");
            return false;
        }
    }

    public void addSupplies(int amount) {
        supplies += amount;
        System.out.println("Dodano " + amount + " jednostek zapasów. Aktualna ilość: " + supplies);
    }

    public void checkStock() {
        System.out.println("Aktualna ilość zapasów: " + supplies);
    }
}
