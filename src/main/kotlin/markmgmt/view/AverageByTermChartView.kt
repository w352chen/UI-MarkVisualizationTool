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


class AverageByTermChartView(private val model:Model): Canvas(), InvalidationListener {
    private var hMargin = 40.0
    private var vMargin = 40.0
    private var hRatio = 1.0
    private var vRatio = 1.0
    private val markLevelColors = listOf<Color>(Color.DARKSLATEGRAY, Color.LIGHTCORAL, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.SILVER, Color.GOLD)
    init{
        model.addListener(this)

        widthProperty().addListener{ _ ->
            hRatio = width / 500.0
            hMargin = hRatio * 40.0
            draw()
        }
        heightProperty().addListener{ _ ->
            vRatio = height / 405.0
            vMargin = vRatio * 40.0
            draw()
        }

        draw()
    }

    private fun draw() {
        val gc = graphicsContext2D

        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, width, height)
        val originPoint = Point2D(hMargin * 1.25, height - vMargin - 10 * vRatio)
        val legendLineGap = (height - vMargin * 2) / 10.0
        gc.textAlign = TextAlignment.CENTER
        gc.textBaseline = VPos.CENTER
        gc.font = Font.font(10.0 * (hRatio + vRatio) / 2.0)
        for (i in 1..10) {
            gc.stroke = Color.LIGHTGRAY
            gc.strokeLine(originPoint.x, originPoint.y - i * legendLineGap, width - hMargin, originPoint.y - i * legendLineGap)
            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.strokeText((i * 10).toString(), originPoint.x - 20.0 * hRatio, originPoint.y - i * legendLineGap)
            gc.fillText((i * 10).toString(), originPoint.x - 20.0 * hRatio, originPoint.y - i * legendLineGap)
        }
        gc.strokeText("0", originPoint.x - 20.0 * hRatio, originPoint.y)
        gc.fillText("0", originPoint.x - 20.0 * hRatio, originPoint.y)
        gc.stroke = Color.BLACK
        gc.strokeLine(originPoint.x, originPoint.y, width - hMargin, originPoint.y)
        gc.strokeLine(originPoint.x, originPoint.y, originPoint.x, vMargin - 10 * vRatio)

        gc.textBaseline = VPos.TOP
        val yAxisLabelGap = (width - hMargin * 2 - 20.0 * hRatio) / model.termAverageData.size
        var x : Double? = null
        var y : Double? = null
        for (i in 0 until model.termAverageData.size) {
            gc.stroke = Color.GRAY
            if (model.termAverageData[i].averageScore >= 0) {
                val x2 = originPoint.x + i * yAxisLabelGap + 5.0 * hRatio + yAxisLabelGap / 2.0
                val y2 = originPoint.y - (originPoint.y - vMargin + 10.0 * vRatio) * model.termAverageData[i].averageScore / 100.0
                if (x != null && y != null) {
                    gc.strokeLine(x, y, x2, y2)
                }
                x = x2
                y = y2
            }
        }
        for (i in 0 until model.termAverageData.size) {
            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.strokeText(
                model.termAverageData[i].term,
                originPoint.x + i * yAxisLabelGap + 5.0 * hRatio + yAxisLabelGap / 2.0,
                originPoint.y + 10 * vRatio)
            gc.fillText(
                model.termAverageData[i].term,
                    originPoint.x + i * yAxisLabelGap + 5.0 * hRatio + yAxisLabelGap / 2.0,
                      originPoint.y + 10 * vRatio)

            if (model.termAverageData[i].averageScore >= 0) {
                gc.fill = markLevelColors[model.getMarkLevel(model.termAverageData[i].averageScore.toString())]
                gc.fillOval(
                    originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                    originPoint.y - 5.0 * vRatio - (originPoint.y - vMargin + 10.0 * vRatio) * model.termAverageData[i].averageScore / 100.0,
                    10.0 * hRatio, 10.0 * vRatio)
            }
        }
    }

    override fun invalidated(observable: Observable?) {
        draw()
    }

}