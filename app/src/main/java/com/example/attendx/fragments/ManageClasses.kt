package com.example.attendx.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.*
import com.example.attendx.R
import com.example.attendx.adapter.StudentAdapter
import com.example.attendx.databinding.FragmentManageClassesBinding
import com.example.attendx.modal.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ManageClasses : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentManageClassesBinding
    private lateinit var addClass : Button
    private lateinit var courseName : EditText
    private lateinit var addBtn : Button
    private lateinit var backBtn : Button
    private lateinit var deleteBtn : Button
    private lateinit var seeResulBtn : Button
    private lateinit var courseID : TextView
    private lateinit var studentList : RecyclerView
    private lateinit var courseDescription : EditText
    private lateinit var courseNameToDisplay : TextView
    private lateinit var courseDescriptionToDisplay : TextView
    private lateinit var showAttendance : Button
    private lateinit var databaseReference: DatabaseReference
    private var courseDescriptionString : String?=null
    private var courseNameString : String?= null
    private lateinit var timer : TextView
    private var professorName : String?=null
    private var id : String?=null
    private var arrayList = ArrayList<Student>()
    private lateinit var startAttendance : Button
    lateinit var form : ConstraintLayout
    lateinit var courseInfo : ConstraintLayout
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var btnNav : BottomNavigationView
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // Use any unique integer value



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageClassesBinding.inflate(inflater, container, false)
        courseName = binding.editText
        addBtn = binding.button5
        courseDescription = binding.editText6
        courseNameToDisplay = binding.textView9
        studentList = binding.studentList
        btnNav = binding.bottomNavigationView2
        courseDescriptionToDisplay = binding.textView10
        startAttendance = binding.attendance
        seeResulBtn = binding.seeResult
        form = binding.form
        courseInfo = binding.courseInfo
        timer = binding.timer
        courseID = binding.courseID
        seeResulBtn.visibility = View.GONE

        courseNameString = courseName.text.toString()
//        courseIdString = courseId.text.toString()
        courseDescriptionString = courseDescription.text.toString()
        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]

        model.message.observe(viewLifecycleOwner) { t ->
            professorName = t!!.toString()
            Log.d("Professor name is ", professorName!!)
        }

        model.id.observe(viewLifecycleOwner
        ) { t ->
            id = t!!.toString()
            Log.d("the id is  ", id.toString())

            if (id == "-1.0") {
                startAttendance.visibility = View.GONE
            } else {
                startAttendance.visibility = View.VISIBLE
            }

            if (id != "-1.0") {
                val ordersRef =
                    FirebaseDatabase.getInstance().getReference("Course")
                        .orderByChild("courseId")
                        .equalTo(id!!)
                val valueEventListener = object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(p0: DataSnapshot) {
                        for (ds in p0.children) {
                            Log.d("add class", ds.toString())
                            val courseName = ds.child("courseName").getValue(String::class.java)
                            val courseDescription =
                                ds.child("courseDescription").getValue(String::class.java)
                            val courseIDS = ds.child("courseId").getValue(String::class.java)

                            courseID.text = "Course ID:  $courseIDS"
                            courseNameToDisplay.text = courseName
                            courseDescriptionToDisplay.text = "Description:  $courseDescription"


                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                }
                ordersRef.addValueEventListener(valueEventListener)
            }
            getkey(id!!)
            startAttendanceFun(id!!)
            seeResult(id!!)
            //showAttendance(id!!)

            btnNav.setOnNavigationItemReselectedListener { item ->
                when (item.itemId) {
                    R.id.backHome1 -> {
                        if (findNavController().currentDestination?.id == R.id.manageClasses) {
                            sendTeacherNameBackHome()
                            findNavController().navigate(R.id.action_manageClasses_to_teacherHomePage)
                        }
                    }
                    R.id.seeAttendance -> {
                        if (findNavController().currentDestination?.id == R.id.manageClasses) {
                            val bundle: Bundle =
                                bundleOf("courseId" to id!!, "professorName" to professorName)
                            findNavController().navigate(
                                R.id.action_manageClasses_to_attendanceRecord,
                                bundle
                            )
                        }
                    }
                    R.id.add1 -> {
                        form.visibility = View.VISIBLE
                        courseInfo.visibility = View.GONE
                        studentList.visibility = View.GONE
                        //showAttendance.visibility = View.GONE
                        startAttendance.visibility = View.GONE
                    }
                    R.id.delete1 -> {
                        if (id == "-1.0") {

                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Deleted")
                            builder.setMessage("This course is successfully deleted!")
                            builder.setIcon(R.drawable.sad)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val refDelete =
                                    FirebaseDatabase.getInstance().getReference("Course")
                                refDelete.child(id!!).removeValue()
                                if (findNavController().currentDestination?.id == R.id.manageClasses) {
                                    sendTeacherNameBackHome()
                                    findNavController().navigate(R.id.teacherHomePage)
                                }
                                val resultRef =
                                    FirebaseDatabase.getInstance()
                                        .getReference("AttendanceResult")
                                resultRef.addListenerForSingleValueEvent(
                                    object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {}
                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (e in p0.children) {
                                                Log.d("e key is ", e.key!!)

                                                if (e.getValue(AttendanceResult::class.java)?.courseId == id) {
                                                    resultRef.child(e.key!!).removeValue()
                                                }
                                            }
                                        }
                                    }
                                )

                                val refAttendanceIndicate = FirebaseDatabase.getInstance()
                                    .getReference("AttendanceIndicator")
                                refAttendanceIndicate.orderByChild("courseId").equalTo(id!!)
                                    .addValueEventListener(
                                        object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {}
                                            override fun onDataChange(p0: DataSnapshot) {
                                                if (p0.exists()) {
                                                    for (e in p0.children) {
                                                        refAttendanceIndicate.child(e.key!!)
                                                            .removeValue()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                val refRecord =
                                    FirebaseDatabase.getInstance().getReference("Record")
                                refRecord.orderByChild("courseId").equalTo(id!!)
                                    .addValueEventListener(
                                        object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {}
                                            override fun onDataChange(p0: DataSnapshot) {
                                                if (p0.exists()) {
                                                    for (e in p0.children) {
                                                        refRecord.child(e.key!!).removeValue()
                                                    }
                                                }
                                            }
                                        }
                                    )

                            }
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                }
            }
        }
        addBtnFun()



        val builder = AlertDialog.Builder(context)


        studentList.addOnItemTouchListener(
            RecyclerItemClickListenr(
                requireContext(),
                studentList,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val studentRef = FirebaseDatabase.getInstance().getReference("Student")
                        studentRef.orderByChild("id")
                            .equalTo(StudentAdapter(arrayList).getCIN(position))
                            .addValueEventListener(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {}
                                    override fun onDataChange(p0: DataSnapshot) {
                                        for (e in p0.children) {

                                            val student = e.getValue(Student::class.java)
                                            val fullName =
                                                student?.firstName + " " + student?.lastName
                                            builder.setTitle(student?.firstName + "' information")
                                            builder.setMessage("Name: " + fullName + "\nCIN: " + student?.id)
//                                builder.setPositiveButton("Ok"){dialog, which ->
//
//                                }
                                            val alert = builder.create()
                                            alert.setIcon(R.drawable.studenticon)
                                            alert.show()
                                        }
                                    }
                                }
                            )

                    }

                    override fun onItemLongClick(view: View?, position: Int) {}
                })
        )

        // setHasOptionsMenu(true)

        return binding.root

    }

    private fun getLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // Check for location permission before requesting the location.
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val courseId = arguments?.getString("courseId") ?: ""
                        val teacherLocation =
                            TeacherLocation(courseId, location.longitude, location.latitude)
                        val teacherLocationRef = FirebaseDatabase.getInstance().getReference("TeacherLocation")
                        val key = teacherLocationRef.push().key ?: ""
                        teacherLocationRef.child(key).setValue(teacherLocation)
                    } else {
                        Toast.makeText(requireContext(), "Hey location is null", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle location retrieval failure.
                    Toast.makeText(
                        requireContext(),
                        "Location retrieval failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            // Request location permission if not granted.
            requestLocationPermission()
        }
    }

    // You should have a function to request location permission if it's not granted.
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Handle the result of the permission request.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can call getLocation() again.
                getLocation()
            } else {
                // Permission denied, handle accordingly.
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seeResult(courseId : String){
        seeResulBtn.setOnClickListener {
            sendCourseId(courseId)
        }
    }

    private fun sendCourseId(courseId: String) {
        if (findNavController().currentDestination?.id == R.id.manageClasses) {
            val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
            model.setIdCommunicator(courseId)
            val myFragment = SeeAttendanceResult()
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            requireView().findNavController().navigate(R.id.action_manageClasses_to_seeAttendanceResult)
        }
    }

    private fun closeAttendanceFun(courseId: String) {
        val attendanceIndicatorRef = FirebaseDatabase.getInstance().getReference("AttendanceIndicator")
        attendanceIndicatorRef.orderByChild("courseId").equalTo(courseId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (e in p0.children) {
                            Log.d("first key", e.key!!)
                            attendanceIndicatorRef.child(e.key!!).child("status").setValue(false)
                        }
                    }
                }
            }
        )
    }

    private fun startAttendanceFun(courseId: String) {
        startAttendance.setOnClickListener {
            startAttendance.visibility = View.GONE
            timer.visibility = View.VISIBLE
            seeResulBtn.visibility = View.GONE

            val ref = FirebaseDatabase.getInstance().getReference("TeacherLocation")
            ref.orderByChild("courseId").equalTo(courseId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            ref.removeValue()
                            getLocation()
                        } else {
                            getLocation()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                }
            )

            val resultRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
            resultRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        for (e in p0.children) {
                            if (e.getValue(AttendanceResult::class.java)?.courseId == courseId) {
                                resultRef.child(e.key!!).removeValue()
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                }
            )

            studentList.visibility = View.GONE

            val attendanceIndicatorRef = FirebaseDatabase.getInstance().getReference("AttendanceIndicator")
            attendanceIndicatorRef.orderByChild("courseId").equalTo(courseId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            for (e in p0.children) {
                                attendanceIndicatorRef.child(e.key!!).child("status").setValue(true)
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                }
            )

            val timer = object : CountDownTimer(10000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    timer.text = "seconds remaining: \n                " + millisUntilFinished / 1000
                }

                override fun onFinish() {
                    closeAttendanceFun(courseId)
                    timer.visibility = View.GONE
                    seeResulBtn.visibility = View.VISIBLE
                    startAttendance.visibility = View.VISIBLE
                }
            }
            timer.start()
        }
    }

    private fun getkey(id: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Student")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                arrayList.clear()
                for (e in p0.children) {
                    databaseReference.child(e.key!!).child("courseId").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(p1: DataSnapshot) {
                            for (e1 in p1.children) {
                                val courseIdKey = e1.key
                                val query = databaseReference.orderByChild("courseId/$courseIdKey").equalTo(id)
                                query.addListenerForSingleValueEvent(
                                    object : ValueEventListener {
                                        override fun onDataChange(p2: DataSnapshot) {
                                            if (p2.exists()) {
                                                for (e2 in p2.children) {
                                                    val student = e2.getValue(Student::class.java)
                                                    arrayList.add(student!!)
                                                }
                                                val adapter = StudentAdapter(arrayList)
                                                studentList.adapter = adapter
                                            }
                                        }

                                        override fun onCancelled(p2: DatabaseError) {}
                                    }
                                )
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    })
                }
            }
        })
    }


    private fun sendTeacherNameBackHome() {
        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
        val user = FirebaseAuth.getInstance().currentUser
        model.setMsgCommunicator(user?.email!!)
        val myFragment = TeacherHomePage()
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun addBtnFun() {
        addBtn.setOnClickListener { view: View ->
            studentList.visibility = View.VISIBLE
            form.visibility = View.GONE
            courseInfo.visibility = View.VISIBLE
            val ref = FirebaseDatabase.getInstance().getReference("Course")
            val attendanceIndicatorRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceIndicator")
            val key = attendanceIndicatorRef.push().key
            val cId = ref.push().key!!
            val attendanceIndicatorObject = AttendanceIndicator(cId, false)
            attendanceIndicatorRef.child(key!!).setValue(attendanceIndicatorObject)

            val course = professorName?.let {
                Course(
                    courseName.text.toString(),
                    cId,
                    "",
                    courseDescription.text.toString(),
                    it,
                    "",
                    ""
                )
            }

            ref.child(cId).setValue(course)
            Toast.makeText(context, "Class added successfully", Toast.LENGTH_LONG).show()
            if (findNavController().currentDestination?.id == R.id.manageClasses) {
                sendTeacherNameBackHome()
                view.findNavController().navigate(R.id.teacherHomePage)
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

        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}