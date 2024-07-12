package org.example.etfbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.YearMonth;
import java.util.*;

public class DataParser implements IDataParser {

    @Override
    public Map<YearMonth, Map<String, Stock>> parseStockData(String csvFile) {
        Map<YearMonth, Map<String, Stock>> stockData = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader((csvFile)));
            String line = reader.readLine();
            String[] dates = line.split(",");

            while ((line = reader.readLine()) != null) {
                String[] historicalCompanyData = line.split(",");
                int datesIndex = 1;
                String companyName = historicalCompanyData[0];
                for (int i = 1; i < historicalCompanyData.length; i++) {
                    String[] splitData = historicalCompanyData[i].split("_");
                    Stock stock = createStockFromData(companyName, splitData);
                    YearMonth date = YearMonth.parse(dates[datesIndex]);
                    stockData.putIfAbsent(date, new HashMap<>());
                    stockData.get(date).put(companyName, stock);
                    datesIndex++;
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot find file " + csvFile);
            return new HashMap<>();
        }
        return stockData;
    }

    @Override
    public Stock createStockFromData(String name, String[] data) {
        String industry = data[0];
        double price = Double.parseDouble(data[1]);
        double netIncome = Double.parseDouble(data[2]);
        double marketCap = Double.parseDouble(data[3]);
        double peRatio = Double.parseDouble(data[4]);
        double salesGrowth = Double.parseDouble(data[5]);
        double debtToEquity = Double.parseDouble(data[6]);

        Stock newStock = new Stock(name, price);
        newStock.setIndustry(industry);
        newStock.setNetIncome(netIncome);
        newStock.setMarketCap(marketCap);
        newStock.setPERatio(peRatio);
        newStock.setSalesGrowth(salesGrowth / 100);
        newStock.setDebtRatio(debtToEquity / 100);
        return newStock;
    }

    @Override
    public Map<YearMonth, Double> parseSP500MarketData(String csvFile) {
        Map<YearMonth, Double> sp500 = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader((csvFile)));
            String line = reader.readLine();
            String[] dates = line.split(",");
            line = reader.readLine();
            String[] historicalSPValue = line.split(",");

            int datesIndex = 1;
            for (int i = 1; i < historicalSPValue.length; i++) {
                double value = Double.parseDouble(historicalSPValue[i]);
                YearMonth date = YearMonth.parse(dates[datesIndex]);
                sp500.put(date, value);
                datesIndex++;
            }
        } catch (IOException e) {
            System.err.println("Cannot find file " + csvFile);
            return new HashMap<>();
        }
        return sp500;
    }


}