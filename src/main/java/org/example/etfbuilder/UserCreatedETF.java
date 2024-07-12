package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.*;

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
        this.amountInvested = 0.0;
        this.stocksInETF = new HashMap<>();
        this.etfPositions = new HashMap<>();
        this.startDate = startDate;
        this.stockMarket = market;
    }

}
