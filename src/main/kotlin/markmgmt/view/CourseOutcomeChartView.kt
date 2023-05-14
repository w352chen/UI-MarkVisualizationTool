package markmgmt.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.scene.canvas.Canvas
import markmgmt.model.Model
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import markmgmt.model.Course
import java.lang.Double.min
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class CourseOutcomeChartView(private val model:Model): Canvas(), InvalidationListener {
    private val margin = 15.0
    private val markLevelColors = listOf<Color>(Color.DARKSLATEGRAY, Color.LIGHTCORAL, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.SILVER, Color.GOLD, Color.WHITE)
    private var hRatio = 1.0
    private var vRatio = 1.0
    private var arcWidth = 0.0
    private var arcX = 0.0
    private var arcY = 0.0

    init{
        model.addListener(this)
        widthProperty().addListener{ _ ->
            hRatio = width / 500.0
            arcWidth = min(width - margin * hRatio * 5, height - margin * vRatio * 5)
            arcX = (width - arcWidth) / 2
            arcY = (height - arcWidth) / 2
            draw()
        }
        heightProperty().addListener{ _ ->
            vRatio = height / 405.0
            arcWidth = min(width - margin * hRatio * 5, height - margin * vRatio * 5)
            arcX = (width - arcWidth) / 2
            arcY = (height - arcWidth) / 2
            draw()
        }

        setOnMouseMoved {
            draw()
            val index = hitTest(it.x, it.y)
            if (index >= 0) {
                displayCourses(model.courseOutcomeRecords.getCourses(index))
            }
        }

        draw()
    }

    private fun hitTest(x : Double, y : Double) : Int {
        val cx = arcX + arcWidth / 2
        val cy = arcY + arcWidth / 2
        val dist = sqrt((x - cx).pow(2.0) + (y - cy).pow(2.0))
        if (dist <= arcWidth / 2) {
            val h = cy - y
            val l = x - cx
            val degree = (360.0 + Math.toDegrees(atan2(h, l))) % 360
            var startAngle = 0.0
            for(i in 0 until model.courseOutcomeRecords.getSize()) {
                val arcExtent = model.courseOutcomeRecords.getCourseCount(i) * 360.0 / model.courseOutcomeRecords.getTotalCourseCount()
                if (degree >= startAngle && degree <= startAngle + arcExtent) {
                    return i
                }
                startAngle += arcExtent
            }
        }
        return -1
    }

    private fun displayCourses(courses: MutableList<Course>) {
        val gc = graphicsContext2D
        gc.fill = Color.GRAY
        gc.stroke = Color.GRAY
        gc.font = Font.font(10.0 * (hRatio + vRatio) / 2.0)
        for ((index, course) in courses.withIndex()) {
            gc.fillText(course.courseCode,10.0 * hRatio, (20.0 + index * 20.0) * vRatio)
            gc.strokeText(course.courseCode,10.0 * hRatio, (20.0 + index * 20.0) * vRatio)
        }
    }

    private fun draw() {
        val gc = graphicsContext2D

        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, width, height)

        var startAngle = 0.0
        gc.stroke = Color.GRAY
        gc.strokeOval(arcX, arcY, arcWidth, arcWidth)
        for(i in 0 until model.courseOutcomeRecords.getSize()) {
            if (model.courseOutcomeRecords.getCourseCount(i) > 0) {
                gc.fill = markLevelColors[i]
                val arcExtent =
                    model.courseOutcomeRecords.getCourseCount(i) * 360.0 / model.courseOutcomeRecords.getTotalCourseCount()
                gc.fillArc(arcX, arcY, arcWidth, arcWidth, startAngle, arcExtent, ArcType.ROUND)
                startAngle += arcExtent
            }
        }
    }

    override fun invalidated(observable: Observable?) {
        draw()
    }

}