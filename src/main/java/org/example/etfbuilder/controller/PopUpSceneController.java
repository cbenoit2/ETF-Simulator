package org.example.etfbuilder.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.etfbuilder.Stock;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.Map;

public class PopUpSceneController {

    private Stage stage;
    private CreateUserETFSceneController controller;
    private Stock stock;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private TableView<Map.Entry<String, String>> stockInfoTable;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> stockMetricName;
    @FXML
    private TableColumn<Map.Entry<String, String>, String> stockMetricValue;


    public void initializeScene(Stage stage, CreateUserETFSceneController controller, Stock stock) {
        this.stage = stage;
        this.controller = controller;
        this.stock = stock;

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000);
        valueFactory.setValue(0);
        quantitySpinner.setValueFactory(valueFactory);

        stockMetricName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey()));
        stockMetricName.setText(stock.getName());
        stockMetricName.setStyle("-fx-font: 12px \"Arial\";");
        stockMetricValue.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValue()));
        stockMetricValue.setStyle("-fx-font: 12px \"Arial\";");
        stockInfoTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        createStockInfoTable();
    }

    private void createStockInfoTable() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        ObservableList<Map.Entry<String, String>> tableData =
                FXCollections.observableArrayList();
        tableData.add(new AbstractMap.SimpleEntry<>("Industry", stock.getIndustry()));
        tableData.add(new AbstractMap.SimpleEntry<>("Share Price",
                currencyFormatter.format(stock.getPrice())));
        tableData.add(new AbstractMap.SimpleEntry<>("Net Income",
                currencyFormatter.format(stock.getNetIncome())));
        tableData.add(new AbstractMap.SimpleEntry<>("Market Capitalization",
                currencyFormatter.format(stock.getMarketCap())));
        tableData.add(new AbstractMap.SimpleEntry<>("Sales Growth",
                String.format("%.2f%%", stock.getSalesGrowth().multiply(new BigDecimal(100)))));
        tableData.add(new AbstractMap.SimpleEntry<>("P/E Ratio",
                String.format("%.2f", stock.getPERatio())));
        tableData.add(new AbstractMap.SimpleEntry<>("Net Debt - Common Ratio",
                String.format("%.2f", stock.getDebtRatio())));

        stockInfoTable.setItems(tableData);
    }

    @FXML
    public void addStockToChosenStocksTable() {
        int quantity = quantitySpinner.getValue();
        if (quantity > 0) {
            controller.addStockToChosenStocksTable(stock, quantity);
        }
        closePopUp();
    }

    @FXML
    public void closePopUp() {
        stage.close();
    }


}
