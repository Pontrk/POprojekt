package restaurant;

import java.util.List;

public class Waiter extends Employee {
    private List<Table> listaStolikow;
    private float napiwki;

    public Waiter(int idPracownika, String imie, String stanowisko, float efektywnosc, List<Table> listaStolikow) {
        super(idPracownika, imie, stanowisko, efektywnosc);
        this.listaStolikow = listaStolikow;
        this.napiwki = 0;
    }

    @Override
    public void wykonajZadanie() {
        System.out.println(imie + " obsługuje gości.");
        for (Table table : listaStolikow) {
            if (table.isOccupied()) {
                // Symulacja obsługi stolika
                try {
                    long czasObslugi = (long) (2000 / efektywnosc);
                    Thread.sleep(Math.max(czasObslugi, 100));
                    // Dodanie napiwków w zależności od efektywności pracy, napiwki nie mogą być ujemne
                    float bazaNapiwek = 1.0f; // Stała wartość bazowa
                    float napiwek = bazaNapiwek + Math.max((efektywnosc / 10), 0);
                    napiwki += napiwek;
                    System.out.println(imie + " obsłużył stolik " + table.getTableId() + ". Napiwki: " + napiwki);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public float getNapiwki() {
        return napiwki;
    }
}
