package org.example.etfbuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public interface IETF {

    void setETFName(String etfName);

    boolean isSystemGenerated();

    BigDecimal getAmountInvested();

    boolean addStock(String companyName, BigDecimal dollars, YearMonth date);

    boolean removeStock(String companyName, BigDecimal dollars);

    BigDecimal getETFValue(YearMonth date);

    YearMonth getStartDate();

    Map<String, BigDecimal> getETFPositions();

}

