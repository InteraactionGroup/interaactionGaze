package utils;

import application.ui.MainPane;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandShortcut implements NativeKeyListener {

    private String keySelected = "";

    MainPane mainPane;

    public CommandShortcut(MainPane mainPane) {
        this.mainPane = mainPane;
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

        if (Objects.equals(this.keySelected, "G") && NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()).equals("A")){
            mainPane.startstop.fire();
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
