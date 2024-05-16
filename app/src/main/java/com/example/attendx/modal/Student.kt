package com.example.attendx.modal

class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String, // Add email property
    val courseId: HashMap<String, String>
) {
    constructor() : this("", "", "", "", HashMap()) {
    }
}
