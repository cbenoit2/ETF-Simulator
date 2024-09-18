/**
 * @author Chloe Benoit (cbenoit2) and Justin Summers (justin2justin)
 */

package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.example.etfbuilder.interfaces.IStockMarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

public class ETFAlgorithm implements IETFAlgorithm {

    private final IStockMarket stockMarket;
    private final double[] preferences;
    private final Set<String> possibleCompanies;
    private YearMonth currAlgoDate;

    public ETFAlgorithm(IStockMarket stockMarket, YearMonth startDate, double[] preferences,
                        String industry) {
        if (stockMarket == null || startDate == null || industry == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (startDate.isBefore(stockMarket.getFirstDateEntry()) ||
                startDate.isAfter(stockMarket.getLastDateEntry())) {
            throw new IllegalArgumentException("invalid date");
        }
        if (preferences.length != 5) {
            throw new IllegalArgumentException("preferences array is incorrect size");
        }
        if (Arrays.stream(preferences).sum() != 1.0) {
            throw new IllegalArgumentException("weights in preferences array do not add up to 1");
        }

        this.stockMarket = stockMarket;
        this.currAlgoDate = startDate;
        this.preferences = preferences;
        this.possibleCompanies = selectIndustry(industry, currAlgoDate);
    }

    @Override
    public Set<String> selectIndustry(String industry, YearMonth date) {
        if (industry == null || date == null) {
            return new HashSet<>();
        }
        industry = industry.toLowerCase();
        Set<String> stocksInIndustry = new HashSet<>();
        // if user selects all, include all company names in set
        if (industry.equals("all")) {
            stocksInIndustry.addAll(stockMarket.getAllCompanyNames(date));
        } else {
            // otherwise, add all names of companies in the selected industry to the set
            Set<Stock> allStocks = stockMarket.getStocksOnDate(date);
            for (Stock stock : allStocks) {
                if (stock.getIndustry().toLowerCase().equals(industry)) {
                    stocksInIndustry.add(stock.getName());
                }
            }
        }
        return stocksInIndustry;
    }

    @Override
    public YearMonth getCurrAlgoDate() {
        return this.currAlgoDate;
    }

    @Override
    public double calcMean(List<BigDecimal> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException();
        }
        double total = 0.0;
        for (BigDecimal num : data) {
            total += num.doubleValue();
        }
        return total / data.size();
    }

    @Override
    public double calcStdDev(List<BigDecimal> data, double mean) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException();
        }
        double sumOfSquaredDifferences = 0;
        for (BigDecimal num : data) {
            sumOfSquaredDifferences += Math.pow(num.doubleValue() - mean, 2);
        }
        return Math.sqrt(sumOfSquaredDifferences / data.size());
    }

    @Override
    public double[][] calcStatsForStockMetrics(YearMonth date) {
        if (possibleCompanies == null || isInvalidDate(date)) {
            return new double[5][2];
        }
        // will store the mean and standard deviation for each of the five metrics used to 
        // score stocks
        double[][] stats = new double[5][2];
        // lists of respective metric values for all stocks being considered
        ArrayList<BigDecimal> debtRatios = new ArrayList<>();
        ArrayList<BigDecimal> netIncomes = new ArrayList<>();
        ArrayList<BigDecimal> marketCaps = new ArrayList<>();
        ArrayList<BigDecimal> peRatios = new ArrayList<>();
        ArrayList<BigDecimal> salesGrowths = new ArrayList<>();
        // add metric values to respective lists
        for (String companyName : possibleCompanies) {
            Stock stock = stockMarket.getStock(companyName, date);
            debtRatios.add(stock.getDebtRatio());
            netIncomes.add(stock.getNetIncome());
            marketCaps.add(stock.getMarketCap());
            peRatios.add(stock.getPERatio());
            salesGrowths.add(stock.getSalesGrowth());
        }
        // calculate the mean for each metric and add it to stats array
        stats[0][0] = calcMean(debtRatios);
        stats[1][0] = calcMean(netIncomes);
        stats[2][0] = calcMean(marketCaps);
        stats[3][0] = calcMean(peRatios);
        stats[4][0] = calcMean(salesGrowths);
        // calculate the standard deviation for each metric and add it to stats array
        stats[0][1] = calcStdDev(debtRatios, stats[0][0]);
        stats[1][1] = calcStdDev(netIncomes, stats[1][0]);
        stats[2][1] = calcStdDev(marketCaps, stats[2][0]);
        stats[3][1] = calcStdDev(peRatios, stats[3][0]);
        stats[4][1] = calcStdDev(salesGrowths, stats[4][0]);

        return stats;
    }

    @Override
    public double calcZScore(BigDecimal value, double mean, double stdDev) {
        if (stdDev == 0.0) {
            return 0.0;
        } else {
            return (value.doubleValue() - mean) / stdDev;
        }
    }

    @Override
    public double calcStockScore(String companyName, YearMonth date, double[][] meansAndStdDevs) {
        // array containing z-score for each metric:
        // 0: net debt ratio z-score, 1: net income z-score, 2: market cap z-score, 
        // 3: pe ratio z-score, 4: sales growth z-score
        Stock companyStock = stockMarket.getStock(companyName, date);
        double[] zScores = new double[5];
        // array containing metric values for inputted company
        BigDecimal[] companyMetrics = new BigDecimal[]{companyStock.getDebtRatio(),
                companyStock.getNetIncome(), companyStock.getMarketCap(), companyStock.getPERatio(),
                companyStock.getSalesGrowth()};
        for (int i = 0; i < 5; i++) {
            double mean = meansAndStdDevs[i][0];
            double stdDev = meansAndStdDevs[i][1];
            BigDecimal metric = companyMetrics[i];
            // calculate z-score
            zScores[i] = calcZScore(metric, mean, stdDev);
            // max z-score of 5.0, min z-score of -5.0
            if (zScores[i] < -5.0) {
                zScores[i] = -5.0;
            } else if (zScores[i] > 5.0) {
                zScores[i] = 5.0;
            }
        }
        return (-1 * zScores[0] * preferences[0]) + (zScores[1] * preferences[1]) +
                (zScores[2] * preferences[2]) + (zScores[3] * preferences[3]) +
                (zScores[4] * preferences[4]);
    }

    @Override
    public double calcWeightedAvgStockScore(String companyName, YearMonth start,
                                            List<double[][]> monthlyMeansAndStdDevs) {
        if (monthlyMeansAndStdDevs.size() == 1) {
            return calcStockScore(companyName, start, monthlyMeansAndStdDevs.get(0));
        }

        double sumOfWeightedScores = 0.0;
        double sumOfWeights = 0.0;
        for (int i = 0; i < monthlyMeansAndStdDevs.size(); i++) {
            YearMonth currDate = start.plusMonths(i);
            double score = calcStockScore(companyName, currDate, monthlyMeansAndStdDevs.get(i));
            double weight = Math.pow(i + 1, 1.25);
            sumOfWeightedScores += score * weight;
            sumOfWeights += weight;
        }
        return sumOfWeightedScores / sumOfWeights;
    }


    @Override
    public List<Map.Entry<Stock, Double>> scoreStocks(YearMonth date) {
        if (possibleCompanies == null || isInvalidDate(date)) {
            return new ArrayList<>();
        }

        List<double[][]> meansAndStdDevs = new ArrayList<>();
        YearMonth current = currAlgoDate;
        while (!current.isAfter(date)) {
            meansAndStdDevs.add(calcStatsForStockMetrics(current));
            current = current.plusMonths(1);
        }

        List<Map.Entry<Stock, Double>> rankings = new ArrayList<>();
        for (String companyName : possibleCompanies) {
            double score = calcWeightedAvgStockScore(companyName, currAlgoDate, meansAndStdDevs);
            Stock stock = stockMarket.getStock(companyName, date);
            rankings.add(new AbstractMap.SimpleEntry<>(stock, score));
        }
        rankings.sort(Map.Entry.<Stock, Double>comparingByValue().reversed());
        // normalize scores to be in a [0, 1] range and return rankings
        return normalizeScores(rankings);
    }

    private List<Map.Entry<Stock, Double>> normalizeScores(List<Map.Entry<Stock, Double>> rankings) {
        if (rankings.isEmpty()) {
            return rankings;
        }
        double maxScore = rankings.get(0).getValue();
        double minScore = rankings.get(rankings.size() - 1).getValue();
        double range = maxScore - minScore;
        if (minScore < 0) {
            for (Map.Entry<Stock, Double> rank : rankings) {
                double score = rank.getValue();
                rank.setValue((score - minScore) / range);
            }
        }
        return rankings;
    }

    @Override
    public Map<String, BigDecimal> runAlgorithm(BigDecimal dollarsToInvest, YearMonth date) {
        if (isInvalidDate(date) || dollarsToInvest == null) {
            return new HashMap<>();
        }
        List<Map.Entry<Stock, Double>> rankings = scoreStocks(date);
        // update the current algo date
        this.currAlgoDate = date;
        // sum up scores of all companies in the threshold
        int endIndex = percentileThreshold(rankings, 70, 10, 75);
        double sumScores = 0.0;
        for (int i = 0; i <= endIndex; i++) {
            Map.Entry<Stock, Double> entry = rankings.get(i);
            double score = entry.getValue();
            sumScores += score;
        }
        // for each selected stock, invest an amount that is proportional to the 
        // stock's calculated score divided by the sum of all the scores
        Map<String, BigDecimal> investmentsToMake = new HashMap<>();
        BigDecimal investedSoFar = BigDecimal.ZERO;
        for (int i = 0; i <= endIndex; i++) {
            Map.Entry<Stock, Double> entry = rankings.get(i);
            Stock stock = entry.getKey();
            BigDecimal proportion = new BigDecimal(entry.getValue() / sumScores);
            BigDecimal quantityToPurchase = dollarsToInvest.multiply(proportion).divide(stock.getPrice(), 2, RoundingMode.DOWN);
            BigDecimal toPurchaseInDollars = quantityToPurchase.multiply(stock.getPrice());

            investmentsToMake.put(stock.getName(), quantityToPurchase);
            investedSoFar = investedSoFar.add(toPurchaseInDollars);
        }

        // if there is any leftover cash allocate it to the top stock and uninvested cash
        BigDecimal leftover = dollarsToInvest.subtract(investedSoFar);
        Stock topStock = rankings.get(0).getKey();
        BigDecimal quantity = leftover.divide(topStock.getPrice(), 2, RoundingMode.DOWN);
        investmentsToMake.compute(topStock.getName(), (k, v) -> v.add(quantity));
        leftover = leftover.subtract(quantity.multiply(topStock.getPrice()));
        if (leftover.compareTo(BigDecimal.ZERO) > 0) {
            investmentsToMake.put("Cash", leftover);
        }
        return investmentsToMake;
    }

    private int percentileThreshold(List<Map.Entry<Stock, Double>> stockRankings,
                                    double percentile, int minNumStocks, int maxNumStocks) {
        int targetIndex = ((int) Math.ceil(((100 - percentile) / 100) * stockRankings.size())) - 1;
        targetIndex = Math.max(0, targetIndex);
        int minIndex = Math.min(stockRankings.size(), minNumStocks) - 1;
        int maxIndex = Math.min(stockRankings.size(), maxNumStocks) - 1;
        // return the index corresponding to the minimum number of stocks if target index is 
        // less than min index 
        if (targetIndex < minIndex) {
            return minIndex;
        } else if (targetIndex > maxIndex) {
            // return the index corresponding to the maximum number of stocks if target index is 
            // greater than max index 
            return maxIndex;
        }
        // otherwise, return the index associated with the percentile
        return targetIndex;
    }

    private boolean isInvalidDate(YearMonth date) {
        return date == null || date.isBefore(currAlgoDate) ||
                date.isBefore(stockMarket.getFirstDateEntry()) ||
                date.isAfter(stockMarket.getLastDateEntry());
    }


}
