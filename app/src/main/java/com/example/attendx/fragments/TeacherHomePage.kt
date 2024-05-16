package com.example.attendx.fragments

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.*
import com.example.attendx.R
import com.example.attendx.activity.AboutActivity
import com.example.attendx.activity.SettingsActivity
import com.example.attendx.adapter.CourseAdapter
import com.example.attendx.databinding.FragmentTeacherHomePageBinding
import com.example.attendx.modal.Course
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class TeacherHomePage : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentTeacherHomePageBinding
    private lateinit var createClassBtn : Button
    private lateinit var name : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val PICK_IMAGE_REQUEST = 3
    private val PREFS_NAME = "MyFile"
    private val IMAGE_URI_KEY = "imageUri1"
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 124
    // private lateinit var listView : ListView
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference : DatabaseReference
    private var temp : String?=null
    var arrayList = ArrayList<Course>()
    private lateinit var btnNav : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if there is a saved image URI and set it
        val savedImageUriString = sharedPreferences.getString(IMAGE_URI_KEY, null)
        if (!savedImageUriString.isNullOrEmpty()) {
            val savedImageUri = Uri.parse(savedImageUriString)
            binding.imageView7.setImageURI(savedImageUri)
        }

        binding.imageView7.setOnClickListener {
            checkPermissionAndPickImage()
        }

    }

    private fun checkPermissionAndPickImage() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with picking an image
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            binding.imageView7.setImageURI(uri)

            // Save the image URI to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString(IMAGE_URI_KEY, uri.toString())
            editor.apply()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                // If the request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with picking an image
                    pickImage()
                } else {
                    // Permission denied, handle accordingly
                    // You may want to show a message or disable functionality that requires this permission
                }
            }
        }
    }






    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTeacherHomePageBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0)
        btnNav = binding.btnnavteacher
        name = binding.nameOfTeacher
        recyclerView = binding.recyclerView
        //  binding.recyclerView.setDivider(R.drawable.recycler_view_divider)
//        listView = binding.listView
        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
        model.message.observe(viewLifecycleOwner) { t ->
            temp = t!!.toString()
            Log.d("hi", temp.toString())

            val ordersRef =
                FirebaseDatabase.getInstance().getReference("Teacher").orderByChild("email")
                    .equalTo(temp)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    for (ds in p0.children) {

                        val firstName = ds.child("firstName").getValue(String::class.java)
                        val lastName = ds.child("lastName").getValue(String::class.java)
                        val fullName = "$firstName $lastName"
                        name.text = fullName
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference("Course")
                    databaseReference.orderByChild("professorName").equalTo(name.text.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0.exists()) {
                                    arrayList.clear()
                                    for (e in p0.children) {
                                        Log.d("The course is ", e.value.toString())
                                        val course = e.getValue(Course::class.java)
                                        arrayList.add(course!!)
                                    }

                                }
                                val adapter = CourseAdapter(arrayList, "Teacher")
                                recyclerView.adapter = adapter

                            }


                            // val adapter =
                            // ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayList)
                            // listView.adapter = adapter


                        })
                }

                override fun onCancelled(p0: DatabaseError) {}
            }
            ordersRef.addListenerForSingleValueEvent(valueEventListener)



            binding.recyclerView.addOnItemTouchListener(
                RecyclerItemClickListenr(
                    requireContext(),
                    binding.recyclerView,
                    object : RecyclerItemClickListenr.OnItemClickListener {
                        @SuppressLint("CommitTransaction")
                        override fun onItemClick(view: View, position: Int) {
                            if (findNavController().currentDestination?.id == R.id.teacherHomePage) {
                                model.setMsgCommunicator(name.text.toString())
                                // here we pass the id of the course to the manage class
                                model.setIdCommunicator(
                                    CourseAdapter(arrayList, "Teacher").getID(
                                        position
                                    )
                                )
                                Log.d(
                                    "I clicked ",
                                    CourseAdapter(arrayList, "Teacher").getID(position)
                                )
                                val myFragment = ManageClasses()
                                val fragmentTransaction = childFragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.myNavHostFragment, myFragment)
                                val bundle: Bundle = bundleOf(
                                    "courseId" to CourseAdapter(
                                        arrayList,
                                        "Teacher"
                                    ).getID(position)
                                )
                                view.findNavController()
                                    .navigate(R.id.action_teacherHomePage_to_manageClasses3, bundle)
                            }
                        }

                        override fun onItemLongClick(view: View?, position: Int) {}
                    })
            )
        }


        btnNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.manageclass -> {
                    if (findNavController().currentDestination?.id == R.id.teacherHomePage) {
                        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]
                        model.setMsgCommunicator(name.text.toString())
                        model.setIdCommunicator("-1.0")
                        findNavController().navigate(R.id.action_teacherHomePage_to_manageClasses3)
                        true
                    } else {
                        false
                    }
                }
                R.id.manageAccount -> {
                    if (findNavController().currentDestination?.id == R.id.teacherHomePage) {
                        findNavController().navigate(R.id.action_teacherHomePage_to_teacherAccountManagement)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }

        //sends user back to the log in page if he/she is logged out
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(findNavController().currentDestination?.id == R.id.teacherHomePage) {
                findNavController().navigate(R.id.mainPage)
            }
        }

        val text = requireActivity().findViewById<TextView>(R.id.textView20)
        val au = FirebaseAuth.getInstance().currentUser
        text.text = au!!.email

        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navView)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    text.text = "Welcome User"
                    findNavController().navigate(R.id.mainPage)
                    drawer.closeDrawers()

                }
                R.id.about ->{
                    val i = Intent(activity, AboutActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }
                R.id.setting ->{
                    val i = Intent(activity, SettingsActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }

            }
            false
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