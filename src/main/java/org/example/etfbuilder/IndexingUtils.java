package org.example.etfbuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for accessing metrics of Stock object. This utility class is used to assist
 * in the indexing of stocks that contain the inputted characteristics.
 */
public class IndexingUtils {

    /**
     * Category prefixes corresponding to different types of Stock metrics
     */
    public static final String NAME_PREFIX = "na";
    public static final String PRICE_PREFIX = "pr";
    public static final String INDUSTRY_PREFIX = "in";
    public static final String MARKET_CAP_PREFIX = "mc";
    public static final String SALES_GROWTH_PREFIX = "sg";
    public static final String NET_INCOME_PREFIX = "ni";
    public static final String PE_RATIO_PREFIX = "pe";
    public static final String DEBT_RATIO_PREFIX = "de";
    public static final Integer PREFIX_LENGTH = 2;

    public static final Set<String> STRING_PREFIXES = new HashSet<>
            (Arrays.asList(NAME_PREFIX, INDUSTRY_PREFIX));
    public static final Set<String> NUMERICAL_PREFIXES = new HashSet<>
            (Arrays.asList(PRICE_PREFIX, MARKET_CAP_PREFIX, SALES_GROWTH_PREFIX, NET_INCOME_PREFIX,
                    PE_RATIO_PREFIX, DEBT_RATIO_PREFIX));

    /**
     * Helper function for getMetricForCategory()
     * <p>
     * Creates and returns a Supplier object that retrieves the specified metric from
     * a given Stock object
     *
     * @param stock  the Stock object for which a metric is being obtained
     * @param getter the Stock object's getter function that retrieves the metric
     * @return A Supplier that when invoked returns the value of the metric from stock
     */
    public static <Stock, F> Supplier<F> getMetricSupplier(Stock stock, Function<Stock, F> getter) {
        return () -> getter.apply(stock);
    }

    /**
     * Creates and returns a Supplier object that retrieves the specified metric
     * from a given Stock object
     *
     * @param s        the Stock object for which a metric is being obtained
     * @param category the abbreviation of the metric we want to obtain:
     *                 na: name,
     *                 pr: price,
     *                 in: industry,
     *                 mc: market capitalization,
     *                 sg: sales growth,
     *                 ni: net income,
     *                 pe: PE ratio,
     *                 de: net debt ratio,
     * @return A Supplier that when invoked returns the value of the category metric
     * of s
     * @throws IllegalArgumentException if an invalid category is entered
     */
    public static Supplier<?> getMetricForCategory(Stock s, String category) {
        switch (category) {
            case (NAME_PREFIX):
                return getMetricSupplier(s, stock -> s.getName());
            case (INDUSTRY_PREFIX):
                return getMetricSupplier(s, stock -> s.getIndustry());
            case (PRICE_PREFIX):
                return getMetricSupplier(s, stock -> s.getPrice());
            case (MARKET_CAP_PREFIX):
                return getMetricSupplier(s, stock -> s.getMarketCap());
            case (SALES_GROWTH_PREFIX):
                return getMetricSupplier(s, stock -> s.getSalesGrowth());
            case (NET_INCOME_PREFIX):
                return getMetricSupplier(s, stock -> s.getNetIncome());
            case (PE_RATIO_PREFIX):
                return getMetricSupplier(s, stock -> s.getPERatio());
            case (DEBT_RATIO_PREFIX):
                return getMetricSupplier(s, stock -> s.getDebtRatio());
            default:
                throw new IllegalArgumentException("Invalid category prefix");
        }
    }

}
