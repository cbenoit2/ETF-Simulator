package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IDataParser;
import org.example.etfbuilder.interfaces.IStockMarket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class StockMarketTest {

    private static Map<YearMonth, Map<String, Stock>> stockData;
    private static Map<YearMonth, BigDecimal> sp500Value;
    private IStockMarket market;

    @BeforeAll
    public static void init() throws IOException {
        IDataParser dp = new DataParser();
        String stocksCSV = StockMarketTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = StockMarketTest.class.getResource("data/S&P500_value.csv").getFile();
        stockData = dp.parseStockData(stocksCSV);
        sp500Value = dp.parseSP500MarketData(sp500CSV);
    }

    @BeforeEach
    public void initStockMarket() {
        Map<YearMonth, Map<String, Stock>> stockDataCopy = stockData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashMap<>(e.getValue())));
        Map<YearMonth, BigDecimal> sp500ValueCopy = sp500Value.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        market = new StockMarket(stockDataCopy, sp500ValueCopy);
    }

    @Test
    public void testStockMarketConstructorNullStockMap() {
        IDataParser dp = new DataParser();
        String sp500CSV = StockMarketTest.class.getResource("data/S&P500_value.csv").getFile();

        assertThrows(IllegalArgumentException.class, () -> {
            new StockMarket(null, dp.parseSP500MarketData(sp500CSV));
        });
    }

    @Test
    public void testStockMarketConstructorNullSP500Map() {
        IDataParser dp = new DataParser();
        String stocksCSV = StockMarketTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        assertThrows(IllegalArgumentException.class, () -> {
            new StockMarket(dp.parseStockData(stocksCSV), null);
        });
    }

    @Test
    public void testGetStockReturnsCorrectStock() {
        Stock stock = market.getStock("Aflac", YearMonth.parse("2023-06"));

        assertEquals("Aflac", stock.getName());
        assertEquals("Financials", stock.getIndustry());
        assertEquals(new BigDecimal("69.80"), stock.getPrice());
        assertEquals(new BigDecimal("4357000000"), stock.getNetIncome());
        assertEquals(new BigDecimal("42175044251"), stock.getMarketCap());
        assertEquals(new BigDecimal("13.5"), stock.getPERatio());
        assertEquals(new BigDecimal("-.1110"), stock.getSalesGrowth());
        assertEquals(new BigDecimal(".1900"), stock.getDebtRatio());
    }

    @Test
    public void testGetStockInvalidDateEntered() {
        Stock stock = market.getStock("Aflac", YearMonth.parse("2021-06"));
        assertNull(stock);

        stock = market.getStock("Aflac", null);
        assertNull(stock);
    }

    @Test
    public void testGetStockInvalidCompanyNameEntered() {
        Stock stock = market.getStock("random", YearMonth.parse("2023-06"));
        assertNull(stock);

        stock = market.getStock(null, YearMonth.parse("2023-06"));
        assertNull(stock);
    }

    @Test
    public void testAddStockToExistentDateInMarket() {
        Stock stock = new Stock("random");
        assertTrue(market.addStock(stock, YearMonth.parse("2023-06")));
    }

    @Test
    public void testAddStockToDateNotYetInMarket() {
        Stock stock = new Stock("random");
        assertTrue(market.addStock(stock, YearMonth.parse("2022-06")));
    }

    @Test
    public void testAddStockDateAlreadyHasAssociatedStock() {
        Stock stock = market.getStock("Aflac", YearMonth.parse("2023-06"));
        assertFalse(market.addStock(stock, YearMonth.parse("2023-06")));
    }

    @Test
    public void testAddStockNullDateEntered() {
        Stock stock = new Stock("random");
        assertFalse(market.addStock(stock, null));
    }

    @Test
    public void testAddStockAddNullStock() {
        assertFalse(market.addStock(null, YearMonth.parse("2023-06")));
    }

    @Test
    public void testGetStocksOnDate() {
        Set<Stock> stocks = market.getStocksOnDate(YearMonth.parse("2023-06"));
        assertEquals(35, stocks.size());
    }

    @Test
    public void testGetStocksOnDateInvalidDateEntered() {
        Set<Stock> stocks = market.getStocksOnDate(YearMonth.parse("2022-06"));
        assertEquals(0, stocks.size());

        stocks = market.getStocksOnDate(null);
        assertEquals(0, stocks.size());
    }


    @Test
    public void testGetSP500ValueOnDateReturnsCorrectValue() {
        assertEquals(new BigDecimal("1073.87"),
                market.getSP500ValueOnDate(YearMonth.parse("2010-01")));
    }

    @Test
    public void testGetSP500ValueDateNoRecordedValueOnDate() {
        assertNull(market.getSP500ValueOnDate(YearMonth.parse("2009-01")));
    }

    @Test
    public void testGetSP500ValueDateNullDate() {
        assertNull(market.getSP500ValueOnDate(null));
    }

    @Test
    public void testAddSP500Value() {
        assertTrue(market.addSP500Value(new BigDecimal("6534.32"),
                YearMonth.parse("2024-08")));
    }

    @Test
    public void testAddSP500ValueNegativeValueEntered() {
        assertFalse(market.addSP500Value(new BigDecimal("-6534.32"),
                YearMonth.parse("2024-08")));
    }

    @Test
    public void testAddSP500ValueNullValueEntered() {
        assertFalse(market.addSP500Value(null, YearMonth.parse("2024-08")));
    }

    @Test
    public void testAddSP500ValueInvalidDateEntered() {
        assertFalse(market.addSP500Value(new BigDecimal("6534.32"), null));
    }

    @Test
    public void testAddSP500ValueForDateAlreadyRecorded() {
        assertFalse(market.addSP500Value(new BigDecimal("6534.32"),
                YearMonth.parse("2022-08")));
    }

    @Test
    public void testGetSP500Data() {
        assertEquals(171, market.getSP500Data().size());
    }

    @Test
    public void testGetAllCompanyNames() {
        Set<String> companyNames = market.getAllCompanyNames(YearMonth.parse("2023-06"));
        assertEquals(35, companyNames.size());
    }

    @Test
    public void testGetAllCompanyNamesInvalidDateEntered() {
        Set<String> companyNames = market.getAllCompanyNames(YearMonth.parse("2009-01"));
        assertEquals(0, companyNames.size());

        companyNames = market.getAllCompanyNames(null);
        assertEquals(0, companyNames.size());
    }


}



