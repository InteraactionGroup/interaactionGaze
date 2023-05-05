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

public class MainPane extends BorderPane {

    boolean running = false;

    HBox hbox;

    public MainPane(Main main, Stage primaryStage) {
        super();
        this.setWidth(600);
        this.setHeight(200);

        Button startstop = createStartStopButton(main, primaryStage);
        Button profils = createProfilButton(main, primaryStage);
        Button options = createOptionsButton(main, primaryStage);

        //Button hide = createHideButton(primaryStage);
        //Button clickActivation = createClickActivationButton(main, primaryStage);

        //hbox = new HBox(startstop, hide, clickActivation, options);
        hbox = new HBox(startstop, profils, options);
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

    public Button createStartStopButton(Main main, Stage primaryStage) {
        Button startstop = new MainButton("Play");
        startstop.setGraphic(createButtonImageView("images/white/play.png"));
        startstop.getStyleClass().add("green");
        startstop.setContentDisplay(ContentDisplay.TOP);
        startstop.setPrefHeight(200);
        startstop.setPrefWidth(495. / 5);
        startstop.setOnAction((e) -> {
            if (running) {
                running = false;
                main.getGazeDeviceManager().setPause(true);
                startstop.setText("Play");
                ((ImageView) startstop.getGraphic()).setImage(new Image("images/white/play.png"));
            } else {
                running = true;
                main.getGazeDeviceManager().setPause(false);
                startstop.setText("Stop");
                ((ImageView) startstop.getGraphic()).setImage(new Image("images/white/stop.png"));
            }
        });
        return startstop;
    }

    public Button createProfilButton(Main main, Stage primaryStage){
        Button profil = new MainButton("Profil");
        profil.setGraphic(createButtonImageView("images/white/user.png"));
        profil.getStyleClass().add("blue");
        profil.setContentDisplay(ContentDisplay.TOP);
        profil.setPrefHeight(200);
        profil.setPrefWidth(495. / 5);
        profil.setOnAction((e) -> {
            main.goToProfils(primaryStage);
        });

        return profil;
    }

    /*public Button createHideButton(Stage primaryStage) {
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
    }*/

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
