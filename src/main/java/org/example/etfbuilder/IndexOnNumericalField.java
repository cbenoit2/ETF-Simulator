package org.example.etfbuilder;

import java.util.*;
import java.util.function.Supplier;

public class IndexOnNumericalField implements IIndexStrategy {

    @Override
    public Set<Stock> indexStocks(Set<Stock> stocks, String searchKey) {
        // creates an index of the stocks based on the search key category, 
        // then looks for stocks in the range of values specified in the search key
        Set<Stock> results = new HashSet<>();
        String category = searchKey.toLowerCase().substring(0, IndexingUtils.KEY_LENGTH);
        if (!IndexingUtils.NUMERICAL_KEYS.contains(category)) {
            throw new IllegalArgumentException
                    ("search key does not contain proper category prefix");
        }
        String valuesSought = searchKey.toLowerCase().substring(IndexingUtils.KEY_LENGTH);
        TreeMap<Double, Set<Stock>> indexMap = new TreeMap<>();
        for (Stock stock : stocks) {
            Supplier<?> fieldGetter = IndexingUtils.getMetricForCategory(stock, category);
            double field = (double) fieldGetter.get();
            Set<Stock> stockSet = indexMap.getOrDefault(field, new HashSet<>());
            stockSet.add(stock);
            indexMap.put(field, stockSet);
        }
        String[] range = valuesSought.split(" - ");
        if (range.length < 2) {
            throw new IllegalArgumentException("search key does not contain a range");
        }
        // get the stocks in the range of values that is specified by the key 
        // the range (with a tolerance of .001 allowed)
        Double begIndexKey = indexMap.ceilingKey(Double.parseDouble(range[0]) - .001);
        Double endIndexKey = indexMap.floorKey(Double.parseDouble(range[1]) + .001);
        if (begIndexKey != null && endIndexKey != null && begIndexKey <= endIndexKey) {
            Collection<Set<Stock>> stockSets = indexMap.subMap(begIndexKey, true,
                    endIndexKey, true).values();
            stockSets.forEach(set -> results.addAll(set));
        }
        return results;
    }

}

