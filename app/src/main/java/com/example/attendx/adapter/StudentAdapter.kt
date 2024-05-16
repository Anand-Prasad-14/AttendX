package com.example.attendx.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.R
import com.example.attendx.modal.Student

class StudentAdapter(private val data : ArrayList<Student>) : RecyclerView.Adapter<StudentAdapter.ViewHolder>()  {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.studentName.text = data[position].firstName + " " + data[position].lastName
        holder.img.setImageResource(R.drawable.studenticon)

    }
    fun getID(position: Int) = data[position].courseId
    fun getName(position: Int) = data[position].firstName
    fun getCIN(position: Int) = data[position].id


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.student_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val img: ImageView = itemView.findViewById(R.id.studentIcon)

    }
}