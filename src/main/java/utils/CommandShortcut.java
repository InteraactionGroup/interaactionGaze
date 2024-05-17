package utils;

import application.Main;
import application.ui.EyeTrackerPane;
import application.ui.MainPane;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandShortcut implements NativeKeyListener {

    private String keySelected = "";
    public String[] playCommand;

    Main main;
    Stage primaryStage;
    MainPane mainPane;
    EyeTrackerPane eyeTrackerPane;

    boolean running = false;

    public CommandShortcut(Main main, Stage primaryStage, MainPane mainPane, EyeTrackerPane eyeTrackerPane) {
        this.main = main;
        this.primaryStage = primaryStage;
        this.mainPane = mainPane;
        this.eyeTrackerPane = eyeTrackerPane;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        System.out.println("Key Typed: " + nativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (Objects.equals(this.keySelected, "")){
            this.keySelected = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
        }

        if (Objects.equals(this.keySelected, this.playCommand[0]) && NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()).equals(this.playCommand[1])){
            if (running){
                this.running = false;
                primaryStage.setWidth(main.width);
                primaryStage.setHeight(main.height);
                main.getGazeDeviceManager().setPause(true);
                main.goToMain(primaryStage);
                main.getGazeDeviceManager().startCheckTobii();
            }else {
                this.running = true;
                main.getGazeDeviceManager().setPause(false);
                main.goToEyeTracker(primaryStage);
                main.getGazeDeviceManager().stopCheckTobii();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        this.keySelected = "";
    }

    public void launch(){
        try {
            GlobalScreen.registerNativeHook();

            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
    }
}
