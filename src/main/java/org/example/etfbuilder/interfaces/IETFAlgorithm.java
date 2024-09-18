package org.example.etfbuilder.interfaces;

import org.example.etfbuilder.Stock;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IETFAlgorithm {

    Set<String> selectIndustry(String industry, YearMonth date);

    YearMonth getCurrAlgoDate();

    double calcStdDev(List<BigDecimal> data, double mean);


    double calcMean(List<BigDecimal> data);

    double[][] calcStatsForStockMetrics(YearMonth date);

    double calcZScore(BigDecimal value, double mean, double stdDev);

    double calcStockScore(String companyName, YearMonth date, double[][] meansAndStdDevs);

    double calcWeightedAvgStockScore(String companyName, YearMonth start,
                                     List<double[][]> monthlyMeansAndStdDevs);

    List<Map.Entry<Stock, Double>> scoreStocks(YearMonth date);

    Map<String, BigDecimal> runAlgorithm(BigDecimal dollarsToInvest, YearMonth date);
}
