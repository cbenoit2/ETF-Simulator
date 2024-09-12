package org.example.etfbuilder.interfaces;

import org.example.etfbuilder.Stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public interface IDataParser {

    /**
     * Parses a formatted csv file to extract financial information about S&P500 stocks.
     * Constructs a map of dates. Each date has a map of all companies' names and the stock
     * object for the company. Each stock's share price, net debt ratio %, PE ratio,
     * net income, market capitalization, industry, and year over year sales growth rate are
     * updated to reflect its value for the respective date.
     *
     * @param csvFile The formatted csv file name to parse
     * @return A map of dates, each of which is mapped to a map of company names and the Stock
     * object for the company on the respective date.
     */
    Map<YearMonth, Map<String, Stock>> parseStockData(String csvFile) throws IOException;

    /**
     * Helper method for parseStockData()
     * Creates a new Stock object for company {@param name} and sets
     * the stock's metrics using the information in {@param data}
     *
     * @param name The company's name
     * @param data An array consisting of the financial metrics for the stock
     *             in string form. Metrics in the array are in the order of:
     *             industry, share price, net income, market cap, PE ratio,
     *             YOY sales growth %, and net debt ratio %
     * @return A Stock containing the information in {@param data}
     */
    Stock createStockFromData(String name, String[] data);


    /**
     * Parses a formatted csv file to retrieve information about the SPX Index's
     * value over time. Creates a map consisting of the dates in the file and the
     * index's value on the respective date.
     *
     * @param csvFile the formatted csv file name to parse
     * @return A map of dates and the SPX Index's value on the respective date
     */
    Map<YearMonth, BigDecimal> parseSP500MarketData(String csvFile) throws IOException;

}
