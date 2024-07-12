package org.example.etfbuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface IStockMarket {

    YearMonth FIRST_DATE_ENTRY = YearMonth.parse("2010-01");
    YearMonth LAST_DATE_ENTRY = YearMonth.parse("2024-03");

    /**
     * Retrieves the stock object of a given company on a given date.
     * The stock object will contain values for all the metrics associated
     * with a company on a date.
     *
     * @param companyName: the target company
     * @param date:        the querying date
     */
    Stock getStock(String companyName, YearMonth date);


    /**
     * Returns the set of stock objects that exist on a given date
     */
    Set<Stock> getStocksOnDate(YearMonth date);


//    /**
//     * todo //////////////////////////////////////////////////////////////////////////////
//     *
//     * @param date
//     * @return
//     */
//    double getSP500Value(YearMonth date);
}
