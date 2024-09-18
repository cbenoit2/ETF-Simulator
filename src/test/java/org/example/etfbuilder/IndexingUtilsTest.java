package org.example.etfbuilder;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IndexingUtilsTest {

    @Test
    public void testGetMetricSupplierCorrectValueRetrievedBySupplier() {
        BigDecimal expectedPrice = new BigDecimal("342.24");
        Stock s = new Stock("random", expectedPrice);
        BigDecimal actualPrice = IndexingUtils.getMetricSupplier(s, stock -> s.getPrice()).get();

        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    public void testGetMetricForCategoryName() {
        Stock s = new Stock("random");
        String expectedName = "random";
        String actualName = (String) IndexingUtils.getMetricForCategory(s, "na").get();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testGetMetricForCategoryIndustry() {
        Stock s = new Stock("random");
        s.setIndustry("health care");
        String expectedIndustry = "health care";
        String actualIndustry = (String) IndexingUtils.getMetricForCategory(s, "in").get();

        assertEquals(expectedIndustry, actualIndustry);
    }

    @Test
    public void testGetMetricForCategoryPrice() {
        BigDecimal expectedPrice = new BigDecimal("342.24");
        Stock s = new Stock("random", expectedPrice);
        BigDecimal actualPrice = (BigDecimal) IndexingUtils.getMetricForCategory(s, "pr").get();

        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    public void testGetMetricForCategoryMarketCap() {
        Stock s = new Stock("random");
        BigDecimal expectedMarketCap = new BigDecimal("7890832");
        s.setMarketCap(expectedMarketCap);
        BigDecimal actualMarketCap = (BigDecimal) IndexingUtils.getMetricForCategory(s, "mc").get();

        assertEquals(expectedMarketCap, actualMarketCap);
    }

    @Test
    public void testGetMetricForCategorySalesGrowth() {
        Stock s = new Stock("random");
        BigDecimal expectedSalesGrowth = new BigDecimal(".7897");
        s.setSalesGrowth(expectedSalesGrowth);
        BigDecimal actualSalesGrowth = (BigDecimal) IndexingUtils.getMetricForCategory(s, "sg").get();

        assertEquals(expectedSalesGrowth, actualSalesGrowth);
    }

    @Test
    public void testGetMetricForCategoryNetIncome() {
        Stock s = new Stock("random");
        BigDecimal expectedNetIncome = new BigDecimal("23323");
        s.setNetIncome(expectedNetIncome);
        BigDecimal actualNetIncome = (BigDecimal) IndexingUtils.getMetricForCategory(s, "ni").get();

        assertEquals(expectedNetIncome, actualNetIncome);
    }

    @Test
    public void testGetMetricForCategoryPERatio() {
        Stock s = new Stock("random");
        BigDecimal expectedPERatio = new BigDecimal("24.23");
        s.setPERatio(expectedPERatio);
        BigDecimal actualPERatio = (BigDecimal) IndexingUtils.getMetricForCategory(s, "pe").get();

        assertEquals(expectedPERatio, actualPERatio);
    }

    @Test
    public void testGetMetricForCategoryNetDebtRatio() {
        Stock s = new Stock("random");
        BigDecimal expectedDebtRatio = new BigDecimal(".4000");
        s.setDebtRatio(expectedDebtRatio);
        BigDecimal actualDebtRatio = (BigDecimal) IndexingUtils.getMetricForCategory(s, "de").get();

        assertEquals(expectedDebtRatio, actualDebtRatio);
    }

    @Test
    public void testGetMetricForCategoryInvalidCategoryCausesException() {
        Stock s = new Stock("random");

        assertThrows(IllegalArgumentException.class, () -> {
            IndexingUtils.getMetricForCategory(s, "invalid").get();
        });
    }

}