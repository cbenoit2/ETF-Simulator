package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.*;

public class Portfolio implements IPortfolio {

    private Set<IETF> etfsInPortfolio;
    private Map<IETF, Map<YearMonth, double[]>> portfolioReturns;

    public Portfolio() {
        this.etfsInPortfolio = new HashSet<>();
        this.portfolioReturns = new HashMap<>();
    }

    public Portfolio(Set<IETF> etfs) {
        this.etfsInPortfolio = etfs;
        this.portfolioReturns = new HashMap<>();
    }

    @Override
    public boolean addETF(IETF etf) {
        return etfsInPortfolio.add(etf);
    }

    @Override
    public boolean removeETF(IETF etf) {
        return etfsInPortfolio.remove(etf);
    }

    @Override
    public Set<IETF> getETFsInPortfolio() {
        return new HashSet<>(etfsInPortfolio);
    }

    @Override
    public Map<IETF, Map<YearMonth, double[]>> runSimulation(YearMonth endDate) {
        // todo error handling

        for (IETF etf : etfsInPortfolio) {
            portfolioReturns.putIfAbsent(etf, new TreeMap<>());
            if (etf.isSystemGenerated()) {
                simulateSystemGeneratedETF((SystemGeneratedETF) etf, endDate);
            } else {
                simulateUserCreatedETF((UserCreatedETF) etf, endDate);
            }
        }
        return new HashMap<>(portfolioReturns);
    }

    private void simulateUserCreatedETF(UserCreatedETF etf, YearMonth endDate) {
        YearMonth currDate = etf.getStartDate();
        while (!currDate.isAfter(endDate)) {
            Map<YearMonth, double[]> etfReturns = portfolioReturns.get(etf);
            double[] returns = calculateReturns(etf.getAmountInvested(), etf.getETFValue(currDate));
            etfReturns.put(currDate, returns);
            currDate = currDate.plusMonths(1);
        }
    }

    private void simulateSystemGeneratedETF(SystemGeneratedETF etf, YearMonth endDate) {
        etf.initializeETF();

        YearMonth currDate = etf.getStartDate();
        int reinvestmentRate = etf.getReinvestmentRate();
        YearMonth reinvestmentDate = currDate.plusMonths(reinvestmentRate);
        Map<YearMonth, double[]> etfReturns = portfolioReturns.get(etf);

        while (!currDate.isAfter(endDate)) {
            if (reinvestmentRate != 0 && currDate.equals(reinvestmentDate)) {
                etf.updateETF(currDate);
                reinvestmentDate = currDate.plusMonths(reinvestmentRate);
            }
            double[] returns = calculateReturns(etf.getAmountInvested(), etf.getETFValue(currDate));
            etfReturns.put(currDate, returns);
            currDate = currDate.plusMonths(1);
        }
    }

    // return array:
    // - 0: current value
    // - 1: return (in $)
    // - 2: return (in %)
    private double[] calculateReturns(double amtInvested, double currValue) {
        double[] etfReturns = new double[]{currValue, currValue - amtInvested,
                (currValue - amtInvested) / amtInvested};

        return etfReturns;
    }

    @Override
    public Map<YearMonth, double[]> getETFReturns(IETF etf, YearMonth fromDate, YearMonth toDate) {
        // todo error handling
        if (!etfsInPortfolio.contains(etf)) {
            return new TreeMap<>();
        }

        TreeMap<YearMonth, double[]> etfReturns =
                (TreeMap<YearMonth, double[]>) portfolioReturns.getOrDefault(etf, new TreeMap<>());
        if (!etfReturns.containsKey(fromDate) || !etfReturns.containsKey(toDate)) {
            runSimulation(toDate);
        }
        etfReturns = (TreeMap<YearMonth, double[]>) portfolioReturns.get(etf);
        return etfReturns.subMap(fromDate, true,
                toDate, true);
    }


}
