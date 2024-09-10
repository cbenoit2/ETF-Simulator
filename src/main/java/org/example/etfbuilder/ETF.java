package org.example.etfbuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

public abstract class ETF implements IETF {

    protected boolean systemGenerated;
    protected String name;
    protected YearMonth startDate;
    protected BigDecimal amountInvested;
    // map of company name and a queue of stocks and the quantity for each stock
    protected Map<String, Queue<Map.Entry<Stock, BigDecimal>>> stocksInETF;
    // total dollar amount invested in each company
    protected Map<String, BigDecimal> etfPositions;
    protected IStockMarket stockMarket;


    @Override
    public void setETFName(String etfName) {
        this.name = etfName;
    }

    @Override
    public boolean isSystemGenerated() {
        return this.systemGenerated;
    }

    @Override
    public YearMonth getStartDate() {
        return this.startDate;
    }

    @Override
    public BigDecimal getAmountInvested() {
        return this.amountInvested;
    }

    @Override
    public Map<String, BigDecimal> getETFPositions() {
        // returns a copy of the etfPositions Map
        return Collections.unmodifiableMap(etfPositions);
    }

    @Override
    public BigDecimal getETFValue(YearMonth date) {
        // todo: may change to throw exception
        if (date == null || date.isBefore(startDate) || date.isBefore(IStockMarket.FIRST_DATE_ENTRY)
                || date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return BigDecimal.ZERO;
        }

        BigDecimal etfValue = BigDecimal.ZERO;
        for (Queue<Map.Entry<Stock, BigDecimal>> stockQueue : stocksInETF.values()) {
            for (Map.Entry<Stock, BigDecimal> entry : stockQueue) {
                String companyName = entry.getKey().getName();
                BigDecimal quantity = entry.getValue().divide(entry.getKey().getPrice(), RoundingMode.HALF_EVEN);
                BigDecimal currStockPrice = stockMarket.getStock(companyName, date).getPrice();
                etfValue = etfValue.add(currStockPrice.multiply(quantity));
            }
        }
        return etfValue;
    }

    @Override
    public boolean addStock(String companyName, BigDecimal dollarsToAdd, YearMonth date) {
        if (dollarsToAdd.compareTo(BigDecimal.ZERO) < 0 || companyName == null || date.isBefore(startDate) ||
                date.isBefore(IStockMarket.FIRST_DATE_ENTRY) || date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return false;
        }

        Stock stock = stockMarket.getStock(companyName, date);
        Queue<Map.Entry<Stock, BigDecimal>> stockQueue =
                stocksInETF.getOrDefault(companyName, new LinkedList<>());
        stockQueue.add(new AbstractMap.SimpleEntry<>(stock, dollarsToAdd));
        stocksInETF.putIfAbsent(companyName, stockQueue);

        amountInvested = amountInvested.add(dollarsToAdd);
        BigDecimal positionInCompany = etfPositions.getOrDefault(companyName, BigDecimal.ZERO);
        etfPositions.put(companyName, positionInCompany.add(dollarsToAdd));

        return true;
    }

    @Override
    public boolean removeStock(String companyName, BigDecimal dollarsToRemove) {
        if (dollarsToRemove.compareTo(BigDecimal.ZERO) < 0 || companyName == null) {
            return false;
        }

        BigDecimal positionInCompany = etfPositions.getOrDefault(companyName, BigDecimal.ZERO);
        if (dollarsToRemove.compareTo(positionInCompany) > 0) {
            return false;
        }
        Queue<Map.Entry<Stock, BigDecimal>> stockQueue = stocksInETF.get(companyName);
        if (stockQueue == null || stockQueue.isEmpty()) {
            return false;
        }

        BigDecimal totalRemoved = BigDecimal.ZERO;
        while (totalRemoved.compareTo(dollarsToRemove) < 0) {
            Map.Entry<Stock, BigDecimal> entry = stockQueue.peek();
            BigDecimal dollarsOfStock = entry.getValue();
            BigDecimal amtLeftToRemove = dollarsToRemove.subtract(totalRemoved);
            BigDecimal toSell = BigDecimal.ZERO;
            if (dollarsOfStock.compareTo(amtLeftToRemove) <= 0) {
                toSell = dollarsOfStock;
                stockQueue.poll();
            } else {
                toSell = amtLeftToRemove;
                entry.setValue(dollarsOfStock.subtract(toSell));
            }
            totalRemoved = totalRemoved.add(toSell);
            amountInvested = amountInvested.subtract(toSell);
        }

        etfPositions.put(companyName, positionInCompany.subtract(totalRemoved));
        if (etfPositions.get(companyName).compareTo(BigDecimal.ZERO) == 0) {
            etfPositions.remove(companyName);
        }

        return true;
    }


    @Override
    public String toString() {
        if (isSystemGenerated()) {
            return this.name + " (algorithm-created)";
        } else {
            return this.name + " (user-created)";
        }
    }

}
