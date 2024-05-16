package com.example.attendx.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.*
import com.example.attendx.adapter.RecordAdapter
import com.example.attendx.databinding.FragmentAttendanceRecordBinding
import com.example.attendx.modal.Course
import com.example.attendx.modal.Record
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AttendanceRecord : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendanceRecordBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var btn : Button
    private lateinit var courseName : TextView
    private var arr = ArrayList<Record>()
    private lateinit var btnNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceRecordBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView2
        courseName = binding.textView17
        btnNav = binding.nv2

        val courseId = arguments?.getString("courseId")
        val ref1 = FirebaseDatabase.getInstance().getReference("Course")
        ref1.orderByChild("courseId").equalTo(courseId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    for (e in p0.children) {
                        val course = e.getValue(Course::class.java)
                        courseName.text = course?.courseName
                    }
                }
            })

        val ref = FirebaseDatabase.getInstance().getReference("Record")
        ref.orderByChild("courseId").equalTo(courseId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    arr.clear()
                    for (e in p0.children) {
                        val record = e.getValue(Record::class.java)
                        record?.let {
                            arr.add(it)
                        }
                    }
                    val layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.layoutManager = layoutManager
                    val adapter = RecordAdapter(arr, requireContext())
                    recyclerView.adapter = adapter
                }
            })

        btnNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    if (findNavController().currentDestination?.id == R.id.attendanceRecord) {
                        val bundle = bundleOf("courseId" to courseId)
                        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                        model.setIdCommunicator(courseId.orEmpty())
                        findNavController()
                            .navigate(R.id.action_attendanceRecord_to_manageClasses, bundle)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }

        return binding.root

    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

    }
}