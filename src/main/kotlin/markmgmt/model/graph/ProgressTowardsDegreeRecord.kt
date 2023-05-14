package markmgmt.model.graph

import javafx.scene.paint.Color

class ProgressTowardsDegreeRecord {

    val categories = listOf("CS", "Math", "Other", "Total")
    val colorSchemeCourseTaken = listOf(Color.YELLOW, Color.DEEPPINK, Color.GRAY, Color.GREEN)
    val colorSchemeRequiredCourse = listOf(Color.LIGHTYELLOW, Color.LIGHTPINK, Color.LIGHTGRAY, Color.LIGHTGREEN)
    val size = 4
    private val requiredNumCourses = listOf(22, 8, 10, 40)
    private val coursesTaken = mutableListOf(0, 0, 0, 0)

    fun clear() {
        for (i in 0 until size) {
            coursesTaken[i] = 0
        }
    }

    fun addCSCourseTaken(n : Int = 1) {
        coursesTaken[0] += n
        coursesTaken[3] += n
    }

    private fun getCSCourseTaken() : Pair<Int, Int> {
        return Pair(coursesTaken[0], requiredNumCourses[0])
    }

    fun addMathCourseTaken(n : Int = 1) {
        coursesTaken[1] += n
        coursesTaken[3] += n
    }

    private fun getMathCourseTaken() : Pair<Int, Int> {
        return Pair(coursesTaken[1], requiredNumCourses[1])
    }

    fun addOtherCourseTaken(n : Int = 1) {
        coursesTaken[2] += n
        coursesTaken[3] += n
    }

    private fun getOtherCourseTaken() : Pair<Int, Int> {
        return Pair(coursesTaken[2], requiredNumCourses[2])
    }

    private fun getTotalCourseTaken() : Pair<Int, Int> {
        return Pair(coursesTaken[3], requiredNumCourses[3])
    }

    fun getCourseTaken(index : Int) : Pair<Int, Int> {
        return when(index) {
            0 -> getCSCourseTaken()
            1 -> getMathCourseTaken()
            2 -> getOtherCourseTaken()
            else -> getTotalCourseTaken()
        }
    }
}