package org.example.etfbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.etfbuilder.controller.*;

import java.io.IOException;
import java.time.YearMonth;


public class ETFBuilderApplication extends Application {

    // todo: error handling, validation, 
    //  code cleanup, make comments, documentation, 
    //  testing
    //  organize folders
    //  how to scale everything

    public static final String[] INDUSTRIES = new String[]{"All", "Communication Services",
            "Consumer Discretionary", "Consumer Staples", "Energy", "Financials", "Health Care",
            "Industrials", "Information Technology", "Materials", "Real Estate", "Utilities"};

    private Stage stage;
    private IStockMarket stockMarket;
    private IPortfolio portfolio;
    private IStockIndexer indexer;
    private YearMonth startDate;

    @Override
    public void start(Stage stage) throws IOException {
        IDataParser dp = new DataParser();
        String stockCSVFile = getClass().getResource("data/combined_stock_data.csv").getFile();
        String sp500CSVFile = getClass().getResource("data/S&P500_value.csv").getFile();
        this.stockMarket = new StockMarket(dp.parseStockData(stockCSVFile),
                dp.parseSP500MarketData(sp500CSVFile));
        this.portfolio = new Portfolio();
        this.indexer = new StockIndexer();
        this.stage = stage;
        switchToStartScene();
    }

    public void switchToStartScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/SimulationStartScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StartSceneController controller = loader.getController();
        controller.initializeScene(this);
        stage.setScene(new Scene(root));
        stage.setTitle("ETF Builder");
        stage.show();
    }

    public void switchToCreateETFMainScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CreateETFScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CreateETFSceneController controller = loader.getController();
        controller.initializeScene(this);
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
    }

    public void switchToCreateSystemETFScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CreateSystemETFScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CreateSystemETFSceneController controller = loader.getController();
        controller.initializeScene(this, stockMarket, startDate);
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
    }

    public void switchToCreateUserETFScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CreateUserETFScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CreateUserETFSceneController controller = loader.getController();
        controller.initializeScene(this, indexer, stockMarket, startDate);
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
    }

    public void switchToPortfolioScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/PortfolioScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PortfolioSceneController controller = loader.getController();
        controller.initializeScene(this, stockMarket, portfolio, startDate);
        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
    }

    public void switchToStockInfoPopUp(Stock selectedStock, CreateUserETFSceneController controller) {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("fxml/StockInfoPopUpScene.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PopUpSceneController popUpController = loader.getController();
        popUpController.initializeScene(newStage, controller, selectedStock);
        Scene popUpScene = new Scene(root);
        newStage.setScene(popUpScene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Stock Information");
        newStage.setResizable(false);
        newStage.showAndWait();
    }

    public void setSimStartDate(YearMonth startDate) {
        this.startDate = startDate;
    }

    public void addETFToPortfolio(IETF etf) {
        portfolio.addETF(etf);
        etf.setETFName("ETF " + (portfolio.getETFsInPortfolio().size()));
        switchToPortfolioScene();
    }

    public static void main(String[] args) {
        launch();
    }
}
