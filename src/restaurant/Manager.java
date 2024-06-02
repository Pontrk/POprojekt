package restaurant;

import java.util.List;

public class Manager extends Employee {
    private List<Employee> podwladni;

    public Manager(int idPracownika, String imie, String stanowisko, float efektywnosc, List<Employee> podwladni) {
        super(idPracownika, imie, stanowisko, efektywnosc);
        this.podwladni = podwladni;
    }

    @Override
    public void wykonajZadanie() {
        System.out.println("Manager nadzoruje prace.");
    }

    @Override
    public void przygotujZamowienie(Order order) {
        System.out.println("Manager nadzoruje przygotowanie zamowienia: " + order.getIdZamowienia());
    }
}
