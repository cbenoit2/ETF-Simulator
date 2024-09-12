package org.example.etfbuilder;


import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ETFAlgorithmTest {

    private static StockMarket market;
    private static YearMonth startDate;
    private static double[] pref;

    @BeforeAll
    public static void init() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = ETFAlgorithmTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = ETFAlgorithmTest.class.getResource("data/S&P500_value.csv").getFile();
        market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));
        startDate = YearMonth.parse("2023-06");
        pref = new double[]{.1, .25, .15, .2, .3};
    }

    @Test
    public void testETFAlgorithmConstructorNullStockMarket() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(null, startDate, pref, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorNullStartDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, null, pref, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorStartDateBeforeFirstEntryInMarket() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, YearMonth.parse("2009-12"), pref, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorStartDateAfterLastEntryInMarket() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, YearMonth.parse("2024-06"), pref, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorSumOfPreferencesArrayExceeds1() {
        double[] preferences = new double[]{.2, .5, .1, .2, .1};
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, startDate, preferences, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorPreferencesArraySizeTooSmall() {
        double[] preferences = new double[]{.2, .5, .1, .2};
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, startDate, preferences, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorPreferencesArraySizeTooLarge() {
        double[] preferences = new double[]{.2, .1, .1, .2, .3, .1};
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, startDate, preferences, "all");
        });
    }

    @Test
    public void testETFAlgorithmConstructorNullIndustry() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ETFAlgorithm(market, startDate, pref, null);
        });
    }

    @Test
    public void testSelectIndustryAll() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Set<String> companies = algo.selectIndustry("all", startDate);

        assertEquals(35, companies.size());
    }

    @Test
    public void testSelectIndustryHealthCare() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Set<String> companies = algo.selectIndustry("health care", startDate);

        assertEquals(8, companies.size());
    }

    @Test
    public void testSelectIndustryNonExistentIndustry() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Set<String> companies = algo.selectIndustry("random", startDate);

        assertEquals(0, companies.size());
    }

    @Test
    public void testSelectIndustryNullIndustry() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Set<String> companies = algo.selectIndustry(null, startDate);

        assertEquals(0, companies.size());
    }

    @Test
    public void testSelectIndustryInvalidDate() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        assertEquals(new HashSet<>(), algo.selectIndustry("all", YearMonth.parse("2009-12")));
        assertEquals(new HashSet<>(), algo.selectIndustry("all", YearMonth.parse("2024-06")));
    }

    @Test
    public void testSelectIndustryNullDate() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        assertEquals(new HashSet<>(), algo.selectIndustry("all", null));
    }


    @Test
    public void testCalcMean() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<BigDecimal> data = new ArrayList<>();
        for (Stock stock : market.getStocksOnDate(startDate)) {
            data.add(stock.getPrice());
        }
        double expected = 625.6217;
        double actual = algo.calcMean(data);
        assertEquals(expected, actual, .0001);
    }

    @Test
    public void testCalcMeanEmptyDataList() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        assertThrows(IllegalArgumentException.class, () -> {
            algo.calcMean(new ArrayList<>());
        });
    }

    @Test
    public void testCalcMeanNullDataList() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        assertThrows(IllegalArgumentException.class, () -> {
            algo.calcMean(null);
        });
    }

    @Test
    public void testCalcStdDev() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<BigDecimal> data = new ArrayList<>();
        for (Stock stock : market.getStocksOnDate(startDate)) {
            data.add(stock.getPrice());
        }
        double expected = 1187.9341;
        double actual = algo.calcStdDev(data, algo.calcMean(data));


        assertEquals(expected, actual, .0001);
    }

    @Test
    public void testCalcStdDevEmptyDataList() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<BigDecimal> data = new ArrayList<>();
        for (Stock stock : market.getStocksOnDate(startDate)) {
            data.add(stock.getPrice());
        }
        double mean = algo.calcMean(data);

        assertThrows(IllegalArgumentException.class, () -> {
            algo.calcStdDev(new ArrayList<>(), mean);
        });
    }

    @Test
    public void testCalcStdDevNullDataList() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<BigDecimal> data = new ArrayList<>();
        for (Stock stock : market.getStocksOnDate(startDate)) {
            data.add(stock.getPrice());
        }
        double mean = algo.calcMean(data);

        assertThrows(IllegalArgumentException.class, () -> {
            algo.calcStdDev(null, mean);
        });
    }

    @Test
    public void testCalcStatsForStockMetrics() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        double[][] expected = new double[][]{{2.8662, 13.6450}, {6656007371.4286, 14108401248.7653},
                {123376454665.8570, 256229527552.6170}, {25.0914, 18.0247}, {0.1283, 0.1600}};
        double[][] actual = algo.calcStatsForStockMetrics(startDate);

        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                assertEquals(expected[i][j], actual[i][j], 0.001);
            }
        }
    }

    @Test
    public void testCalcZScore() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Stock agilent = market.getStock("Agilent Technologies", startDate);
        List<BigDecimal> data = new ArrayList<>();
        for (Stock stock : market.getStocksOnDate(startDate)) {
            data.add(stock.getPrice());
        }

        double expected = -0.425420659;
        double mean = algo.calcMean(data);
        double stdDev = algo.calcStdDev(data, mean);
        double actual = algo.calcZScore(agilent.getPrice(), mean, stdDev);

        assertEquals(expected, actual, .0001);
    }

    @Test
    public void testCalcStockScore() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        double[][] meansAndStdDevs = algo.calcStatsForStockMetrics(startDate);

        double expected = -0.226522162;
        double actual = algo.calcStockScore("Agilent Technologies",
                startDate, meansAndStdDevs);

        assertEquals(expected, actual, .0001);
    }

    @Test
    public void testCalcWeightedAvgStockScore() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<double[][]> meansAndStdDevs = new ArrayList<>();
        meansAndStdDevs.add(algo.calcStatsForStockMetrics(startDate));
        meansAndStdDevs.add(algo.calcStatsForStockMetrics(startDate.plusMonths(1)));
        meansAndStdDevs.add(algo.calcStatsForStockMetrics(startDate.plusMonths(2)));

        double expected = -0.245713516;
        double actual = algo.calcWeightedAvgStockScore("Agilent Technologies",
                startDate, meansAndStdDevs);

        assertEquals(expected, actual, .0001);
    }


    @Test
    public void testScoreStocksCorrectAmtOfStocks() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        List<Map.Entry<Stock, Double>> stockScores = algo.scoreStocks(startDate);

        assertEquals(35, stockScores.size());
    }

    @Test
    public void testScoreStocksCorrectTopAndBottomStocks() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Stock expectedTopStock = market.getStock("Alphabet Inc (Class A)", startDate);
        double expectedTopScore = 1;
        Stock expectedBottomStock = market.getStock("Viatris", startDate);
        double expectedBottomScore = 0;

        List<Map.Entry<Stock, Double>> stockScores = algo.scoreStocks(startDate);
        Stock actualTopStock = stockScores.get(0).getKey();
        double actualTopScore = stockScores.get(0).getValue();
        Stock actualBottomStock = stockScores.get(34).getKey();
        double actualBottomScore = stockScores.get(34).getValue();

        assertEquals(expectedTopStock, actualTopStock);
        assertEquals(expectedTopScore, actualTopScore);
        assertEquals(expectedBottomStock, actualBottomStock);
        assertEquals(expectedBottomScore, actualBottomScore);
    }

    @Test
    public void testRunAlgorithm() {
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Alphabet Inc (Class A)", new BigDecimal("8.99"));
        expected.put("ExxonMobil", new BigDecimal("8.60"));
        expected.put("S&P Global", new BigDecimal("1.61"));
        expected.put("Booking Holdings", new BigDecimal("0.23"));
        expected.put("Equinix", new BigDecimal("0.78"));
        expected.put("Prologis", new BigDecimal("4.90"));
        expected.put("Goldman Sachs", new BigDecimal("1.50"));
        expected.put("Take Two Interactive", new BigDecimal("3.27"));
        expected.put("Vulcan Materials Company", new BigDecimal("2.05"));
        expected.put("Chipotle Mexican Grill", new BigDecimal("0.21"));
        expected.put("Halliburton", new BigDecimal("12.93"));
        expected.put("Uninvested Cash", new BigDecimal("0.0821"));

        Map<String, BigDecimal> actual = algo.runAlgorithm(new BigDecimal("6780.33"), startDate);

        for (Map.Entry<String, BigDecimal> actualEntry : actual.entrySet()) {
            String company = actualEntry.getKey();
            BigDecimal quantity = actualEntry.getValue();

            assertEquals(expected.get(company), quantity);
        }
    }


}
