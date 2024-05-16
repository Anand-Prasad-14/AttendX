package com.example.attendx.modal

class Record (
    val courseId : String,
    val date : String,
    val dateRec : String,
    val students : ArrayList<String>
    ){
    constructor():this("","","",ArrayList())
}