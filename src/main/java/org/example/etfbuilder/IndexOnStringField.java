package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IIndexStrategy;

import java.util.*;
import java.util.function.Supplier;

public class IndexOnStringField implements IIndexStrategy {

    @Override
    public Set<Stock> indexStocks(Set<Stock> stocksToIndex, String searchKey) {
        // creates an index of the stocks based on the search key category, 
        // then looks for specified string value in the search key
        if (searchKey.length() < IndexingUtils.PREFIX_LENGTH) {
            throw new IllegalArgumentException("invalid search key");
        }

        String category = searchKey.toLowerCase().substring(0, IndexingUtils.PREFIX_LENGTH);
        String valueSought = searchKey.toLowerCase().substring(IndexingUtils.PREFIX_LENGTH);

        Set<Stock> results = new HashSet<>();
        for (Stock stock : stocksToIndex) {
            Supplier<?> metricGetter = IndexingUtils.getMetricForCategory(stock, category);
            String metric = ((String) metricGetter.get()).toLowerCase();
            if (metric.equals(valueSought)) {
                results.add(stock);
            }
        }
        return results;
    }

}
