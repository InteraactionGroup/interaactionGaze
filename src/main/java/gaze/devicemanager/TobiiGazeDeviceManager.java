package gaze.devicemanager;

import application.Main;
import gaze.MouseInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import tobii.Tobii;
import utils.CalibrationConfig;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TobiiGazeDeviceManager extends AbstractGazeDeviceManager {

    private MouseInfo mouseInfo;
    private ExecutorService executorService;

    private PositionPollerRunnable positionPollerRunnable;
    private CalibrationConfig calibrationConfig;
    private IntegerProperty dwellRatio = new SimpleIntegerProperty(0);
    private Boolean alreadyStarted = false;
    public Timeline checkTobii;
    public Boolean tobiiEnabled = false;
    public Boolean eyeTrackerStatus = false;

    public TobiiGazeDeviceManager(Main main, CalibrationConfig calibrationConfig) {
        super();
        this.mouseInfo = main.getMouseInfo();
        this.calibrationConfig = calibrationConfig;
    }

    public void setPause(boolean b) {
        positionPollerRunnable.setPauseRequested(b);
    }

    public void init(Main main) {

        log.info("Init Tobii Gaze Device Manager");

        try {
            Tobii.gazePosition();
            this.checkTobiiStatus();
            positionPollerRunnable = new PositionPollerRunnable(mouseInfo, calibrationConfig, this);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        this.checkTobii(main);
        this.startCheckTobii();
    }

    public void checkTobii(Main main){
        float[] eyeTrackerPosition = Tobii.gazePosition();
        checkTobii = new Timeline();
        checkTobii.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO),
                new KeyFrame(Duration.millis(5000)));
        checkTobii.setOnFinished((e) -> {
            float[] eyeTrackerNextPosition = Tobii.gazePosition();
            eyeTrackerStatus = (eyeTrackerPosition[0] != eyeTrackerNextPosition[0] || eyeTrackerPosition[1] != eyeTrackerNextPosition[1]);
            main.getDecoratedPane().updateEyeTracker(eyeTrackerStatus);

            if (eyeTrackerStatus) {
                this.startExecutorService();
            }else {
                this.destroyExecutorService();
                this.reloadTobii();
            }

            eyeTrackerPosition[0] = eyeTrackerNextPosition[0];
            eyeTrackerPosition[1] = eyeTrackerNextPosition[1];

            checkTobii.play();
        });
    }

    public void startCheckTobii(){
        this.checkTobii.play();
    }

    public void stopCheckTobii(){
        this.checkTobii.stop();
    }

    public void startExecutorService(){
        if (!this.alreadyStarted){
            this.alreadyStarted = true;
            positionPollerRunnable.setStopRequested(false);
            executorService = Executors.newFixedThreadPool(4,
                    (Runnable r) -> {
                        Thread t = new Thread(r);
                        t.setDaemon(true);
                        return t;
                    }
            );
            executorService.submit(positionPollerRunnable);
        }

    }
    public void destroyExecutorService() {
        this.alreadyStarted = false;
        positionPollerRunnable.setStopRequested(true);
        ExecutorService executorService = this.executorService;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void reloadTobii(){
        if (!this.tobiiEnabled){
            Tobii.reloadIfNotLoaded();
            this.checkTobiiStatus();
        }
    }

    public void checkTobiiStatus(){
        this.tobiiEnabled = Tobii.getTobiiStatus() == 0;
    }
}
