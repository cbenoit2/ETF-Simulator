package org.example.etfbuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IndexOnNumericalFieldTest {

    private static Set<Stock> stocks;
    private static IIndexStrategy numSearchStrategy;

    @BeforeAll
    public static void init() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = StockMarketTest.class
                .getResource("org/example/etfbuilder/data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = StockMarketTest.class.getResource("data/S&P500_value.csv").getFile();
        IStockMarket market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));

        numSearchStrategy = new IndexOnNumericalField();
        stocks = market.getStocksOnDate(YearMonth.parse("2024-03"));
    }

    @Test
    public void testIndexStocks() {
        List<String> expectedNames = new ArrayList<>(Arrays.asList("Mettler Toledo",
                "Agilent Technologies", "Aflac", "Amphenol", "Waters Corporation", "BlackRock",
                "IBM", "West Pharmaceutical Services"));
        Set<Stock> actual = numSearchStrategy.
                indexStocks(stocks, IndexingUtils.SALES_GROWTH_PREFIX + "-.035_.022");
        // test that there are 8 stocks in the set returned
        assertEquals(8, actual.size());
        // test that the correct stocks are returned
        for (Stock s : actual) {
            assertTrue(expectedNames.contains(s.getName()));
        }
    }


    @Test
    public void testIndexStocksBegRangeAndEndRangeAreTheSame() {
        String expectedName = "BlackRock";
        Set<Stock> actual = numSearchStrategy.
                indexStocks(stocks, IndexingUtils.SALES_GROWTH_PREFIX + "-.0010_-.0010");
        // test that there is 1 stock in the set returned
        assertEquals(1, actual.size());
        // test that the correct stock is returned
        for (Stock s : actual) {
            assertEquals(expectedName, s.getName());
        }
    }

    @Test
    public void testIndexStocksEndNumIsLessThanBegNum() {
        assertThrows(IllegalArgumentException.class, () -> {
            numSearchStrategy.indexStocks(stocks,
                    IndexingUtils.SALES_GROWTH_PREFIX + ".022_-.035");
        });
    }

    @Test
    public void testIndexStocksReturnsEmptySetIfNoStocksMatchSearchKey() {
        Set<Stock> expected = new HashSet<>();
        Set<Stock> actual = numSearchStrategy.
                indexStocks(stocks, IndexingUtils.DEBT_RATIO_PREFIX + "85_90");
        // test that there are no stocks in the set returned
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexStocksReturnsEmptySetIfStocksToSearchIsEmpty() {
        Set<Stock> actual = numSearchStrategy.
                indexStocks(new HashSet<>(), IndexingUtils.SALES_GROWTH_PREFIX + "-.035_.022");
        // test that there are no stocks in the set returned
        assertEquals(0, actual.size());
    }

    @Test
    public void testIndexStocksThrowsExceptionIfSearchKeyDoesNotHavePrefix() {
        assertThrows(IllegalArgumentException.class, () -> {
            numSearchStrategy.indexStocks(stocks, "2345");
        });

    }

    @Test
    public void testIndexStocksThrowsExceptionIfSearchKeyDoesNotHaveNumericalRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            numSearchStrategy.indexStocks(stocks, IndexingUtils.NET_INCOME_PREFIX + "9089");
        });
    }

}