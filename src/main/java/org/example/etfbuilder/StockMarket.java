package org.example.etfbuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class StockMarket implements IStockMarket {

    private Map<YearMonth, Map<String, Stock>> stockData;
    // private Map<YearMonth, Double> sp500Data;

    public StockMarket(Map<YearMonth, Map<String, Stock>> stockData) {
        this.stockData = stockData;
        //this.sp500Data = sp500Data;
    }

    @Override
    public Stock getStock(String companyName, YearMonth date) {
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

//    @Override
//    public double getSP500Value(YearMonth date) {
//        return sp500Data.get(date);
//    }

}