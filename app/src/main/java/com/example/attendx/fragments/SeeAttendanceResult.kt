package com.example.attendx.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.*
import com.example.attendx.adapter.ResultAdapter
import com.example.attendx.databinding.FragmentSeeAttendanceResultBinding
import com.example.attendx.modal.AttendanceResult
import com.example.attendx.modal.Course
import com.example.attendx.modal.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class SeeAttendanceResult : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentSeeAttendanceResultBinding
    private lateinit var attendedStudentList : RecyclerView
    private var arrayList = ArrayList<Student>()
    private lateinit var btnNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeeAttendanceResultBinding.inflate(layoutInflater)
        attendedStudentList = binding.attendanceListView
        btnNav = binding.nav2

        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]

        model.id.observe(viewLifecycleOwner) { t ->
            val courseId = t.toString()
            val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    arrayList.clear()

                    for (e in p0.children) {
                        Log.d("FIRST level key", e.key!!)
                        databaseReference.child(e.key!!).child("courseId")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(p1: DataSnapshot) {
                                    for (e1 in p1.children) {
                                        Log.d("second level key", e1.key!!)
                                        val query =
                                            databaseReference.orderByChild("courseId/" + e1.key)
                                                .equalTo(courseId)
                                        query.addListenerForSingleValueEvent(
                                            object : ValueEventListener {
                                                @SuppressLint("NotifyDataSetChanged")
                                                override fun onDataChange(p2: DataSnapshot) {
                                                    if (p2.exists()) {
                                                        for (e2 in p2.children) {
                                                            Log.d("hello", e2.value.toString())
                                                            val student =
                                                                e2.getValue(Student::class.java)
                                                            arrayList.add(student!!)
                                                        }

                                                        // Create the adapter and set it to the RecyclerView
                                                        val adapter =
                                                            ResultAdapter(arrayList, courseId)
                                                        attendedStudentList.adapter = adapter

                                                        // Notify the adapter of data changes
                                                        adapter.notifyDataSetChanged()
                                                    }
                                                }

                                                override fun onCancelled(p2: DatabaseError) {
                                                    // Handle onCancelled event
                                                }
                                            }
                                        )
                                    }
                                }

                                override fun onCancelled(p1: DatabaseError) {
                                    // Handle onCancelled event
                                }
                            })

                        btnNav.setOnNavigationItemReselectedListener { item ->
                            when (item.itemId) {
                                R.id.backHome4 -> {
                                    if (findNavController().currentDestination?.id == R.id.seeAttendanceResult) {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        model.setMsgCommunicator(user?.email!!)
                                        val myFragment = TeacherHomePage()
                                        val fragmentTransaction =
                                            fragmentManager!!.beginTransaction()
                                        fragmentTransaction.replace(
                                            R.id.myNavHostFragment,
                                            myFragment
                                        )
                                        fragmentTransaction.addToBackStack(null)
                                        fragmentTransaction.commit()
                                        findNavController().navigate(R.id.teacherHomePage)
                                    }
                                }
                                R.id.share -> {
                                    shareFun(courseId)
                                }
                            }
                        }
                    }
                }
            })
        }

        return binding.root
    }




    fun findAbsences(courseId: String, courseName: String){
        val list = ArrayList<String>()
        val list2 = ArrayList<String>()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Student")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                for (e in p0.children) {
                    Log.d("FIRST level key ", e.key!!)

                    databaseReference.child(e.key!!).child("courseId")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p1: DataSnapshot) {
                                arrayList.clear()
                                for (e1 in p1.children) {
                                    Log.d("second level key ", e1.key!!)
                                    val query =
                                        databaseReference.orderByChild("courseId/" + e1.key)
                                            .equalTo(courseId)
                                    query.addListenerForSingleValueEvent(
                                        object : ValueEventListener {
                                            override fun onDataChange(p2: DataSnapshot) {

                                                if (p2.exists()) {

                                                    for (e2 in p2.children) {
                                                        Log.d(
                                                            "hello",
                                                            e2.value.toString()
                                                        )
                                                        val student =
                                                            e2.getValue(Student::class.java)
                                                        list.add("Name: "+student?.firstName + " " + student?.lastName + "\n CIN: " + student?.id)

                                                        val attendanceRef = FirebaseDatabase.getInstance()
                                                            .getReference("AttendanceResult")
                                                        attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                                                            object : ValueEventListener {
                                                                override fun onCancelled(p3: DatabaseError) {}
                                                                @SuppressLint("SimpleDateFormat",
                                                                    "IntentReset"
                                                                )
                                                                override fun onDataChange(p3: DataSnapshot) {
                                                                    if(p3.exists()){
                                                                        for(e3 in p3.children) {
                                                                            val attendanted = e3.getValue(
                                                                                AttendanceResult::class.java)
                                                                            list2.add("Name: "+attendanted!!.name + "\n CIN: " + student?.id)
                                                                        }

                                                                    }
                                                                    list.removeAll(list2.toSet())
                                                                    val current = Date()
                                                                    val formatter =
                                                                        SimpleDateFormat("yyyy-MM-dd")
                                                                    val final = formatter.format(current)
                                                                    val user = FirebaseAuth.getInstance().currentUser
                                                                    val mIntent =
                                                                        Intent(Intent.ACTION_SEND)
                                                                    mIntent.data =
                                                                        Uri.parse("mailto:")
                                                                    mIntent.type = "text/plain"
                                                                    Log.d(
                                                                        "The user email iss ",
                                                                        user?.email!!
                                                                    )
                                                                    mIntent.putExtra(
                                                                        Intent.EXTRA_EMAIL,arrayOf(
                                                                        user.email!!))
                                                                    mIntent.putExtra(
                                                                        Intent.EXTRA_SUBJECT,
                                                                        "Absent Student for $courseName on $final"
                                                                    )

                                                                    val sb = StringBuilder()
                                                                    for (s in list) {
                                                                        sb.append(s)
                                                                        sb.append("\n")
                                                                    }
                                                                    mIntent.putExtra(Intent.EXTRA_TEXT,sb.toString())

                                                                    try {
                                                                        //start email intent
                                                                        startActivity(
                                                                            Intent.createChooser(
                                                                                mIntent,
                                                                                "Choose Email Client..."
                                                                            )
                                                                        )
                                                                    }
                                                                    catch (_: Exception){

                                                                    }



                                                                }


                                                            })

                                                    }

                                                }
                                            }

                                            override fun onCancelled(p2: DatabaseError) {
                                            }
                                        }
                                    )

                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                            }


                        })

                }
            }


        })






    }


    fun shareFun(courseId : String){
        val attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
        attendanceRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
            object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    for(e in p0.children){
                        val result = e.getValue(AttendanceResult::class.java)
                        val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                        courseRef.orderByChild("courseId").equalTo(courseId).addValueEventListener(
                            object: ValueEventListener {
                                override fun onCancelled(p1: DatabaseError) {}
                                override fun onDataChange(p1: DataSnapshot) {

                                    for(e1 in p1.children){
                                        val course = e1.getValue(Course::class.java)!!
                                        findAbsences(courseId, course.courseName)
                                    }

                                }
                            }
                        )


                    }
                }
            }
        )

    }

    // TODO: Rename method, update argument and hook method into UI event
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