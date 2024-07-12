package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.*;

public abstract class ETF implements IETF {

    protected boolean systemGenerated;
    protected YearMonth startDate;
    protected double amountInvested;
    // map of company name and a queue of stocks and the quantity for each stock
    protected Map<String, Queue<Map.Entry<Stock, Double>>> stocksInETF;
    // total dollar amount invested in each company
    protected Map<String, Double> etfPositions;
    protected IStockMarket stockMarket;


    @Override
    public boolean isSystemGenerated() {
        return this.systemGenerated;
    }

    @Override
    public YearMonth getStartDate() {
        return this.startDate;
    }

    @Override
    public double getAmountInvested() {
        return this.amountInvested;
    }

    @Override
    public Map<String, Double> getETFPositions() {
        // returns a copy of the etfPositions Map
        return new HashMap<>(etfPositions);
    }

    @Override
    public double getETFValue(YearMonth date) {
        if (date.isBefore(startDate) || date.isBefore(IStockMarket.FIRST_DATE_ENTRY) ||
                date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return 0.0;
        }

        double etfValue = 0.0;
        for (Queue<Map.Entry<Stock, Double>> stockQueue : stocksInETF.values()) {
            for (Map.Entry<Stock, Double> entry : stockQueue) {
                String companyName = entry.getKey().getName();
                double quantity = entry.getValue() / entry.getKey().getPrice();
                double currStockPrice = stockMarket.getStock(companyName, date).getPrice();
                etfValue += quantity * currStockPrice;
            }
        }

        return etfValue;
    }

    @Override
    public boolean addStock(String companyName, double dollars, YearMonth date) {
        if (dollars < 0 || companyName == null || date.isBefore(startDate) ||
                date.isBefore(IStockMarket.FIRST_DATE_ENTRY) || date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return false;
        }

        Stock stock = stockMarket.getStock(companyName, date);
        Queue<Map.Entry<Stock, Double>> stockQueue =
                stocksInETF.getOrDefault(companyName, new LinkedList<>());
        stockQueue.add(new AbstractMap.SimpleEntry<>(stock, dollars));
        stocksInETF.putIfAbsent(companyName, stockQueue);

        amountInvested += dollars;
        double positionInCompany = etfPositions.getOrDefault(companyName, 0.0);
        etfPositions.put(companyName, positionInCompany + dollars);

        return true;
    }

    @Override
    public boolean removeStock(String companyName, double dollars) {
        if (dollars < 0 || companyName == null) {
            return false;
        }

        double positionInCompany = etfPositions.getOrDefault(companyName, 0.0);
        if (dollars > positionInCompany) {
            return false;
        }

        Queue<Map.Entry<Stock, Double>> stockQueue = stocksInETF.get(companyName);
        if (stockQueue == null || stockQueue.isEmpty()) {
            return false;
        }
        double totalRemoved = 0.0;
        while (totalRemoved < dollars) {
            Map.Entry<Stock, Double> entry = stockQueue.peek();
            double amountOfStock = entry.getValue();
            double toSell = 0.0;
            if (amountOfStock <= dollars - totalRemoved) {
                toSell = amountOfStock;
                stockQueue.poll();
            } else {
                toSell = dollars - totalRemoved;
                entry.setValue(amountOfStock - toSell);
            }
            totalRemoved += toSell;
            amountInvested -= toSell;
        }

        etfPositions.put(companyName, positionInCompany - totalRemoved);
        if (etfPositions.get(companyName) == 0.0) {
            etfPositions.remove(companyName);
        }

        return true;
    }


}
