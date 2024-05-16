package com.example.attendx.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.modal.Course
import com.example.attendx.R
import com.squareup.picasso.Picasso
import java.util.*

class CourseAdapter(private val data: ArrayList<Course>, val type : String) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val course = data[position]

        if(type == "Student") {
            holder.className.text = data[position].courseName
            holder.ids.text = "ID: " + data[position].courseId
            holder.name.text = "Teacher: " + data[position].professorName
            holder.dept.text =  data[position].departmentName
            holder.branch.text = data[position].branchYear

            val imageUrl = course.imageUrl

            if (imageUrl.isNotEmpty()){
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.teachercourse) // Placeholder image while loading
                    .error(R.drawable.teachercourse) // Error image if the download fails
                    .into(holder.img)
            }
            else{
                Log.e("Firebase", "Empty imageUrl for courseId: ${data[position].courseId}")
                Log.d("Firebase", "Image URL: $imageUrl")
                holder.img.setImageResource(R.drawable.teachercourse)
            }




        }

        if(type == "Teacher"){
            holder.className.text = data[position].courseName
            holder.ids.text = "ID: " + data[position].courseId
            holder.name.text = Date().toString()
            holder.dept.text =  data[position].departmentName
            holder.branch.text = data[position].branchYear
            val imageUrl = course.imageUrl

            if (imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.teachercourse)
                    .error(R.drawable.teachercourse)
                    .into(holder.img)
            } else {
                Log.e("Firebase", "Empty imageUrl for courseId: ${course.courseId}")
                Log.d("Firebase", "Image URL: $imageUrl")
                // Set a default image when the URL is empty
                holder.img.setImageResource(R.drawable.teachercourse)
            }


        }
    }
    fun getID(position: Int) = data[position].courseId

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_item_view, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val className: TextView = itemView.findViewById(R.id.className)
        val img : ImageView = itemView.findViewById(R.id.imageView2)
        val ids : TextView = itemView.findViewById(R.id.ids)
        val name : TextView = itemView.findViewById(R.id.name)
        val dept : TextView = itemView.findViewById(R.id.deptName)
        val branch : TextView = itemView.findViewById(R.id.branchtext)
    }
}