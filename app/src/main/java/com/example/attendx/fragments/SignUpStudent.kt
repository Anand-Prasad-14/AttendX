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
import com.example.attendx.databinding.FragmentSignUpStudentBinding
import com.example.attendx.modal.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpStudent : Fragment() {

    private var listener: MainPage.OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentSignUpStudentBinding
    lateinit var submitBtn : Button
    lateinit var fName : EditText
    lateinit var lName : EditText
    lateinit var pWord : EditText
    lateinit var email : EditText
    lateinit var databaseReference: DatabaseReference
    lateinit var database : FirebaseDatabase
    lateinit var mAuth : FirebaseAuth
    lateinit var studentId : String
    lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpStudentBinding.inflate(inflater, container, false)
        submitBtn = binding.button
        fName = binding.simpleEditText
        lName = binding.simpleEditText3
        pWord = binding.simpleEditText6
        email = binding.simpleEditText4
        nav = binding.nav
        databaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        submitBtn.setOnClickListener { view: View ->
            if (fName.text.toString().isNotEmpty()
                && lName.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && pWord.text.toString().isNotEmpty()
                && isEmailValid(email.text.toString())
            ) {
                saveStudent()
                if (findNavController().currentDestination?.id == R.id.signUpStudent) {
                    view.findNavController().navigate(R.id.action_signUpStudent_to_logIn)
                }
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("ERROR")
                if (!isEmailValid(email.text.toString())) {
                    builder.setMessage("Please enter a valid email address")
                } else {
                    builder.setMessage("Please fill in all the fields!")
                }
                builder.setPositiveButton("Ok") { dialog, which -> }
                val alert = builder.create()
                alert.show()
            }
        }

        nav.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    if (findNavController().currentDestination?.id == R.id.signUpStudent) {
                        findNavController().navigate(R.id.mainPage)
                    }
                }
            }
        }

        return binding.root
    }

    private fun saveStudent() {
        val firstName = fName.text.toString().trim()
        val lastName = lName.text.toString().trim()
        val password = pWord.text.toString().trim()
        val emailA = email.text.toString().trim()

        if (isEmailValid(emailA)) {
            val ref = FirebaseDatabase.getInstance().getReference("Student")
            val studentId = ref.push().key ?: return  // Exit if studentId is null

            val student = Student(studentId, firstName, lastName, emailA, HashMap())

            ref.child(studentId).setValue(student)

            val mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(emailA, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Registered successfully",
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
            builder.setMessage("Please Enter a valid email!")
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    private fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
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