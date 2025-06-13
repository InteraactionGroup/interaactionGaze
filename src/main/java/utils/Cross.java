package utils;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Cross extends Group {

    public Cross() {
        Line horizontalLine = new Line();
        horizontalLine.setStartX(-10);
        horizontalLine.setEndX(10);

        Line verticalLine = new Line();
        verticalLine.setStartY(-10);
        verticalLine.setEndY(10);

        Circle center = new Circle();
        center.setRadius(2);

        getChildren().addAll(horizontalLine, verticalLine, center);
        setMouseTransparent(true);
    }


}
