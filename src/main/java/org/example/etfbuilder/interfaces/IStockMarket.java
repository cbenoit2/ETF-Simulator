package org.example.etfbuilder.interfaces;

import org.example.etfbuilder.Stock;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.Set;

public interface IStockMarket {


    YearMonth getFirstDateEntry();

    YearMonth getLastDateEntry();


    Stock getStock(String companyName, YearMonth date);

    Set<Stock> getStocksOnDate(YearMonth date);

    BigDecimal getSP500ValueOnDate(YearMonth date);

    boolean addSP500Value(BigDecimal value, YearMonth date);

    Map<YearMonth, BigDecimal> getSP500Data();

    Set<String> getAllCompanyNames(YearMonth date);

}
