package org.example.etfbuilder;

import org.example.etfbuilder.interfaces.IStockMarket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserCreatedETFTest {

    private static YearMonth startDate;
    private static IStockMarket market;

    @BeforeAll
    public static void parseData() throws IOException {
        DataParser dp = new DataParser();
        String stocksCSV = UserCreatedETFTest.class
                .getResource("data/medium_stock_data_sample.csv").getFile();
        String sp500CSV = UserCreatedETFTest.class.getResource("data/S&P500_value.csv").getFile();
        market = new StockMarket(dp.parseStockData(stocksCSV),
                dp.parseSP500MarketData(sp500CSV));
        startDate = YearMonth.parse("2023-06");
    }

    @Test
    public void testUserCreatedETFConstructor() {
        UserCreatedETF userETF = new UserCreatedETF(market, startDate);

        assertFalse(userETF.isSystemGenerated());
        assertEquals(startDate, userETF.getStartDate());
        assertEquals(new HashMap<>(), userETF.getETFPositions());
    }

    @Test
    public void testUserCreatedETFConstructorNullStockMarket() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserCreatedETF(null, startDate);
        });
    }

    @Test
    public void testUserCreatedETFConstructorNullStartDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserCreatedETF(market, null);
        });
    }

    @Test
    public void testUserCreatedETFConstructorInvalidStartDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserCreatedETF(market, startDate.minusYears(1));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new UserCreatedETF(market, startDate.plusYears(1));
        });
    }

}