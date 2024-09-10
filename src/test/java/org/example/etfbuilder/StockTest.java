package org.example.etfbuilder;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class StockTest {

    @Test
    public void testStockConstructor() {
        BigDecimal price = new BigDecimal("56.43");
        Stock stock = new Stock("random", price);
        // test that constructor sets stock's name and price
        assertEquals("random", stock.getName());
        assertEquals(price, stock.getPrice());
    }

    @Test
    public void testStockConstructorInvalidPrice() {
        BigDecimal price = new BigDecimal("-56.43");
        // test that constructor throws exception if share price is invalid
        assertThrows(IllegalArgumentException.class, () -> {
            new Stock("random", price);
        });
    }

    @Test
    public void testStockToString() {
        BigDecimal price = new BigDecimal("56.43");
        Stock stock = new Stock("random", price);
        // test that toString returns the company's name
        assertEquals("random", stock.toString());
    }

    @Test
    public void testGetAndSetIndustry() {
        Stock stock = new Stock("random", new BigDecimal("45.34"));
        stock.setIndustry("technology");
        assertEquals("technology", stock.getIndustry());
    }

    @Test
    public void testGetAndSetName() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        assertEquals("random", stock.getName());
        stock.setName("new name");
        assertEquals("new name", stock.getName());
    }

    @Test
    public void testGetAndSetPrice() {
        BigDecimal price = new BigDecimal("313.32");
        Stock stock = new Stock("random", price);
        price = new BigDecimal("23.22");
        stock.setPrice(price);
        assertEquals(price, stock.getPrice());
    }

    @Test
    public void testSetPriceInvalidPrice() {
        Stock stock = new Stock("random", new BigDecimal("32.23"));
        assertThrows(IllegalArgumentException.class, () -> {
            stock.setPrice(new BigDecimal("-432"));
        });
    }

    @Test
    public void testGetAndSetDebtRatio() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        BigDecimal debtRatio = new BigDecimal(".9");
        stock.setDebtRatio(debtRatio);
        assertEquals(debtRatio, stock.getDebtRatio());
    }

    @Test
    public void testGetAndSetNetIncome() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        BigDecimal netIncome = new BigDecimal("2434543");
        stock.setNetIncome(netIncome);
        assertEquals(netIncome, stock.getNetIncome());
    }

    @Test
    public void testGetAndSetMarketCap() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        BigDecimal marketCap = new BigDecimal("3534524634");
        stock.setMarketCap(marketCap);
        assertEquals(marketCap, stock.getMarketCap());
    }

    @Test
    public void testGetAndSetSalesGrowth() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        BigDecimal salesGrowth = new BigDecimal(".3452");
        stock.setSalesGrowth(salesGrowth);
        assertEquals(salesGrowth, stock.getSalesGrowth());
    }

    @Test
    public void testGetAndSetPERatio() {
        Stock stock = new Stock("random", new BigDecimal("234.32"));
        BigDecimal peRatio = new BigDecimal("2.4");
        stock.setPERatio(peRatio);
        assertEquals(peRatio, stock.getPERatio());
    }

    @Test
    public void testStockNameComparator() {
        List<Stock> stocks = new ArrayList<>(Arrays.asList(new Stock("eBay"),
                new Stock("accenture"), new Stock("Apple Inc"),
                new Stock("eqt"), new Stock("Expedia Group")));
        String[] orderedNames = new String[]{"accenture", "Apple Inc", "eBay", "eqt",
                "Expedia Group"};

        stocks.sort(new Stock.StockNameComparator());
        for (int i = 0; i < stocks.size(); i++) {
            String expected = orderedNames[i];
            String actual = stocks.get(i).getName();
            assertEquals(expected, actual);
        }
    }

}