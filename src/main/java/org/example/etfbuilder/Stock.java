package org.example.etfbuilder;

import java.math.BigDecimal;
import java.util.Comparator;

public class Stock {

    private String name;
    private String industry;
    private BigDecimal price;
    private BigDecimal debtRatio;
    private BigDecimal netIncome;
    private BigDecimal marketCap;
    private BigDecimal salesGrowth;
    private BigDecimal peRatio;

    public Stock(String name, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("stock price cannot be negative");
        }
        this.name = name;
        this.price = price;
    }

    public Stock(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getIndustry() {
        return this.industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("stock price cannot be negative");
        }
        this.price = price;
    }

    public BigDecimal getDebtRatio() {
        return this.debtRatio;
    }

    public void setDebtRatio(BigDecimal debtRatio) {
        this.debtRatio = debtRatio;
    }

    public BigDecimal getNetIncome() {
        return this.netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public BigDecimal getMarketCap() {
        return this.marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getSalesGrowth() {
        return this.salesGrowth;
    }

    public void setSalesGrowth(BigDecimal salesGrowth) {
        this.salesGrowth = salesGrowth;
    }

    public BigDecimal getPERatio() {
        return this.peRatio;
    }

    public void setPERatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }


    public static class StockNameComparator implements Comparator<Stock> {
        @Override
        public int compare(Stock stock1, Stock stock2) {
            String stock1Name = stock1.getName().toLowerCase();
            String stock2Name = stock2.getName().toLowerCase();
            return stock1Name.compareTo(stock2Name);
        }
    }

}
