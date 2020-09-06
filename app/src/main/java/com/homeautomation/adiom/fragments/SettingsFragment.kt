package com.homeautomation.adiom.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homeautomation.adiom.LoginActivity
import com.homeautomation.adiom.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {
    private var mAuth: FirebaseAuth? = null

    companion object {
        var instance : SettingsFragment = SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       val v= inflater.inflate(R.layout.fragment_settings, container, false)
        mAuth = FirebaseAuth.getInstance()
        val myTypeface: Typeface = Typeface.createFromAsset(activity?.assets,"Lment-v02.otf")
        v.textView2.setTypeface(myTypeface)


        return v
    }

    override fun onResume() {
        super.onResume()
        fetchUserInfo()

        signOutBtn.setOnClickListener(View.OnClickListener {
            mAuth!!.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity!!.finish()

        })

    }

    private fun fetchUserInfo() {
        val userId=mAuth!!.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/UserInfo")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Profile ","Failed")

            }

            override fun onDataChange(p0: DataSnapshot) {

              val uname=  p0.child("Name").value.toString()
                val uemail=p0.child("Email").value.toString()
                val uphone=p0.child("Phone").value.toString()

               if (!uname.isNullOrBlank()) {
                   usernameTv.text = uname
                   uemailTv.text = uemail
                   uphoneTv.text = uphone
               }


            }
        })
    }

}
