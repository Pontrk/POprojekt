package restaurant;

public abstract class Employee {
    protected int idPracownika;
    protected String imie;
    protected String stanowisko;
    protected float efektywnosc;
    protected boolean zmotywowany;

    public Employee(int idPracownika, String imie, String stanowisko, float efektywnosc) {
        this.idPracownika = idPracownika;
        this.imie = imie;
        this.stanowisko = stanowisko;
        this.efektywnosc = efektywnosc;
        this.zmotywowany = false;
    }

    public abstract void wykonajZadanie();

    public void przygotujZamowienie(Order order) {
        try {
            long czasPrzygotowania = (long) (2000 / efektywnosc);
            Thread.sleep(Math.max(czasPrzygotowania, 100)); // Czas przygotowania zamówienia
            order.setStatus("gotowe");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid sleep time calculated: " + e.getMessage());
        }
    }

    public int getIdPracownika() {
        return idPracownika;
    }

    public String getImie() {
        return imie;
    }

    public String getStanowisko() {
        return stanowisko;
    }

    public float getEfektywnosc() {
        return efektywnosc;
    }

    public boolean isZmotywowany() {
        return zmotywowany;
    }

    public void setZmotywowany(boolean zmotywowany) {
        this.zmotywowany = zmotywowany;
        if (zmotywowany) {
            this.efektywnosc *= 1.2; // Zwiększenie efektywności o 20% jeśli pracownik jest zmotywowany
        } else {
            this.efektywnosc /= 1.2; // Przywrócenie oryginalnej efektywności, gdy przestaje być zmotywowany
        }
    }
}
