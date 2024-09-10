package org.example.etfbuilder.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.textfield.TextFields;
import org.example.etfbuilder.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class CreateUserETFSceneController {

    private ETFBuilderApplication app;
    private IStockMarket stockMarket;
    private IStockIndexer indexer;
    private YearMonth startDate;
    @FXML
    private TextField companyNameText;
    @FXML
    private SearchableComboBox<String> industryComboBox;
    @FXML
    private TextField minMarketCapText, maxMarketCapText;
    @FXML
    private TextField minNetDebtText, maxNetDebtText;
    @FXML
    private TextField minNetIncomeText, maxNetIncomeText;
    @FXML
    private TextField minSalesGrowthText, maxSalesGrowthText;
    @FXML
    private TextField minSharePriceText, maxSharePriceText;
    @FXML
    private TextField minPERatioText, maxPERatioText;
    @FXML
    private ListView<Stock> stockResultsList;
    @FXML
    private TableView<ChosenStocksTableData> chosenStocksTable;
    @FXML
    private Label totalValueLabel;
    @FXML
    private TableColumn<ChosenStocksTableData, String> nameColumn;
    @FXML
    private TableColumn<ChosenStocksTableData, Integer> quantityColumn;
    @FXML
    private TableColumn<ChosenStocksTableData, BigDecimal> totalColumn;

    public void initializeScene(ETFBuilderApplication app, IStockIndexer indexer,
                                IStockMarket stockMarket, YearMonth startDate) {
        this.app = app;
        this.indexer = indexer;
        this.stockMarket = stockMarket;
        this.startDate = startDate;

        // set up combobox with industries
        industryComboBox.setStyle("-fx-font: 12px \"Arial\";");
        industryComboBox.getItems().addAll(ETFBuilderApplication.INDUSTRIES);
        industryComboBox.getSelectionModel().selectFirst();
        // set up text fields to format text entered by user
        TextField[] toFormat = new TextField[]{minSharePriceText, maxSharePriceText,
                minNetIncomeText, maxNetIncomeText, minMarketCapText, maxMarketCapText,
                minPERatioText, maxPERatioText, minSalesGrowthText, maxSalesGrowthText,
                minNetDebtText, maxNetDebtText};
        //todo format text box after numbers are entered
//        for (TextField textBox : toFormat) {
//            textBox.textProperty().addListener((observable, oldValue, newValue) -> {
//                DecimalFormat format = new DecimalFormat("#,###.##");
//                String formattedStr;
//                if (newValue.matches("\\d*")) {
//                    formattedStr = format.format(Double.parseDouble(newValue));
//                } else {
//                    formattedStr = newValue.replaceAll("[^\\d]", "");
//                    formattedStr = format.format(Double.parseDouble(formattedStr));
//                }
//                textBox.setText(formattedStr);
//            });
//        }

        // add all stocks to stock results ListView
        Set<Stock> allStocks = new TreeSet<>(new Stock.StockNameComparator());
        allStocks.addAll(stockMarket.getStocksOnDate(startDate));
        addStocksToListView(allStocks);
        // create listener for stock results ListView
        stockResultsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stock>() {
            @Override
            public void changed(ObservableValue<? extends Stock> observableValue, Stock oldValue, Stock newValue) {
                if (newValue != null) {
                    switchToStockInfoPopUp();
                }
            }
        });
        // add auto complete feature for company name text field
        TextFields.bindAutoCompletion(companyNameText, stockMarket.getAllCompanyNames(startDate));
        // initialize columns in table for stocks chosen to be in etf
        nameColumn.setCellValueFactory(new PropertyValueFactory<ChosenStocksTableData, String>("companyName"));
        nameColumn.setStyle("-fx-font: 12px \"Arial\";");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<ChosenStocksTableData, Integer>("quantity"));
        quantityColumn.setStyle("-fx-font: 12px \"Arial\";");
        totalColumn.setCellValueFactory(new PropertyValueFactory<ChosenStocksTableData, BigDecimal>("totalDollars"));
        totalColumn.setStyle("-fx-font: 12px \"Arial\";");
        chosenStocksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addStocksToListView(Set<Stock> toAdd) {
        stockResultsList.getItems().clear();
        stockResultsList.getItems().addAll(toAdd);
    }

    @FXML
    public void indexStocks() {
        Set<String> searchKeys = new HashSet<>();
        String field = industryComboBox.getValue();
        if (!field.equals("All")) {
            searchKeys.add(IndexingUtils.INDUSTRY_PREFIX + field);
        }
        field = companyNameText.getText();
        if (!field.isEmpty()) {
            searchKeys.add(IndexingUtils.NAME_PREFIX + field);
        }
        String[] formattedKeys = new String[6];
        formattedKeys[0] = formatSearchKey(IndexingUtils.PRICE_PREFIX, minSharePriceText.getText(),
                maxSharePriceText.getText());
        formattedKeys[1] = formatSearchKey(IndexingUtils.NET_INCOME_PREFIX, minNetIncomeText.getText(),
                minNetIncomeText.getText());
        formattedKeys[2] = formatSearchKey(IndexingUtils.MARKET_CAP_PREFIX, minMarketCapText.getText(),
                minMarketCapText.getText());
        formattedKeys[3] = formatSearchKey(IndexingUtils.SALES_GROWTH_PREFIX, minSalesGrowthText.getText(),
                maxSalesGrowthText.getText());
        formattedKeys[4] = formatSearchKey(IndexingUtils.PE_RATIO_PREFIX, minPERatioText.getText(),
                maxPERatioText.getText());
        formattedKeys[5] = formatSearchKey(IndexingUtils.DEBT_RATIO_PREFIX, minNetDebtText.getText(),
                maxNetDebtText.getText());

        for (String key : formattedKeys) {
            if (!key.substring(IndexingUtils.PREFIX_LENGTH).
                    equals(Double.MIN_VALUE + "_" + Double.MAX_VALUE)) {
                searchKeys.add(key);
            }
        }
        Set<Stock> results = indexer.multiDimensionalIndex(stockMarket.getStocksOnDate(startDate), searchKeys);
        addStocksToListView(results);
    }

    private String formatSearchKey(String prefix, String min, String max) {
        if (min.isEmpty()) {
            min = "" + Double.MIN_VALUE;
        } else {
            min = min.replace(",", "");
            if (prefix.equals(IndexingUtils.SALES_GROWTH_PREFIX)) {
                min = "" + new BigDecimal(min).divide(new BigDecimal(100), 4,
                        RoundingMode.HALF_UP);
            }
        }

        if (max.isEmpty()) {
            max = "" + Double.MAX_VALUE;
        } else {
            max = max.replace(",", "");
            if (prefix.equals(IndexingUtils.SALES_GROWTH_PREFIX)) {
                max = "" + new BigDecimal(max).divide(new BigDecimal(100), 4,
                        RoundingMode.HALF_UP);
            }
        }
        return prefix + min + "_" + max;
    }

    @FXML
    public void switchToStockInfoPopUp() {
        Stock selectedStock = stockResultsList.getSelectionModel().getSelectedItem();
        app.switchToStockInfoPopUp(selectedStock, this);
    }

    public void addStockToChosenStocksTable(Stock stock, int quantity) {
        chosenStocksTable.getItems().add(new ChosenStocksTableData(stock, quantity));
        updateTotalETFValueLabel();
    }

    @FXML
    public void updateTotalETFValueLabel() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        BigDecimal totalETFValue = BigDecimal.ZERO;
        for (ChosenStocksTableData data : chosenStocksTable.getItems()) {
            BigDecimal val = data.getTotalDollars();
            totalETFValue = totalETFValue.add(val);
        }
        totalValueLabel.setText("Total: " + currencyFormatter.format(totalETFValue));
    }

    @FXML
    public void createETF() {
        IETF etf = new UserCreatedETF(stockMarket, startDate);
        for (ChosenStocksTableData data : chosenStocksTable.getItems()) {
            String companyName = data.getCompanyName();
            BigDecimal total = data.getTotalDollars();
            etf.addStock(companyName, total, startDate);
        }
        app.addETFToPortfolio(etf);
        app.switchToPortfolioScene();
    }

    @FXML
    public void switchToCreateETFScene() {
        app.switchToCreateETFMainScene();
    }

    @FXML
    public void switchToPortfolioScene() {
        app.switchToPortfolioScene();
    }

    public static class ChosenStocksTableData {
        private final Stock stock;
        private final String companyName;
        private int quantity;
        private BigDecimal totalDollars;

        public ChosenStocksTableData(Stock stock, int quantity) {
            this.stock = stock;
            this.companyName = stock.getName();
            this.quantity = quantity;
            this.totalDollars = stock.getPrice().multiply(
                    new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
        }

        public String getCompanyName() {
            return this.companyName;
        }

        public void setQuantity(int quantity) {
            if (quantity < 0) {
                return;
            }
            totalDollars = stock.getPrice().multiply(new BigDecimal(quantity));
            this.quantity = quantity;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public BigDecimal getTotalDollars() {
            return this.totalDollars;
        }


    }

}
