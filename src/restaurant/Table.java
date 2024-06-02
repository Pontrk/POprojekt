package restaurant;

public class Table {
    private int numerStolika;
    private String status;

    public Table(int numerStolika, String status) {
        this.numerStolika = numerStolika;
        this.status = status;
    }

    public void assignGuest() {
        this.status = "zajety";
        System.out.println("Przypisanie goscia do stolika " + numerStolika);
    }

    public void releaseTable() {
        this.status = "wolny";
        System.out.println("Zwolnienie stolika " + numerStolika);
    }

    public int getNumerStolika() {
        return numerStolika;
    }

    public String getStatus() {
        return status;
    }
}
