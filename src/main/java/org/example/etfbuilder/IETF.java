package org.example.etfbuilder;

import java.time.YearMonth;
import java.util.Map;

public interface IETF {

//    String getEtfName();
//
//    void setEtfName(String etfName);

    boolean isSystemGenerated();

    double getAmountInvested();

    boolean addStock(String companyName, double dollars, YearMonth date);

    boolean removeStock(String companyName, double dollars);

    double getETFValue(YearMonth date);

    YearMonth getStartDate();

    Map<String, Double> getETFPositions();


//    //todo separate simulateETF into a simulation class or into portfolio class
//
//    /**
//     * Readjusts the decision of which stocks are included
//     *
//     * @param startDate The start of the simulation.
//     * @param endDate   The end of the simulation.
//     * @param testRate  The time period in months between re-testing portfolio.
//     * @return An ArrayList of all the transaction events during the simulation.
//     */
//    TreeSet<Transaction> simulateETF(LocalDate startDate, LocalDate endDate, int testRate);


}

