package com.example.attendx.modal

class TeacherLocation(
    val courseId : String,
    val longitude : Double,
    val latitude : Double
) {
    constructor():this("",0.0,0.0)
}