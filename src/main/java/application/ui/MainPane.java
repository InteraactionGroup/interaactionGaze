package application.ui;

import application.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainPane extends BorderPane {

    boolean runningEyeTracker = false;
    boolean runningWebcam = false;
    boolean displayed = true;
    //boolean iscancelled = false;

    HBox hbox;

    public MainPane(Main main, Stage primaryStage) {
        super();
        this.setWidth(600);
        this.setHeight(200);

        Button startStopEyeTracker = createStartStopEyeTrackerButton(main, primaryStage);

        Button startstopWebcam = createStartStopWebcamButton(main, primaryStage);

        Button hide = createHideButton(primaryStage);

        //Button clickActivation = createClickActivationButton(main, primaryStage);

        Button options = createOptionsButton(main, primaryStage);

        hbox = new HBox(startStopEyeTracker, startstopWebcam, hide, options);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");

        main.getMouseInfo().initTimer();
    }

    public static ImageView createButtonImageView(String url) {
        ImageView image = new ImageView(new Image(url));
        image.setPreserveRatio(true);
        image.setFitWidth(495. / 6);
        return image;
    }

    public Button createStartStopEyeTrackerButton(Main main, Stage primaryStage) {
        Button startStopEyeTracker = new MainButton("Play Eye Tracker");
        startStopEyeTracker.setGraphic(createButtonImageView("images/white/play.png"));
        startStopEyeTracker.getStyleClass().add("green");
        startStopEyeTracker.setContentDisplay(ContentDisplay.TOP);
        startStopEyeTracker.setPrefHeight(200);
        startStopEyeTracker.setPrefWidth(495. / 5);
        startStopEyeTracker.setOnAction((e) -> {
            if (runningEyeTracker) {
                runningEyeTracker = false;
                main.getGazeDeviceManager().setPause(true);
                startStopEyeTracker.setText("Play Eye Tracker");
                ((ImageView) startStopEyeTracker.getGraphic()).setImage(new Image("images/white/play.png"));
            } else {
                runningEyeTracker = true;
                main.getGazeDeviceManager().setPause(false);
                startStopEyeTracker.setText("Stop Eye Tracker");
                ((ImageView) startStopEyeTracker.getGraphic()).setImage(new Image("images/white/stop.png"));
            }
        });
        return startStopEyeTracker;
    }
    public Button createStartStopWebcamButton(Main main, Stage primaryStage) {
        Button startStopWebcam = new MainButton("Play Webcam");
        startStopWebcam.setGraphic(createButtonImageView("images/white/play.png"));
        startStopWebcam.getStyleClass().add("orange");
        startStopWebcam.setContentDisplay(ContentDisplay.TOP);
        startStopWebcam.setPrefHeight(200);
        startStopWebcam.setPrefWidth(495. / 5);
        startStopWebcam.setOnAction((e) -> {
            if (runningWebcam) {
                runningWebcam = false;
                main.getWebcam().WebcamManager();
                startStopWebcam.setText("Play Webcam");
                ((ImageView) startStopWebcam.getGraphic()).setImage(new Image("images/white/play.png"));
            } else {
                runningWebcam = true;
                main.getWebcam().WebcamManager();
                startStopWebcam.setText("Stop Webcam");
                ((ImageView) startStopWebcam.getGraphic()).setImage(new Image("images/white/stop.png"));
            }
        });
        return startStopWebcam;
    }

    public Button createHideButton(Stage primaryStage) {
        Button hide = new MainButton("Cacher le curseur");
        hide.setGraphic(createButtonImageView("images/white/hide.png"));
        hide.getStyleClass().add("blue");
        hide.setContentDisplay(ContentDisplay.TOP);
        hide.setPrefHeight(200);
        hide.setPrefWidth(495. / 5);
        hide.setOnAction((e) -> {
            if (displayed) {
                displayed = false;
                this.setCursor(Cursor.NONE);
                hide.setText("Afficher le curseur");
                ((ImageView) hide.getGraphic()).setImage(new Image("images/white/show.png"));
            } else {
                displayed = true;
                this.setCursor(Cursor.DEFAULT);
                hide.setText("Cacher le curseur");
                ((ImageView) hide.getGraphic()).setImage(new Image("images/white/hide.png"));
            }
        });
        return hide;
    }

    /*public Button createClickActivationButton(Main main, Stage primaryStage) {
        Button clickActivation = new MainButton("Desactiver le click");
        clickActivation.setGraphic(createButtonImageView("images/white/click-disabled.png"));
        clickActivation.getStyleClass().add("orange");
        clickActivation.setContentDisplay(ContentDisplay.TOP);
        clickActivation.setPrefHeight(200);
        clickActivation.setPrefWidth(495. / 5);
        clickActivation.setOnAction((e) -> {
            if (main.getMouseInfo().isClikcActivated()) {
                main.getMouseInfo().setClikcActivated(false);
                iscancelled = true;
                clickActivation.setText("Activer le clic");
                ((ImageView) clickActivation.getGraphic()).setImage(new Image("images/white/click-enabled.png"));
            } else {
                main.getMouseInfo().setClikcActivated(true);
                iscancelled = false;
                clickActivation.setText("Desactiver le clic");
                ((ImageView) clickActivation.getGraphic()).setImage(new Image("images/white/click-disabled.png"));
            }
        });
        return clickActivation;
    }*/

    public Button createOptionsButton(Main main, Stage primaryStage) {
        Button options = new MainButton("Options");
        options.setGraphic(createButtonImageView("images/white/option.png"));
        options.getStyleClass().add("red");
        options.setContentDisplay(ContentDisplay.TOP);
        options.setPrefHeight(200);
        options.setPrefWidth(495. / 5);
        options.setOnAction((e) -> {
            main.goToOptions(primaryStage);
        });
        return options;
    }
}
