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
import markmgmt.model.CourseException
import markmgmt.model.Model


class Toolbar2(private val model: Model) : HBox(), InvalidationListener {

    init {
        model.addListener(this)
        val courseCodeUI = TextField()
        courseCodeUI.maxWidth = 80.0
        courseCodeUI.prefWidth = 80.0
        courseCodeUI.minWidth = 80.0
        val termListUI = ComboBox(FXCollections.observableList(model.termNames))
        termListUI.maxWidth = 75.0
        termListUI.prefWidth = 75.0
        termListUI.minWidth = 75.0
        val markUI = TextField()
        markUI.maxWidth = 40.0
        markUI.prefWidth = 40.0
        markUI.minWidth = 40.0
        val createButton = Button("Create")
        createButton.maxWidth = 60.0
        createButton.prefWidth = 60.0
        createButton.minWidth = 60.0
        createButton.onAction = EventHandler {
            val courseCode = courseCodeUI.text
            var term = termListUI.value
            if (term == null) {
                term = ""
            }
            val mark = markUI.text
            try {
                model.addCourse(courseCode, term, mark) // delete courseName, at the second pos
            } catch (e : CourseException) {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Course data error"
                alert.contentText = e.message
                alert.showAndWait()
            }

        }
        val blankUI = Label()
        blankUI.maxWidth = 60.0
        blankUI.prefWidth = 60.0
        blankUI.minWidth = 60.0
        children.addAll(courseCodeUI, termListUI, markUI, blankUI, createButton) // delete courseName at the second pos
        padding = Insets(10.0)
        spacing = 10.0
        alignment = Pos.CENTER_LEFT
        border = Border(
            BorderStroke(
                Color.GRAY,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
        )
        prefHeight = 35.0
        maxHeight = 35.0
        minWidth = 150.0 // change from 300.0 into 150.0
        background = Background(BackgroundFill(Color.LIGHTGRAY , CornerRadii.EMPTY, Insets.EMPTY))
    }

    override fun invalidated(observable: Observable?) {
    }
}