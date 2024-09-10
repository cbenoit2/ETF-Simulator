package org.example.etfbuilder;


import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DataParserTest {

    private IDataParser dp = new DataParser();

    @Test
    public void testParseStockDataReturnMapContainsAllDates() throws IOException {
        String csvFile = getClass().getResource("data/medium_stock_data_sample.csv").getFile();
        Map<YearMonth, Map<String, Stock>> datesMap = dp.parseStockData(csvFile);
        // there should be 10 dates in the map
        assertEquals(10, datesMap.size());
    }

    @Test
    public void testParseStockDataReturnMapContainsAllStocksForDates() throws IOException {
        String csvFile = getClass().getResource("data/medium_stock_data_sample.csv").getFile();
        YearMonth date = YearMonth.parse("2023-08");
        Map<String, Stock> augustStocksMap = dp.parseStockData(csvFile).get(date);
        // there should be 35 stocks in the map for August
        assertEquals(35, augustStocksMap.size());
    }

    @Test
    public void testParseStockDataStockHasCorrectInfo() throws IOException {
        String csvFile = getClass().getResource("data/medium_stock_data_sample.csv").getFile();
        YearMonth date = YearMonth.parse("2023-06");
        Map<String, Stock> juneStocksMap = dp.parseStockData(csvFile).get(date);
        Stock agilentStock = juneStocksMap.get("agilent technologies");

        // test that the fields of the stock are set to the correct values
        assertEquals("Agilent Technologies", agilentStock.getName());
        assertEquals("Health Care", agilentStock.getIndustry());
        assertEquals(new BigDecimal("120.25"), agilentStock.getPrice());
        assertEquals(new BigDecimal("1351000000"), agilentStock.getNetIncome());
        assertEquals(new BigDecimal("35518942115"), agilentStock.getMarketCap());
        assertEquals(new BigDecimal("24.4"), agilentStock.getPERatio());
        assertEquals(new BigDecimal(".0790"), agilentStock.getSalesGrowth());
        assertEquals(new BigDecimal(".2700"), agilentStock.getDebtRatio());
    }

    @Test
    public void testParseStockDataBadFile() {
        // an exception should be thrown if a bad file is read
        assertThrows(FileNotFoundException.class, () -> {
            dp.parseStockData("bad_file.csv");
        });
    }


    @Test
    public void testCreateStockFromDataStockHasCorrectInfo() {
        String name = "random";
        String[] data = {"health care", "23.40", "23434", "3443234", "4", "34", "5"};
        Stock actualStock = dp.createStockFromData(name, data);

        // test that the fields of the stock are set to the correct values
        assertEquals("random", actualStock.getName());
        assertEquals("health care", actualStock.getIndustry());
        assertEquals(new BigDecimal(data[1]), actualStock.getPrice());
        assertEquals(new BigDecimal(data[2]), actualStock.getNetIncome());
        assertEquals(new BigDecimal(data[3]), actualStock.getMarketCap());
        assertEquals(new BigDecimal(data[4]), actualStock.getPERatio());
        assertEquals(new BigDecimal(".3400"), actualStock.getSalesGrowth());
        assertEquals(new BigDecimal(".0500"), actualStock.getDebtRatio());
    }

    @Test
    public void testParseSP500MarketDataReturnMapContainsAllDates() throws IOException {
        String csvFile = getClass().getResource("data/S&P500_value.csv").getFile();
        Map<YearMonth, BigDecimal> sp500MarketData = dp.parseSP500MarketData(csvFile);
        // map should have 171 dates in it
        assertEquals(171, sp500MarketData.size());
    }

    @Test
    public void testParseSP500MarketDataReturnMapContainsCorrectValues() throws IOException {
        String csvFile = getClass().getResource("data/S&P500_value.csv").getFile();
        Map<YearMonth, BigDecimal> sp500MarketData = dp.parseSP500MarketData(csvFile);

        // test that dates in the map have the correct value
        assertEquals(new BigDecimal("1030.71"),
                sp500MarketData.get(YearMonth.parse("2010-06")));
        assertEquals(new BigDecimal("2823.81"),
                sp500MarketData.get(YearMonth.parse("2018-01")));
        assertEquals(new BigDecimal("4766.18"),
                sp500MarketData.get(YearMonth.parse("2021-12")));
        assertEquals(new BigDecimal("4109.31"),
                sp500MarketData.get(YearMonth.parse("2023-03")));
    }


    @Test
    // assert throws
    public void testParseSP500MarketBadFile() {
        // an exception should be thrown if a bad file is read
        assertThrows(FileNotFoundException.class, () -> {
            dp.parseSP500MarketData("bad_file.csv");
        });
    }

}