package OpenDataPhilly.common;

public class Property {
    private String zipCode;
    private int totalLivableArea;
    private int marketValue;

    public Property(String zipCode, int marketValue, int totalLivableArea) {
        this.marketValue = marketValue;
        this.totalLivableArea = totalLivableArea;
        this.zipCode = zipCode;
    }

    //accessors and modifyers
    public int getTotalLivableArea(){
        return totalLivableArea;
    }

    public void setTotalLivableArea(int totalLivableArea) {
        this.totalLivableArea = totalLivableArea;
    }

    public int getMarketValue(){
        return marketValue;
    }

    public void setMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }

    public String getzipCode(){
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Property |" +
                "zipCode:'" + zipCode +
                ", Market Value:" + marketValue +
                ", Total Livable Area:" + totalLivableArea +
                '|';
    }
}
