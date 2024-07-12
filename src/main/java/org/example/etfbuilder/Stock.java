package org.example.etfbuilder;

public class Stock {

    private String name;
    private String industry;
    private double price;
    private double debtRatio;
    private double netIncome;
    private double marketCap;
    private double salesGrowth;
    private double peRatio;

    /**
     * Constructs a new Stock with the inputted name and its share
     * price set to the inputted price
     *
     * @param name  the name of the stock
     * @param price the price of the stock
     */
    public Stock(String name, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("stock price cannot be negative");
        }
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("stock price cannot be negative");
        }
        this.price = price;
    }

    public double getDebtRatio() {
        return debtRatio;
    }

    public void setDebtRatio(double debtRatio) {
        this.debtRatio = debtRatio;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    public double getSalesGrowth() {
        return salesGrowth;
    }

    public void setSalesGrowth(double salesGrowth) {
        this.salesGrowth = salesGrowth;
    }

    public double getPERatio() {
        return peRatio;
    }

    public void setPERatio(double peRatio) {
        this.peRatio = peRatio;
    }
}
