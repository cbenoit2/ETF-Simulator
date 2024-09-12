package org.example.etfbuilder.interfaces;

import org.example.etfbuilder.Stock;

import java.util.Set;

public interface IIndexStrategy {

    /**
     * @param stocksToIndex The set of stocks to perform the search on
     * @param searchKey     A concatenation of the metric category and the metric value to look
     *                      for
     * @return The set of stocks that fit the specified search key value
     */
    Set<Stock> indexStocks(Set<Stock> stocksToIndex, String searchKey);

}

