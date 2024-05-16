package com.example.attendx.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.attendx.GeneralCommunicator
import com.example.attendx.R
import com.example.attendx.databinding.FragmentTeacherAccountManagementBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class TeacherAccountManagement : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentTeacherAccountManagementBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "MyFile"
    private val IMAGE_URI_KEY = "imageUri1"
    private lateinit var enteredCurrentPass : EditText
    private lateinit var enteredNewPass: EditText
    private lateinit var enteredConfirm : EditText
    private lateinit var submit : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var email : TextView
    private lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if there is a saved image URI and set it
        val savedImageUriString = sharedPreferences.getString(IMAGE_URI_KEY, null)
        if (!savedImageUriString.isNullOrEmpty()) {
            val savedImageUri = Uri.parse(savedImageUriString)
            binding.imageView3.setImageURI(savedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTeacherAccountManagementBinding.inflate(layoutInflater)
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0)
        enteredCurrentPass = binding.editText2
        enteredNewPass = binding.editText3
        enteredConfirm = binding.editText4
        submit = binding.button2
        auth = FirebaseAuth.getInstance()
        email = binding.textView
        nav = binding.bottomNavigationView

        submit.setOnClickListener { changePassword() }
        email.text = FirebaseAuth.getInstance().currentUser?.email

        nav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    if (findNavController().currentDestination?.id == R.id.teacherAccountManagement) {
                        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                        model.setNameCommunicator(FirebaseAuth.getInstance().currentUser?.email!!)
                        findNavController().navigate(R.id.action_teacherAccountManagement_to_teacherHomePage)
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

    private fun changePassword() {
        val currentPassword = enteredCurrentPass.text.toString()
        val newPassword = enteredNewPass.text.toString()
        val confirmPassword = enteredConfirm.text.toString()

        if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (newPassword == confirmPassword) {
                val user = auth.currentUser
                if (user != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                    user.reauthenticate(credential)
                        .addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { updatePasswordTask ->
                                        if (updatePasswordTask.isSuccessful) {
                                            Log.d("success", "password updated successfully")
                                            auth.signOut()
                                            if (findNavController().currentDestination?.id == R.id.teacherAccountManagement) {
                                                findNavController().navigate(R.id.mainPage)
                                            }
                                        }
                                    }
                            }
                        }
                }
            } else {
                enteredConfirm.error = "Password mismatching"
            }
        } else {
            if (currentPassword.isEmpty()) {
                enteredCurrentPass.error = "Please enter current password."
            }
            if (newPassword.isEmpty()) {
                enteredNewPass.error = "Please enter new password."
            }
            if (confirmPassword.isEmpty()) {
                enteredConfirm.error = "Please re-enter password to confirm."
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