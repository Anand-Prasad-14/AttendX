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
import com.example.attendx.databinding.FragmentTeacherLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TeacherLogin : Fragment() {

    private var listener : StudentLogin.OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentTeacherLoginBinding
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var logInBtn : Button
    lateinit var mAuth : FirebaseAuth
    lateinit var model : GeneralCommunicator
    lateinit var forgetPass : TextView
    lateinit var nav : BottomNavigationView
    lateinit var loadingpanel1 : ConstraintLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTeacherLoginBinding.inflate(inflater, container, false)
        email = binding.simpleEditText
        password = binding.simpleEditText3
        logInBtn = binding.button
        forgetPass = binding.forgetPass
        nav = binding.nav4
        loadingpanel1 = binding.loadingpanel1


        model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
        mAuth = FirebaseAuth.getInstance()
        logInBtn.setOnClickListener { view: View ->
            if(email.text.toString().isNotEmpty()&&
                password.text.toString().isNotEmpty()){
                loadingpanel1.visibility = View.VISIBLE
                val emailA = email.text.toString().trim()
                val pass = password.text.toString().trim()
                val refTeacher = FirebaseDatabase.getInstance().getReference("Teacher")
                if (emailA.isNotEmpty() && pass.isNotEmpty()) {
                    this.mAuth.signInWithEmailAndPassword(emailA, pass)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                refTeacher.orderByChild("email").equalTo(emailA).addValueEventListener(
                                    object : ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                if (findNavController().currentDestination?.id == R.id.teacher_login) {
                                                    model.setMsgCommunicator(emailA)
                                                    val myFragment = TeacherHomePage()
                                                    val fragmentTransaction =
                                                        parentFragmentManager.beginTransaction()
                                                    fragmentTransaction.replace(
                                                        R.id.myNavHostFragment,
                                                        myFragment
                                                    )
                                                    fragmentTransaction.addToBackStack(null)
                                                    fragmentTransaction.commit()
                                                    view.findNavController()
                                                        .navigate(R.id.action_teacher_login_to_teacherHomePage)
                                                }
                                            } else {
                                                loadingpanel1.visibility = View.GONE
                                                email.error = "Teacher Email Not Exists"
                                                password.error = "Teacher Email Not Exists"
//
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                        }
                                    }
                                )
                            } else {
                                loadingpanel1.visibility = View.GONE
//
                                email.error = "Incorrect Credentials"
                                password.error = "Incorrect Credentials"
                            }
                        }
                }
            }else{
                email.error = "Please enter teacher email"
                password.error = "Please enter teacher password"
            }
        }

        forgetPass.setOnClickListener{view : View ->
            val bundle: Bundle = bundleOf("Type" to "Teacher")
            view.findNavController().navigate(R.id.action_teacher_login_to_resetPassword, bundle)
        }
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    val currentDestinationId = findNavController().currentDestination?.id
                    if (currentDestinationId == R.id.teacher_login) {
                        findNavController().navigate(R.id.mainPage)
                    }
                    true
                }
                else -> false
            }
        }


        return binding.root

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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

    }
}