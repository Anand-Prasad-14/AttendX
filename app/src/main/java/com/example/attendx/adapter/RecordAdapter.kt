package com.example.attendx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.R
import com.example.attendx.modal.Record

class RecordAdapter(private val data: ArrayList<Record>, val context : Context): RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.date.text = data[position].date
        holder.list.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1,data[position].students)
        holder.courseName.text = data[position].courseId
        holder.dateRec.text = data[position].dateRec
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() : Int {
    return data.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.studentName)
        val list: ListView = itemView.findViewById(R.id.Student)
        val courseName: TextView = itemView.findViewById(R.id.textView15)
        val dateRec: TextView = itemView.findViewById(R.id.studentName)
    }
}