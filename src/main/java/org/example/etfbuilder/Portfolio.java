package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETF;
import org.example.etfbuilder.interfaces.IPortfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

public class Portfolio implements IPortfolio {

    private final List<IETF> etfsInPortfolio;
    private final Map<IETF, Map<YearMonth, BigDecimal[]>> portfolioReturns;

    public Portfolio() {
        this.etfsInPortfolio = new ArrayList<>();
        this.portfolioReturns = new HashMap<>();
    }

    @Override
    public boolean addETF(IETF etf) {
        if (etfsInPortfolio.contains(etf)) {
            return false;
        }
        return etfsInPortfolio.add(etf);
    }

    @Override
    public boolean removeETF(IETF etf) {
        return etfsInPortfolio.remove(etf);
    }

    @Override
    public List<IETF> getETFsInPortfolio() {
        return Collections.unmodifiableList(etfsInPortfolio);
    }

    @Override
    public Map<YearMonth, BigDecimal[]> runSimulationOnETF(IETF etf, YearMonth endDate) {
        // todo error handling

        portfolioReturns.putIfAbsent(etf, new TreeMap<>());
        if (etf.isSystemGenerated()) {
            simulateSystemGeneratedETF((SystemGeneratedETF) etf, endDate);
        } else {
            simulateUserCreatedETF((UserCreatedETF) etf, endDate);
        }

        return Collections.unmodifiableMap(portfolioReturns.get(etf));
    }

    private void simulateUserCreatedETF(UserCreatedETF etf, YearMonth endDate) {
        YearMonth currDate = etf.getStartDate();
        while (!currDate.isAfter(endDate)) {
            Map<YearMonth, BigDecimal[]> etfReturns = portfolioReturns.get(etf);
            BigDecimal[] returns = calculateETFReturns(etf.getAmountInvested(), etf.getETFValue(currDate));
            etfReturns.put(currDate, returns);
            currDate = currDate.plusMonths(1);
        }
    }

    private void simulateSystemGeneratedETF(SystemGeneratedETF etf, YearMonth endDate) {
        etf.initializeETF();

        YearMonth currDate = etf.getStartDate();
        int reinvestmentRate = etf.getReinvestmentRate();
        YearMonth reinvestmentDate = currDate.plusMonths(reinvestmentRate);
        Map<YearMonth, BigDecimal[]> etfReturns = portfolioReturns.get(etf);

        while (!currDate.isAfter(endDate)) {
            if (reinvestmentRate != 0 && currDate.equals(reinvestmentDate)) {
                etf.updateETF(currDate);
                reinvestmentDate = currDate.plusMonths(reinvestmentRate);
            }
            BigDecimal[] returns = calculateETFReturns(etf.getAmountInvested(), etf.getETFValue(currDate));
            etfReturns.put(currDate, returns);
            currDate = currDate.plusMonths(1);
        }
    }

    @Override
    public Map<IETF, Map<YearMonth, BigDecimal[]>> runSimulationOnPortfolio(YearMonth endDate) {
        // todo error handling
        for (IETF etf : etfsInPortfolio) {
            runSimulationOnETF(etf, endDate);
        }
        return Collections.unmodifiableMap(portfolioReturns);
    }

    // return array:
    // - 0: current value
    // - 1: return (in $)
    // - 2: return (in %)
    private BigDecimal[] calculateETFReturns(BigDecimal amtInvested, BigDecimal currValue) {
        return new BigDecimal[]{currValue, currValue.subtract(amtInvested),
                (currValue.subtract(amtInvested)).divide(amtInvested, 4, RoundingMode.HALF_UP)};
    }

    @Override
    public Map<YearMonth, BigDecimal[]> getETFReturns(IETF etf, YearMonth fromDate, YearMonth toDate) {
        // todo error handling
        if (!etfsInPortfolio.contains(etf)) {
            return new TreeMap<>();
        }
        TreeMap<YearMonth, BigDecimal[]> etfReturns =
                (TreeMap<YearMonth, BigDecimal[]>) portfolioReturns.getOrDefault(etf, new TreeMap<>());
        if (!etfReturns.containsKey(fromDate) || !etfReturns.containsKey(toDate)) {
            runSimulationOnETF(etf, toDate);
        }
        etfReturns = (TreeMap<YearMonth, BigDecimal[]>) portfolioReturns.get(etf);
        return etfReturns.subMap(fromDate, true,
                toDate, true);
    }

    @Override
    public Map<YearMonth, BigDecimal> getMarketReturns(Map<YearMonth, BigDecimal> sp500Value,
                                                       YearMonth investmentStart, YearMonth investmentEnd) {
        Map<YearMonth, BigDecimal> marketReturns = new TreeMap<>();
        BigDecimal startVal = sp500Value.get(investmentStart);
        YearMonth currDate = investmentStart;
        while (!currDate.isAfter(investmentEnd)) {
            BigDecimal currMonthValue = sp500Value.get(currDate);
            BigDecimal returnPercentage =
                    (currMonthValue.subtract(startVal)).divide(startVal, 4, RoundingMode.HALF_UP);
            marketReturns.put(currDate, returnPercentage);
            currDate = currDate.plusMonths(1);
        }
        return marketReturns;
    }


}
