package markmgmt.model.graph

import markmgmt.model.Course

class CourseOutcomeRecord {
    private val size = 6
    private val courseCounts = mutableListOf(0, 0, 0, 0, 0, 0, 40)
    private val courses = listOf<MutableList<Course>>(
        mutableListOf(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf() )
    var includeMissingCourses = false

    fun getSize() : Int {
        return size + if (includeMissingCourses) 1 else 0
    }
    fun clear() {
        for (i in 0 until size) {
            courseCounts[i] = 0
            courses[i].clear()
        }
        courseCounts[size] = 40
    }

    fun getTotalCourseCount() : Int {
        var total = 0
        for (i in 0 until size) {
            total += courseCounts[i]
        }
        if (includeMissingCourses) {
            total += courseCounts[size]
        }
        return total
    }

    fun addCourse(course: Course) {
        courseCounts[course.getMarkLevel()] += 1
        courses[course.getMarkLevel()].add(course)
        if (course.getMarkLevel() > 0) {
            courseCounts[size]--
        }
    }
    fun getCourseCount(level : Int) : Int {
        return courseCounts[level]
    }

    fun getCourses(level : Int) : MutableList<Course> {
        return courses[level]
    }
}