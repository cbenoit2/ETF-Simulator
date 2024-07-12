package org.example.etfbuilder;

import java.util.*;

public class StockIndexer implements IStockIndexer {

    @Override
    public Set<Stock> index(IIndexStrategy strategy, Set<Stock> stocks, String searchKey) {
        return strategy.indexStocks(stocks, searchKey);
    }

    @Override
    public Set<Stock> multiDimensionalIndex(Set<Stock> stocks, Set<String> searchKeys) {
        Set<Stock> results = new TreeSet<>(Comparator.comparing(Stock::getName));
        for (String searchKey : searchKeys) {
            String category = searchKey.toLowerCase().substring(0, IndexingUtils.KEY_LENGTH);
            if (IndexingUtils.STRING_KEYS.contains(category)) {
                stocks = index(new IndexOnStringField(), stocks, searchKey);
            } else {
                stocks = index(new IndexOnNumericalField(), stocks, searchKey);
            }
        }
        results.addAll(stocks);

        return results;
    }

}
