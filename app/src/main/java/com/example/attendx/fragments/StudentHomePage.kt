package com.example.attendx.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.*
import com.example.attendx.activity.AboutActivity
import com.example.attendx.adapter.CourseAdapter
import com.example.attendx.databinding.FragmentStudentHomePageBinding
import com.example.attendx.modal.Course
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StudentHomePage : Fragment() {

    private var listener : OnFragmentInteractionListener? = null
    private lateinit var binding: FragmentStudentHomePageBinding
    private lateinit var addClassBtn : Button
    private lateinit var name : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val PICK_IMAGE_REQUEST = 1
    private val PREFS_NAME = "StudentFile"
    private val IMAGE_URI_KEY = "imageUri2"
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 125
    private var temp : String?= null
    private lateinit var courseList : RecyclerView
    //private lateinit var databaseReference : DatabaseReference
    private var arrayList = ArrayList<Course>()
    private lateinit var btmNav : BottomNavigationView
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        // Check if there is a saved image URI and set it
        val savedImageUriString = sharedPreferences.getString(IMAGE_URI_KEY, null)
        if (!savedImageUriString.isNullOrEmpty()) {
            val savedImageUri = Uri.parse(savedImageUriString)
            binding.imageView.setImageURI(savedImageUri)
        }

        binding.imageView.setOnClickListener {
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            binding.imageView.setImageURI(uri)

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
        binding = FragmentStudentHomePageBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0)
        name = binding.nameOfStudent
//        addClassBtn = binding.button3
        courseList = binding.courseList
        btmNav = binding.bottomNav
        val model = ViewModelProvider(requireActivity())[GeneralCommunicator::class.java]

        model.message.observe(viewLifecycleOwner) { t ->
            temp = t!!.toString()
            var key = ""
            val ref = FirebaseDatabase.getInstance().reference
            val ordersRef = ref.child("Student").orderByChild("email").equalTo(temp)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (ds in p0.children) {
                            val nameTemp =
                                ds.child("firstName").getValue(String::class.java) + " " + ds.child(
                                    "lastName"
                                ).getValue(String::class.java)

                            key = ds.key!!

                            name.text = nameTemp
                        }
                        sendKeyToEnrollment(key)


                        val courseRef = FirebaseDatabase.getInstance().getReference("Course")
                        val databaseReference =
                            FirebaseDatabase.getInstance().getReference("Student")
                        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                for (e in p0.children) {
                                    arrayList.clear()
                                    e.key?.let { Log.d("studentHome", it) }
                                    databaseReference.child(e.key!!)
                                        .child("courseId")
                                        .addValueEventListener(
                                            object : ValueEventListener {
                                                override fun onDataChange(p2: DataSnapshot) {

                                                    for (e2 in p2.children) {
                                                        Log.d("e2", e2.value.toString())
                                                        courseRef.orderByChild(
                                                            "courseId"
                                                        ).equalTo(
                                                            e2.getValue(
                                                                String::class.java
                                                            )
                                                        )
                                                            .addListenerForSingleValueEvent(
                                                                object :
                                                                    ValueEventListener {
                                                                    override fun onCancelled(
                                                                        p3: DatabaseError
                                                                    ) {
                                                                    }

                                                                    override fun onDataChange(
                                                                        p3: DataSnapshot
                                                                    ) {

                                                                        for (e3 in p3.children) {

                                                                            val course =
                                                                                e3.getValue(
                                                                                    Course::class.java
                                                                                )
                                                                            arrayList.add(
                                                                                course!!
                                                                            )
                                                                        }

                                                                        val adapter =
                                                                            CourseAdapter(
                                                                                arrayList, "Student"
                                                                            )
                                                                        courseList.adapter =
                                                                            adapter
                                                                    }

                                                                })

                                                    }
                                                }

                                                override fun onCancelled(p2: DatabaseError) {}
                                            }
                                        )
                                }
                            }
                        })
                    }


                }

                override fun onCancelled(p0: DatabaseError) {
                }

            }
            ordersRef.addListenerForSingleValueEvent(valueEventListener)
        }

        courseList.addOnItemTouchListener(
            RecyclerItemClickListenr(
                requireContext(),
                courseList,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (navController.currentDestination?.id == R.id.studentHomePage) {
                            model.setIdCommunicator(
                                CourseAdapter(arrayList, "Student").getID(
                                    position
                                )
                            )
                            Log.d("I clicked ", CourseAdapter(arrayList, "Student").getID(position))
                            model.setNameCommunicator(name.text.toString())

                            navController.navigate(R.id.attendancePage)
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {}
                })
        )


        //sends user back to the log in page if he/she is logged out
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            if(navController.currentDestination?.id == R.id.studentHomePage) {
                navController.navigate(R.id.mainPage)
            }
        }


        val text = requireActivity().findViewById<TextView>(R.id.textView20)
        // Check if the TextView was found before attempting to set its text
        if (text != null) {
            val au = FirebaseAuth.getInstance().currentUser
            text.text = au?.email
        } else {
            // Handle the case where the TextView is not found in the layout
        }
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navView)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    text.text = "Welcome User"
                    navController.navigate(R.id.mainPage)
                    drawer.closeDrawers()
                }
                R.id.about ->{
                    val i = Intent(activity, AboutActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }



            }
            false
        }

        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().actionBar?.setHomeButtonEnabled(false)

        return binding.root

    }

    private fun sendKeyToEnrollment(str: String) {
        btmNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add -> {
                    if (navController.currentDestination?.id == R.id.studentHomePage) {
                        val bundle = bundleOf("key" to str)
                        navController.navigate(R.id.action_studentHomePage_to_studentEnroll, bundle)
                    }
                    true
                }
                R.id.manageAccount -> {
                    if (navController.currentDestination?.id == R.id.studentHomePage) {
                        navController.navigate(R.id.action_studentHomePage_to_studentAccountManagement2)
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(R.id.mainPage)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    // onAttach method remains unchanged

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