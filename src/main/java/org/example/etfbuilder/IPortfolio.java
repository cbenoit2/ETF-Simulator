package org.example.etfbuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface IPortfolio {

    boolean addETF(IETF etf);

    boolean removeETF(IETF etf);

    List<IETF> getETFsInPortfolio();

    Map<YearMonth, BigDecimal[]> runSimulationOnETF(IETF etf, YearMonth endDate);

    Map<IETF, Map<YearMonth, BigDecimal[]>> runSimulationOnPortfolio(YearMonth endDate);

    Map<YearMonth, BigDecimal[]> getETFReturns(IETF etf, YearMonth fromDate, YearMonth toDate);

    Map<YearMonth, BigDecimal> getMarketReturns(Map<YearMonth, BigDecimal> sp500Value,
                                                YearMonth fromDate, YearMonth toDate);

}

