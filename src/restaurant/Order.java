package restaurant;

import java.util.List;

public class Order {
    private int idZamowienia;
    private List<String> listaDan;
    private String status;

    public Order(int idZamowienia, List<String> listaDan, String status) {
        this.idZamowienia = idZamowienia;
        this.listaDan = listaDan;
        this.status = status;
    }

    public int getIdZamowienia() {
        return idZamowienia;
    }

    public List<String> getListaDan() {
        return listaDan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
