package markmgmt

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import markmgmt.model.Model
import markmgmt.view.*

class MainApplication : Application() {
    override fun start(stage: Stage) {

        val model = Model()
        val courseListViewPane = ScrollPane(CourseListView(model))
        courseListViewPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        courseListViewPane.prefWidth = 400.0

        val graphTab = TabPane().apply {
            val tab1 = Tab("Average by Term", CanvasPane(AverageByTermChartView(model)))
            val tab2 = Tab("Progress towards Degree", CanvasPane(ProgressTowardsDegreeChartView(model)))
            val tab3 = Tab("Course Outcomes", CourseOutcomePane(model))
            val tab4 = Tab("Incremental Average", CanvasPane(IncrementalAverageChartView(model)))
            tab1.isClosable = false
            tab2.isClosable = false
            tab3.isClosable = false
            tab4.isClosable = false
            style = "-fx-font-size: 12px"
            tabs.add(tab1)
            tabs.add(tab2)
            tabs.add(tab3)
            tabs.add(tab4)
        }
        val pane = BorderPane()
        pane.left = VBox(Toolbar2(model), courseListViewPane)
        pane.bottom = StatusBar(model)
        pane.center = graphTab

        stage.apply {
            title = "CS349 - A2 My Mark Visualization - w352chen"
            scene = Scene(pane.apply {
            }, 900.0, 450.0)
        }.show()
    }
}