package com.contri

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.a_welcom.*
import kotlinx.android.synthetic.main.f_welcome.view.*


class WelcomActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: WelcomActivity.SectionsPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_welcom)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        welcome_container.adapter = mSectionsPagerAdapter

        tab_layout.setupWithViewPager(welcome_container, true)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return WelcomActivity.PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 3
        }
    }


    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.f_welcome, container, false)
            val i= arguments?.getInt(ARG_SECTION_NUMBER)
            if(i==1){
                rootView.welcome_tv1.visibility=View.VISIBLE
                rootView.welcome_tv.text="Welcome"
                rootView.welcome_im.setImageResource(R.drawable.gradient)
            }
            else if(i==2) {
                rootView.welcome_im.setImageDrawable(resources.getDrawable(R.drawable.view_pager1))

            }
            else if(i==3){
                rootView.welcome_im.setImageDrawable(resources.getDrawable(R.drawable.view_pager2))
                rootView.welcome_cv.visibility=View.VISIBLE
                rootView.welcome_cv.setOnClickListener {
                    startActivity(Intent(context,SignInActivity::class.java))
                }

            }

            return rootView
        }

        companion object {

            private val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): WelcomActivity.PlaceholderFragment {
                val fragment = WelcomActivity.PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
    class LastFrag
}
