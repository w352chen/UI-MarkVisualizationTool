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


class IncrementalAverageChartView(private val model:Model): Canvas(), InvalidationListener {
    private val margin = 40.0
    private val markLevelColors = listOf<Color>(Color.DARKSLATEGRAY, Color.LIGHTCORAL, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.SILVER, Color.GOLD)
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
        val originPoint = Point2D(margin * hRatio, height - margin * vRatio)
        gc.stroke = Color.BLACK
        gc.strokeLine(originPoint.x, originPoint.y, width - margin * hRatio, originPoint.y)
        gc.strokeLine(originPoint.x, originPoint.y, originPoint.x, margin * 0.75 * vRatio)

        val legendLineGap = (height - margin * 1.75 * vRatio) / 10.0
        gc.textAlign = TextAlignment.CENTER
        gc.textBaseline = VPos.CENTER
        gc.font = Font.font(10 * (hRatio + vRatio) / 2.0)
        for (i in 1..10) {
            gc.stroke = Color.LIGHTGRAY
            gc.strokeLine(originPoint.x, originPoint.y - i * legendLineGap, width - margin * hRatio, originPoint.y - i * legendLineGap)
            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.fillText((i * 10).toString(), originPoint.x - 20.0 * hRatio, originPoint.y - i * legendLineGap)
            gc.strokeText((i * 10).toString(), originPoint.x - 20.0 * hRatio, originPoint.y - i * legendLineGap)
        }
        gc.fillText("0", originPoint.x - 20.0 * hRatio, originPoint.y)
        gc.strokeText("0", originPoint.x - 20.0 * hRatio, originPoint.y)

        gc.textBaseline = VPos.TOP
        val yAxisLabelGap = (width - (margin * 2.0 - 10.0) * hRatio) / model.incrementalAverageData.size
        for (i in 0 until model.incrementalAverageData.size) {
            gc.stroke = Color.GRAY
            gc.fill = Color.GRAY
            gc.fillText(
                model.incrementalAverageData[i].term,
                    originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                      originPoint.y + 10 * vRatio)
            gc.strokeText(
                model.incrementalAverageData[i].term,
                originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                originPoint.y + 10 * vRatio)

            if (model.incrementalAverageData[i].currTermCourses > 0) {
                gc.stroke = Color.BLACK
                gc.strokeLine(originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                    originPoint.y + 5 * vRatio - (originPoint.y - margin * 0.75 * vRatio) *
                              model.incrementalAverageData[i].average / 100.0,
                    originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average - model.incrementalAverageData[i].deviation) / 100.0)
                gc.strokeLine(originPoint.x + i * yAxisLabelGap - 10.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average - model.incrementalAverageData[i].deviation) / 100.0,
                    originPoint.x + i * yAxisLabelGap + 10.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average - model.incrementalAverageData[i].deviation) / 100.0)
                gc.strokeLine(originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                    originPoint.y - 5 * vRatio - (originPoint.y - margin * 0.75 * vRatio)
                            * model.incrementalAverageData[i].average / 100.0,
                    originPoint.x + i * yAxisLabelGap + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average + model.incrementalAverageData[i].deviation) / 100.0)
                gc.strokeLine(originPoint.x + i * yAxisLabelGap - 10.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average + model.incrementalAverageData[i].deviation) / 100.0,
                    originPoint.x + i * yAxisLabelGap + 10.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - (originPoint.y - margin * 0.75 * vRatio) *
                            (model.incrementalAverageData[i].average + model.incrementalAverageData[i].deviation) / 100.0)
                gc.strokeOval(originPoint.x + i * yAxisLabelGap - 5.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - 5 * vRatio - (originPoint.y - margin * 0.75 * vRatio) * model.incrementalAverageData[i].average / 100.0,
                    10.0 * hRatio, 10.0 * vRatio)
                gc.fill = markLevelColors[model.getMarkLevel(model.incrementalAverageData[i].average.toString())]
                gc.fillOval(
                    originPoint.x + i * yAxisLabelGap - 5.0 * hRatio + yAxisLabelGap / 2.0,
                    originPoint.y - 5 * vRatio - (originPoint.y - margin * 0.75 * vRatio) * model.incrementalAverageData[i].average / 100.0,
                    10.0 * hRatio, 10.0 * vRatio)
                for (course in model.incrementalAverageData[i].courseList) {
                    gc.stroke = markLevelColors[course.getMarkLevel()]
                    gc.strokeOval(originPoint.x + i * yAxisLabelGap - 5.0 * hRatio + yAxisLabelGap / 2.0,
                        originPoint.y - 5 * vRatio - (originPoint.y - margin * 0.75 * vRatio) * course.getMark().toDouble() / 100.0,
                        10.0 * hRatio, 10.0 * vRatio)
                }
            }
        }
    }


    override fun invalidated(observable: Observable?) {
        draw()
    }

}