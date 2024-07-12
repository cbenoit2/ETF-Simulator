package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.*;

public class ETFAlgorithm implements IETFAlgorithm {

    private IStockMarket stockMarket;
    private double[] preferences;
    private Set<Stock> possibleStocks;
    private YearMonth currDate;


    public ETFAlgorithm(IStockMarket stockMarket, YearMonth startDate, double[] preferences,
                        String industry) {
        if (stockMarket == null || startDate == null || industry == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (startDate.isBefore(IStockMarket.FIRST_DATE_ENTRY) || startDate.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            throw new IllegalArgumentException("invalid date");
        }
        if (preferences.length != 5) {
            throw new IllegalArgumentException("preferences array is incorrect size");
        }
        if (Arrays.stream(preferences).sum() != 1.0) {
            throw new IllegalArgumentException("weights in preferences array do not add up to 1");
        }

        this.stockMarket = stockMarket;
        this.currDate = startDate;
        this.preferences = preferences;
        this.possibleStocks = selectIndustry(industry);
    }

    @Override
    public void setCurrDate(YearMonth date) {
        this.currDate = date;
    }

    @Override
    public YearMonth getCurrDate() {
        return this.currDate;
    }

    @Override
    public IStockMarket getStockMarket() {
        return this.stockMarket;
    }

    @Override
    public Set<Stock> selectIndustry(String industry) {
        if (industry == null) {
            return new HashSet<>();
        }

        industry = industry.toLowerCase();
        Set<Stock> allStocks = stockMarket.getStocksOnDate(currDate);
        // if user selects All, include all stocks in set
        if (industry.equals("all")) {
            return allStocks;
        }
        // otherwise, add all companies in selected industry to new set 
        Set<Stock> stocksInIndustry = new HashSet<>();
        for (Stock stock : allStocks) {
            if (stock.getIndustry().toLowerCase().equals(industry)) {
                stocksInIndustry.add(stock);
            }
        }

        return stocksInIndustry;
    }

    @Override
    public double calcStdDev(List<Double> data, double mean) {
        if (data == null || data.isEmpty()) {
            return 0;
        }

        double sumOfSquaredDifferences = 0;
        for (double num : data) {
            sumOfSquaredDifferences += Math.pow(num - mean, 2);
        }

        return Math.sqrt(sumOfSquaredDifferences / data.size());
    }

    @Override
    public double calcMean(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (double num : data) {
            total += num;
        }
        return total / data.size();
    }

    @Override
    public double[][] calcStatsForMetrics(YearMonth date) {
        if (possibleStocks == null || date == null || date.isBefore(currDate) ||
                date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return new double[5][2];
        }

        // stores the mean and standard deviation for each of the
        // five metrics used to score stocks
        double[][] stats = new double[5][2];

        // list of respective metric values for all stocks being considered
        ArrayList<Double> debtRatios = new ArrayList<>();
        ArrayList<Double> netIncomes = new ArrayList<>();
        ArrayList<Double> marketCaps = new ArrayList<>();
        ArrayList<Double> peRatios = new ArrayList<>();
        ArrayList<Double> salesGrowths = new ArrayList<>();

        for (Stock stock : possibleStocks) {
            debtRatios.add(stock.getDebtRatio());
            netIncomes.add(stock.getNetIncome());
            marketCaps.add(stock.getMarketCap());
            peRatios.add(stock.getPERatio());
            salesGrowths.add(stock.getSalesGrowth());
        }

        stats[0][0] = calcMean(debtRatios);
        stats[1][0] = calcMean(netIncomes);
        stats[2][0] = calcMean(marketCaps);
        stats[3][0] = calcMean(peRatios);
        stats[4][0] = calcMean(salesGrowths);

        stats[0][1] = calcStdDev(debtRatios, stats[0][0]);
        stats[1][1] = calcStdDev(netIncomes, stats[1][0]);
        stats[2][1] = calcStdDev(marketCaps, stats[2][0]);
        stats[3][1] = calcStdDev(peRatios, stats[3][0]);
        stats[4][1] = calcStdDev(salesGrowths, stats[4][0]);

        return stats;
    }

    @Override
    public double calcCompanyScore(Stock company, double[][] meansAndStdDevs) {
        // todo: account for improper parameters
        // todo /////////////////////////////////////////////////////////////////////

        // array containing z-score for each metric:
        // 0 - net debt ratio z-score
        // 1 - net income z-score
        // 2 - market cap z-score
        // 3 - pe ratio z-score
        // 4 - sales growth z-score
        double[] zScores = new double[5];
        // array containing metric values for inputted company
        double[] companyMetrics = new double[]{company.getDebtRatio(), company.getNetIncome(),
                company.getMarketCap(), company.getPERatio(), company.getSalesGrowth()};

        for (int i = 0; i < 5; i++) {
            double mean = meansAndStdDevs[i][0];
            double stdDev = meansAndStdDevs[i][1];
            double metric = companyMetrics[i];

            // calculate z-score
            if (stdDev == 0.0) {
                zScores[i] = 0.0;
            } else {
                zScores[i] = (metric - mean) / stdDev;
            }

            // max z-score of 3.0, min z-score of -3.0
            if (zScores[i] < -3.0) {
                zScores[i] = -3.0;
            } else if (zScores[i] > 3.0) {
                zScores[i] = 3.0;
            }
        }

        return (-1 * zScores[0] * preferences[0]) + (zScores[1] * preferences[1]) +
                (zScores[2] * preferences[2]) + (zScores[3] * preferences[3]) +
                (zScores[4] * preferences[4]);
    }

    public double calcWeightedAvgCompanyScore(Stock company, List<double[][]> monthlyMeansAndStdDevs) {
        // todo: account for null and empty parameters
        double sumOfWeightedScores = 0.0;
        double sumOfWeights = 0.0;
        for (int i = 0; i < monthlyMeansAndStdDevs.size(); i++) {
            double score = calcCompanyScore(company, monthlyMeansAndStdDevs.get(i));
            double weight = Math.pow(i + 1, 1.25);
//            double weight = Math.pow(1.25, i);
            sumOfWeightedScores += score * weight;
            sumOfWeights += weight;
        }

        return sumOfWeightedScores / sumOfWeights;
    }


    @Override
    public List<Map.Entry<String, Double>> scoreStocks(YearMonth date) {
        // todo: handle case where date entered is before currDate
        if (date == null || possibleStocks == null || date.isBefore(currDate) ||
                date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return new ArrayList<>();
        }

        List<double[][]> meansAndStdDevs = new ArrayList<>();
        YearMonth current = currDate;
        while (!current.isAfter(date)) {
            meansAndStdDevs.add(calcStatsForMetrics(current));
            current = current.plusMonths(1);
        }

        List<Map.Entry<String, Double>> rankings = new ArrayList<>();
        for (Stock company : possibleStocks) {
            double score = calcWeightedAvgCompanyScore(company, meansAndStdDevs);
            rankings.add(new AbstractMap.SimpleEntry<>(company.getName(), score));
        }
        rankings.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        // normalize scores to be in a [0, 1] range and return rankings
        return normalizeScores(rankings);
    }

    ///////////////////////////////////////////////////////////////////////
    private List<Map.Entry<String, Double>> shiftScores(List<Map.Entry<String, Double>> rankings) {
        double minScore = rankings.get(rankings.size() - 1).getValue();
        if (minScore < 0) {
            for (Map.Entry<String, Double> rank : rankings) {
                double score = rank.getValue();
                rank.setValue(score + (-1 * minScore) + 1);
            }
        }
        return rankings;
    }

    private List<Map.Entry<String, Double>> normalizeScores(List<Map.Entry<String, Double>> rankings) {
        if (rankings.isEmpty()) {
            return rankings;
        }

        double maxScore = rankings.get(0).getValue();
        double minScore = rankings.get(rankings.size() - 1).getValue();
        double range = maxScore - minScore;

        if (minScore < 0) {
            for (Map.Entry<String, Double> rank : rankings) {
                double score = rank.getValue();
                rank.setValue((score - minScore) / range);
            }
        }
        return rankings;
    }


    @Override
    // determines stock selection
    public Map<String, Double> runAlgorithm(double dollarsToInvest, YearMonth date) {
        if (date.isBefore(currDate) || date.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            return new HashMap<>();
        }

        Map<String, Double> stockSelections = new HashMap<>();

        List<Map.Entry<String, Double>> rankings = scoreStocks(date);
        setCurrDate(date); // update the current date

        double threshold = percentileThreshold(rankings, .35, 10);
        // add all companies that meet the score threshold to the map of selected stocks
        for (Map.Entry<String, Double> entry : rankings) {
            String companyName = entry.getKey();
            double score = entry.getValue();
            if (score >= threshold) {
                stockSelections.put(companyName, score);
            } else {
                break;
            }
        }
        // sum up the scores of all the selected stocks
        double sumScores = 0.0;
        for (Map.Entry<String, Double> company : stockSelections.entrySet()) {
            sumScores += company.getValue();
        }
        // for each selected stock, invest an amount that is proportional to the 
        // stock's calculated score divided by the sum of all the scores
        // todo fix comment
        double investedSoFar = 0.0;
        for (Map.Entry<String, Double> company : stockSelections.entrySet()) {
            double score = company.getValue();
            double proportion = score / sumScores;
            double toInvest = Math.min(proportion * dollarsToInvest, dollarsToInvest - investedSoFar);
            company.setValue(toInvest);

            investedSoFar += toInvest;
        }

        return stockSelections;
    }


    private double percentileThreshold(List<Map.Entry<String, Double>> stockRankings, double percentile, int minNumOfStocks) {
        // todo error handling
        if (stockRankings.isEmpty() || percentile < 0 || minNumOfStocks < 0) {
            return -1;
        }

        int targetIndex = (int) Math.ceil(percentile * stockRankings.size()) - 1;
        targetIndex = Math.max(0, targetIndex);
        int minIndex = Math.min(stockRankings.size() - 1, minNumOfStocks - 1);

        if (targetIndex < minIndex) {
            return stockRankings.get(minIndex).getValue();
        }
        return stockRankings.get(targetIndex).getValue();
    }


}
