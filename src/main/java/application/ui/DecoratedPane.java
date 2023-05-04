package application.ui;

import application.Main;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecoratedPane extends BorderPane {

    private double xOffset = 0;
    private double yOffset = 0;
    Label profil;

    public DecoratedPane(Main main, Stage primaryStage) {
        Button exit = new Button("fermer");
        ImageView exitImage = new ImageView(new Image("images/white/close.png"));
        exit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        exit.getStyleClass().add("topBarButton");
        exitImage.setPreserveRatio(true);
        exitImage.setFitWidth(50);
        exit.setGraphic(exitImage);
        exit.setOnAction((e) -> {
            System.exit(0);
        });

        Button minimize = new Button("minimiser");
        ImageView minimizeImage = new ImageView(new Image("images/white/minimize.png"));
        minimize.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        minimize.getStyleClass().add("topBarButton");
        minimizeImage.setPreserveRatio(true);
        minimizeImage.setFitWidth(50);
        minimize.setGraphic(minimizeImage);
        minimize.setOnAction((e) -> {
            primaryStage.setIconified(true);
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        {
            this.profil = new Label("Profil actuel : " + main.getMouseInfo().nameUser);
            gridPane.add(profil, 2, 0);
            profil.getStyleClass().add("profil");
        }

        HBox topBar = new HBox(gridPane, minimize, exit);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        gridPane.setAlignment(Pos.CENTER_LEFT);
        BorderPane.setAlignment(topBar, Pos.CENTER_RIGHT);
        this.setTop(topBar);
        this.setStyle("-fx-background-color: #282e35; -fx-background-radius: 15");

        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        topBar.setOnMouseDragged(event -> {
            double tempX, tempY;
            ObservableList<Screen> screens = Screen.getScreensForRectangle(primaryStage.getX(),primaryStage.getY(), primaryStage.getWidth(),primaryStage.getHeight());
            Rectangle2D primaryScreenBounds = screens.get(0).getVisualBounds();

                tempX = event.getScreenX() - xOffset;

            if (event.getScreenY() - yOffset < 0) {
                tempY = 0;
            } else
                tempY = Math.min(event.getScreenY() - yOffset, primaryScreenBounds.getHeight() - primaryStage.getHeight() / 3);

            primaryStage.setX(tempX);
            primaryStage.setY(tempY);
        });
    }

    public void updateProfil(String name){
        this.profil.setText("Profil actuel : " +  name);
    }
}
