package markmgmt.model

class Course (
    var courseCode : String,
    var term : String,
    var mark : Int,
    var hasWithdrawn : Boolean) {

    fun getMark() : String {
        return if (hasWithdrawn) {
            "WD"
        } else {
            mark.toString()
        }
    }

    fun getMarkLevel() : Int {
        return if (hasWithdrawn) {
            0
        } else if (mark < 50) {
            1
        } else if (mark < 60) {
            2
        } else if (mark < 91) {
            3
        } else if (mark < 96) {
            4
        } else {
            5
        }
    }

    fun getTermStart() : String {
        return term.slice(1..2) +
            when(term.slice(0..0)) {
                "W" -> "01"
                "S" -> "05"
                else -> "09"
            }
    }

    fun isCSCourse() : Boolean {
        return courseCode.startsWith("CS")
    }

    fun isMathCourse() : Boolean {
        return courseCode.startsWith("MATH") ||
                courseCode.startsWith("STAT") ||
                courseCode.startsWith("CO")
    }

}