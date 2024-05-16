package com.example.attendx.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.attendx.MainActivity
import com.example.attendx.R
import com.example.attendx.adapter.SliderAdapter
import com.example.attendx.modal.SliderModal

class SplashSlider : AppCompatActivity() {
    // creating variables for view pager,
    // liner layout, adapter and our array list.
    private var viewPager: ViewPager? = null
    private var dotsLL: LinearLayout? = null
    var adapter: SliderAdapter? = null
    private var sliderModalArrayList: ArrayList<SliderModal>? = null
    private lateinit var dots: Array<TextView?>
    var size = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider)

        // initializing all our views.
        viewPager = findViewById(R.id.idViewPager)
        dotsLL = findViewById(R.id.idLLDots)
        val btnSkip: Button = findViewById(R.id.idBtnSkip)

        // in below line we are creating a new array list.
        sliderModalArrayList = ArrayList()

        // on below 3 lines we are adding data to our array list.
        sliderModalArrayList!!.add(
            SliderModal(
                "AttendX: Where Attendance Meets Innovation" ,
                "Seamlessly record daily attendance\n" +
                        "Utilizes cutting-edge location-based technology\n" +
                        "Ensures eligibility by proximity verification\n" +
                        "A smarter, more accurate way to manage classroom presence",
                R.drawable.collegelogo,
                R.drawable.gradient_one
            )
        )
        sliderModalArrayList!!.add(
            SliderModal(
                " Welcome Teacher's ",

                "Create, Track, and Share Classes\n" +
                        "Efficient Attendance Taking\n" +
                        "Easy Data Deletion\n" +
                        "In-Depth Attendance Analysis\n" +
                        "Smarter Classroom Management",
                R.drawable.teacherprofile1,
                R.drawable.gradient_two
            )
        )
        sliderModalArrayList!!.add(
            SliderModal(
                "Welcome Student's ",
                " Join Courses with Ease\n" +
                        "Delete Unneeded Courses\n" +
                        "Seamlessly Take Attendance",
                R.drawable.studentprofile,
                R.drawable.gradient_three
            )
        )

        // below line is use to add our array list to adapter class.
        adapter = SliderAdapter(this@SplashSlider, sliderModalArrayList!!)

        // below line is use to set our
        // adapter to our view pager.
        viewPager?.adapter = adapter


        // we are storing the size of our
        // array list in a variable.
        size = sliderModalArrayList!!.size

        // calling method to add dots indicator
        addDots(size, 0)

        // below line is use to call on
        // page change listener method.
        viewPager?.addOnPageChangeListener(viewListener)

        btnSkip.setOnClickListener {
            // Define the destination activity you want to navigate to
            val intent = Intent(this, MainActivity::class.java)



            // Start the destination activity
            startActivity(intent)
            finish()
        }

    }

    private fun addDots(size: Int, pos: Int) {
        // inside this method we are
        // creating a new text view.
        dots = arrayOfNulls(size)

        // below line is use to remove all
        // the views from the linear layout.
        dotsLL!!.removeAllViews()

        // running a for loop to add
        // number of dots to our slider.
        for (i in 0 until size) {
            // below line is use to add the
            // dots and modify its color.
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("•")
            dots[i]!!.textSize = 35f

            // below line is called when the dots are not selected.
            dots[i]!!.setTextColor(resources.getColor(R.color.black))
            dotsLL!!.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            // this line is called when the dots
            // inside linear layout are selected
            dots[pos]!!.setTextColor(resources.getColor(R.color.purple_200))
        }
    }

    // creating a method for view pager for on page change listener.
    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            // we are calling our dots method to
            // change the position of selected dots.
            addDots(size, position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}