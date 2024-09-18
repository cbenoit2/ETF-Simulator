package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IDataParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class DataParser implements IDataParser {

    @Override
    public Map<YearMonth, Map<String, Stock>> parseStockData(String csvFile) throws IOException {
        Map<YearMonth, Map<String, Stock>> stockData = new HashMap<>();
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
                stockData.get(date).put(companyName.toLowerCase(), stock);
                datesIndex++;
            }
        }

        return stockData;
    }

    @Override
    public Stock createStockFromData(String name, String[] data) {
        String industry = data[0];
        BigDecimal price = new BigDecimal(data[1]).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netIncome = new BigDecimal(data[2]);
        BigDecimal marketCap = new BigDecimal(data[3]);
        BigDecimal peRatio = new BigDecimal(data[4]);
        BigDecimal salesGrowth = new BigDecimal(data[5]);
        BigDecimal debtRatio = new BigDecimal(data[6]);

        Stock newStock = new Stock(name, price);
        newStock.setIndustry(industry);
        newStock.setNetIncome(netIncome);
        newStock.setMarketCap(marketCap);
        newStock.setPERatio(peRatio);

        BigDecimal divisor = new BigDecimal(100);
        newStock.setSalesGrowth(salesGrowth.divide(divisor, 4, RoundingMode.HALF_UP));
        newStock.setDebtRatio(debtRatio.divide(divisor, 4, RoundingMode.HALF_UP));
        return newStock;
    }

    @Override
    public Map<YearMonth, BigDecimal> parseSP500MarketData(String csvFile) throws IOException {
        Map<YearMonth, BigDecimal> sp500 = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader((csvFile)));
        String line = reader.readLine();
        String[] dates = line.split(",");
        line = reader.readLine();
        String[] historicalSPValue = line.split(",");

        int datesIndex = 1;
        for (int i = 1; i < historicalSPValue.length; i++) {
            BigDecimal value = new BigDecimal(historicalSPValue[i])
                    .setScale(2, RoundingMode.HALF_UP);
            YearMonth date = YearMonth.parse(dates[datesIndex]);
            sp500.put(date, value);
            datesIndex++;
        }
        return sp500;
    }


}