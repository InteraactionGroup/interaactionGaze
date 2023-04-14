package application.ui;

import application.Main;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeEvent;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.CalibrationConfig;
import utils.CalibrationPoint;
import utils.Cross;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CalibrationPane extends Pane {

    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int RIGHT_CENTER = 3;
    public static final int BOTTOM_RIGHT = 4;
    public static final int BOTTOM_CENTER = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int LEFT_CENTER = 7;
    public static final int CENTER = 8;
    public static final int TESTENDED = 9;

    Circle target;
    Circle imgTarget;
    Timeline rotateCalibrationCross;

    GazeDeviceManager gazeDeviceManager;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    Cross calibrationCross;
    int currentTest = TOP_LEFT;

    Point2D curCoord = new Point2D(0, 0);
    Stage primaryStage;

    CalibrationConfig calibrationConfig;
    String data;

    public CalibrationPane(Stage primaryStage, GazeDeviceManager gazeDeviceManager, CalibrationConfig mainCalibrationConfig) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.primaryStage = primaryStage;
        this.calibrationConfig = mainCalibrationConfig;

        this.setFocusTraversable(true);
    }

    public void startCalibration(Main main, String data) {
        this.setBackground(new Background(new BackgroundFill(main.getMouseInfo().COLOR_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().clear();
        currentTest = 0;
        resetCalibrationPoints();
        calibrationCross = new Cross();
        calibrationCross.setOpacity(0);
        getChildren().add(calibrationCross);
        this.data = data;

        EventHandler<Event> event = e -> {
            if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                curCoord = getGazePosition(((GazeEvent) e).getX(), ((GazeEvent) e).getY());
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                curCoord = new Point2D(((MouseEvent) e).getX(), ((MouseEvent) e).getY());
            }
        };

        this.addEventHandler(GazeEvent.GAZE_MOVED, event);
        this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
        gazeDeviceManager.addEventFilter(this);

        startCurrentTest(main);
    }

    public void saveCalibration() {
        calibrationConfig.setAllAngles();
        try {
            calibrationConfig.save();
            System.out.println("save done !");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("save fail !");
        }
    }

    public void returnGazeMenu(Main main){

        if (Objects.equals(this.data, "true")){
            Platform.exit();
        }else {
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToMain(primaryStage);
        }
    }

    public void startCurrentTest(Main main) {

        double width = primaryScreenBounds.getWidth() / 10;
        double height = primaryScreenBounds.getHeight() / 10;

        if (currentTest == TESTENDED) {
            imgTarget.setOpacity(0);
            resetTarget();
            calibrationCross.setOpacity(0);
            saveCalibration();
            messageCalibration(main);
        } else {
            if (currentTest == TOP_LEFT) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(height);
            } else if (currentTest == TOP_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(height);
                imgTarget.setOpacity(0);
            } else if (currentTest == TOP_RIGHT) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(height);
                imgTarget.setOpacity(0);
            } else if (currentTest == RIGHT_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
                imgTarget.setOpacity(0);
            } else if (currentTest == BOTTOM_RIGHT) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
                imgTarget.setOpacity(0);
            } else if (currentTest == BOTTOM_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
                imgTarget.setOpacity(0);
            } else if (currentTest == BOTTOM_LEFT) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
                imgTarget.setOpacity(0);
            } else if (currentTest == LEFT_CENTER) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
                imgTarget.setOpacity(0);
            } else if (currentTest == CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
                imgTarget.setOpacity(0);
            }
            calibrationConfig.get(currentTest).setCross(nextCross());

            resetTarget();
            target = new Circle(primaryScreenBounds.getHeight() / 5);
            target.setCenterX(calibrationCross.getLayoutX());
            target.setCenterY(calibrationCross.getLayoutY());
            target.setFill(Color.LIGHTGREY);
            target.setOpacity(0.1);

            EventHandler<Event> event = e -> {
                if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                    calibrate(main);
                } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                    calibrate(main);
                }
            };

            target.addEventHandler(GazeEvent.GAZE_MOVED, event);
            gazeDeviceManager.addEventFilter(target);

            setImgTarget(main);
            this.getChildren().add(target);
            addExitButton(main);
        }
    }

    public void addExitButton(Main main){
        if (currentTest == TOP_LEFT) {
            Button backHome = new Button("Terminer");
            backHome.setOnAction((e) -> {
                returnGazeMenu(main);
            });

            this.getChildren().add(backHome);
        }
    }

    public void messageCalibration(Main main){

        String os = System.getProperty("os.name").toLowerCase();
        FileWriter myWritter = null;

        if (os.contains("win")){
            this.data = "false";
        }

        Rectangle2D sizeScreen = Screen.getPrimary().getVisualBounds();
        double posX = sizeScreen.getWidth();
        double posY = sizeScreen.getHeight();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calibration");
        alert.setHeaderText(null);
        alert.setContentText("Calibration réussi ! Vous allez sortir de la calibration !");
        alert.setX((posX / 2.0) - 50.0 );
        alert.setY((posY / 2.0) - 50.0 );
        alert.show();
        SequentialTransition sleep = new SequentialTransition(new PauseTransition(Duration.seconds(5)));
        sleep.setOnFinished(event -> {
            alert.close();
            returnGazeMenu(main);
        });
        sleep.play();

        try {
            if (os.contains("nux")){
                myWritter = new FileWriter("calibration.txt", StandardCharsets.UTF_8);
                myWritter.write("false");
            }else {
                String userName = System.getProperty("user.name");
                myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\calibration.txt", StandardCharsets.UTF_8);
                myWritter.write("false");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert myWritter != null;
                myWritter.close();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
    }

    public void setImgTarget(Main main){

        imgTarget = new Circle(main.getMouseInfo().SIZE_TARGET);
        imgTarget.setCenterX(calibrationCross.getLayoutX());
        imgTarget.setCenterY(calibrationCross.getLayoutY());

        Image addImgTarget = new Image(calibrationConfig.getImgUse());
        imgTarget.setFill(new ImagePattern(addImgTarget));

        this.getChildren().add(imgTarget);

        rotateCalibrationCross = new Timeline();
        rotateCalibrationCross.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), // set start position at 0
                        new KeyValue(imgTarget.rotateProperty(), 0),
                        new KeyValue(imgTarget.rotateProperty(), 360)));
        rotateCalibrationCross.setCycleCount(Timeline.INDEFINITE);
        rotateCalibrationCross.play();
    }

    public void resetTarget() {
        if (target != null) {
            target.setOnMouseMoved(null);
            this.getChildren().remove(target);
        }
    }

    public Cross nextCross() {
        Cross newCross = new Cross();
        newCross.setLayoutX(calibrationCross.getLayoutX());
        newCross.setLayoutY(calibrationCross.getLayoutY());
        newCross.setOpacity(0);
        this.getChildren().add(newCross);
        return newCross;
    }

    @SuppressFBWarnings
    public void addValue(int numberOfCoordinateToTest) {
        if (calibrationConfig.get(currentTest).capturedCoordinates.size() < numberOfCoordinateToTest) {
            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(curCoord.getX());
            c.setCenterY(curCoord.getY());
            c.setFill(Color.RED);
            c.setOpacity(0);
            c.setMouseTransparent(true);
            calibrationConfig.get(currentTest).capturedCoordinates.add(curCoord);
            //getChildren().add(c);
        } else if (calibrationConfig.get(currentTest).capturedCoordinates.size() == numberOfCoordinateToTest && calibrationConfig.get(currentTest).circle == null) {
            double coordXsum = 0, coordYsum = 0;


            for (int i = calibrationConfig.get(currentTest).capturedCoordinates.size() / 2;
                 i < calibrationConfig.get(currentTest).capturedCoordinates.size(); i++) {
                coordXsum = coordXsum + calibrationConfig.get(currentTest).capturedCoordinates.get(i).getX();
                coordYsum = coordYsum + calibrationConfig.get(currentTest).capturedCoordinates.get(i).getY();
            }
            coordXsum = coordXsum / (double) (calibrationConfig.get(currentTest).capturedCoordinates.size() / 2);
            coordYsum = coordYsum / (double) (calibrationConfig.get(currentTest).capturedCoordinates.size() / 2);


            Circle newCircle = new Circle();
            newCircle.setRadius(5);
            newCircle.setCenterX(coordXsum);
            newCircle.setCenterY(coordYsum);
            newCircle.setOpacity(0);
            newCircle.setFill(Color.LIGHTSKYBLUE);

            getChildren().add(newCircle);
            calibrationConfig.get(currentTest).setCircle(newCircle);
        }
    }

    public void resetCalibrationPoints() {
        calibrationConfig.setAngleSetUpDone(false);
        for (CalibrationPoint calibrationPoint : calibrationConfig.calibrationPoints) {
            calibrationPoint.capturedCoordinates.clear();
            calibrationPoint.circle = null;
            calibrationPoint.offsetY = 0;
            calibrationPoint.offsetX = 0;
        }
    }

    public void calibrate(Main main) {
        if (currentTest < 9 && calibrationConfig.get(currentTest).circle == null) {
            addValue(100);
        } else if (currentTest < 9 && calibrationConfig.get(currentTest).circle != null) {
            currentTest++;
            rotateCalibrationCross.stop();
            startCurrentTest(main);
        }
    }

    public Point2D getGazePosition(double x, double y) {
        Point2D pointCoord = new Point2D(x, y);
        Point2D localPointCoord = this.screenToLocal(pointCoord.getX(), pointCoord.getY());
        if (localPointCoord != null) {
            return localPointCoord;
        }
        System.out.println(x + " " + y);
        return pointCoord;
    }
}
