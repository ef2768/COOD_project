package OpenDataPhilly.common;

public class Population {
    private String zipCode;
    private int population;

    public Population(String zipCode, int population) {
        this.zipCode = zipCode;
        this.population = population;
    }

    //accessors and modifyers
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    @Override
    public String toString() {
        return "Population |" +
                "zipCode:'" + zipCode +
                ", Population Count:" + population +
                '|';
    }

}
