package com.example.attendx.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.attendx.*
import com.example.attendx.databinding.FragmentAttendancePageBinding
import com.example.attendx.modal.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class AttendancePage : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentAttendancePageBinding
    private lateinit var courseName : TextView
    private lateinit var courseDesc : TextView
    private lateinit var professorName : TextView
    private lateinit var attendanceBtn : Button
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val arr = ArrayList<String>()
    private lateinit var navBtn : BottomNavigationView
    private lateinit var navController : NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    @SuppressLint("CommitTransaction")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendancePageBinding.inflate(inflater, container, false)
        courseName = binding.courseName
        courseDesc = binding.courseDescrp
        attendanceBtn = binding.attendance
        professorName = binding.professorName
        navBtn = binding.btnNav3
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())



        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
        model.id.observe(viewLifecycleOwner) { t ->
            val id = t.toString()

            val courseRef = FirebaseDatabase.getInstance().getReference("Course")
            courseRef.orderByChild("courseId").equalTo(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        for (e in p0.children) {
                            val course = e.getValue(Course::class.java)
                            courseName.text = course?.courseName
                            courseDesc.text = course?.courseDescription
                            professorName.text = course?.professorName
                        }
                    }

                })
            detectAttendanceFun(id)
            getLocation(id)

            navBtn.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.backHome2 -> {
                        backFun()
                        true
                    }
                    R.id.dropCourse -> {
                        dropClass(id)
                        true
                    }
                    else -> false
                }
            }
        }

        val ft = requireFragmentManager().beginTransaction()
        ft.setReorderingAllowed(false)
        ft.detach(this).attach(this)


//        val arr = FloatArray(1)
//        val distanceBtnLoc1And2 = Location.distanceBetween(34.07921,-117.9635383, 34.0793078,-117.9635858,arr)
//
//        Log.d("The arr", arr[0].toString())

        return binding.root

    }

    private fun getLocation(courseId: String){
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if(location!= null){
                    var tLatitude = 0.0
                    var tLongtitude = 0.0

                    val ref = FirebaseDatabase.getInstance().getReference("TeacherLocation")
                    ref.orderByChild("courseId").equalTo(courseId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                for(e in p0.children){
                                    val tLocation = e.getValue(TeacherLocation::class.java)
                                    tLatitude = tLocation?.latitude!!
                                    tLongtitude = tLocation.longitude
                                }


                                val arr = FloatArray(1)
                                val distanceBtnLoc1And2 = Location.distanceBetween(
                                    tLatitude,
                                    tLongtitude,
                                    location.latitude,
                                    location.longitude,
                                    arr
                                )

                                Log.d(
                                    "The student location is ",
                                    location.latitude.toString() + " - " + location.longitude.toString()
                                )
                                Log.d("The arr", arr[0].toString())


                                if(arr[0] < 80.0){
                                    attendanceBtn.setOnClickListener { takeAttendance(courseId)}
                                }else{
                                    attendanceBtn.setOnClickListener {
                                        val builder = AlertDialog.Builder(context)
                                        builder.setTitle("FATAL")
                                        builder.setMessage("YOU ARE NOT IN CLASSROOM!!")
                                        val alert = builder.create()
                                        alert.setIcon(R.drawable.angry)
                                        alert.show()
                                    }
                                }


                            }
                        })

                }
                else{
                    Toast.makeText(requireContext(), "Hey location is null", Toast.LENGTH_LONG).show()
                }


            }

    }

    private fun takeAttendance(courseId: String) {
        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]

        model.name.observe(viewLifecycleOwner) { t ->
            val name = t.toString()

            Log.d("name is ", name)
            val attendanceResult = FirebaseDatabase.getInstance().getReference("AttendanceResult")
            val key = attendanceResult.push().key
            val attendance = AttendanceResult(courseId, name)
            attendanceResult.child(key!!).setValue(attendance)

            val recRef = FirebaseDatabase.getInstance().getReference("AttendanceResult")
            recRef.orderByChild("courseId").equalTo(courseId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        val arr = mutableListOf<String>()
                        for (e in p0.children) {
                            arr.add(e.getValue(AttendanceResult::class.java)?.name!!)
                        }

                        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault())
                        val currentDate = sdf.format(Date())
                        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateRecord = df.format(Date())
                        val record = Record(courseId, currentDate, dateRecord, arr as ArrayList<String>)
                        val ref1 = FirebaseDatabase.getInstance().getReference("Record")
                        val keys = ref1.push().key!!
                        ref1.child(keys).setValue(record)
                    }
                })

            attendanceBtn.alpha = 0.5f
            attendanceBtn.isEnabled = false
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Success")
            builder.setMessage("Your attendance is recorded!")
            builder.setPositiveButton("Ok") { _, _ ->
                // Handle Ok button click if needed
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun detectAttendanceFun(courseId: String) {
        val attendanceIndicatorRef = FirebaseDatabase.getInstance().getReference("AttendanceIndicator")
        attendanceIndicatorRef.orderByChild("courseId").equalTo(courseId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (e in p0.children) {
                            Log.d("first key", e.key!!)
                            val status = e.getValue(AttendanceIndicator::class.java)
                            if (status?.status == true) {
                                Log.d("status is ", status.status.toString())
                                attendanceBtn.alpha = 1.0f
                                attendanceBtn.isEnabled = true
                            } else {
                                if (status != null) {
                                    Log.d("status is ", status.status.toString())
                                }
                                attendanceBtn.alpha = 0.5f
                                attendanceBtn.isEnabled = false
                            }
                        }
                    }
                }
            })
    }

    private fun dropClass(courseId: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val studentRef = FirebaseDatabase.getInstance().getReference("Student")
        studentRef.orderByChild("email").equalTo(user!!.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    for (e in p0.children) {
                        val studentKey = e.key
                        studentRef.child(studentKey!!).child("courseId")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(p1: DataSnapshot) {
                                    for (e1 in p1.children) {
                                        if (e1.getValue(String::class.java) == courseId) {
                                            studentRef.child(studentKey).child("courseId")
                                                .child(e1.key!!).removeValue()
                                        }
                                    }
                                }

                                override fun onCancelled(p1: DatabaseError) {}
                            })
                    }
                }
            })

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Dropped")
        builder.setMessage("This course is successfully dropped!")
        builder.setIcon(R.drawable.sad)
        builder.setPositiveButton("Ok") { _,  _->
            if (navController.currentDestination?.id == R.id.attendancePage) {
                val user = FirebaseAuth.getInstance().currentUser
                val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                model.setMsgCommunicator(user?.email!!)

                view?.findNavController()?.navigate(R.id.studentHomePage)
            }
        }
        val alert = builder.create()
        alert.show()
    }

    private fun backFun() {
        if (navController.currentDestination?.id == R.id.attendancePage) {
            val user = FirebaseAuth.getInstance().currentUser
            val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
            model.setMsgCommunicator(user?.email!!)

            navController.navigate(R.id.studentHomePage)
        }
    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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