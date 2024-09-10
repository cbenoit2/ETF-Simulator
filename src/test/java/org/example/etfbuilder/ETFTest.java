//import org.junit.Before;
//import org.junit.Test;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.Assert.*;
//
//
//public class ETFTest {
//    private StockMarket market;
//    private DataParser parser;
//    private IndexBuilder builder;
//    private ETF etf;
//    private LocalDate startDate;
//    private LocalDate endDate;
//
//    private int testRate;
//
//    @Before
//    public void init() {
//        parser = new DataParser();
//        String dir = System.getProperty("user.dir");
//        String fileName = "/S&P500_Data/subsample_stock_data_testing.csv";
//        String toParse = dir + fileName;
//        market = new StockMarket(parser.parseStockData(toParse));
//        startDate = LocalDate.of(2010, 1, 1);
//
//
//        builder = new IndexBuilder(market, startDate);
//        double[] preferences = new double[]{20, 20, 20, 20, 20};
//        String industry = "All";
//        builder.loadPreferences(industry, preferences);
//
//        String etfName = "myETF";
//        double dollars = 100000;
//        double concentration = 0.25;
//
//        etf = new ETF(etfName, builder, dollars, concentration, startDate);
//        endDate = LocalDate.of(2011, 1, 1);
//        testRate = 2;
//    }
//
//
//    @Test
//    public void formETFTest() {
//        // tests that ETFChangeLater is formed to right size based on inputs
//
//        TreeMap<String, Double> allScores = etf.pullStockScores();
//        TreeMap<String, Double> inclStocks = etf.getIncludedStocks();
//        TreeMap<String, Double> exclStocks = etf.getStockDecisions()[1];
//
//        int allSize = allScores.size();
//        int etfSize = inclStocks.size();
//        int exclSize = exclStocks.size();
//
//        int expAll = 9;
//        int expETF = 4;
//        int expExcl = 5;
//
//        assertEquals(expAll, allSize);
//        assertEquals(expETF, etfSize);
//        assertEquals(expExcl, exclSize);
//    }
//
//
//    @Test
//    public void scoringCutOffTest() {
//        // tests that ETFChangeLater is grabbing the stocks with the highest scores
//        TreeMap<String, Double> allScores = etf.pullStockScores();
//        TreeMap<String, Double> inclStocks = etf.getIncludedStocks();
//
//        // get top 4 scores from allScores
//        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(allScores.entrySet());
//        sortedEntries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
//
//        // Assuming that the top scores needed are 4
//        List<Double> topFourScores = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            topFourScores.add(sortedEntries.get(i).getValue());
//        }
//
//        // each score in inclStocks is in the top four scores from allScores
//        for (Double score : inclStocks.values()) {
//            assertTrue(topFourScores.contains(score));
//        }
//    }
//
//    @Test
//    public void simulationTest() {
//        // Tests that the ETFChangeLater swaps properly and on correct schedule
//
//        // rest everything on the larger dataset
//        parser = new DataParser();
//        String dir = System.getProperty("user.dir");
//        String fileName = "/S&P500_Data/adjusted_subsample_stock_data_testing.csv";
//        String toParse = dir + fileName;
//        market = new StockMarket(parser.parseStockData(toParse));
//
//        builder = new IndexBuilder(market, startDate);
//        // set all to the MarketCap and add dummy companies to know values
//        double[] preferences = new double[]{0, 0, 100, 0, 0};
//        String industry = "All";
//        builder.loadPreferences(industry, preferences);
//
//        String etfName = "myETF";
//        double dollars = 100000;
//        double concentration = 0.25;
//
//        etf = new ETF(etfName, builder, dollars, concentration, startDate);
//        endDate = LocalDate.of(2011, 1, 1);
//        testRate = 1;
//
//        TreeSet<Transaction> transactions = etf.simulateETF(startDate, endDate, testRate);
//        for (Transaction trans : transactions) {
//            LocalDate d = trans.getTransactionDate();
//            // Used for testing
//            //System.out.println(d);
//        }
//
//
//        int transCount = transactions.size();
//        // 5 fake co transactions + 1 actual where ADM briefly becomes 4th largest MC)
//        int expCount = 6;
//
//        assertEquals(expCount, transCount);
//
//
//        // test with steps of 3 which should skip last transaction (never test on Nov. 2010)
//        int otherTestRate = 3;
//        TreeSet<Transaction> transactions3 = etf.simulateETF(startDate, endDate, otherTestRate);
//
//        int transCount3 = transactions3.size();
//        int expCount3 = 5;
//
//        assertEquals(expCount3, transCount3);
//    }
//
//
//    @Test
//    public void userInputedSimulationTest() {
//        // tests that user input portfolio has no transactions
//
//        TreeMap<String, Double> userPort = new TreeMap<>();
//        userPort.put("Apple Inc", 1000.0);
//        userPort.put("Agilent Technologies", 1000.0);
//        userPort.put("Abbott", 1000.0);
//
//        etf = new ETF("etf", builder, userPort, startDate);
//
//        endDate = LocalDate.of(2011, 1, 1);
//        testRate = 1;
//
//        TreeSet<Transaction> transactions = etf.simulateETF(startDate, endDate, testRate);
//
//        int transCount = transactions.size();
//        // Should have no transactions
//        int expCount = 0;
//
////        Following is used solely for self checking matrix
////        Portfolio port = new Portfolio(parser,market, etf);
////        port.runPortfolio(startDate, endDate, 1);
////        double moic = port.moicCalc(startDate, endDate);
////        System.out.println(moic);
////        double[][] matrix = port.betaMatrix(startDate, endDate);
////        port.printMatrixStats(matrix);
//
//        assertEquals(expCount, transCount);
//
//    }
//}
