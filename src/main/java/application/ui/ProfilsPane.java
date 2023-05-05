package application.ui;

import application.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static application.ui.MainPane.createButtonImageView;
@Slf4j
public class ProfilsPane extends BorderPane {

    HBox hbox;
    boolean isWindowsOpen = false;
    String userSelected = "";
    public ProfilsPane(Stage primaryStage, Main main) {
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
        back.setOnAction((e) -> {
            main.goToMain(primaryStage);
        });
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

            if (!this.isWindowsOpen){

                this.isWindowsOpen = true;
                String[] listName = this.getAllUser();

                GridPane selectProfilGridPane = new GridPane();
                selectProfilGridPane.setAlignment(Pos.CENTER);
                selectProfilGridPane.setHgap(10);
                selectProfilGridPane.setVgap(10);
                selectProfilGridPane.setPadding(new Insets(25, 25, 25, 25));

                Stage selectProfilStage = new Stage();

                if (listName.length == 0){
                    Text noUser = new Text("Il n'y a pas d'utilisateur !");
                    noUser.setFill(Color.RED);
                    selectProfilGridPane.add(noUser, 0, 1);
                }else {
                    Label allUser = new Label("Liste de tous les utilisateur :");
                    selectProfilGridPane.add(allUser, 0, 1);

                    ToggleGroup groupNames = new ToggleGroup();
                    int maxIndex = 0;

                    for (int i=0; i<listName.length; i++) {
                        RadioButton btnName = new RadioButton(listName[i]);
                        btnName.setToggleGroup(groupNames);
                        selectProfilGridPane.add(btnName, 0, i+2);
                        maxIndex = i+2;
                    }

                    groupNames.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                        if (groupNames.getSelectedToggle() != null) {
                            String getBtnSelected = groupNames.getSelectedToggle().toString();
                            String[] getName = getBtnSelected.split("'");
                            userSelected = getName[1];
                        }
                    });

                    Button selectDelete = new Button("Sélectionner");
                    HBox hbBtnSelect = new HBox(10);
                    hbBtnSelect.setAlignment(Pos.BOTTOM_LEFT);
                    hbBtnSelect.getChildren().add(selectDelete);
                    selectProfilGridPane.add(hbBtnSelect, 0, maxIndex+1);

                    selectDelete.setOnAction( event -> {
                        if (!Objects.equals(this.userSelected, "")){
                            this.isWindowsOpen = false;
                            this.selectUser(main);
                            selectProfilStage.close();
                        }
                    });
                }

                Scene deleteProfilScene = new Scene(selectProfilGridPane, 400, 250);
                deleteProfilScene.getStylesheets().add("style.css");

                selectProfilStage.setTitle("Supprimer un profil");
                selectProfilStage.setScene(deleteProfilScene);
                selectProfilStage.setX(primaryStage.getX() + 100);
                selectProfilStage.setY(primaryStage.getY() + 10);

                selectProfilStage.show();

                selectProfilStage.setOnCloseRequest(event -> this.isWindowsOpen = false);
            }
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

            if (!this.isWindowsOpen){

                this.isWindowsOpen = true;

                GridPane addProfilGridPane = new GridPane();
                addProfilGridPane.setAlignment(Pos.CENTER);
                addProfilGridPane.setHgap(10);
                addProfilGridPane.setVgap(10);
                addProfilGridPane.setPadding(new Insets(25, 25, 25, 25));

                Label userName = new Label("Nom utilisateur:");
                addProfilGridPane.add(userName, 0, 1);

                TextField userTextField = new TextField();
                addProfilGridPane.add(userTextField, 1, 1);

                Button btnAdd = new Button("Créer");
                HBox hbBtnAdd = new HBox(10);
                hbBtnAdd.setAlignment(Pos.BOTTOM_LEFT);
                hbBtnAdd.getChildren().add(btnAdd);
                addProfilGridPane.add(hbBtnAdd, 1, 4);

                final Text error = new Text();
                addProfilGridPane.add(error, 1, 6);

                Scene addProfilScene = new Scene(addProfilGridPane, 400, 200);
                addProfilScene.getStylesheets().add("style.css");

                Stage addProfilStage = new Stage();
                addProfilStage.setTitle("Ajouter un profil");
                addProfilStage.setScene(addProfilScene);
                addProfilStage.setX(primaryStage.getX() + 100);
                addProfilStage.setY(primaryStage.getY() + 10);

                addProfilStage.show();

                btnAdd.setOnAction( event -> {

                    boolean emptyName = Objects.equals(userTextField.getText(), "");
                    boolean alreadyExist = this.checkIfNameAlreadyExist(userTextField.getText());

                    if (emptyName){
                        error.setFill(Color.RED);
                        error.setText("Nom invalide ! Ne peut pas être vide !");
                    } else if (alreadyExist) {
                        error.setFill(Color.RED);
                        error.setText("Nom invalide ! Existe déjà !");
                    } else {
                        this.isWindowsOpen = false;
                        this.saveUser(userTextField.getText());
                        addProfilStage.close();
                    }

                });

                addProfilStage.setOnCloseRequest(event -> this.isWindowsOpen = false);
            }
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

            if (!this.isWindowsOpen){

                this.isWindowsOpen = true;
                String[] listName = this.getAllUser();

                GridPane deleteProfilGridPane = new GridPane();
                deleteProfilGridPane.setAlignment(Pos.CENTER);
                deleteProfilGridPane.setHgap(10);
                deleteProfilGridPane.setVgap(10);
                deleteProfilGridPane.setPadding(new Insets(25, 25, 25, 25));

                Stage deleteProfilStage = new Stage();

                if (listName.length == 0){
                    Text noUser = new Text("Il n'y a pas d'utilisateur !");
                    noUser.setFill(Color.RED);
                    deleteProfilGridPane.add(noUser, 0, 1);
                }else {
                    Label allUser = new Label("Liste de tous les utilisateur :");
                    deleteProfilGridPane.add(allUser, 0, 1);

                    ToggleGroup groupNames = new ToggleGroup();
                    int maxIndex = 0;

                    for (int i=0; i<listName.length; i++) {
                        RadioButton btnName = new RadioButton(listName[i]);
                        btnName.setToggleGroup(groupNames);
                        deleteProfilGridPane.add(btnName, 0, i+2);
                        maxIndex = i+2;
                    }

                    groupNames.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                        if (groupNames.getSelectedToggle() != null) {
                            String getBtnSelected = groupNames.getSelectedToggle().toString();
                            String[] getName = getBtnSelected.split("'");
                            userSelected = getName[1];
                        }
                    });

                    Button btnDelete = new Button("Supprimer");
                    HBox hbBtnDelete = new HBox(10);
                    hbBtnDelete.setAlignment(Pos.BOTTOM_LEFT);
                    hbBtnDelete.getChildren().add(btnDelete);
                    deleteProfilGridPane.add(hbBtnDelete, 0, maxIndex+1);

                    final Text error = new Text();
                    deleteProfilGridPane.add(error, 0, maxIndex+2);

                    btnDelete.setOnAction( event -> {
                        if (!Objects.equals(this.userSelected, "") && !Objects.equals(this.userSelected, "default")){
                            this.isWindowsOpen = false;
                            this.deleteUser();
                            deleteProfilStage.close();
                        } else if (Objects.equals(this.userSelected, "")){
                            error.setFill(Color.RED);
                            error.setText("Aucun utilisateur sélectionner !");
                        }else {
                            error.setFill(Color.RED);
                            error.setText("Default ne peut être supprimer !");
                        }
                    });
                }

                Scene deleteProfilScene = new Scene(deleteProfilGridPane, 400, 250);
                deleteProfilScene.getStylesheets().add("style.css");

                deleteProfilStage.setTitle("Supprimer un profil");
                deleteProfilStage.setScene(deleteProfilScene);
                deleteProfilStage.setX(primaryStage.getX() + 100);
                deleteProfilStage.setY(primaryStage.getY() + 10);

                deleteProfilStage.show();

                deleteProfilStage.setOnCloseRequest(event -> this.isWindowsOpen = false);
            }
        });
        return deleteProfil;
    }

    public boolean checkIfNameAlreadyExist(String name){

        String[] pathnames;
        String userName = System.getProperty("user.name");

        File f = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
        pathnames = f.list();

        if (pathnames != null){
            for (String pathname : pathnames) {
                if (Objects.equals(pathname, name)){
                    return true;
                }
            }
        }
        return false;
    }
    public void saveUser(String name){

        String userName = System.getProperty("user.name");
        File newUser = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + name);
        boolean createUser = newUser.mkdirs();
        log.info("Create new user = " + createUser);

        JSONObject json = new JSONObject();
        try {
            json.put("Name", name);
            json.put("FixationLength", 2000);
            json.put("SizeTarget", 50);
            json.put("RedColorBackground", "1.0");
            json.put("BlueColorBackground", "1.0");
            json.put("GreenColorBackground", "1.0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try (PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + name + "\\settings.json", StandardCharsets.UTF_8))) {
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getAllUser(){

        String[] pathnames;
        String userName = System.getProperty("user.name");

        File f = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
        pathnames = f.list();

        return pathnames;
    }

    public void deleteUser(){
        String userName = System.getProperty("user.name");
        File folder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + this.userSelected);
        File[] contents = folder.listFiles();

        if (contents != null) {
            for (File f : contents) {
                boolean deleteFile = f.delete();
                log.info("File deleted -> " + deleteFile);
            }
        }
        boolean deleteUser = folder.delete();
        log.info("User deleted -> " + deleteUser);
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

            fileReader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
