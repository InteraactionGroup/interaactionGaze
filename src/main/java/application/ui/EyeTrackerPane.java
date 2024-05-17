package application.ui;

import application.Main;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class EyeTrackerPane extends BorderPane {

    Main main;
    Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;
    HBox hbox;

    public EyeTrackerPane(Main main, Stage primaryStage){
        super();
        this.main = main;
        this.primaryStage = primaryStage;

        Button stop = addStopButton(main, primaryStage);

        hbox = new HBox(stop);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 15 15 15 15");
    }

    private Button addStopButton(Main main, Stage primaryStage){
        Button stop = new MainButton("");
        stop.getStyleClass().add("red");
        stop.setPrefHeight(30);
        stop.setPrefWidth(30);
        stop.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (event.getClickCount() == 2){
                    primaryStage.setWidth(main.width);
                    primaryStage.setHeight(main.height);
                    main.getGazeDeviceManager().setPause(true);
                    main.goToMain(primaryStage);
                    main.getGazeDeviceManager().startCheckTobii();
                }
            }
        });
        this.addDragAndDrop(stop, primaryStage);
        return stop;
    }

    private void addDragAndDrop(Button button, Stage primaryStage){
        button.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        button.setOnMouseDragged(event -> {
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
}
