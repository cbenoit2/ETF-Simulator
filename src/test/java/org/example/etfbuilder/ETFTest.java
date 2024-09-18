package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IETF;
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

public class ETFTest {

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
        double[] pref = new double[]{.1, .25, .15, .2, .3};
        IETFAlgorithm algo = new ETFAlgorithm(market, startDate, pref, "all");

        userETF = new UserCreatedETF(market, startDate);
        systemETF = new SystemGeneratedETF(market, algo, new BigDecimal("500"), 0);
    }


    @Test
    public void testIsSystemGeneratedUserCreatedETF() {
        assertFalse(userETF.isSystemGenerated());
    }

    @Test
    public void testIsSystemGeneratedSystemGeneratedETF() {
        assertTrue(systemETF.isSystemGenerated());
    }

    @Test
    public void testToStringUserCreatedETF() {
        userETF.setETFName("test 1");
        assertEquals("test 1 (user-created)", userETF.toString());
    }

    @Test
    public void testToStringSystemGeneratedSystemGeneratedETF() {
        systemETF.setETFName("test 2");
        assertEquals("test 2 (algorithm-created)", systemETF.toString());
    }

    @Test
    public void testGetStartDate() {
        assertEquals(startDate, userETF.getStartDate());
    }

    @Test
    public void testBuyStock() {
        assertTrue(userETF.buyStock("Costco", new BigDecimal("23.45"), startDate));
    }

    @Test
    public void testBuyStockZeroQuantity() {
        assertFalse(userETF.buyStock("Costco", new BigDecimal("0"), startDate));
    }

    @Test
    public void testBuyStockNegativeQuantity() {
        assertFalse(userETF.buyStock("Costco", new BigDecimal("124").negate(), startDate));
    }

    @Test
    public void testBuyStockNullQuantity() {
        assertFalse(userETF.buyStock("Costco", null, startDate));
    }

    @Test
    public void testBuyStockCompanyNotInStockMarket() {
        assertFalse(userETF.buyStock("test", new BigDecimal("124"), startDate));
    }

    @Test
    public void testBuyStockNullCompanyName() {
        assertFalse(userETF.buyStock(null, new BigDecimal("34"), startDate));
    }

    @Test
    public void testBuyStockCompanyInvalidDate() {
        assertFalse(userETF.buyStock("Costco", new BigDecimal("124"),
                YearMonth.parse("2002-02")));
        assertFalse(userETF.buyStock("Costco", new BigDecimal("124"),
                YearMonth.parse("2024-06")));
    }

    @Test
    public void testBuyStockCompanyNullDate() {
        assertFalse(userETF.buyStock("Costco", new BigDecimal("124"), null));
    }

    @Test
    public void testGetETFPositions() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Costco", new BigDecimal("12625.0110"));
        expected.put("Chipotle Mexican Grill", new BigDecimal("8556.0000"));
        expected.put("Equinix", new BigDecimal("7839.4000"));
        expected.put("Mettler Toledo", new BigDecimal("6558.2000"));

        userETF.buyStock("Costco", new BigDecimal("23.45"), startDate);
        userETF.buyStock("Chipotle Mexican Grill", new BigDecimal("4"), startDate);
        userETF.buyStock("Equinix", new BigDecimal("10"), startDate);
        userETF.buyStock("Mettler Toledo", new BigDecimal("5"), startDate);

        assertEquals(expected, userETF.getETFPositions());
    }

    @Test
    public void testBuyStockQuantityRoundsDownWhenPrecisionMoreThan2() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Costco", new BigDecimal("18428.7474"));

        userETF.buyStock("Costco", new BigDecimal("34.2365"), startDate);
        assertEquals(expected, userETF.getETFPositions());
    }

    @Test
    public void testETFPositionsAfterBuyingSameStockMultipleTimes() {
        userETF.buyStock("Costco", new BigDecimal("23.45"), startDate);
        userETF.buyStock("Costco", new BigDecimal("34.23"), startDate);
        userETF.buyStock("Costco", new BigDecimal("10"), startDate);

        assertEquals(new BigDecimal("36437.5584"), userETF.getETFPositions().get("Costco"));
    }

    @Test
    public void testETFPositionsDoesNotUpdateIfBuyStockFails() {
        userETF.buyStock("Costco", new BigDecimal("23.45"), startDate);
        userETF.buyStock("Costco", new BigDecimal("34.23"), startDate);
        userETF.buyStock("Costco", new BigDecimal("10"), startDate);
        userETF.buyStock("Costco", new BigDecimal("5"), startDate.plusYears(2));

        assertEquals(new BigDecimal("36437.5584"), userETF.getETFPositions().get("Costco"));
    }

    @Test
    public void testSellStock() {
        userETF.buyStock("Costco", new BigDecimal("67"), startDate);

        assertTrue(userETF.sellStock("Costco", new BigDecimal("10"), startDate));
        assertTrue(userETF.sellStock("Costco", new BigDecimal("57"), startDate));
    }


    @Test
    public void testSellQuantityToSellIsGreaterThanQuantityHeld() {
        userETF.buyStock("Costco", new BigDecimal("12"), startDate);

        assertFalse(userETF.sellStock("Costco", new BigDecimal("13"), startDate.plusMonths(1)));
    }

    @Test
    public void testSellStockZeroQuantity() {
        userETF.buyStock("Costco", new BigDecimal("124"), startDate);

        assertFalse(userETF.sellStock("Costco", new BigDecimal("0"), startDate));
    }

    @Test
    public void testSellStockNegativeQuantity() {
        userETF.buyStock("Costco", new BigDecimal("124"), startDate);

        assertFalse(userETF.sellStock("Costco", new BigDecimal("124").negate(), startDate));
    }

    @Test
    public void testSellStockNullQuantity() {
        userETF.buyStock("Costco", new BigDecimal("12345"), startDate);

        assertFalse(userETF.sellStock("Costco", null, startDate));
    }

    @Test
    public void testSellStockCompanyNotInETF() {
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertFalse(userETF.sellStock("Capital One", new BigDecimal("124"), startDate));
    }

    @Test
    public void testSellStockNullCompanyName() {
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertFalse(userETF.sellStock(null, new BigDecimal("12"), startDate));
    }

    @Test
    public void testSellStockCompanyInvalidDate() {
        userETF.buyStock("Costco", new BigDecimal("145"), startDate);

        assertFalse(userETF.sellStock("Costco", new BigDecimal("12"),
                YearMonth.parse("2002-02")));
        assertFalse(userETF.sellStock("Costco", new BigDecimal("80"),
                YearMonth.parse("2024-06")));
    }

    @Test
    public void testSellStockCompanyNullDate() {
        userETF.buyStock("Costco", new BigDecimal("124"), startDate);

        assertFalse(userETF.sellStock("Costco", new BigDecimal("124"), null));
    }

    @Test
    public void testETFPositionsAfterSellingStock() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Capital One", new BigDecimal("5905.9800"));
        expected.put("Costco", new BigDecimal("37189.5804"));

        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("124.56"), startDate);
        userETF.buyStock("Costco", new BigDecimal("12"), startDate.plusMonths(1));
        userETF.sellStock("Costco", new BigDecimal("67.98"), startDate.plusMonths(2));

        assertEquals(expected, userETF.getETFPositions());
    }

    @Test
    public void testSellStockQuantityRoundsDownWhenPrecisionMoreThan2() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Costco", new BigDecimal("7365.0384"));

        userETF.buyStock("Costco", new BigDecimal("34.23"), startDate);
        userETF.sellStock("Costco", new BigDecimal("20.555"), startDate.plusMonths(1));

        assertEquals(expected, userETF.getETFPositions());
    }

    @Test
    public void testETFPositionsDoesNotUpdateIfSellStockFails() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("Capital One", new BigDecimal("5905.9800"));
        expected.put("Costco", new BigDecimal("73788.6528"));

        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("124.56"), startDate);
        userETF.buyStock("Costco", new BigDecimal("12"), startDate.plusMonths(1));
        userETF.sellStock("Costco", new BigDecimal("200"), startDate.plusMonths(2));

        assertEquals(expected, userETF.getETFPositions());
    }


    @Test
    public void testGetAmountInvestedAfterBuyingStock() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("12"), startDate);
        userETF.buyStock("Costco", new BigDecimal("20"), startDate.plusMonths(1));

        assertEquals(new BigDecimal("23579.9400"), userETF.getAmountInvested());
    }

    @Test
    public void testGetAmountInvestedAfterSellingStock() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("12"), startDate);
        userETF.buyStock("Costco", new BigDecimal("20"), startDate.plusMonths(1));
        userETF.sellStock("Costco", new BigDecimal("16"), startDate.plusMonths(2));

        assertEquals(new BigDecimal("14876.7000"), userETF.getAmountInvested());
    }


    @Test
    public void testGetAmountInvestedEqualsSumOfETFPositionsValues() {
        userETF.buyStock("Capital One", new BigDecimal("78"), startDate);
        userETF.buyStock("Costco", new BigDecimal("45"), startDate);
        userETF.buyStock("Costco", new BigDecimal("20"), startDate.plusMonths(1));
        userETF.sellStock("Costco", new BigDecimal("34"), startDate.plusMonths(2));

        BigDecimal positionTotal = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : userETF.getETFPositions().entrySet()) {
            positionTotal = positionTotal.add(entry.getValue());
        }

        assertEquals(userETF.getAmountInvested(), positionTotal);
    }

    @Test
    public void testGetETFValueOnStartDate() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertEquals(new BigDecimal("17750.3400"), userETF.getETFValue(startDate));
    }

    @Test
    public void testGetETFValueThreeMonthsAfterStartDate() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertEquals(new BigDecimal("17669.8200"), userETF.getETFValue(startDate.plusMonths(3)));
    }

    @Test
    public void testGetETFValueInvalidDate() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertThrows(IllegalArgumentException.class, () -> {
            userETF.getETFValue(startDate.plusYears(1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            userETF.getETFValue(startDate.minusYears(1));
        });
    }

    @Test
    public void testGetETFValueNullDate() {
        userETF.buyStock("Capital One", new BigDecimal("54"), startDate);
        userETF.buyStock("Costco", new BigDecimal("22"), startDate);

        assertThrows(IllegalArgumentException.class, () -> {
            userETF.getETFValue(null);
        });
    }


}
