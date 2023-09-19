package application.ui;

import javafx.scene.control.Button;

public class MainButton extends Button {

    public MainButton(String name) {
        super(name);
        getStyleClass().add("customizedButton");
        this.applyCss();
    }
}
