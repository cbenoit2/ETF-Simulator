package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IStockMarket;

import java.time.YearMonth;
import java.util.HashMap;

public class UserCreatedETF extends ETF {

    public UserCreatedETF(IStockMarket market, YearMonth startDate) {
        if (market == null || startDate == null) {
            throw new IllegalArgumentException("input parameter is null");
        }
        if (startDate.isBefore(IStockMarket.FIRST_DATE_ENTRY) ||
                startDate.isAfter(IStockMarket.LAST_DATE_ENTRY)) {
            throw new IllegalArgumentException("invalid date");
        }

        this.systemGenerated = false;
        this.stocksInETF = new HashMap<>();
        this.etfPositions = new HashMap<>();
        this.startDate = startDate;
        this.stockMarket = market;
    }

}
