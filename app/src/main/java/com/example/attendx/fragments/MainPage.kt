package com.example.attendx.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.attendx.GeneralCommunicator
import com.example.attendx.R
import com.example.attendx.databinding.FragmentMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainPage : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentMainPageBinding
    lateinit var logInBtn : Button
    lateinit var signUpBtn : Button
    lateinit var adminLog : Button
    lateinit var sw : SwitchCompat
    lateinit var textBelow : TextView
    lateinit var progressBar : ProgressBar
    lateinit var im : ImageView
    lateinit var afterAnimation : ConstraintLayout
    private lateinit var model : GeneralCommunicator
    private var isCheck : Boolean? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        signUpBtn = binding.button
        logInBtn = binding.button1


        sw = binding.switch1

        afterAnimation = binding.afterAnimation
        sw.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Teacher" else "Student"
            sw.text = msg
            isCheck = isChecked
        }

        // Initialize the NavController instance
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.myNavHostFragment // Replace with your navigation graph ID
        )
        signUpBtn.setOnClickListener { view ->
            val destinationId = if (isCheck == true) R.id.signUpTeacher else R.id.signUpStudent
            if (findNavController().currentDestination?.id == R.id.mainPage) {
                view.findNavController().navigate(destinationId)
            }
        }


        logInBtn.setOnClickListener { view ->
            val destinationId = if (isCheck == true) R.id.teacher_login else R.id.logIn
            if (findNavController().currentDestination?.id == R.id.mainPage) {
                view.findNavController().navigate(destinationId)
            }
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val refStudent = FirebaseDatabase.getInstance().getReference("Student")
            val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")

            refStudent.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Handle error
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists() && findNavController().currentDestination?.id == R.id.mainPage) {
                            model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                            model.setMsgCommunicator(user.email!!)

                            if (findNavController().currentDestination?.id == R.id.mainPage) {

                                findNavController().navigate(R.id.action_mainPage_to_studentHomePage)
                            }
                        }
                    }
                })

            refTeacher.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Handle error
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists() && findNavController().currentDestination?.id == R.id.mainPage) {
                            model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                            model.setMsgCommunicator(user.email!!)

                            if (findNavController().currentDestination?.id == R.id.mainPage) {
                                val myFragment = TeacherHomePage()
                                val fragmentTransaction = parentFragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                                fragmentTransaction.addToBackStack(null)

                                findNavController().navigate(R.id.action_mainPage_to_teacherHomePage)
                            }
                        }
                    }
                })
        } else {
            // Handle the case when the user is not logged in
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

    // Listener interface
    interface OnFragmentInteractionListener {
        // Define a method for fragment interaction
        fun onFragmentInteraction(uri: Uri)
    }


    companion object {

    }
}