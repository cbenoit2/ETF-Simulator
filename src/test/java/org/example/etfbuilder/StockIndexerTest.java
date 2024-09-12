package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IStockIndexer;
import org.example.etfbuilder.interfaces.IStockMarket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StockIndexerTest {

    private static Set<Stock> stocks;
    private static IStockIndexer indexer;

    @BeforeAll
    public static void init() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = StockMarketTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = StockMarketTest.class.getResource("data/S&P500_value.csv").getFile();
        IStockMarket market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));

        indexer = new StockIndexer();
        stocks = market.getStocksOnDate(YearMonth.parse("2024-03"));
    }

    @Test
    public void testIndexOnIndustry() {
        List<String> expectedNames = new ArrayList<>(Arrays.asList("Automatic Data Processing",
                "W W Grainger"));
        Set<Stock> actual = indexer.index(new IndexOnStringField(), stocks,
                IndexingUtils.INDUSTRY_PREFIX + "industrials");

        // test that there are two stocks in the set
        assertEquals(2, actual.size());
        // test that the name of the stocks are correct
        for (Stock s : actual) {
            assertTrue(expectedNames.contains(s.getName()));
        }
    }

    @Test
    public void testIndexOnName() {
        Set<Stock> actual = indexer.index(new IndexOnStringField(), stocks,
                IndexingUtils.NAME_PREFIX + "AutoZone");
        Stock s = (Stock) actual.toArray()[0];

        // test that there is one stock in the set
        assertEquals(1, actual.size());
        // test that the stock is AutoZone
        assertEquals("AutoZone", s.getName());
    }

    @Test
    public void testIndexNoStocksFitSearchKey() {
        Set<Stock> expected = new HashSet<>();
        Set<Stock> actual = indexer.index(new IndexOnStringField(), stocks,
                IndexingUtils.NAME_PREFIX + "Apple Inc");

        // test that there are no stocks in the set
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOnPrice() {
        Set<Stock> actual = indexer.index(new IndexOnNumericalField(), stocks,
                IndexingUtils.PRICE_PREFIX + "120_200");

        // test that there are 11 stocks in the set
        assertEquals(11, actual.size());
    }


    @Test
    public void testIndexInvalidSearchKeyFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            indexer.index(new IndexOnNumericalField(), stocks, "120_200");
        });
    }

    @Test
    public void testIndexEmptySetOfStocksPassedIn() {
        Set<Stock> expected = new HashSet<>();
        Set<Stock> actual = indexer.index(new IndexOnStringField(), new HashSet<>(),
                IndexingUtils.INDUSTRY_PREFIX + "industrials");

        // test that there are no stocks in the set
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }


    @Test
    public void testMultidimensionalIndexOnIndustryAndPrice() {
        List<String> expectedNames = new ArrayList<>(Arrays.asList("AutoZone", "Booking Holdings",
                "Chipotle Mexican Grill"));
        Set<String> indexKeys = new HashSet<>(Arrays.asList(IndexingUtils.INDUSTRY_PREFIX +
                "Consumer Discretionary", IndexingUtils.PRICE_PREFIX + "2900_4000"));
        Set<Stock> actual = indexer.multiDimensionalIndex(stocks, indexKeys);

        // test that three stocks are in the set
        assertEquals(3, actual.size());
        // test that the stocks in the set are correct and are in alphabetical order
        int i = 0;
        for (Stock s : actual) {
            assertEquals(expectedNames.get(i), s.getName());
            i++;
        }
    }

    @Test
    public void testMultidimensionalIndexOnIndustryNetIncomeDERatioAndPrice() {
        List<String> expectedNames = new ArrayList<>(Arrays.asList("Capital One", "BlackRock"));
        Set<String> indexKeys = new HashSet<>(Arrays.asList(IndexingUtils.INDUSTRY_PREFIX +
                        "Financials", IndexingUtils.NET_INCOME_PREFIX + "4899000_" + Double.MAX_VALUE,
                IndexingUtils.DEBT_RATIO_PREFIX + "0_0.12", IndexingUtils.PRICE_PREFIX + "140_900"));
        Set<Stock> actual = indexer.multiDimensionalIndex(stocks, indexKeys);

        // test that there are two stocks in the set
        assertEquals(2, actual.size());
        // test that the stocks in the set are Capital One and BlackRock
        for (Stock s : actual) {
            assertTrue(expectedNames.contains(s.getName()));
        }
    }

    @Test
    public void testMultidimensionalIndexNoStockFitsSearchKeySet() {
        Set<Stock> expected = new HashSet<>();
        Set<String> indexKeys = new HashSet<>(Arrays.asList(IndexingUtils.DEBT_RATIO_PREFIX + "20_25",
                IndexingUtils.INDUSTRY_PREFIX + "Financials"));
        Set<Stock> actual = indexer.multiDimensionalIndex(new HashSet<>(), indexKeys);

        // test that there are no stocks in the set
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMultidimensionalIndexSearchKeySetContainsInvalidKeyFormat() {
        Set<String> indexKeys = new HashSet<>(Arrays.asList(IndexingUtils.DEBT_RATIO_PREFIX + "0_2",
                "Financials"));

        assertThrows(IllegalArgumentException.class, () -> {
            indexer.multiDimensionalIndex(new HashSet<>(), indexKeys);
        });
    }

    @Test
    public void testMultidimensionalIndexEmptySetOfStocksPassedIn() {
        Set<Stock> expected = new HashSet<>();
        Set<String> indexKeys = new HashSet<>(Arrays.asList(
                IndexingUtils.DEBT_RATIO_PREFIX + "0_2",
                IndexingUtils.INDUSTRY_PREFIX + "Financials"));
        Set<Stock> actual = indexer.multiDimensionalIndex(new HashSet<>(), indexKeys);

        // test that there are no stocks in the set
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }


}