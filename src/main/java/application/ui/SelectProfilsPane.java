package application.ui;

import application.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SelectProfilsPane extends BorderPane {

    HBox hbox;
    String userSelected = "";

    public SelectProfilsPane(Main main, Stage primaryStage, ProfilsPane profilsPane){
        super();
        this.setWidth(main.width);
        this.setHeight(main.height);

        GridPane selectProfilsGridPane = new GridPane();
        selectProfilsGridPane.setHgap(10);
        selectProfilsGridPane.setVgap(10);
        {
            String[] listName = profilsPane.getAllUser();
            primaryStage.setHeight(profilsPane.getHeightNeeded(main, listName.length));

            Label allUser = new Label("Liste de tous les utilisateurs :");
            allUser.setStyle("-fx-text-fill: white; -fx-font-size: 20px");
            selectProfilsGridPane.add(allUser, 0, 1);

            ToggleGroup groupNames = new ToggleGroup();

            VBox vbBtnUser = new VBox(10);
            vbBtnUser.setAlignment(Pos.CENTER_LEFT);
            for (String s : listName) {
                RadioButton btnName = new RadioButton(s);
                btnName.setStyle("-fx-text-fill: white; -fx-font-size: 15px");
                btnName.setToggleGroup(groupNames);
                vbBtnUser.getChildren().add(btnName);
            }

            selectProfilsGridPane.add(vbBtnUser, 0, 2);

            groupNames.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                if (groupNames.getSelectedToggle() != null) {
                    String getBtnSelected = groupNames.getSelectedToggle().toString();
                    String[] getName = getBtnSelected.split("'");
                    userSelected = getName[1];
                }
            });

            Button btnSelect = new Button("Sélectionner");
            Button btnReturn = new Button("Retour");
            HBox hbBtnSelect = new HBox(10);
            hbBtnSelect.setAlignment(Pos.CENTER);
            hbBtnSelect.getChildren().addAll(btnReturn, btnSelect);
            selectProfilsGridPane.add(hbBtnSelect, 0, 3);

            final Text error = new Text();
            error.setStyle("-fx-font-size: 15px;");
            selectProfilsGridPane.add(error, 0, 4);

            btnSelect.setOnAction( event -> {
                if (!Objects.equals(this.userSelected, "")){
                    this.selectUser(main);
                    primaryStage.setHeight(main.height);
                    main.goToProfils(primaryStage);
                }else {
                    error.setFill(Color.RED);
                    error.setText("Pas de profil sélectionner !");
                }
            });

            btnReturn.setOnAction(event -> {
                main.goToProfils(primaryStage);
            });
        }

        hbox = new HBox(selectProfilsGridPane);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public void selectUser(Main main){
        try {
            String userName = System.getProperty("user.name");
            FileReader fileReader = new FileReader("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + this.userSelected + "\\settings.json", StandardCharsets.UTF_8);
            Object settings = new JsonParser().parse(fileReader);
            JsonObject jsonDefaultSettings = (JsonObject) settings;

            String name = jsonDefaultSettings.get("Name").getAsString();
            String fixationLength = String.valueOf(jsonDefaultSettings.get("FixationLength"));
            String sizeTarget = String.valueOf(jsonDefaultSettings.get("SizeTarget"));

            String playCommand = String.valueOf(jsonDefaultSettings.get("PlayStopCommand")).replace("\"", "");

            double redColorBackground = Double.parseDouble(jsonDefaultSettings.get("RedColorBackground").getAsString());
            double blueColorBackground = Double.parseDouble(jsonDefaultSettings.get("BlueColorBackground").getAsString());
            double greenColorBackground = Double.parseDouble(jsonDefaultSettings.get("GreenColorBackground").getAsString());

            main.getMouseInfo().nameUser = name;
            main.getMouseInfo().DWELL_TIME = Integer.parseInt(fixationLength);
            main.getMouseInfo().SIZE_TARGET = Integer.parseInt(sizeTarget);
            main.getMouseInfo().COLOR_BACKGROUND = Color.color(redColorBackground, blueColorBackground, greenColorBackground);

            main.getDecoratedPane().updateProfil(this.userSelected);
            main.getCalibrationConfig().loadSave(main);
            main.getOptionsPane().updateSettings(fixationLength, sizeTarget, Color.color(redColorBackground, blueColorBackground, greenColorBackground));

            main.getCommandShortcut().playCommand = playCommand.split("-");

            fileReader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
