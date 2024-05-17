package application.ui;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandShortcutPane extends BorderPane {

    HBox hbox;

    public CommandShortcutPane(Main main, Stage primaryStage){
        super();
        this.setWidth(main.width);
        this.setHeight(main.height);

        Button back = createBackButton(main, primaryStage);

        GridPane commandShortcutGridPane = new GridPane();
        commandShortcutGridPane.setHgap(5);
        commandShortcutGridPane.setVgap(5);
        {
            Label allUser = new Label("Liste de tous les raccourcis :");
            allUser.setStyle("-fx-text-fill: white; -fx-font-size: 20px");
            commandShortcutGridPane.add(allUser, 0, 1);

            Label startStopGaze = new Label("Lancer/Arrêter Gaze : ");
            startStopGaze.setStyle("-fx-text-fill: white; -fx-font-size: 15px");
            commandShortcutGridPane.add(startStopGaze, 0, 2);

            HBox hbStartStopGaze = new HBox();
            TextField tfStartStopGaze1 = new TextField(main.getCommandShortcut().playCommand[0]);
            tfStartStopGaze1.setPrefColumnCount(2);
            tfStartStopGaze1.setOnKeyPressed(event -> main.getCommandShortcut().playCommand[0] = tfStartStopGaze1.getText());
            TextField tfStartStopGaze2 = new TextField(main.getCommandShortcut().playCommand[1]);
            tfStartStopGaze2.setPrefColumnCount(2);
            tfStartStopGaze2.setOnKeyPressed(event -> main.getCommandShortcut().playCommand[1] = tfStartStopGaze2.getText());
            Label lbStartStopGaze = new Label(" + ");
            lbStartStopGaze.setStyle("-fx-text-fill: white; -fx-font-size: 15px");
            hbStartStopGaze.getChildren().addAll(tfStartStopGaze1, lbStartStopGaze, tfStartStopGaze2);
            commandShortcutGridPane.add(hbStartStopGaze, 1, 2);
        }

        hbox = new HBox(back, commandShortcutGridPane);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public static ImageView createButtonImageView(String url) {
        ImageView image = new ImageView(new Image(url));
        image.setPreserveRatio(true);
        image.setFitWidth(495. / 6);
        return image;
    }
    public Button createBackButton(Main main, Stage primaryStage) {
        Button back = new MainButton("Retour");
        back.setGraphic(createButtonImageView("images/white/back.png"));
        back.getStyleClass().add("grey");
        back.setContentDisplay(ContentDisplay.TOP);
        back.setPrefHeight(200);
        back.setPrefWidth(495. / 5);
        back.setOnAction((e) -> main.goToOptions(primaryStage));
        return back;
    }
}
