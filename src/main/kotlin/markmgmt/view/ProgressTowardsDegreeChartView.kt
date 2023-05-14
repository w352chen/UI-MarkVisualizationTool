package markmgmt.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import markmgmt.model.Model
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment


class ProgressTowardsDegreeChartView(private val model:Model): Canvas(), InvalidationListener {
    private val margin = 40.0
    private var hRatio = 1.0
    private var vRatio = 1.0

    init{
        model.addListener(this)
        widthProperty().addListener{ _ ->
            hRatio = width / 500.0
            draw()
        }
        heightProperty().addListener{ _ ->
            vRatio = height / 405.0
            draw()
        }

        draw()
    }

    private fun draw() {
        val gc = graphicsContext2D

        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, width, height)
        val originPoint = Point2D(margin * hRatio, height - margin * vRatio * 2)
        val verticalLineGap = width / 5.0
        gc.textBaseline = VPos.CENTER
        gc.textAlign = TextAlignment.LEFT
        gc.font = Font(10.0 * (hRatio + vRatio) / 2.0)
        val horizontalGap = (height - margin * vRatio * 3.55) / model.progressTowardsDegreeRecord.size
        gc.stroke = Color.BLACK
        for (i in 0..3) {
            val courseTaken = model.progressTowardsDegreeRecord.getCourseTaken(i)

            gc.fill = model.progressTowardsDegreeRecord.colorSchemeRequiredCourse[i]
            var barWidth = courseTaken.second / 40.0 * (verticalLineGap * 4)
            gc.fillRect(originPoint.x + margin * hRatio - 25.0 * hRatio, margin * vRatio * 2 + 10.0 * vRatio + horizontalGap * i - horizontalGap / 4, barWidth, horizontalGap / 2)

            gc.fill = model.progressTowardsDegreeRecord.colorSchemeCourseTaken[i]
            barWidth = courseTaken.first / 40.0 * (verticalLineGap * 4)
            gc.fillRect(originPoint.x + margin * hRatio - 25.0 * hRatio, margin * vRatio * 2 + 10.0 * vRatio + horizontalGap * i - horizontalGap / 4, barWidth, horizontalGap / 2)

            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.fillText(model.progressTowardsDegreeRecord.categories[i],
                originPoint.x - 25 * hRatio, margin * vRatio * 2 + 10.0 * vRatio + horizontalGap * i)
            gc.strokeText(model.progressTowardsDegreeRecord.categories[i],
                originPoint.x - 25 * hRatio, margin * vRatio * 2 + 10.0 * vRatio + horizontalGap * i)
        }

        gc.stroke = Color.BLACK
        gc.textAlign = TextAlignment.CENTER
        gc.textBaseline = VPos.TOP
        for (i in 0..4) {
            gc.stroke = Color.BLACK
            gc.strokeLine(originPoint.x + i * verticalLineGap + margin * hRatio - 25.0 * hRatio, originPoint.y + 10.0 * vRatio ,
                originPoint.x + i * verticalLineGap + margin * hRatio - 25.0 * hRatio, margin * vRatio + 10.0 * vRatio)
            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.fillText((i * 5.0).toString(), originPoint.x + i * verticalLineGap + margin * hRatio - 25.0 * hRatio,
                originPoint.y + 20.0 * vRatio)
            gc.strokeText((i * 5.0).toString(), originPoint.x + i * verticalLineGap + margin * hRatio - 25.0 * hRatio,
                originPoint.y + 20.0 * vRatio)
        }
    }

    override fun invalidated(observable: Observable?) {
        draw()
    }

}