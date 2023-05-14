package markmgmt.view

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import markmgmt.model.Model

class StatusBar(private val model: Model) : HBox(), InvalidationListener {
    private val courseAverageUI = Label()
    private val courseTakenUI = Label()
    private val courseFailedUI = Label()
    private val courseWithdrawnUI = Label()

    init {
        model.addListener(this)
        courseAverageUI.prefWidth = 130.0
        courseTakenUI.prefWidth = 110.0
        courseFailedUI.prefWidth = 110.0
        courseWithdrawnUI.prefWidth = 120.0
        children.addAll(courseAverageUI, Separator(Orientation.VERTICAL), courseTakenUI,
            Separator(Orientation.VERTICAL), courseFailedUI, Separator(Orientation.VERTICAL), courseWithdrawnUI)
        invalidated(null)
        alignment = Pos.CENTER_LEFT
        spacing = 5.0
    }

    override fun invalidated(observable: Observable?) {
        courseAverageUI.text = "Courses average: " + model.courseAverage
        courseTakenUI.text = "Courses taken: " + model.courseTaken
        courseFailedUI.text = "Courses failed: " + model.courseFailed
        courseWithdrawnUI.text = "Courses WD'ed: " + model.courseWithdrawn
    }
}