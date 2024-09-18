package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IIndexStrategy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class IndexOnNumericalField implements IIndexStrategy {

    @Override
    public Set<Stock> indexStocks(Set<Stock> stocksToIndex, String searchKey) {
        // creates an index of the stocks based on the search key category, 
        // then looks for stocks in the range of values specified in the search key
        String category = searchKey.toLowerCase().substring(0, IndexingUtils.PREFIX_LENGTH);
        String[] range = searchKey.substring(IndexingUtils.PREFIX_LENGTH).split("_");
        if (range.length < 2) {
            throw new IllegalArgumentException("search key does not have a range");
        }

        BigDecimal begIndexKey = new BigDecimal(range[0]);
        BigDecimal endIndexKey = new BigDecimal(range[1]);
        if (begIndexKey.compareTo(endIndexKey) > 0) {
            throw new IllegalArgumentException("search key does not have a valid range");
        }

        Set<Stock> results = new HashSet<>();
        for (Stock stock : stocksToIndex) {
            Supplier<?> metricGetter = IndexingUtils.getMetricForCategory(stock, category);
            BigDecimal metricValue = (BigDecimal) metricGetter.get();
            if (metricValue.compareTo(begIndexKey) >= 0 && metricValue.compareTo(endIndexKey) <= 0) {
                results.add(stock);
            }
        }
        return results;
    }

}

