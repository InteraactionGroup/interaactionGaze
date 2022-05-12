package application;

import application.ui.*;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import gaze.devicemanager.Webcam;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

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
    Webcam webcam;
    @Getter
    OptionsPane optionsPane;
    @Getter
    OptionsCalibrationPane optionsCalibrationPane;

    DecoratedPane decoratedPane;

    public static void main(String[] args) {

        String os = System.getProperty("os.name").toLowerCase();
        FileWriter myWritter = null;

        try {
            if (os.contains("nux") || os.contains("mac")){
                File myFile = new File("calibration.txt");
                log.info(String.valueOf(myFile));
                myWritter = new FileWriter("calibration.txt", StandardCharsets.UTF_8);
                myWritter.write(args[0]);
            }else{
                String userName = System.getProperty("user.name");
                File myFolder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze");
                boolean createFolder = myFolder.mkdirs();
                log.info("Folder created, path = " + createFolder);
                File myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt");
                if (!myFile.exists()){
                    myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt", StandardCharsets.UTF_8);
                    myWritter.write("true");

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (myWritter != null){
                    myWritter.close();
                }
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        nu.pattern.OpenCV.loadShared();

        primaryStage.setWidth(600);
        primaryStage.setHeight(250);
        primaryStage.setTitle("InteraactionGaze");

        mouseInfo = new MouseInfo();
        webcam = new Webcam();
        CalibrationConfig calibrationConfig = new CalibrationConfig();
        gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this, calibrationConfig);

        optionsPane = new OptionsPane(primaryStage, this);
        optionsCalibrationPane = new OptionsCalibrationPane(primaryStage, this, calibrationConfig);
        calibrationPane = new CalibrationPane(primaryStage, gazeDeviceManager, calibrationConfig);
        home = new MainPane(this, primaryStage);

        decoratedPane = new DecoratedPane(primaryStage);
        decoratedPane.setCenter(home);

        Scene calibScene = new Scene(decoratedPane, primaryStage.getWidth(), primaryStage.getHeight());
        calibScene.getStylesheets().add("style.css");
        primaryStage.setScene(calibScene);
        calibScene.setFill(Color.TRANSPARENT);
        // calibrationPane.installEventHandler(primaryStage, this);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        this.getGazeDeviceManager().setPause(true);

        String os = System.getProperty("os.name").toLowerCase();

        try {
            File myFile;
            if (os.contains("win")){
                String userName = System.getProperty("user.name");
                myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt");
            }else {
                myFile = new File("calibration.txt");
            }

            Scanner myReader = new Scanner(myFile, StandardCharsets.UTF_8);
            String data = myReader.nextLine();

            if (Objects.equals(data, "true")){
                this.getGazeDeviceManager().setPause(false);
                if (os.contains("win")){
                    startMessageCalibration(primaryStage, data);
                }else {
                    startCalibration(primaryStage, data);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.show();
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

        startCalibration(primaryStage, data);
        primaryStage.setIconified(false);
    }

    public void startCalibration(Stage primaryStage, String data) {
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

    public void goToOptions(Stage primaryStage) {
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToMain(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        primaryStage.getScene().setRoot(decoratedPane);
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getHome());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
}
