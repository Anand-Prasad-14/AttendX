package com.example.attendx.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.attendx.R
import com.example.attendx.modal.SliderModal

class SliderAdapter(private val context: Context, private val sliderModalArrayList: List<SliderModal>) :
    PagerAdapter() {

    override fun getCount(): Int {
        return sliderModalArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @SuppressLint("DiscouragedApi", "UseCompatLoadingForDrawables")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.slider_layout, container, false)

        val imageView: ImageView = view.findViewById(R.id.idIV)
        val titleTV: TextView = view.findViewById(R.id.idTVtitle)
        val headingTV: TextView = view.findViewById(R.id.idTVheading)
        val sliderRL: RelativeLayout = view.findViewById(R.id.idRLSlider)

        val modal = sliderModalArrayList[position]
        titleTV.text = modal.title
        headingTV.text = modal.heading

        modal.imgUrl?.let { imageView.setImageResource(it) }


        sliderRL.background = context.getDrawable(modal.backgroundDrawable)

        container.addView(view)
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}