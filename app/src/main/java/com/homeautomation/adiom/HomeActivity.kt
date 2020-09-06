package com.homeautomation.adiom

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.homeautomation.adiom.fragments.DevicesFragment
import com.homeautomation.adiom.fragments.EnergyFragment
import com.homeautomation.adiom.fragments.HomeFragment
import com.homeautomation.adiom.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                view_pager.currentItem=0
                view_pager.adapter?.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_devices -> {

                view_pager.currentItem=1
                view_pager.adapter?.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_energy -> {


                view_pager.currentItem=2
                view_pager.adapter?.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
//                message.setText(R.string.title_settings)

                view_pager.currentItem=3
                view_pager.adapter?.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        FirebaseApp.initializeApp(this)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("streamurl")

//        myRef.setValue("Hello, World!");
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        view_pager.adapter = FragmentAdapter(supportFragmentManager)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home
        view_pager.currentItem = 0
        view_pager.adapter?.notifyDataSetChanged()

        view_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {

                when(position){
                    0->{
                        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                        navigation.selectedItemId = R.id.navigation_home
                    }
                    1-> {
                        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                        navigation.selectedItemId = R.id.navigation_devices
                    }
                    2-> {
                        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                        navigation.selectedItemId = R.id.navigation_energy                    }
                    3-> {
                        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                        navigation.selectedItemId = R.id.navigation_settings
                    }
                    else-> null
                }
                view_pager.adapter?.notifyDataSetChanged()
            }

        })

        if(FirebaseAuth.getInstance().currentUser==null) {
            gotoLogin()
        }
    }
    private fun gotoLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

        class FragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

            override fun getItem(position: Int): Fragment? {

                return when (position) {
                    0 -> HomeFragment.instance
                    1 -> DevicesFragment.instance
                    2 -> EnergyFragment.instance
                    3 -> SettingsFragment.instance
                    else -> null
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "Hub"
                    1 -> "Market"
                    2 -> "Setting"
                    3 -> "kuvhbi"
                    else -> null
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }


}
