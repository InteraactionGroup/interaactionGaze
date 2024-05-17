package utils;

import application.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class Settings {

    Main main;
    String os;
    public Settings(Main main){
        os = System.getProperty("os.name").toLowerCase();
        this.main = main;
        this.initDefaultSettings();
    }
    public void initDefaultSettings() {
        FileWriter myWritter = null;

        try {
            if (os.contains("nux") || os.contains("mac")){

                File myFolder = new File("~/Documents/interAACtionGaze");
                boolean createFolder = myFolder.mkdirs();

                File profils = new File("~/Documents/interAACtionGaze/profils");
                boolean createProfilsFolder = profils.mkdirs();

                File defaultSettings = new File("~/Documents/interAACtionGaze/profils/default");
                boolean createDefaultSettingsFolder = defaultSettings.mkdirs();

                File myLinuxFile = new File("~/Documents/interAACtionGaze/calibration.txt");
                if (!myLinuxFile.exists()){
                    myWritter = new FileWriter("~/Documents/interAACtionGaze/calibration.txt", StandardCharsets.UTF_8);
                    myWritter.write("true");
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("Name", "Default");
                    json.put("FixationLength", 2000);
                    json.put("SizeTarget", 50);
                    json.put("RedColorBackground", "1.0");
                    json.put("BlueColorBackground", "1.0");
                    json.put("GreenColorBackground", "1.0");
                    json.put("PlayCommand", "G-A");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try (PrintWriter out = new PrintWriter(new FileWriter("~/Documents/interAACtionGaze/profils/default/settings.json", StandardCharsets.UTF_8))) {
                    out.write(json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info("Folder created, path = " + createFolder + ", " + createDefaultSettingsFolder + ", " + createProfilsFolder);

            }else{
                String userName = System.getProperty("user.name");

                File myFolder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze");
                boolean createFolder = myFolder.mkdirs();

                File profils = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
                boolean createProfilsFolder = profils.mkdirs();

                File defaultSettings = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\default");
                boolean createDefaultSettingsFolder = defaultSettings.mkdirs();

                File myWindowsFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt");
                if (!myWindowsFile.exists()){
                    myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt", StandardCharsets.UTF_8);
                    myWritter.write("true");
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("Name", "Default");
                    json.put("FixationLength", 2000);
                    json.put("SizeTarget", 50);
                    json.put("RedColorBackground", "1.0");
                    json.put("BlueColorBackground", "1.0");
                    json.put("GreenColorBackground", "1.0");
                    json.put("PlayStopCommand", "G-A");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try (PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\default\\settings.json", StandardCharsets.UTF_8))) {
                    out.write(json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info("Folder created, path = " + createFolder + ", " + createDefaultSettingsFolder + ", " + createProfilsFolder);
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
    }

    public void loadDefaultSettings(Stage primaryStage){

        try {
            File myFile;
            if (os.contains("win")){
                String userName = System.getProperty("user.name");
                myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt");
                this.extractDefaultSettingsFile("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\default\\settings.json");
            }else {
                myFile = new File("calibration.txt");
                this.extractDefaultSettingsFile("~/Documents/interAACtionGaze/profils/default/settings.json");
            }

            Scanner myReader = new Scanner(myFile, StandardCharsets.UTF_8);
            String data = myReader.nextLine();

            if (Objects.equals(data, "true")){
                if (os.contains("win")){
                    main.startMessageCalibration(primaryStage, data);
                }else {
                    main.goToCalibration(primaryStage, data);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractDefaultSettingsFile(String path) {
        try {
            FileReader fileReader = new FileReader(path, StandardCharsets.UTF_8);
            Object defaultSettings = new JsonParser().parse(fileReader);
            JsonObject jsonDefaultSettings = (JsonObject) defaultSettings;

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

            main.getCommandShortcut().playCommand = playCommand.split("-");

            fileReader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
