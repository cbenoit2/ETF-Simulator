package org.example.etfbuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IETFAlgorithm {

    Set<String> selectIndustry(String industry, YearMonth date);

    void setCurrAlgoDate(YearMonth date);

    YearMonth getCurrAlgoDate();

    /**
     * Calculates standard deviation for a set of metrics
     *
     * @param data The raw metric data.
     * @param mean The mean of the associated metric data.
     * @return The standard deviation of the data set.
     */
    double calcStdDev(List<BigDecimal> data, double mean);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param data
     * @return
     */
    double calcMean(List<BigDecimal> data);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param date
     * @return
     */
    double[][] calcStatsForStockMetrics(YearMonth date);

    double calcZScore(BigDecimal value, double mean, double stdDev);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     */
    double calcStockScore(String companyName, YearMonth date, double[][] meansAndStdDevs);

    double calcWeightedAvgStockScore(String companyName, YearMonth start,
                                     List<double[][]> monthlyMeansAndStdDevs);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param date
     * @return
     */
    List<Map.Entry<Stock, Double>> scoreStocks(YearMonth date);


    // determine stock selection
    Map<String, BigDecimal> runAlgorithm(BigDecimal dollarsToInvest, YearMonth date);
}
