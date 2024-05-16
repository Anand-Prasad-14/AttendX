package com.example.attendx.modal

class AttendanceIndicator(
    val courseId : String,
    val status : Boolean
) {
    constructor():this("",false)
}