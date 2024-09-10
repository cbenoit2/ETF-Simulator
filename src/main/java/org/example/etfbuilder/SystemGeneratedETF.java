package org.example.etfbuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SystemGeneratedETF extends ETF {

    private final IETFAlgorithm algo;
    private final BigDecimal dollarsToInvest;
    private final int reinvestmentRate;
    private boolean initialized;

    public SystemGeneratedETF(IStockMarket market, IETFAlgorithm algorithm,
                              BigDecimal dollarsToInvest, int reinvestmentRate) {
        if (market == null || algorithm == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (reinvestmentRate < 0) {
            throw new IllegalArgumentException("reinvestment rate is negative");
        }
        if (dollarsToInvest.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("initial investment must be greater than $0");
        }

        this.systemGenerated = true;
        this.initialized = false;
        this.amountInvested = BigDecimal.ZERO;
        this.stocksInETF = new HashMap<>();
        this.etfPositions = new HashMap<>();
        this.startDate = algorithm.getCurrAlgoDate();
        this.stockMarket = market;
        this.algo = algorithm;
        this.dollarsToInvest = dollarsToInvest;
        this.reinvestmentRate = reinvestmentRate;
    }

    // may put elsewhere
    public int getReinvestmentRate() {
        return this.reinvestmentRate;
    }


    // todo: change return type
    public void initializeETF() {
        if (initialized) {
            return;
        }

        Map<String, BigDecimal> stockSelections = algo.runAlgorithm(dollarsToInvest, startDate);
        for (Map.Entry<String, BigDecimal> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            BigDecimal amtDollars = entry.getValue();
            addStock(companyName, amtDollars, startDate);
        }
        this.initialized = true;
    }


    public void updateETF(YearMonth currDate) {
        if (!initialized || !currDate.isAfter(startDate)) {
            return;
        }

        BigDecimal dollarsToInvest = getETFValue(currDate);
        Map<String, BigDecimal> stockSelections = algo.runAlgorithm(dollarsToInvest, currDate);

        for (Map.Entry<String, BigDecimal> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            BigDecimal targetAmt = entry.getValue();
            BigDecimal currAmtHeld = etfPositions.getOrDefault(companyName, BigDecimal.ZERO);

            if (targetAmt.compareTo(currAmtHeld) > 0) {
                addStock(companyName, targetAmt.subtract(currAmtHeld), currDate);
            } else if (targetAmt.compareTo(currAmtHeld) < 0) {
                removeStock(companyName, currAmtHeld.subtract(targetAmt));
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
            BigDecimal positionInCompany = etfPositions.get(name);
            removeStock(name, positionInCompany);
        }
    }


}
