package org.example.etfbuilder.interfaces;

import org.example.etfbuilder.Stock;

import java.util.Set;

public interface IStockIndexer {


    /**
     * Indexes stocks with the specified field in the search key
     *
     * @param strategy  The IIndexStrategy to utilize for searching
     * @param stocks    The set of stocks to perform the search on
     * @param searchKey A string concatenation of the field category and the value to search for
     * @return The set of stocks that fit the search key
     */
    Set<Stock> index(IIndexStrategy strategy, Set<Stock> stocks, String searchKey);

    /**
     * Performs a multidimensional index on a set of stocks using inputted search keys
     *
     * @param stocks     The set of stocks to perform the search on
     * @param searchKeys A set of search keys, each querying a specific metric. Each search key is
     *                   a concatenation of the metric category and the metric value to look for.
     * @return The set of stocks (alphabetically ordered  by company name) that satisfy all the
     * indexing criteria
     */
    Set<Stock> multiDimensionalIndex(Set<Stock> stocks, Set<String> searchKeys);


}
