package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.example.etfbuilder.interfaces.IStockMarket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SystemGeneratedETFTest {

    private static IStockMarket market;
    private static YearMonth startDate;
    private IETFAlgorithm algo;

    @BeforeAll
    public static void parseData() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = SystemGeneratedETFTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = SystemGeneratedETFTest.class.getResource("data/S&P500_value.csv").getFile();
        market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));
        startDate = YearMonth.parse("2023-06");
    }

    @BeforeEach
    public void initAlgo() {
        double[] pref = new double[]{.1, .25, .15, .2, .3};
        algo = new ETFAlgorithm(market, startDate, pref, "all");
    }

    @Test
    public void testSystemGeneratedETFConstructor() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 2);

        assertTrue(etf.isSystemGenerated());
        assertEquals(startDate, etf.getStartDate());
        assertEquals(2, etf.getReinvestmentRate());
    }

    @Test
    public void testSystemGeneratedETFCorrectInitialStocksPurchased() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);
        Map<String, BigDecimal> expectedPositions = new HashMap<>();
        expectedPositions.put("Alphabet Inc (Class A)", new BigDecimal("890.5680"));
        expectedPositions.put("ExxonMobil", new BigDecimal("761.4750"));
        expectedPositions.put("S&P Global", new BigDecimal("533.1837"));
        expectedPositions.put("Booking Holdings", new BigDecimal("513.0627"));
        expectedPositions.put("Equinix", new BigDecimal("509.5610"));
        expectedPositions.put("Prologis", new BigDecimal("495.4252"));
        expectedPositions.put("Goldman Sachs", new BigDecimal("399.9496"));
        expectedPositions.put("Take Two Interactive", new BigDecimal("397.3320"));
        expectedPositions.put("Vulcan Materials Company", new BigDecimal("383.2480"));
        expectedPositions.put("Chipotle Mexican Grill", new BigDecimal("363.6300"));
        expectedPositions.put("Halliburton", new BigDecimal("352.0033"));
        expectedPositions.put("Cash", new BigDecimal("0.8115"));

        assertEquals(expectedPositions, etf.getETFPositions());
    }

    @Test
    public void testSystemGeneratedETFNullStockMarket() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SystemGeneratedETF(null, algo, new BigDecimal("5600.25"), 0);
        });
    }

    @Test
    public void testSystemGeneratedETFNullETFAlgorithm() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SystemGeneratedETF(market, null, new BigDecimal("5600.25"), 2);
        });
    }

    @Test
    public void testSystemGeneratedETFNullDollarsToInvest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SystemGeneratedETF(market, algo, null, 2);
        });
    }

    @Test
    public void testSystemGeneratedETFNegativeDollarsToInvest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SystemGeneratedETF(market, algo, new BigDecimal("-5600.25"), 2);
        });
    }

    @Test
    public void testSystemGeneratedETFNegativeReinvestmentRate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), -2);
        });
    }

    @Test
    public void testGetETFValue() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);

        assertEquals(new BigDecimal("5823.7594"), etf.getETFValue(startDate.plusMonths(2)));
    }

    @Test
    public void testGetETFValueNullDate() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            etf.getETFValue(null);
        });
    }

    @Test
    public void testGetETFValueInvalidDate() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            etf.getETFValue(startDate.minusYears(1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            etf.getETFValue(startDate.plusYears(1));
        });
    }

    @Test
    public void testUpdateETFPositionsCorrectlyUpdates() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);
        Map<String, BigDecimal> expectedPositions = new HashMap<>();
        expectedPositions.put("Alphabet Inc (Class A)", new BigDecimal("890.5680"));
        expectedPositions.put("ExxonMobil", new BigDecimal("603.8175"));
        expectedPositions.put("Prologis", new BigDecimal("609.6892"));
        expectedPositions.put("Goldman Sachs", new BigDecimal("537.5878"));
        expectedPositions.put("Equinix", new BigDecimal("525.1886"));
        expectedPositions.put("Booking Holdings", new BigDecimal("405.0495"));
        expectedPositions.put("First Solar", new BigDecimal("476.5824"));
        expectedPositions.put("S&P Global", new BigDecimal("473.0502"));
        expectedPositions.put("Take Two Interactive", new BigDecimal("381.1444"));
        expectedPositions.put("Discover Financial", new BigDecimal("365.6842"));
        expectedPositions.put("Costco", new BigDecimal("362.5248"));
        expectedPositions.put("Cash", new BigDecimal("1.0410"));

        etf.updateETF(startDate.plusMonths(2));
        assertEquals(expectedPositions, etf.getETFPositions());
    }

    @Test
    public void testUpdateAmountInvestedCorrectlyUpdates() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);
        etf.updateETF(startDate.plusMonths(2));

        assertEquals(new BigDecimal("5631.9276"), etf.getAmountInvested());
    }

    @Test
    public void testUpdateETFValueAfterUpdate() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);
        etf.updateETF(startDate.plusMonths(2));

        assertEquals(new BigDecimal("5823.7594"), etf.getETFValue(startDate.plusMonths(2)));
    }

    @Test
    public void testUpdateCorrectQuantitiesHeld() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);
        Map<String, BigDecimal> expectedQuantities = new HashMap<>();
        expectedQuantities.put("Alphabet Inc (Class A)", new BigDecimal("7.44"));
        expectedQuantities.put("ExxonMobil", new BigDecimal("5.63"));
        expectedQuantities.put("Prologis", new BigDecimal("4.96"));
        expectedQuantities.put("Goldman Sachs", new BigDecimal("1.66"));
        expectedQuantities.put("Equinix", new BigDecimal("0.67"));
        expectedQuantities.put("Booking Holdings", new BigDecimal("0.15"));
        expectedQuantities.put("First Solar", new BigDecimal("2.52"));
        expectedQuantities.put("S&P Global", new BigDecimal("1.18"));
        expectedQuantities.put("Take Two Interactive", new BigDecimal("2.59"));
        expectedQuantities.put("Discover Financial", new BigDecimal("4.06"));
        expectedQuantities.put("Costco", new BigDecimal("0.66"));

        etf.updateETF(startDate.plusMonths(2));
        for (Map.Entry<String, BigDecimal> entry : expectedQuantities.entrySet()) {
            assertEquals(entry.getValue(), etf.getTotalQuantityHeld(entry.getKey()));
        }
        // etf should only hold all of the above companies + cash balance
        assertEquals(12, etf.getETFPositions().size());
    }

    @Test
    public void testUpdateETFNullDate() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            etf.updateETF(null);
        });
    }

    @Test
    public void testUpdateETFInvalidDate() {
        SystemGeneratedETF etf =
                new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            etf.updateETF(startDate.minusYears(1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            etf.updateETF(startDate.plusYears(1));
        });
    }

}