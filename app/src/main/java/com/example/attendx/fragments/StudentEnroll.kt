package com.example.attendx.fragments

import android.R
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.attendx.*
import com.example.attendx.databinding.FragmentStudentEnrollBinding
import com.example.attendx.modal.Course
import com.example.attendx.modal.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StudentEnroll : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentEnrollBinding
    private lateinit var addCourseBtn : Button
    private lateinit var backBtn : Button
    private lateinit var btnNav : BottomNavigationView
    private var temp : String?=null
    private lateinit var spinnerList : Spinner
    private var arr = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentEnrollBinding.inflate(inflater, container, false)
        addCourseBtn = binding.addClassBtn
        spinnerList = binding.courseInput
        btnNav = binding.bottomNavigationView

        val courseRef = FirebaseDatabase.getInstance().getReference("Course")
        val user = FirebaseAuth.getInstance().currentUser

        courseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arr.clear()

                for (e in dataSnapshot.children) {
                    val course = e.getValue(Course::class.java)
                    arr.add(course!!.courseName + " -- " + course.professorName)

                    val studentCourseRef = FirebaseDatabase.getInstance().getReference("Student")
                    studentCourseRef.orderByChild("email").equalTo(user?.email).addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (e1 in snapshot.children) {
                                    val studentCourseList = e1.getValue(Student::class.java)?.courseId?.values
                                    if (studentCourseList?.contains(course.courseId) == true) {
                                        arr.remove(course.courseName + " -- " + course.professorName)
                                    }
                                }

                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.simple_spinner_item,
                                    arr
                                )
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerList.adapter = arrayAdapter
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        spinnerList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ref = FirebaseDatabase.getInstance().getReference("Course")
                val selected = arr[position].split(" -- ")

                ref.orderByChild("courseName").equalTo(selected[0]).addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            for (e in p0.children) {
                                val courseId = e.getValue(Course::class.java)?.courseId
                                if (courseId != null) {
                                    addClassFun(courseId)
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    }
                )
            }
        }

        btnNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                com.example.attendx.R.id.backHome -> {
                    if (findNavController().currentDestination?.id == com.example.attendx.R.id.studentEnroll) {

                        findNavController().navigate(com.example.attendx.R.id.studentHomePage)
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

    private fun addClassFun(courseInput: String) {
        addCourseBtn.setOnClickListener { view: View ->
            if (findNavController().currentDestination?.id == com.example.attendx.R.id.studentEnroll) {
                val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                val user = FirebaseAuth.getInstance().currentUser
                model.setMsgCommunicator(user?.email!!)

                val databaseReference = FirebaseDatabase.getInstance().reference
                val studentKey = arguments?.getString("key").toString()
                databaseReference.child("Student").child(studentKey).child("courseId").push()
                    .setValue(courseInput)

                view.findNavController().navigate(com.example.attendx.R.id.studentHomePage)
            }
        }
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