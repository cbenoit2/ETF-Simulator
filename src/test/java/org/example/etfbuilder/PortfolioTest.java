//import org.junit.Before;
//import org.junit.Test;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.Assert.*;
//
//
//public class PortfolioTest {
//    private StockMarket market;
//    private DataParser parser;
//    private IndexBuilder builder;
//    private ETF etf;
//    private LocalDate startDate;
//    private LocalDate endDate;
//
//    private Portfolio port;
//
//    private int testRate;
//
//    @Before
//    public void init() {
//        parser = new DataParser();
//        String dir = System.getProperty("user.dir");
//        String fileName = "/S&P500_Data/portfolio_testing_stock_data.csv";
//        String toParse = dir + fileName;
//        market = new StockMarket(parser.parseStockData(toParse));
//        market.makeMetricMap();
//
//
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
//        testRate = 1;
//
//        port = new Portfolio(parser, market, etf);
//        port.runPortfolio(startDate, endDate, testRate);
//    }
//
//
//    @Test
//    public void portfolioReturnsTest() {
//        // use dummy file to set returns over year to 2x to test return call function
//        double[] returns = port.portfolioReturns(startDate, endDate);
//
//        double val = 1.0;
//
//        for (int i =0; i < returns.length; i++) {
//            val *= (1 + returns[i]);
//        }
//        double expReturns = 2.0;
//
//        double moic = port.moicCalc(startDate, endDate);
//        double expMOIC = 2.0;
//
//
//        assertEquals(expReturns, val, 0.001);
//        assertEquals(expMOIC, moic, 0.001);
//    }
//
//    @Test
//    public void stockDataCallingTest() {
//        // use dummy file to set returns over year to 2x to test return call function
//        double[] returnsApple = port.individualStockReturns("Apple Inc",
//                startDate.plusMonths(1), endDate.minusMonths(1));
//
//        LocalDate testDate = LocalDate.of(2010,3,1);
//        double pxMarch = market.getMetric("Apple Inc", testDate,StockMarket.INDEX_PRICE);
//        double pxApril = market.getMetric("Apple Inc", testDate.plusMonths(1),
//                StockMarket.INDEX_PRICE);
//
//        double expReturns = ((pxApril / pxMarch) - 1);
//
//        double actReturn = returnsApple[1];
//
//        assertEquals(expReturns, actReturn, 0.001);
//    }
//
//
//    @Test
//    public void betaTest() {
//        // Checks to ensure the beta formula is running properly between two stocks
//        // Should have unequal correlations between returns
//        parser = new DataParser();
//        String dir = System.getProperty("user.dir");
//        String fileName = "/S&P500_Data/combined_stock_data.csv";
//        String toParse = dir + fileName;
//        market = new StockMarket(parser.parseStockData(toParse));
//        market.makeMetricMap();
//
//        startDate = LocalDate.of(2010, 1, 1);
//
//        builder = new IndexBuilder(market, startDate);
//        double[] preferences = new double[]{20, 20, 20, 20, 20};
//        String industry = "All";
//        builder.loadPreferences(industry, preferences);
//
//        String etfName = "myETF";
//        double dollars = 100000;
//        double concentration = 0.05;
//
//        etf = new ETF(etfName, builder, dollars, concentration, startDate);
//        endDate = LocalDate.of(2011, 1, 1);
//        testRate = 1;
//
//        port = new Portfolio(parser, market, etf);
//        port.runPortfolio(startDate, endDate, testRate);
//
//
//        double betaOne = port.betaCalc(IPortfolio.STOCK_ONE, IPortfolio.STOCK_TWO,
//                "Agilent Technologies", "Apple Inc", startDate, endDate);
//
//        double betaTwo = port.betaCalc(IPortfolio.STOCK_ONE, IPortfolio.STOCK_TWO,
//                "Apple Inc","Agilent Technologies", startDate, endDate);
//
//        assertTrue(betaOne != betaTwo);
//    }
//
//
//    @Test
//    public void matrixTest() {
//        // Checks to ensure the matrix is in right positions
//        parser = new DataParser();
//        String dir = System.getProperty("user.dir");
//        String fileName = "/S&P500_Data/combined_stock_data.csv";
//        String toParse = dir + fileName;
//        market = new StockMarket(parser.parseStockData(toParse));
//        market.makeMetricMap();
//
//        startDate = LocalDate.of(2010, 1, 1);
//
//        builder = new IndexBuilder(market, startDate);
//        double[] preferences = new double[]{20, 20, 20, 20, 20};
//        String industry = "All";
//        builder.loadPreferences(industry, preferences);
//
//        String etfName = "myETF";
//        double dollars = 100000;
//        double concentration = 0.02;
//
//        etf = new ETF(etfName, builder, dollars, concentration, startDate);
//        endDate = LocalDate.of(2024, 3, 1);
//        testRate = 1;
//
//        port = new Portfolio(parser, market, etf);
//        port.runPortfolio(startDate, endDate, testRate);
//
//        // run the beta matrix
//        double[][] betaMatrix = port.betaMatrix(startDate, endDate);
//
//
//        ArrayList<String> portfolio = port.getMatrixSortedStocks();
//        System.out.println(portfolio.toString());
//        // use numbers further in to test
//        String companyOne = portfolio.get(2);
//        String companyTwo = portfolio.get(3);
//
//        double betaOne = port.betaCalc(IPortfolio.STOCK_ONE, IPortfolio.STOCK_TWO,
//                companyOne, companyTwo, startDate, endDate);
//
//        double betaTwo = port.betaCalc(IPortfolio.STOCK_ONE, IPortfolio.STOCK_TWO,
//                companyTwo,companyOne, startDate, endDate);
//
//        double expScore = port.stockRelationshipScore(betaOne, betaTwo);
//        double actMatrixScore = betaMatrix[3][2];
//
//        // also test that any set of same references returns 1
//        double expSame = 1;
//        double actSame = betaMatrix[2][2];
//
//        //port.printMatrixStats(betaMatrix);
//
//
//        assertEquals(expScore, actMatrixScore, 0.001);
//        assertEquals(expSame, actSame, 0.001);
//    }
//
//
//}
//
//
