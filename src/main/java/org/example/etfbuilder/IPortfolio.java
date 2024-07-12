package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.Map;
import java.util.Set;

public interface IPortfolio {
    boolean addETF(IETF etf);

    boolean removeETF(IETF etf);

    Set<IETF> getETFsInPortfolio();

    Map<IETF, Map<YearMonth, double[]>> runSimulation(YearMonth endDate);

    Map<YearMonth, double[]> getETFReturns(IETF etf, YearMonth fromDate, YearMonth toDate);

    // todo: run simulation (simulate system generated + user created), get returns, get total returns, 
//    // calc beta
//
//    /**
//     * Matrix of how to call each metric from StockMarket
//     * 0: Debt
//     * 1: NetIncome
//     * 2: MarketCap
//     * 3: PE Ratio
//     * 4: Price
//     * 5: Sales
//     */
//
//    Integer DEBT = 0;
//    Integer NETINCOME = 1;
//    Integer MARKETCAP = 2;
//    Integer PE = 3;
//    Integer PRICE = 4;
//    Integer SALES = 5;
//
//    /**
//     * How to call which Beta calculation method
//     */
//    public static final int PORTFOLIO = 0;
//    public static final int SP500 = 1;
//    public static final int STOCK_ONE = 2;
//    public static final int STOCK_TWO = 3;
//
//    /**
//     * Create adjusted relationship score to support Graphing Algorithm
//     */
//    public static final double BETA_ADJ = 2;
//    public static final double SPREAD_FACTOR = 10;
//
//
//    /**
//     * Buys stock based on the specified parameters and returns a new position.
//     *
//     * @param stock   The stock symbol to buy.
//     * @param dollars The amount of dollars to invest.
//     * @param date    The date of the transaction.
//     * @return The new position created by the purchase.
//     */
//    Position buyStock(String stock, double dollars, LocalDate date);
//
//    /**
//     * Sells the specified position on the given date and returns the proceeds.
//     *
//     * @param pos  The position to sell.
//     * @param date The date of the sale.
//     * @return The proceeds from selling the stock.
//     */
//    double sellStock(Position pos, LocalDate date);
//
//    /**
//     * Runs the portfolio between the specified start and end dates with a specified test rate.
//     *
//     * @param start    The start date of the portfolio run.
//     * @param end      The end date of the portfolio run.
//     * @param testRate The frequency in months at which the ETF is tested and rebalanced.
//     */
//    void runPortfolio(LocalDate start, LocalDate end, int testRate);
//
//    /**
//     * Calculates the portfolio returns between two dates.
//     *
//     * @param startDate The start date for the calculation.
//     * @param endDate   The end date for the calculation.
//     * @return An array of returns for each period.
//     */
//    double[] portfolioReturns(LocalDate startDate, LocalDate endDate);
//
//    /**
//     * Calculates the returns of an individual stock between two dates.
//     *
//     * @param stock     The stock symbol.
//     * @param startDate The start date for the calculation.
//     * @param endDate   The end date for the calculation.
//     * @return An array of returns for each period.
//     */
//    double[] individualStockReturns(String stock, LocalDate startDate, LocalDate endDate);
//
//    /**
//     * Calculates the S&P 500 returns between two dates.
//     *
//     * @param startDate The start date for the calculation.
//     * @param endDate   The end date for the calculation.
//     * @return An array of returns for each period.
//     */
//    double[] sp500Returns(LocalDate startDate, LocalDate endDate);
//
//    /**
//     * Calculates the Multiple of Invested Capital (MOIC) between two dates.
//     *
//     * @param start The start date for the calculation.
//     * @param end   The end date for the calculation.
//     * @return The MOIC.
//     */
//    double moicCalc(LocalDate start, LocalDate end);
//
//    /**
//     * Calculates the Internal Rate of Return (IRR) between two dates.
//     *
//     * @param start The start date for the calculation.
//     * @param end   The end date for the calculation.
//     * @return The IRR.
//     */
//    double irrCalc(LocalDate start, LocalDate end);
//
//    /**
//     * Calculates the beta value of stocks or the portfolio against a benchmark over a
//     * specified period.
//     *
//     * @param beta       The type of returns to be calculated.
//     * @param comparedTo The benchmark against which the returns are compared.
//     * @param oneStock   The first stock symbol if required.
//     * @param twoStock   The second stock symbol if required.
//     * @param start      The start date for the calculation.
//     * @param end        The end date for the calculation.
//     * @return The calculated beta value.
//     */
//    double betaCalc(int beta, int comparedTo, String oneStock, String twoStock, LocalDate start,
//                    LocalDate end);
//
//    /**
//     * Calculates and returns a beta matrix for a set of stocks over a specified period.
//     *
//     * @param startDate The start date for the calculation.
//     * @param endDate   The end date for the calculation.
//     * @return The beta matrix.
//     */
//    double[][] betaMatrix(LocalDate startDate, LocalDate endDate);
//
//    /**
//     * Computes the total weight of all edges in the provided matrix.
//     *
//     * @param betaMatrix The matrix representing the weights between nodes in a network.
//     * @return The sum of all weights in the upper triangle of the matrix.
//     */
//    double computeTotalEdgeWeight(double[][] betaMatrix);

}

