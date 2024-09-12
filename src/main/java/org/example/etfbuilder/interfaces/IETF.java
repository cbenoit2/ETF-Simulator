package org.example.etfbuilder.interfaces;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public interface IETF {

    void setETFName(String etfName);

    boolean isSystemGenerated();

    BigDecimal getAmountInvested();

    boolean buyStock(String companyName, BigDecimal quantityToBuy, YearMonth buyDate);

    boolean sellStock(String companyName, BigDecimal quantityToSell, YearMonth sellDate);

    BigDecimal getETFValue(YearMonth date);

    YearMonth getStartDate();

    Map<String, BigDecimal> getETFPositions();

}

