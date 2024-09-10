package org.example.etfbuilder.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.etfbuilder.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PortfolioSceneController {

    private ETFBuilderApplication app;
    private IStockMarket stockMarket;
    private IPortfolio portfolio;
    private YearMonth startDate;
    @FXML
    private ComboBox<IETF> etfsInPortfolioComboBox;
    @FXML
    private Label etfValueLabel;
    @FXML
    private TextFlow textFlow;
    @FXML
    private LineChart<String, BigDecimal> returnsChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ToggleButton compareToMarketButton;
    @FXML
    private TableView<Map.Entry<String, BigDecimal>> etfHoldingsTable;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, String> companyColumn;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, BigDecimal> positionColumn;


    public void initializeScene(ETFBuilderApplication app, IStockMarket stockMarket,
                                IPortfolio portfolio, YearMonth startDate) {
        this.app = app;
        this.stockMarket = stockMarket;
        this.portfolio = portfolio;
        this.startDate = startDate;

        List<IETF> etfs = portfolio.getETFsInPortfolio();
        etfsInPortfolioComboBox.setStyle("-fx-font: 12px \"Arial\";");
        etfsInPortfolioComboBox.getItems().addAll(etfs);
        etfsInPortfolioComboBox.getSelectionModel().selectLast();

        companyColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKey()));
        companyColumn.setStyle("-fx-font: 12px \"Arial\";");
        positionColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValue()));
        positionColumn.setStyle("-fx-font: 12px \"Arial\";");
        etfHoldingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        displayETFReturns();
        displayETFHoldings();
    }

    @FXML
    public void displayReturns() {
        if (compareToMarketButton.isSelected()) {
            displayETFAndMarketReturns();
        } else {
            displayETFReturns();
        }
        displayETFHoldings();
    }

    private void displayETFHoldings() {
        IETF selectedETF = etfsInPortfolioComboBox.getValue();
        if (selectedETF == null) {
            return;
        }
        Map<String, BigDecimal> etfPositions = selectedETF.getETFPositions();
        ObservableList<Map.Entry<String, BigDecimal>> tableData =
                FXCollections.observableArrayList(etfPositions.entrySet());
        etfHoldingsTable.setItems(tableData);
        positionColumn.setSortType(TableColumn.SortType.DESCENDING);
        etfHoldingsTable.getSortOrder().add(positionColumn);
    }

    private void displayETFAndMarketReturns() {
        IETF selectedETF = etfsInPortfolioComboBox.getValue();
        if (selectedETF == null) {
            return;
        }
        Map<YearMonth, BigDecimal[]> etfReturns =
                portfolio.getETFReturns(selectedETF, startDate, IStockMarket.LAST_DATE_ENTRY);
        Map<YearMonth, BigDecimal> marketReturns =
                portfolio.getMarketReturns(stockMarket.getSP500Data(), startDate,
                        IStockMarket.LAST_DATE_ENTRY);
        XYChart.Series<String, BigDecimal> etfSeries = new XYChart.Series<>();
        etfSeries.setName("ETF");
        XYChart.Series<String, BigDecimal> marketSeries = new XYChart.Series<>();
        marketSeries.setName("S&P 500 Index");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        BigDecimal oneHundred = new BigDecimal(100);

        for (Map.Entry<YearMonth, BigDecimal[]> etfEntry : etfReturns.entrySet()) {
            YearMonth date = etfEntry.getKey();
            String formattedDate = formatter.format(date);
            BigDecimal etfReturnPercentage = etfEntry.getValue()[2].multiply(oneHundred);
            BigDecimal marketReturnPercentage = marketReturns.get(date).multiply(oneHundred);
            // add point to etf series
            XYChart.Data<String, BigDecimal> etfPoint =
                    new XYChart.Data<>(formattedDate, etfReturnPercentage);
            etfSeries.getData().add(etfPoint);
            // add point to market series
            XYChart.Data<String, BigDecimal> marketPoint =
                    new XYChart.Data<>(formattedDate, marketReturnPercentage);
            marketSeries.getData().add(marketPoint);
        }
        // clear old data in chart
        etfValueLabel.setText("ETF vs. S&P 500 Index Returns");
        etfValueLabel.setTextFill(Color.BLACK);
        textFlow.getChildren().clear();
        returnsChart.getData().clear();
        // add etf and market series to chart
        returnsChart.getData().add(etfSeries);
        returnsChart.getData().add(marketSeries);
        returnsChart.setLegendVisible(true);
        yAxis.setLabel("Returns (%)");
        // add disclaimer text for system generated etfs that have a reinvestment rate
        if (selectedETF.isSystemGenerated() &&
                ((SystemGeneratedETF) selectedETF).getReinvestmentRate() > 0) {
            Text text = new Text("*Note: ETF reinvestment rate of " +
                    ((SystemGeneratedETF) selectedETF).getReinvestmentRate() + " month(s)");
            text.setFill(Color.BLACK);
            text.setFont(Font.font("Arial", 12));
            textFlow.getChildren().add(text);
        }
    }

    private void displayETFReturns() {
        IETF selectedETF = etfsInPortfolioComboBox.getValue();
        if (selectedETF == null) {
            return;
        }
        Map<YearMonth, BigDecimal[]> etfReturns =
                portfolio.getETFReturns(selectedETF, startDate, IStockMarket.LAST_DATE_ENTRY);
        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();

        drawETFReturnsChart(series, etfReturns);
        formatText(series, etfReturns);
    }

    private void drawETFReturnsChart(XYChart.Series<String, BigDecimal> series,
                                     Map<YearMonth, BigDecimal[]> etfReturns) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        for (Map.Entry<YearMonth, BigDecimal[]> monthEntry : etfReturns.entrySet()) {
            YearMonth date = monthEntry.getKey();
            String formattedDate = date.format(formatter);
            BigDecimal currETFVal = monthEntry.getValue()[0];
            XYChart.Data<String, BigDecimal> newPoint = new XYChart.Data<>(formattedDate, currETFVal);
            series.getData().add(newPoint);
        }
        returnsChart.getData().clear();
        returnsChart.getData().add(series);
        returnsChart.setLegendVisible(false);
        yAxis.setLabel("ETF Value ($)");
    }

    private void formatText(XYChart.Series<String, BigDecimal> series,
                            Map<YearMonth, BigDecimal[]> etfReturns) {
        // set up labels and chart coloring
        BigDecimal etfValue = etfReturns.get(IStockMarket.LAST_DATE_ENTRY)[0];
        BigDecimal returnsDollars = etfReturns.get(IStockMarket.LAST_DATE_ENTRY)[1];
        BigDecimal returnsPercentage =
                etfReturns.get(IStockMarket.LAST_DATE_ENTRY)[2].multiply(new BigDecimal(100));
        String valueStr = String.format("$%,.2f", etfValue);
        etfValueLabel.setText(valueStr);
        Text returnsText = null;
        if (returnsDollars.compareTo(BigDecimal.ZERO) >= 0) {
            etfValueLabel.setTextFill(Color.DARKGREEN);
            returnsText = new Text(String.format("+ $%,.2f (%.2f%%)", returnsDollars, returnsPercentage));
            returnsText.setFill(Color.GREEN);
            series.getNode().setStyle("-fx-stroke: green;");
        } else {
            etfValueLabel.setTextFill(Color.DARKRED);
            returnsText = new Text(String.format("- $%,.2f (%.2f%%)", returnsDollars, returnsPercentage));
            returnsText.setFill(Color.RED);
            series.getNode().setStyle("-fx-stroke: red;");
        }
        returnsText.setFont(Font.font("Arial", 14));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        Text dateText = new Text("  " + IStockMarket.LAST_DATE_ENTRY.format(formatter));
        dateText.setFill(Color.BLACK);
        dateText.setFont(Font.font("Arial", 14));

        textFlow.getChildren().clear();
        textFlow.getChildren().add(returnsText);
        textFlow.getChildren().add(dateText);
    }

    @FXML
    public void switchToCreateETFScene() {
        app.switchToCreateETFMainScene();
    }

}
