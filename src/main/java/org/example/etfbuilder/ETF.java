package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETF;
import org.example.etfbuilder.interfaces.IStockMarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

public abstract class ETF implements IETF {
    
    protected boolean systemGenerated;
    protected String name;
    protected YearMonth startDate;
    // map of company name and a queue of map entries consisting of 
    // stocks and the quantity of that stock purchased
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
        return etfPositions.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getETFPositions() {
        // return a copy of the etfPositions Map
        return Collections.unmodifiableMap(etfPositions);
    }

    @Override
    public BigDecimal getETFValue(YearMonth date) {
        if (isInvalidDate(date)) {
            throw new IllegalArgumentException("invalid date entered");
        }
        BigDecimal etfValue = BigDecimal.ZERO;
        for (String companyName : etfPositions.keySet()) {
            BigDecimal totalQuantity = getTotalQuantityHeld(companyName);
            BigDecimal currStockPrice = stockMarket.getStock(companyName, date).getPrice();
            etfValue = etfValue.add(currStockPrice.multiply(totalQuantity));
        }
        return etfValue;
    }

    @Override
    public boolean buyStock(String companyName, BigDecimal quantityToBuy, YearMonth buyDate) {
        if (quantityToBuy == null) {
            return false;
        }

        quantityToBuy = quantityToBuy.setScale(2, RoundingMode.DOWN);
        if (quantityToBuy.compareTo(BigDecimal.ZERO) <= 0 || companyName == null || isInvalidDate(buyDate)) {
            return false;
        }
        Stock stock = stockMarket.getStock(companyName, buyDate);
        if (stock == null) {
            return false;
        }

        Queue<Map.Entry<Stock, BigDecimal>> stockQueue =
                stocksInETF.computeIfAbsent(companyName, k -> new LinkedList<>());
        stockQueue.add(new AbstractMap.SimpleEntry<>(stock, quantityToBuy));
        updateETFPositions(companyName,
                quantityToBuy.multiply(stock.getPrice()).setScale(4, RoundingMode.UNNECESSARY));
        return true;
    }

    @Override
    public boolean sellStock(String companyName, BigDecimal quantityToSell, YearMonth sellDate) {
        if (quantityToSell == null) {
            return false;
        }

        quantityToSell = quantityToSell.setScale(2, RoundingMode.DOWN);
        if (quantityToSell.compareTo(BigDecimal.ZERO) <= 0 || companyName == null ||
                etfPositions.get(companyName) == null || isInvalidDate(sellDate) ||
                quantityToSell.compareTo(getTotalQuantityHeld(companyName)) > 0) {
            return false;
        }

        // queue of all Stock objects and quantities held by ETF for specified company
        Queue<Map.Entry<Stock, BigDecimal>> companyStockQueue = stocksInETF.get(companyName);
        BigDecimal amtRemainingToSell = quantityToSell;
        while (amtRemainingToSell.compareTo(BigDecimal.ZERO) > 0) {
            Map.Entry<Stock, BigDecimal> queueEntry = companyStockQueue.peek();
            Stock stock = queueEntry.getKey();
            // quantity of this Stock object held by ETF
            BigDecimal stockQuantity = queueEntry.getValue();

            if (stockQuantity.compareTo(amtRemainingToSell) <= 0) {
                companyStockQueue.poll();
                updateETFPositions(companyName, stockQuantity.multiply(stock.getPrice()).negate());
                amtRemainingToSell = amtRemainingToSell.subtract(stockQuantity);
            } else {
                queueEntry.setValue(stockQuantity.subtract(amtRemainingToSell));
                updateETFPositions(companyName, amtRemainingToSell.multiply(stock.getPrice()).negate());
                amtRemainingToSell = BigDecimal.ZERO;
            }
        }
        if (etfPositions.get(companyName).compareTo(BigDecimal.ZERO) == 0) {
            etfPositions.remove(companyName);
        }
        if (companyStockQueue.isEmpty()) {
            stocksInETF.remove(companyName);
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

    protected boolean isInvalidDate(YearMonth date) {
        return date == null || date.isBefore(startDate) ||
                date.isBefore(IStockMarket.FIRST_DATE_ENTRY) ||
                date.isAfter(IStockMarket.LAST_DATE_ENTRY);
    }

    protected BigDecimal getTotalQuantityHeld(String companyName) {
        Queue<Map.Entry<Stock, BigDecimal>> stockQueue = stocksInETF.get(companyName);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (Map.Entry<Stock, BigDecimal> entry : stockQueue) {
            totalQuantity = totalQuantity.add(entry.getValue());
        }
        return totalQuantity;
    }

    private void updateETFPositions(String companyName, BigDecimal dollars) {
        BigDecimal position = etfPositions.getOrDefault(companyName, BigDecimal.ZERO);
        etfPositions.put(companyName, position.add(dollars));
    }

}
