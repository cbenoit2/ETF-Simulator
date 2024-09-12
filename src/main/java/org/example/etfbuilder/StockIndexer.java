package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IIndexStrategy;
import org.example.etfbuilder.interfaces.IStockIndexer;

import java.util.Set;
import java.util.TreeSet;

public class StockIndexer implements IStockIndexer {

    @Override
    public Set<Stock> index(IIndexStrategy strategy, Set<Stock> stocks, String searchKey) {
        return strategy.indexStocks(stocks, searchKey);
    }

    @Override
    public Set<Stock> multiDimensionalIndex(Set<Stock> stocks, Set<String> searchKeys) {
        Set<Stock> results = new TreeSet<>(new Stock.StockNameComparator());
        for (String searchKey : searchKeys) {
            String category = searchKey.toLowerCase().substring(0, IndexingUtils.PREFIX_LENGTH);
            if (IndexingUtils.STRING_PREFIXES.contains(category)) {
                stocks = index(new IndexOnStringField(), stocks, searchKey);
            } else {
                stocks = index(new IndexOnNumericalField(), stocks, searchKey);
            }
        }
        results.addAll(stocks);

        return results;
    }

}
