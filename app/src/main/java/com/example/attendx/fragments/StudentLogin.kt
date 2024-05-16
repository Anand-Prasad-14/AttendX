package com.example.attendx.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.attendx.GeneralCommunicator
import com.example.attendx.R
import com.example.attendx.databinding.FragmentStudentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StudentLogin : Fragment() {

    private var listener : MainPage.OnFragmentInteractionListener? = null
    private lateinit var binding : FragmentStudentLoginBinding
    lateinit var email : EditText
    private lateinit var forgetPassword : TextView
    lateinit var password : EditText
    lateinit var logInBtn : Button
    lateinit var mAuth : FirebaseAuth
    lateinit var model : GeneralCommunicator
    lateinit var nav : BottomNavigationView
    lateinit var loadingpanel : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentLoginBinding.inflate(inflater, container, false)
        email = binding.simpleEditText
        password = binding.simpleEditText3
        logInBtn = binding.button
        forgetPassword = binding.forgetPass1
        nav = binding.nav1
        loadingpanel = binding.loadingpanel
        model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
        mAuth = FirebaseAuth.getInstance()

        logInBtn.setOnClickListener {
            val emailA = email.text.toString().trim()
            val pass = password.text.toString().trim()

            if (emailA.isNotEmpty() && pass.isNotEmpty()) {
                loadingpanel.visibility = View.VISIBLE

                mAuth.signInWithEmailAndPassword(emailA, pass)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val refStudent = FirebaseDatabase.getInstance().getReference("Student")
                            refStudent.orderByChild("email").equalTo(emailA).addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        loadingpanel.visibility = View.GONE
                                        if (snapshot.exists()) {
                                            model.setMsgCommunicator(emailA)
                                            findNavController().navigate(R.id.studentHomePage)
                                        } else {
                                            email.error = "Student Email Not Exists"
                                            password.error = "Student Email Not Exists"
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                }
                            )
                        } else {
                            loadingpanel.visibility = View.GONE
                            email.error = "Incorrect Credentials"
                            password.error = "Incorrect Credentials"
                        }
                    }
            } else {
                email.error = "Please enter student email"
                password.error = "Please enter student password"
            }
        }

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    if (findNavController().currentDestination?.id == R.id.logIn) {
                        findNavController().navigate(R.id.mainPage)
                    }
                    true // Return true to indicate that the item selection is handled
                }
                else -> false // Return false for other item selections
            }
        }

        forgetPassword.setOnClickListener { view: View ->
            val bundle: Bundle = bundleOf("Type" to "Student")
            view.findNavController().navigate(R.id.action_logIn_to_resetPassword, bundle)
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