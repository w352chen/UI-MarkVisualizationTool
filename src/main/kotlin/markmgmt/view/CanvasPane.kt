package markmgmt.view

import javafx.scene.canvas.Canvas
import javafx.scene.layout.*

class CanvasPane(canvas: Canvas) : BorderPane() {

    init {
        center = canvas

        canvas.widthProperty().bind(widthProperty())
        canvas.heightProperty().bind(heightProperty())

        minWidth = 0.0
        minHeight = 0.0
        prefWidth = Double.MAX_VALUE
        prefHeight = Double.MAX_VALUE

    }

}