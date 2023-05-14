package markmgmt.model.graph

import markmgmt.model.Course
import kotlin.math.pow

class IncrementalAverageEntry(preCourseList : MutableList<Course>) {
    var term : String = ""
    var average = 0.0
    var deviation = 0.0
    val courseList = mutableListOf<Course>()
    var currTermCourses = 0

    init {
        preCourseList.forEach{ c -> courseList.add(c) }
    }

    fun calculateAverageAndStandardDeviation() {
        average = 0.0
        var count = 0
        for (course in courseList) {
            if (!course.hasWithdrawn) {
                average += course.getMark().toDouble()
                count ++
            }
        }
        if (count > 0) {
            average /= count
        }
        deviation = 0.0
        if (courseList.size > 1) {
            for (course in courseList) {
                if (!course.hasWithdrawn) {
                    deviation += (course.getMark().toDouble() - average).pow(2.0)
                }
            }
            deviation = kotlin.math.sqrt(deviation / (courseList.size - 1))
        }
    }

}