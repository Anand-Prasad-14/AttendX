package com.example.attendx.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.attendx.R
import com.example.attendx.databinding.FragmentSignUpTeacherBinding
import com.example.attendx.modal.Teacher
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpTeacher : Fragment() {

    private var listener : MainPage.OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentSignUpTeacherBinding
    lateinit var fname : EditText
    lateinit var lname : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var submitBtn : Button
    lateinit var databaseReference: DatabaseReference
    lateinit var database : FirebaseDatabase
    lateinit var mAuth : FirebaseAuth
    lateinit var teacherId : String
    lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpTeacherBinding.inflate(inflater, container, false)
        submitBtn = binding.button
        fname = binding.simpleEditText
        lname = binding.simpleEditText2
        email = binding.simpleEditText5
        password = binding.simpleEditText7
        nav = binding.nav5
        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        submitBtn.setOnClickListener { view : View ->
            val firstName = fname.text.toString()
            val lastName = lname.text.toString()
            val emailA = email.text.toString()
            val pass = password.text.toString()

            if (firstName.isNotEmpty()
                && lastName.isNotEmpty()
                && emailA.isNotEmpty()
                && pass.isNotEmpty()
                && isEmailValid(emailA)) {

                saveTeacher()

                if (findNavController().currentDestination?.id == R.id.signUpTeacher) {
                    view.findNavController().navigate(R.id.action_signUpTeacher_to_teacher_login)
                }
            }
                else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("ERROR")

                    val message = if (!isEmailValid(emailA)) {
                        "Please enter a valid email address"
                    }
                    else {
                        "Please fill in all the fields!"
                    }
                    builder.setMessage(message)

                    builder.setPositiveButton("OK") {_, _ ->

                    }

                    val alert = builder.create()
                    alert.show()
                }

        }

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    if (findNavController().currentDestination?.id == R.id.signUpTeacher) {
                        findNavController().navigate(R.id.mainPage)
                    }
                    true // Return true to indicate that the item selection is handled
                }
                else -> false // Return false for other item selections
            }
        }

        return binding.root

    }

    private fun saveTeacher() {
        val firstName = fname.text.toString().trim()
        val lastName = lname.text.toString().trim()
        val emailA = email.text.toString().trim()
        val pass = password.text.toString().trim()

        if (isEmailValid(emailA)) {
            mAuth.createUserWithEmailAndPassword(emailA, pass)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val ref = FirebaseDatabase.getInstance().getReference("Teacher")
                        teacherId = ref.push().key!!
                        val teacher = Teacher(0, firstName, lastName, emailA)
                        ref.child(teacherId).setValue(teacher)

                        Toast.makeText(
                            requireContext(),
                            "Registration successful",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Registration unsuccessful",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("ERROR")
            builder.setMessage("Please enter a valid email!")
            builder.setPositiveButton("Ok") { _, _ ->
                // Do nothing or handle button click if needed
            }

            val alert = builder.create()
            alert.show()
        }
    }

    private val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    private fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
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