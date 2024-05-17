package application.ui;

import application.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import static application.ui.MainPane.createButtonImageView;
@Slf4j
public class ProfilsPane extends BorderPane {

    HBox hbox;
    boolean isWindowsOpen = false;
    String userSelected = "";

    SelectProfilsPane selectProfilsPane;
    AddProfilsPane addProfilsPane ;
    DeleteProfilsPane deleteProfilsPane ;

    public ProfilsPane(Main main, Stage primaryStage) {
        super();

        Button selectProfil = createSelectProfilButton(main, primaryStage);
        Button addProfil = createAddProfilButton(main, primaryStage);
        Button deleteProfil = createDeleteProfilButton(main, primaryStage);
        Button back = createBackButton(main, primaryStage);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        hbox = new HBox(back, selectProfil, addProfil, deleteProfil);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public Button createBackButton(Main main, Stage primaryStage) {
        Button back = new MainButton("Retour");
        back.setGraphic(createButtonImageView("images/white/back.png"));
        back.getStyleClass().add("grey");
        back.setContentDisplay(ContentDisplay.TOP);
        back.setPrefHeight(200);
        back.setPrefWidth(495. / 5);
        back.setOnAction((e) -> main.goToMain(primaryStage));
        return back;
    }

    public Button createSelectProfilButton(Main main, Stage primaryStage){
        Button selectProfil = new MainButton("Choisir Profil");
        selectProfil.setGraphic(createButtonImageView("images/white/selectUser.png"));
        selectProfil.getStyleClass().add("red");
        selectProfil.setContentDisplay(ContentDisplay.TOP);
        selectProfil.setPrefHeight(200);
        selectProfil.setPrefWidth(495. / 5);
        selectProfil.setOnAction((e) -> {
            selectProfilsPane = new SelectProfilsPane(main,primaryStage, this);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.selectProfilsPane);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });
        return selectProfil;
    }

    public Button createAddProfilButton(Main main, Stage primaryStage){
        Button addProfil = new MainButton("ajouter Profil");
        addProfil.setGraphic(createButtonImageView("images/white/addUser.png"));
        addProfil.getStyleClass().add("blue");
        addProfil.setContentDisplay(ContentDisplay.TOP);
        addProfil.setPrefHeight(200);
        addProfil.setPrefWidth(495. / 5);
        addProfil.setOnAction((e) -> {
            addProfilsPane = new AddProfilsPane(main,primaryStage);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.addProfilsPane);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });
        return addProfil;
    }

    public Button createDeleteProfilButton(Main main, Stage primaryStage){
        Button deleteProfil = new MainButton("Supprimer Profil");
        deleteProfil.setGraphic(createButtonImageView("images/white/deleteUser.png"));
        deleteProfil.getStyleClass().add("purple");
        deleteProfil.setContentDisplay(ContentDisplay.TOP);
        deleteProfil.setPrefHeight(200);
        deleteProfil.setPrefWidth(495. / 5);
        deleteProfil.setOnAction((e) -> {
            deleteProfilsPane = new DeleteProfilsPane(main,primaryStage,this);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.deleteProfilsPane);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });
        return deleteProfil;
    }

    public String[] getAllUser(){

        String[] pathnames;
        String userName = System.getProperty("user.name");

        File f = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
        pathnames = f.list();

        return pathnames;
    }

    public int getHeightNeeded(Main main, int nbUser){
        int heightNeeded = nbUser - 2;
        if (heightNeeded < 0){
            heightNeeded = 0;
        }
        return main.height + 35 * heightNeeded;
    }
}
