package markmgmt.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import markmgmt.model.graph.CourseOutcomeRecord
import markmgmt.model.graph.IncrementalAverageEntry
import markmgmt.model.graph.ProgressTowardsDegreeRecord
import markmgmt.model.graph.TermAverageEntry


class Model : Observable {

    private var courseList = mutableListOf<Course>() // all courses
    val termNames  = listOf("F19", "W20", "S20", "F20", "W21", "S21", "F21", "W22", "S22", "F22", "W23")
    var courseAverage = 0.0
    var courseTaken = 0
    var courseFailed = 0
    var courseWithdrawn = 0

    val termAverageData = mutableListOf<TermAverageEntry>()
    val progressTowardsDegreeRecord = ProgressTowardsDegreeRecord()
    val courseOutcomeRecords = CourseOutcomeRecord()
    val incrementalAverageData = mutableListOf<IncrementalAverageEntry>()


    // a list of all subscribed views / views that listen to the model / views that observe the model
    private val views = mutableListOf<InvalidationListener?>()

    /**
     * Add listener to receive notifications about changes in the [Model].
     * @param listener the listener that is added to the [Model]
     */
    override fun addListener(listener: InvalidationListener?) {
        views.add(listener)
    }

    /**
     * Remove listener to stop receiving notifications about changes in the [Model].
     * @param listener the listener that is removed from the [Model]
     */
    override fun removeListener(listener: InvalidationListener?) {
        views.remove(listener)
    }

    private fun updateAllViews() {
        views.forEach { it?.invalidated(this) }
    }

    fun getCourseList() : List<Course> {
        return courseList
    }

    fun getCourse(courseCode : String) : Course? {
        for (course in courseList) {
            if (course.courseCode == courseCode) {
                return course
            }
        }
        return null
    }

    private fun calculateCourseStatsAndGraphData() {
        // sort by term
        courseList.sortWith{ c1, c2 ->
            c1.getTermStart().compareTo(c2.getTermStart())
        }

        // calculate stats
        var totalMarks = 0
        courseTaken = 0
        courseFailed = 0
        courseWithdrawn = 0
        courseList.forEach{
            if (it.hasWithdrawn) {
                courseWithdrawn ++
            } else {
                courseTaken++
                totalMarks += it.mark
                if (it.mark < 50) {
                    courseFailed++
                }
            }
        }
        courseAverage = if (courseTaken == 0) 0.0 else totalMarks / courseTaken * 1.0

        // Calculate Graph Data - Term Average
        termAverageData.clear()
        var effectiveTerm = false
        termNames.forEach { term ->
            var totalCourses = 0
            var totalScore = 0.0
            var hasWDCourse = false
            courseList.forEach{ course ->
                if (term == course.term) {
                    if (!course.hasWithdrawn) {
                        totalCourses++
                        totalScore += course.mark
                    } else {
                        hasWDCourse = true
                    }
                }
            }
            if (totalCourses > 0 || hasWDCourse) {
                effectiveTerm = true
            }
            if (effectiveTerm) {
                var average = -1.0
                if (totalCourses != 0) {
                    average = totalScore / totalCourses
                } else if (hasWDCourse) {
                    average = -2.0  // has only WD courses
                }
                termAverageData.add(TermAverageEntry(term, average))
            }
        }

        for (i in termAverageData.size - 1 downTo 0) {
            if (termAverageData[i].averageScore == -1.0) {
                termAverageData.removeAt(i)
            } else {
                break
            }
        }

        // Calculate Graph Data - Progress towards Degree
        progressTowardsDegreeRecord.clear()
        courseList.forEach{ course ->
            if (!course.hasWithdrawn) {
                if (course.isCSCourse()) {
                    progressTowardsDegreeRecord.addCSCourseTaken()
                } else if (course.isMathCourse()) {
                    progressTowardsDegreeRecord.addMathCourseTaken()
                } else {
                    progressTowardsDegreeRecord.addOtherCourseTaken()
                }
            }
        }

        // Calculate Graph Data - Course Outcome
        courseOutcomeRecords.clear()
        for (course in courseList) {
            courseOutcomeRecords.addCourse(course)
        }

        // Calculate Graph Data - Incremental Average
        incrementalAverageData.clear()
        effectiveTerm = false
        var preCourseList = mutableListOf<Course>()
        termNames.forEach { term ->
            var totalCourseInCurrentTerm = 0
            val incrementalAverageEntry = IncrementalAverageEntry(preCourseList)
            incrementalAverageEntry.term = term
            var hasWDCourses = false
            courseList.forEach{ course ->
                if (term == course.term) {
                    if (!course.hasWithdrawn) {
                        incrementalAverageEntry.courseList.add(course)
                        totalCourseInCurrentTerm++
                    } else {
                        hasWDCourses = true
                    }
                }
            }
            if (totalCourseInCurrentTerm == 0 && hasWDCourses) {
                totalCourseInCurrentTerm = -2
            }
            incrementalAverageEntry.currTermCourses = totalCourseInCurrentTerm
            if (totalCourseInCurrentTerm != 0) {
                effectiveTerm = true
            }
            if (effectiveTerm) {
                incrementalAverageEntry.calculateAverageAndStandardDeviation()
                incrementalAverageData.add(incrementalAverageEntry)
                preCourseList = incrementalAverageEntry.courseList
            }
        }

        for (i in incrementalAverageData.size - 1 downTo 0) {
            if (incrementalAverageData[i].currTermCourses == 0) {
                incrementalAverageData.removeAt(i)
            } else {
                break
            }
        }
    }

    fun addCourse(courseCode : String, term : String, markStr : String) {
        val mark = validateCourseData(courseCode.uppercase(), term, markStr.uppercase())
        courseList.add(Course(courseCode.uppercase(), term, mark, mark == -1))
        calculateCourseStatsAndGraphData()
        updateAllViews()
    }

    private fun validateCourseData(courseCode: String?, term: String?, markStr: String?, forUpdate : Boolean = false): Int {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw CourseException("Course code is mandatory!")
        } else if (term == null || term.trim().isEmpty()) {
            throw CourseException("Course term is mandatory!")
        } else if (markStr == null || markStr.trim().isEmpty()) {
            throw CourseException("Course mark must be an integer between 0 and 100, or WD!")
        }
        if (!forUpdate) {
            for (course in courseList) {
                if (course.courseCode == courseCode) {
                    throw CourseException("Course code ($courseCode) has already existed.")
                }
            }
        }
        val mark : Int
        if (markStr == "WD") {
            mark = -1
        } else {
            try{
                mark = Integer.parseInt(markStr)
                if (mark < 0 || mark > 100) {
                    throw CourseException("Course mark must be an integer between 0 and 100, or WD!")
                }
            } catch (e: NumberFormatException) {
                throw CourseException("Course mark must be an integer between 0 and 100, or WD!")
            }
        }
        return mark
    }

    fun removeCourse(courseCode: String?) {
        for (course in courseList) {
            if (course.courseCode == courseCode) {
                courseList.remove(course)
                calculateCourseStatsAndGraphData()
                updateAllViews()
                break
            }
        }
    }

    fun updateCourse(courseCode: String, term : String, markStr: String) {
        for (course in courseList) {
            if (course.courseCode == courseCode) {
                val mark = validateCourseData(courseCode, term, markStr.uppercase(), true)
                course.term = term
                course.mark = mark
                course.hasWithdrawn = (mark == -1)
                calculateCourseStatsAndGraphData()
                updateAllViews()
                break
            }
        }
    }

    fun getMarkLevel(markStr : String) : Int {
        val mark : Double = if (markStr.uppercase() == "WD") -1.0 else markStr.toDouble()
        return if (mark == -1.0) {
            0
        } else if (mark < 50.0) {
            1
        } else if (mark < 60.0) {
            2
        } else if (mark < 91.0) {
            3
        } else if (mark < 96.0) {
            4
        } else {
            5
        }
    }

    fun includeMissingCoursesInCourseOutcomeRecord(include : Boolean) {
        courseOutcomeRecords.includeMissingCourses = include
        updateAllViews()
    }
}