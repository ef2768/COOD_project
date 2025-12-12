package OpenDataPhilly.common;

public class Property {
    private String zipCode;
    private double totalLivableArea;
    private double marketValue;

    public Property(String zipCode, double marketValue, double totalLivableArea) {
        this.marketValue = marketValue;
        this.totalLivableArea = totalLivableArea;
        this.zipCode = zipCode;
    }

    //accessors and modifyers
    public double getTotalLivableArea(){
        return totalLivableArea;
    }

    public void setTotalLivableArea(double totalLivableArea) {
        this.totalLivableArea = totalLivableArea;
    }

    public double getMarketValue(){
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public String getzipCode(){
        //System.out.println(zipCode);
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Property |" +
                "zipCode:" + zipCode +
                ", Market Value:" + marketValue +
                ", Total Livable Area:" + totalLivableArea +
                '|';
    }
}
