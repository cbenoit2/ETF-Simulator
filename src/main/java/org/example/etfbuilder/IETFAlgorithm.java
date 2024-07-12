package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.*;

public interface IETFAlgorithm {


    void setCurrDate(YearMonth date);

    YearMonth getCurrDate();

    IStockMarket getStockMarket();

    /**
     * Narrows the stock options available for inclusion by their industry.
     *
     * @param industry The industry focus selected by user.
     * @return A Set of stocks included in this industry group.
     */
    Set<Stock> selectIndustry(String industry);


    /**
     * Calculates standard deviation for a set of metrics
     *
     * @param data The raw metric data.
     * @param mean The mean of the associated metric data.
     * @return The standard deviation of the data set.
     */
    double calcStdDev(List<Double> data, double mean);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param data
     * @return
     */
    double calcMean(List<Double> data);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param date
     * @return
     */
    public double[][] calcStatsForMetrics(YearMonth date);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param company
     * @param meansAndStdDevs
     * @return
     */
    double calcCompanyScore(Stock company, double[][] meansAndStdDevs);

    /**
     * todo /////////////////////////////////////////////////////////////////////////////////////
     *
     * @param date
     * @return
     */
    List<Map.Entry<String, Double>> scoreStocks(YearMonth date);


    // determine stock selection
    Map<String, Double> runAlgorithm(double dollarsToInvest, YearMonth date);
}
