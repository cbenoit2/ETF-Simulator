package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETF;
import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.example.etfbuilder.interfaces.IPortfolio;
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


public class PortfolioTest {

    private static YearMonth startDate;
    private static IStockMarket market;
    private static IETF userETF;
    private static IETF systemETF;

    @BeforeAll
    public static void parseData() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = ETFTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = ETFTest.class.getResource("data/S&P500_value.csv").getFile();
        market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));
        startDate = YearMonth.parse("2023-06");
    }

    @BeforeEach
    public void initETFs() {
        userETF = new UserCreatedETF(market, startDate);
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        double[] pref = new double[]{.1, .25, .15, .2, .3};
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        systemETF = new SystemGeneratedETF(market, algo, new BigDecimal("5600.25"), 2);
    }

    @Test
    public void testAddETF() {
        IPortfolio portfolio = new Portfolio();
        assertTrue(portfolio.addETF(userETF));
    }

    @Test
    public void testAddETFPortfolioAlreadyContainsETF() {
        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);
        assertFalse(portfolio.addETF(userETF));
    }

    @Test
    public void testRemoveETF() {
        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);
        assertTrue(portfolio.removeETF(userETF));
    }

    @Test
    public void testRemoveETFPortfolioDoesNotContainETF() {
        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(systemETF);
        assertFalse(portfolio.removeETF(userETF));
    }

    @Test
    public void testGetETFsInPortfolio() {
        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(systemETF);

        assertEquals(1, portfolio.getETFsInPortfolio().size());
        assertTrue(portfolio.getETFsInPortfolio().contains(systemETF));
    }

    @Test
    public void testRunSimulationOnETFUserCreatedETF() {
        Map<YearMonth, BigDecimal[]> expectedReturns = new HashMap<>();
        expectedReturns.put(startDate,
                new BigDecimal[]{new BigDecimal("17750.3400"), new BigDecimal("0.0000"), new BigDecimal(".0000")});
        expectedReturns.put(startDate.plusMonths(1),
                new BigDecimal[]{new BigDecimal("18653.8200"), new BigDecimal("903.4800"), new BigDecimal("0.0509")});
        expectedReturns.put(startDate.plusMonths(2),
                new BigDecimal[]{new BigDecimal("17613.2200"), new BigDecimal("-137.1200"), new BigDecimal("-0.0077")});

        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);

        Map<YearMonth, BigDecimal[]> actualReturns =
                portfolio.runSimulationOnETF(userETF, startDate.plusMonths(2));
        for (Map.Entry<YearMonth, BigDecimal[]> actual : actualReturns.entrySet()) {
            YearMonth date = actual.getKey();
            for (int i = 0; i < 3; i++) {
                BigDecimal actualValue = actual.getValue()[i];
                BigDecimal expectedValue = expectedReturns.get(date)[i];

                assertEquals(expectedValue, actualValue);
            }
        }
    }

    @Test
    public void testRunSimulationOnETFSystemGeneratedETF() {
        Map<YearMonth, BigDecimal[]> expectedReturns = new HashMap<>();
        expectedReturns.put(startDate,
                new BigDecimal[]{new BigDecimal("5600.2500"), new BigDecimal("0.0000"), new BigDecimal(".0000")});
        expectedReturns.put(startDate.plusMonths(1),
                new BigDecimal[]{new BigDecimal("5848.8786"), new BigDecimal("248.6286"), new BigDecimal("0.0444")});
        expectedReturns.put(startDate.plusMonths(2),
                new BigDecimal[]{new BigDecimal("5823.7594"), new BigDecimal("191.8318"), new BigDecimal(".0341")});

        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(systemETF);

        Map<YearMonth, BigDecimal[]> actualReturns =
                portfolio.runSimulationOnETF(systemETF, startDate.plusMonths(2));
        for (Map.Entry<YearMonth, BigDecimal[]> actual : actualReturns.entrySet()) {
            YearMonth date = actual.getKey();
            for (int i = 0; i < 3; i++) {
                BigDecimal actualValue = actual.getValue()[i];
                BigDecimal expectedValue = expectedReturns.get(date)[i];

                assertEquals(expectedValue, actualValue);
            }
        }
    }

    @Test
    public void testRunSimulationOnPortfolio() {
        Map<IETF, Map<YearMonth, BigDecimal[]>> expectedReturns = new HashMap<>();
        Map<YearMonth, BigDecimal[]> etfReturns = new HashMap<>();
        etfReturns.put(startDate, new BigDecimal[]{
                new BigDecimal("17750.3400"),
                new BigDecimal("0.0000"),
                new BigDecimal(".0000")});
        etfReturns.put(startDate.plusMonths(1), new BigDecimal[]{
                new BigDecimal("18653.8200"),
                new BigDecimal("903.4800"),
                new BigDecimal("0.0509")});
        etfReturns.put(startDate.plusMonths(2), new BigDecimal[]{
                new BigDecimal("17613.2200"),
                new BigDecimal("-137.1200"),
                new BigDecimal("-0.0077")});
        expectedReturns.put(userETF, etfReturns);

        etfReturns = new HashMap<>();
        etfReturns.put(startDate, new BigDecimal[]{
                new BigDecimal("5600.2500"),
                new BigDecimal("0.0000"),
                new BigDecimal(".0000")});
        etfReturns.put(startDate.plusMonths(1), new BigDecimal[]{
                new BigDecimal("5848.8786"),
                new BigDecimal("248.6286"),
                new BigDecimal("0.0444")});
        etfReturns.put(startDate.plusMonths(2), new BigDecimal[]{
                new BigDecimal("5823.7594"),
                new BigDecimal("191.8318"),
                new BigDecimal(".0341")});
        expectedReturns.put(systemETF, etfReturns);

        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);
        portfolio.addETF(systemETF);
        Map<IETF, Map<YearMonth, BigDecimal[]>> actualReturns =
                portfolio.runSimulationOnPortfolio(startDate.plusMonths(2));

        for (Map.Entry<IETF, Map<YearMonth, BigDecimal[]>> etfEntry : actualReturns.entrySet()) {
            IETF etf = etfEntry.getKey();
            for (Map.Entry<YearMonth, BigDecimal[]> etfReturnsEntry : etfEntry.getValue().entrySet()) {
                YearMonth date = etfReturnsEntry.getKey();
                for (int i = 0; i < 3; i++) {
                    BigDecimal actualValue = etfReturnsEntry.getValue()[i];
                    BigDecimal expectedValue = expectedReturns.get(etf).get(date)[i];

                    assertEquals(expectedValue, actualValue);
                }
            }
        }
    }

    @Test
    public void testGetETFReturnsDifferentFromAndToDate() {
        Map<YearMonth, BigDecimal[]> expectedReturns = new HashMap<>();
        expectedReturns.put(startDate.plusMonths(1), new BigDecimal[]{
                new BigDecimal("18653.8200"),
                new BigDecimal("903.4800"),
                new BigDecimal("0.0509")});
        expectedReturns.put(startDate.plusMonths(2), new BigDecimal[]{
                new BigDecimal("17613.2200"),
                new BigDecimal("-137.1200"),
                new BigDecimal("-0.0077")});

        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);

        Map<YearMonth, BigDecimal[]> actualReturns =
                portfolio.getETFReturns(userETF, startDate.plusMonths(1), startDate.plusMonths(2));
        for (Map.Entry<YearMonth, BigDecimal[]> actual : actualReturns.entrySet()) {
            YearMonth date = actual.getKey();
            for (int i = 0; i < 3; i++) {
                BigDecimal actualValue = actual.getValue()[i];
                BigDecimal expectedValue = expectedReturns.get(date)[i];

                assertEquals(expectedValue, actualValue);
            }
        }
    }

    @Test
    public void testGetETFReturnsSameFromAndToDate() {
        BigDecimal[] expectedReturns = new BigDecimal[]{
                new BigDecimal("18653.8200"),
                new BigDecimal("903.4800"),
                new BigDecimal("0.0509")};

        IPortfolio portfolio = new Portfolio();
        portfolio.addETF(userETF);
        Map<YearMonth, BigDecimal[]> returnsMap =
                portfolio.getETFReturns(userETF, startDate.plusMonths(1), startDate.plusMonths(1));
        BigDecimal[] actualReturns = returnsMap.get(startDate.plusMonths(1));

        assertEquals(1, returnsMap.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(expectedReturns[i], actualReturns[i]);
        }
    }

    @Test
    public void testGetMarketReturnsDifferentInvestmentStartAndEnd() {
        Map<YearMonth, BigDecimal> expectedReturns = new HashMap<>();
        expectedReturns.put(startDate, new BigDecimal("0.0000"));
        expectedReturns.put(startDate.plusMonths(1), new BigDecimal("0.0311"));
        expectedReturns.put(startDate.plusMonths(2), new BigDecimal("0.0129"));

        IPortfolio portfolio = new Portfolio();
        Map<YearMonth, BigDecimal> actualReturns = portfolio.getMarketReturns(market.getSP500Data(), startDate, startDate.plusMonths(2));

        assertEquals(expectedReturns, actualReturns);
    }

    @Test
    public void testGetMarketReturnsSameInvestmentStartAndEnd() {
        Map<YearMonth, BigDecimal> expectedReturns = new HashMap<>();
        expectedReturns.put(startDate, new BigDecimal("0.0000"));

        IPortfolio portfolio = new Portfolio();
        Map<YearMonth, BigDecimal> actualReturns =
                portfolio.getMarketReturns(market.getSP500Data(), startDate, startDate);

        assertEquals(expectedReturns, actualReturns);
    }

}


