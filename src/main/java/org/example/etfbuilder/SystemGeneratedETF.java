package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;


public class SystemGeneratedETF extends ETF {

    private IETFAlgorithm algo;
    private double dollarsToInvest;
    private int reinvestmentRate;
    private boolean initialized;

    public SystemGeneratedETF(IStockMarket market, IETFAlgorithm algorithm,
                              double dollarsToInvest, int reinvestmentRate) {
        if (market == null || algorithm == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (reinvestmentRate < 0) {
            throw new IllegalArgumentException("reinvestment rate is negative");
        }
        if (dollarsToInvest <= 0) {
            throw new IllegalArgumentException("initial investment must be greater than $0");
        }

        this.systemGenerated = true;
        this.initialized = false;
        this.amountInvested = 0.0;
        this.stocksInETF = new HashMap<>();
        this.etfPositions = new HashMap<>();
        this.startDate = algorithm.getCurrDate();
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

        Map<String, Double> stockSelections = algo.runAlgorithm(dollarsToInvest, startDate);
        for (Map.Entry<String, Double> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            double amtDollars = entry.getValue();
            addStock(companyName, amtDollars, startDate);
        }
        this.initialized = true;
    }


    public void updateETF(YearMonth currDate) {
        if (!initialized || !currDate.isAfter(startDate)) {
            return;
        }

        double dollarsToInvest = getETFValue(currDate);
        Map<String, Double> stockSelections = algo.runAlgorithm(dollarsToInvest, currDate);

        for (Map.Entry<String, Double> entry : stockSelections.entrySet()) {
            String companyName = entry.getKey();
            double targetAmt = entry.getValue();
            double currAmtHeld = etfPositions.getOrDefault(companyName, 0.0);

            if (targetAmt > currAmtHeld) {
                addStock(companyName, targetAmt - currAmtHeld, currDate);
            } else if (targetAmt < currAmtHeld) {
                removeStock(companyName, currAmtHeld - targetAmt);
            }
        }

        // remove any currently held stocks that are not in the generated list
        for (Map.Entry<String, Double> entry : etfPositions.entrySet()) {
            String companyName = entry.getKey();
            double positionInCompany = entry.getValue();

            if (!stockSelections.containsKey(companyName)) {
                removeStock(companyName, positionInCompany);
            }
        }

    }


}
