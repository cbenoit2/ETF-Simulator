/**
 * @author Chloe Benoit (cbenoit2) and Justin Summers (justin2justin)
 */

package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.example.etfbuilder.interfaces.IStockMarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SystemGeneratedETF extends ETF {

    private final IETFAlgorithm algo;
    private final int reinvestmentRate;

    public SystemGeneratedETF(IStockMarket market, IETFAlgorithm algorithm,
                              BigDecimal dollarsToInvest, int reinvestmentRate) {
        if (market == null || algorithm == null || dollarsToInvest == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (reinvestmentRate < 0) {
            throw new IllegalArgumentException("reinvestment rate is negative");
        }
        if (dollarsToInvest.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("initial investment must be greater than $0");
        }
        this.systemGenerated = true;
        this.stocksInETF = new HashMap<>();
        this.etfPositions = new HashMap<>();
        this.startDate = algorithm.getCurrAlgoDate();
        this.stockMarket = market;
        this.algo = algorithm;
        this.reinvestmentRate = reinvestmentRate;
        initializeETF(dollarsToInvest.setScale(4, RoundingMode.DOWN));
    }


    public int getReinvestmentRate() {
        return this.reinvestmentRate;
    }

    private void initializeETF(BigDecimal dollarsToInvest) {
        Map<String, BigDecimal> stockSelections = algo.runAlgorithm(dollarsToInvest, startDate);
        for (Map.Entry<String, BigDecimal> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            if (companyName.equals("Cash")) {
                etfPositions.put(companyName, entry.getValue());
            } else {
                BigDecimal quantity = entry.getValue();
                buyStock(companyName, quantity, startDate);
            }
        }
    }

    public void updateETF(YearMonth currDate) {
        if (isInvalidDate(currDate) || !currDate.isAfter(startDate)) {
            throw new IllegalArgumentException();
        }

        BigDecimal dollarsToInvest = getETFValue(currDate);
        Map<String, BigDecimal> stockSelections = algo.runAlgorithm(dollarsToInvest, currDate);

        // buy and sell stock to align with stock selections
        for (Map.Entry<String, BigDecimal> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            if (companyName.equals("Cash")) {
                etfPositions.put(companyName, entry.getValue());
            } else {
                BigDecimal targetQuantity = entry.getValue();
                BigDecimal currQuantityHeld = getTotalQuantityHeld(companyName);
                if (targetQuantity.compareTo(currQuantityHeld) > 0) {
                    buyStock(companyName, targetQuantity.subtract(currQuantityHeld), currDate);
                } else if (targetQuantity.compareTo(currQuantityHeld) < 0) {
                    sellStock(companyName, currQuantityHeld.subtract(targetQuantity), currDate);
                }
            }
        }

        Set<String> toSell = new HashSet<>();
        // remove any currently held stocks that are not in the generated list
        for (Map.Entry<String, BigDecimal> entry : etfPositions.entrySet()) {
            String companyName = entry.getKey();
            if (!stockSelections.containsKey(companyName)) {
                toSell.add(companyName);
            }
        }
        for (String name : toSell) {
            sellStock(name, getTotalQuantityHeld(name), currDate);
        }
    }

    @Override
    public BigDecimal getETFValue(YearMonth date) {
        if (isInvalidDate(date)) {
            throw new IllegalArgumentException("invalid date entered");
        }

        BigDecimal etfValue = BigDecimal.ZERO;
        for (String companyName : etfPositions.keySet()) {
            if (companyName.equals("Cash")) {
                etfValue = etfValue.add(etfPositions.get("Cash"));
            } else {
                BigDecimal totalQuantity = getTotalQuantityHeld(companyName);
                BigDecimal currStockPrice = stockMarket.getStock(companyName, date).getPrice();
                etfValue = etfValue.add(currStockPrice.multiply(totalQuantity));
            }
        }
        return etfValue;
    }


}
