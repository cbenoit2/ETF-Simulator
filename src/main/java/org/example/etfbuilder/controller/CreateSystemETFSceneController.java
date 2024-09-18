package org.example.etfbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;
import org.example.etfbuilder.ETFAlgorithm;
import org.example.etfbuilder.ETFBuilderApplication;
import org.example.etfbuilder.SystemGeneratedETF;
import org.example.etfbuilder.interfaces.IETF;
import org.example.etfbuilder.interfaces.IETFAlgorithm;
import org.example.etfbuilder.interfaces.IStockMarket;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.YearMonth;

public class CreateSystemETFSceneController {


    private ETFBuilderApplication app;
    private IStockMarket stockMarket;
    private YearMonth startDate;
    @FXML
    private Spinner<Integer> netDebtSpinner;
    @FXML
    private Spinner<Integer> netIncomeSpinner;
    @FXML
    private Spinner<Integer> marketCapSpinner;
    @FXML
    private Spinner<Integer> peRatioSpinner;
    @FXML
    private Spinner<Integer> salesGrowthSpinner;
    @FXML
    private Spinner<Integer> reinvestmentRateSpinner;
    @FXML
    private SearchableComboBox<String> industryComboBox;
    @FXML
    private TextField totalToInvestTextBox;

    public void initializeScene(ETFBuilderApplication app, IStockMarket stockMarket, YearMonth startDate) {
        this.app = app;
        this.stockMarket = stockMarket;
        this.startDate = startDate;

        // set up spinners for preference weights
        Spinner<Integer>[] prefSpinners = new Spinner[]{netDebtSpinner, netIncomeSpinner,
                marketCapSpinner, peRatioSpinner, salesGrowthSpinner};
        for (Spinner<Integer> spinner : prefSpinners) {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100);
            valueFactory.setValue(20);
            spinner.setValueFactory(valueFactory);
        }
        // set up spinner for reinvestment rate
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 180);
        valueFactory.setValue(0);
        reinvestmentRateSpinner.setValueFactory(valueFactory);
        // set up text field for total to invest
        totalToInvestTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            DecimalFormat format = new DecimalFormat("#,###");
            String formattedStr;
            if (newValue.matches("\\d*")) {
                formattedStr = format.format(Double.parseDouble(newValue));
            } else {
                formattedStr = newValue.replaceAll("[^\\d]", "");
                formattedStr = format.format(Double.parseDouble(formattedStr));
            }
            totalToInvestTextBox.setText(formattedStr);
        });
        // set up combobox with industries
        industryComboBox.setStyle("-fx-font: 14px \"Arial\";");
        industryComboBox.getItems().addAll(ETFBuilderApplication.INDUSTRIES);
        industryComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void createETF() {
        BigDecimal dollarsToInvest =
                new BigDecimal(totalToInvestTextBox.getText().replace(",", ""));
        int reinvestmentRate = reinvestmentRateSpinner.getValue();
        String industry = industryComboBox.getValue();
        double[] preferences = new double[5];
        preferences[0] = netDebtSpinner.getValue() / 100.0;
        preferences[1] = netIncomeSpinner.getValue() / 100.0;
        preferences[2] = marketCapSpinner.getValue() / 100.0;
        preferences[3] = peRatioSpinner.getValue() / 100.0;
        preferences[4] = salesGrowthSpinner.getValue() / 100.0;

        IETFAlgorithm algo = new ETFAlgorithm(stockMarket, startDate, preferences, industry);
        IETF etf = new SystemGeneratedETF(stockMarket, algo, dollarsToInvest, reinvestmentRate);
        app.addETFToPortfolio(etf);
    }

    @FXML
    public void switchToCreateETFScene() {
        app.switchToCreateETFMainScene();
    }

    @FXML
    public void switchToPortfolioScene() {
        app.switchToPortfolioScene();
    }


}

