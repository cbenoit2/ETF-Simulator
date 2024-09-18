package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IStockMarket;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public class StockMarket implements IStockMarket {

    private final YearMonth firstDateEntry;
    private final YearMonth lastDateEntry;
    private final Map<YearMonth, Map<String, Stock>> stockData;
    private final Map<YearMonth, BigDecimal> sp500Data;

    public StockMarket(Map<YearMonth, Map<String, Stock>> stockData,
                       Map<YearMonth, BigDecimal> sp500Data) {
        if (stockData == null || sp500Data == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        this.stockData = stockData;
        this.sp500Data = sp500Data;
        this.firstDateEntry = Collections.min(stockData.keySet());
        this.lastDateEntry = Collections.max(stockData.keySet());
    }

    @Override
    public YearMonth getFirstDateEntry() {
        return this.firstDateEntry;
    }

    @Override
    public YearMonth getLastDateEntry() {
        return this.lastDateEntry;
    }

    @Override
    public Stock getStock(String companyName, YearMonth date) {
        if (companyName == null || date == null) {
            return null;
        }
        companyName = companyName.toLowerCase();
        if (!stockData.containsKey(date) || !stockData.get(date).containsKey(companyName)) {
            return null;
        }

        return stockData.get(date).get(companyName);
    }

    @Override
    public Set<Stock> getStocksOnDate(YearMonth date) {
        Set<Stock> stockSet = new HashSet<>();
        Map<String, Stock> stockMap = this.stockData.get(date);
        if (stockMap != null) {
            stockSet.addAll(stockMap.values());
        }
        return stockSet;
    }

    @Override
    public BigDecimal getSP500ValueOnDate(YearMonth date) {
        return sp500Data.get(date);
    }

    @Override
    public boolean addSP500Value(BigDecimal value, YearMonth date) {
        if (value == null || date == null || sp500Data.containsKey(date) ||
                value.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        sp500Data.put(date, value);
        return true;
    }

    @Override
    public Map<YearMonth, BigDecimal> getSP500Data() {
        return new HashMap<>(sp500Data);
    }

    @Override
    public Set<String> getAllCompanyNames(YearMonth date) {
        Set<String> names = new HashSet<>();
        for (Stock stock : getStocksOnDate(date)) {
            names.add(stock.getName());
        }
        return names;
    }

}