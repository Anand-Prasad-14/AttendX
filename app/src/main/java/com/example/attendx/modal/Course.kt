package com.example.attendx.modal

class Course (
    val courseName : String,
    val courseId : String,
    val departmentName: String ,
    val courseDescription : String,
    val professorName : String,
    val branchYear : String,
    val imageUrl: String

){
    constructor() : this("","","","","","",""){}
}

