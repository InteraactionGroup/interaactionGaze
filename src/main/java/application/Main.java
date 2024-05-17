package application;

import application.ui.*;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.CalibrationConfig;
import utils.CommandShortcut;
import utils.Settings;

@Slf4j
public class Main extends Application {

    @Getter
    TobiiGazeDeviceManager gazeDeviceManager;
    @Getter
    CalibrationPane calibrationPane;
    @Getter
    MainPane home;
    @Getter
    MouseInfo mouseInfo;
    @Getter
    OptionsPane optionsPane;
    @Getter
    OptionsCalibrationPane optionsCalibrationPane;
    @Getter
    CalibrationConfig calibrationConfig;
    @Getter
    ProfilsPane profilsPane;
    @Getter
    DecoratedPane decoratedPane;
    @Getter
    EyeTrackerPane eyeTrackerPane;
    @Getter
    Settings settings;
    @Getter
    CommandShortcut commandShortcut;

    public int width = 600;
    public int height = 250;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setWidth(this.width);
        primaryStage.setHeight(this.height);
        primaryStage.setTitle("InteraactionGaze");
        primaryStage.setAlwaysOnTop(false);

        mouseInfo = new MouseInfo();
        calibrationConfig = new CalibrationConfig(this);
        decoratedPane = new DecoratedPane(this, primaryStage);
        gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this, calibrationConfig);
        eyeTrackerPane = new EyeTrackerPane(this, primaryStage);
        optionsPane = new OptionsPane(this, primaryStage);
        profilsPane = new ProfilsPane(this, primaryStage);
        optionsCalibrationPane = new OptionsCalibrationPane(primaryStage, this, calibrationConfig);
        calibrationPane = new CalibrationPane(primaryStage, gazeDeviceManager, calibrationConfig);
        home = new MainPane(this, primaryStage);
        commandShortcut = new CommandShortcut(this, primaryStage, home, eyeTrackerPane);
        settings = new Settings(this);

        decoratedPane.setCenter(home);
        Scene calibScene = new Scene(decoratedPane, primaryStage.getWidth(), primaryStage.getHeight());
        calibScene.getStylesheets().add("style.css");
        primaryStage.setScene(calibScene);
        calibScene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setOnCloseRequest(event -> {
            mouseInfo.closeScriptMouseCursor();
            gazeDeviceManager.stopCheckTobii();
        });

        this.getGazeDeviceManager().setPause(true);

        this.goToMain(primaryStage);
        primaryStage.show();

        this.settings.loadDefaultSettings(primaryStage);
        this.commandShortcut.launch();
    }

    public void startMessageCalibration(Stage primaryStage, String data) {
        primaryStage.show();
        primaryStage.setIconified(true);

        Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
        startAlert.setTitle("Start Calibration");
        startAlert.setHeaderText(null);
        startAlert.setContentText("Nous allons commencer avec une première calibration !");
        startAlert.showAndWait();

        Alert eyeTrackerAlert = new Alert(Alert.AlertType.INFORMATION);
        eyeTrackerAlert.setTitle("Start Calibration");
        eyeTrackerAlert.setHeaderText(null);
        eyeTrackerAlert.setContentText("Veuillez brancher votre Eye Tracker avant de continuer !");
        eyeTrackerAlert.showAndWait();

        goToCalibration(primaryStage, data);
        primaryStage.setIconified(false);
    }

    public void goToCalibration(Stage primaryStage, String data) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.getScene().setRoot(this.getCalibrationPane());
        calibrationPane.startCalibration(this, data);
    }

    public void goToOptionsCalibration(Stage primaryStage){
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsCalibrationPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
    public void goToProfils(Stage primaryStage){
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getProfilsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToOptions(Stage primaryStage) {
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToMain(Stage primaryStage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //primaryStage.setFullScreen(false);
        primaryStage.getScene().setRoot(decoratedPane);
        primaryStage.setX((primaryScreenBounds.getWidth() - this.width)/2);
        primaryStage.setY((primaryScreenBounds.getHeight() - this.height)/2);
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getHome());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToEyeTracker(Stage primaryStage){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(3 * primaryScreenBounds.getWidth()/4);
        primaryStage.setY(5);
        primaryStage.setWidth(35);
        primaryStage.setHeight(35);
        primaryStage.getScene().setRoot(this.getEyeTrackerPane());
    }
}
