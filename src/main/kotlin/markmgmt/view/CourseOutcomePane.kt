package markmgmt.view

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import markmgmt.model.Model

class CourseOutcomePane(model: Model) : BorderPane() {

    init {
        val includeMissingCourseCbx = CheckBox("Include Missing Courses")
        val vbox = VBox(includeMissingCourseCbx)
        vbox.alignment = Pos.CENTER
        center = CanvasPane(CourseOutcomeChartView(model))
        bottom = vbox

        includeMissingCourseCbx.onAction = EventHandler {
            model.includeMissingCoursesInCourseOutcomeRecord(includeMissingCourseCbx.isSelected)
        }

    }
}