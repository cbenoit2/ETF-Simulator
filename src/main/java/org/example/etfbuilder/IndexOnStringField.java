package org.example.etfbuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class IndexOnStringField implements IIndexStrategy {

    @Override
    public Set<Stock> indexStocks(Set<Stock> stocks, String searchKey) {
        // creates an index of the stocks based on the search key category, 
        // then looks for specified string value in the search key
        String category = searchKey.toLowerCase().substring(0, IndexingUtils.KEY_LENGTH);
        if (!IndexingUtils.STRING_KEYS.contains(category)) {
            throw new IllegalArgumentException
                    ("search key does not contain proper category prefix");
        }
        String valueSought = searchKey.toLowerCase().substring(IndexingUtils.KEY_LENGTH);
        Map<String, Set<Stock>> indexMap = new HashMap<>();
        for (Stock stock : stocks) {
            Supplier<?> fieldGetter = IndexingUtils.getMetricForCategory(stock, category);
            String field = (String) fieldGetter.get();
            Set<Stock> stockSet = indexMap.getOrDefault(field.toLowerCase(), new HashSet<>());
            stockSet.add(stock);
            indexMap.put(field.toLowerCase(), stockSet);
        }
        return indexMap.getOrDefault(valueSought, new HashSet<>());
    }

}
