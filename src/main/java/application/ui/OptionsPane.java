package application.ui;

import application.Main;
import com.google.gson.JsonParser;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static application.ui.MainPane.createButtonImageView;

@Slf4j
public class OptionsPane extends BorderPane {

    HBox hbox;
    int sizePref = 80;
    TextField dwellTime;
    TextField sizeTarget;
    ColorPicker colorPicker;

    public OptionsPane(Stage primaryStage, Main main) {
        super();

        Button back = createBackButton(main, primaryStage);

        Button calibrate = createCalibrateButton(main, primaryStage);

        Button settingsCalibration = createSettingsCalibrationButton(main, primaryStage);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        {
            // Fixation length

            Label fixationLabel = new Label("Temps de fixation:");
            Label milliSecondesLabel = new Label("ms");
            dwellTime = new TextField("" + main.getMouseInfo().DWELL_TIME);
            dwellTime.setPrefWidth(sizePref);

            gridPane.add(fixationLabel, 0, 0);
            gridPane.add(dwellTime, 1, 0);
            gridPane.add(milliSecondesLabel, 2, 0);

            fixationLabel.getStyleClass().add("text");
            milliSecondesLabel.getStyleClass().add("text");

            dwellTime.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    dwellTime.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue.equals("")) {
                    dwellTime.setText("0");
                }
                main.getMouseInfo().DWELL_TIME = Integer.parseInt(dwellTime.getText());

            });

            // Size Target

            Label sizeTargetLabel = new Label("Taille des cibles:");
            sizeTarget = new TextField("" + main.getMouseInfo().SIZE_TARGET);
            sizeTarget.setPrefWidth(sizePref);
            Label pourcentageLabel = new Label("%");

            sizeTargetLabel.getStyleClass().add("text");
            pourcentageLabel.getStyleClass().add("text");

            gridPane.add(sizeTargetLabel, 0, 1);
            gridPane.add(sizeTarget, 1, 1);
            gridPane.add(pourcentageLabel, 2, 1);

            sizeTarget.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    sizeTarget.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue.equals("")) {
                    sizeTarget.setText("0");
                }
                main.getMouseInfo().SIZE_TARGET = Integer.parseInt(sizeTarget.getText());

            });

            // Color background

            Label colorChoiceLabel = new Label("Couleur fond d'écran:");
            gridPane.add(colorChoiceLabel, 0, 2);
            colorChoiceLabel.getStyleClass().add("text");

            colorPicker = new ColorPicker();
            colorPicker.setValue(main.getMouseInfo().COLOR_BACKGROUND);
            colorPicker.setOnAction(e -> {
                main.getMouseInfo().COLOR_BACKGROUND = colorPicker.getValue();
                main.getMouseInfo().redColor = colorPicker.getValue().getRed();
                main.getMouseInfo().blueColor = colorPicker.getValue().getBlue();
                main.getMouseInfo().greenColor = colorPicker.getValue().getGreen();
            });

            colorPicker.setPrefWidth(sizePref);
            gridPane.add(colorPicker, 1, 2);
        }
        hbox = new HBox(back, calibrate, settingsCalibration, gridPane);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public Button createCalibrateButton(Main main, Stage primaryStage) {
        Button calibrate = new MainButton("Calibrer");
        calibrate.setGraphic(createButtonImageView("images/white/calibrate.png"));
        calibrate.getStyleClass().add("purple");
        calibrate.setContentDisplay(ContentDisplay.TOP);
        calibrate.setPrefHeight(200);
        calibrate.setPrefWidth(495. / 5);
        calibrate.setOnAction((e) -> {
            main.startCalibration(primaryStage, "false");
        });
        return calibrate;
    }

    public Button createBackButton(Main main, Stage primaryStage) {
        Button back = new MainButton("Retour");
        back.setGraphic(createButtonImageView("images/white/back.png"));
        back.getStyleClass().add("grey");
        back.setContentDisplay(ContentDisplay.TOP);
        back.setPrefHeight(200);
        back.setPrefWidth(495. / 5);
        back.setOnAction((e) -> {
            this.saveSettings(main);
            main.goToMain(primaryStage);
        });
        return back;
    }

    public Button createSettingsCalibrationButton(Main main, Stage primaryStage){
        Button settingsCalibration = new MainButton("Options Calibration");
        settingsCalibration.setGraphic(createButtonImageView("images/white/option.png"));
        settingsCalibration.getStyleClass().add("rose");
        settingsCalibration.setContentDisplay(ContentDisplay.TOP);
        settingsCalibration.setPrefHeight(200);
        settingsCalibration.setPrefWidth(495. / 5);
        settingsCalibration.setOnAction((e) -> {
            primaryStage.setWidth(600);
            primaryStage.setHeight(600);
            main.goToOptionsCalibration(primaryStage);
        });
        return settingsCalibration;
    }

    public void saveSettings(Main main){
        if (!Objects.equals(main.getMouseInfo().nameUser, "default")){
            String userName = System.getProperty("user.name");
            File settings = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + main.getMouseInfo().nameUser + "\\settings.json");

            if (settings.exists()){
                boolean deleteFile = settings.delete();
                log.info("Settings file deleted !" + deleteFile);
            }

            JSONObject json = new JSONObject();
            try {
                json.put("Name", main.getMouseInfo().nameUser);
                json.put("FixationLength", main.getMouseInfo().DWELL_TIME);
                json.put("SizeTarget", main.getMouseInfo().SIZE_TARGET);
                json.put("RedColorBackground", String.valueOf(main.getMouseInfo().redColor));
                json.put("BlueColorBackground", String.valueOf(main.getMouseInfo().blueColor));
                json.put("GreenColorBackground", String.valueOf(main.getMouseInfo().greenColor));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + main.getMouseInfo().nameUser + "\\settings.json", StandardCharsets.UTF_8))) {
                out.write(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSettings(String dwelltime, String targetSize, Color color){
        this.dwellTime.setText(dwelltime);
        this.sizeTarget.setText(targetSize);
        this.colorPicker.setValue(color);
    }
}
