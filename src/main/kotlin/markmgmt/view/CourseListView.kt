package markmgmt.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import markmgmt.model.Course
import markmgmt.model.CourseException
import markmgmt.model.Model

class CourseListView(private val model: Model) : VBox(), InvalidationListener {
    private val termNames  = listOf("F19", "W20", "S20", "F20", "W21", "S21", "F21", "W22", "S22", "F22", "W23")
    private val markLevelColors = listOf(Color.DARKSLATEGRAY, Color.LIGHTCORAL, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.SILVER, Color.GOLD)

    init {
        model.addListener(this)
        maxWidth = Double.MAX_VALUE
        prefWidth = 400.0
        minWidth = 400.0
        refreshView()

    }

    private fun refreshView() {
        children.clear()
        model.getCourseList().forEach { course ->
            val row = renderCourseRow(course)
            children.add(row)
        }
    }

    private fun renderCourseRow(course: Course): HBox {
        val courseCodeUI = TextField(course.courseCode)
        courseCodeUI.maxWidth = 80.0
        courseCodeUI.prefWidth = 80.0
        courseCodeUI.minWidth = 80.0
        courseCodeUI.isEditable = false
        val termListUI = ComboBox(FXCollections.observableList(termNames))
        termListUI.value = course.term
        termListUI.maxWidth = 75.0
        termListUI.prefWidth = 75.0
        termListUI.minWidth = 75.0
        val markUI = TextField(course.getMark())
        markUI.maxWidth = 40.0
        markUI.prefWidth = 40.0
        markUI.minWidth = 40.0
        markUI.isEditable = true
        val updateButton = Button("Update")
        updateButton.maxWidth = 60.0
        updateButton.prefWidth = 60.0
        updateButton.minWidth = 60.0
        updateButton.isDisable = true
        val undoOrDeleteButton = Button("Delete")
        undoOrDeleteButton.maxWidth = 60.0
        undoOrDeleteButton.prefWidth = 60.0
        undoOrDeleteButton.minWidth = 60.0
        val row = HBox(courseCodeUI, termListUI, markUI, updateButton, undoOrDeleteButton)
        row.padding = Insets(10.0)
        row.spacing = 10.0
        row.alignment = Pos.CENTER_LEFT
        row.border = Border(
            BorderStroke(
                Color.GRAY,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
        )
        row.prefHeight = 35.0
        row.maxHeight = 35.0
        row.maxWidth = 400.0
        row.prefWidth = 400.0
        row.minWidth = 400.0
        row.background = Background(BackgroundFill(markLevelColors[course.getMarkLevel()], CornerRadii.EMPTY, Insets.EMPTY))

        var disableUpdateEvent = false
        termListUI.valueProperty().addListener{ _, _, _->
            if (!disableUpdateEvent) {
                updateButton.isDisable = false
                undoOrDeleteButton.text = "Undo"
            }
        }
        markUI.textProperty().addListener{ _, _, _->
            if (!disableUpdateEvent) {
                updateButton.isDisable = false
                undoOrDeleteButton.text = "Undo"
            }
        }
        updateButton.onAction = EventHandler {
            try {
                model.updateCourse(courseCodeUI.text, termListUI.value, markUI.text)
                updateButton.isDisable = true
                undoOrDeleteButton.text = "Delete"
                row.background = Background(BackgroundFill(markLevelColors[model.getMarkLevel(markUI.text)], CornerRadii.EMPTY, Insets.EMPTY))
            } catch (e : CourseException) {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Course data error"
                alert.contentText = e.message
                alert.showAndWait()
            }
        }
        undoOrDeleteButton.onAction = EventHandler{
            if (undoOrDeleteButton.text.equals("Undo")) {
                updateButton.isDisable = true
                undoOrDeleteButton.text = "Delete"
                val c : Course? = model.getCourse(courseCodeUI.text)
                if (c != null) {
                    disableUpdateEvent = true
                    termListUI.value = c.term
                    markUI.text = c.getMark()
                    disableUpdateEvent = false
                }
            } else {
                model.removeCourse(courseCodeUI.text)
            }
        }
        return row
    }

    override fun invalidated(observable: Observable?) {
        refreshView()
    }
}