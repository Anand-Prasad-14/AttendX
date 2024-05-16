package com.example.attendx.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.attendx.R
import com.example.attendx.databinding.FragmentResetPasswordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class ResetPassword : Fragment() {

    private var listener : OnFragmentInteractionListener? =null
    private lateinit var binding : FragmentResetPasswordBinding
    private lateinit var email : EditText
    private lateinit var sendBtn : Button
    private lateinit var nav : BottomNavigationView

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
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        email = binding.enteredEmail
        sendBtn = binding.submit
        nav = binding.nav6
        Log.d("argument is ", arguments?.getString("Type").toString())
        sendBtn.setOnClickListener {  reset() }
        nav.setOnNavigationItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome ->{
                    if(arguments?.getString("Type").toString() == "Student") {
                        findNavController().navigate(R.id.action_resetPassword_to_logIn)
                    }
                    if(arguments?.getString("Type").toString() == "Teacher"){
                        findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                    }
                }
            }
        }
        return binding.root

    }

    private fun reset(){
        val emailA = email.text.toString().trim()

        val mAuth = FirebaseAuth.getInstance()
        mAuth.sendPasswordResetEmail(emailA)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    if(arguments?.getString("Type").toString() == "Student") {
                        findNavController().navigate(R.id.action_resetPassword_to_logIn)
                    }
                    if(arguments?.getString("Type").toString() == "Teacher"){
                        findNavController().navigate(R.id.action_resetPassword_to_teacher_login)
                    }

                    Toast.makeText(context, "email successful sent", Toast.LENGTH_LONG).show()

                }
                else{
                    Toast.makeText(context, "email not sent", Toast.LENGTH_LONG).show()
                }

            }

    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {

    }
}