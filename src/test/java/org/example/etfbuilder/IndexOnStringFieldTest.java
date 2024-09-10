package org.example.etfbuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IndexOnStringFieldTest {

    private static Set<Stock> stocks;
    private static IIndexStrategy strSearchStrategy;

    @BeforeAll
    public static void init() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = StockMarketTest.class
                .getResource("org/example/etfbuilder/data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = StockMarketTest.class.getResource("data/S&P500_value.csv").getFile();
        IStockMarket market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));

        strSearchStrategy = new IndexOnStringField();
        stocks = market.getStocksOnDate(YearMonth.parse("2024-03"));
    }

    @Test
    public void testIndexStocksReturnsCorrectAmountOfStocks() {
        Set<Stock> actual = strSearchStrategy.
                indexStocks(stocks, IndexingUtils.INDUSTRY_PREFIX + "Health Care");

        // test that there are 8 stocks returned
        assertEquals(8, actual.size());
    }

    @Test
    public void testIndexStocksReturnsCorrectStocks() {
        List<String> expectedNames = new ArrayList<>(Arrays.asList("Halliburton", "ExxonMobil"));
        Set<Stock> actual = strSearchStrategy.
                indexStocks(stocks, IndexingUtils.INDUSTRY_PREFIX + "Energy");

        // test that there are 2 stocks returned
        assertEquals(2, actual.size());
        // test that the two stocks in the set are Halliburton and ExxonMobil
        for (Stock stock : actual) {
            assertTrue(expectedNames.contains(stock.getName()));
        }
    }

    @Test
    public void testIndexStocksReturnsEmptySetIfNoStocksMatchSearchKey() {
        Set<Stock> expected = new HashSet<>();
        Set<Stock> actual = strSearchStrategy.
                indexStocks(stocks, IndexingUtils.NAME_PREFIX + "Random Inc");

        // test that 0 stocks come up in the search for Random Inc
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexStocksReturnsEmptySetIfStocksToSearchIsEmpty() {
        Set<Stock> expected = new HashSet<>();
        Set<Stock> actual = strSearchStrategy.
                indexStocks(new HashSet<>(), IndexingUtils.INDUSTRY_PREFIX + "Health Care");

        // test that an empty set is returned if an empty set of stocks is passed in 
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexStocksThrowsExceptionIfSearchKeyIsImproperlyFormatted() {
        // test that an exception is thrown if the search key does not contain a 
        // prefix with what metric the search key is looking for
        assertThrows(IllegalArgumentException.class, () -> {
            strSearchStrategy.indexStocks(stocks, "Health Care");
        });
    }

}