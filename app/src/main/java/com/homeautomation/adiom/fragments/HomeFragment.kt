package com.homeautomation.adiom.fragments

import android.app.ProgressDialog
import android.content.Intent
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
import com.homeautomation.adiom.R
import com.homeautomation.adiom.RoomsDetail
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.content_scrolling_list_frag_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home_item.view.*
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {

    var progressDialog: ProgressDialog?=null

    private var mAuth: FirebaseAuth? = null
    companion object {
        var instance : HomeFragment = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v=inflater.inflate(R.layout.fragment_home, container, false)

        mAuth = FirebaseAuth.getInstance()

        var date = System.currentTimeMillis();

        var sdf = SimpleDateFormat("MMM dd, yyyy h:mm a")
        var dateString = sdf.format(date)
         v.timeDisplay.text=dateString
        return v;
    }

    override fun onResume() {
        super.onResume()
        fetchRoomInfo()
    }


    private fun fetchRoomInfo() {

        val userId=mAuth!!.currentUser?.uid

        progressDialog= ProgressDialog.show(context,null,"Please wait....",false,false)

        progressDialog?.show()
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Profile ","Failed")
                progressDialog?.dismiss()

            }

            override fun onDataChange(p0: DataSnapshot) {
                val myAdapter= GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("MyCars Fragment","Dtasnapshot ${it.toString()}")
                    val vehicleInfo=it.key

                        myAdapter.add(RoomItem(vehicleInfo, activity as AppCompatActivity))

                }
                recyclerview_home_list?.adapter=myAdapter
                progressDialog?.dismiss()
            }
        })
    }

    class RoomItem(val veh:String?, val mContext:AppCompatActivity): Item<ViewHolder>()
    {
        override fun bind(viewHolder: ViewHolder, position: Int) {


            var StringGenerated=when(veh){
                "Livingroom"-> "livingroom"
                "Bedroom"-> "bedroom"
                "Kitchen"-> "kitchen"
                "Bathroom"-> "bathroom"
                "Garage"-> "garage"
                "Storeroom"-> "storeroom"
                else -> "livingroom"
            }
            var imageId=mContext.resources.getIdentifier("com.homeautomation.adiom:drawable/" + StringGenerated, null, null)
           viewHolder.itemView.textView.setText("$veh")
            viewHolder.itemView.roomItemBgImg.setImageResource(imageId)
            viewHolder.itemView.setOnClickListener {
                var In:Intent= Intent(mContext, RoomsDetail::class.java)
                In.putExtra("roomname",veh)
                mContext.startActivity(In)
            }


        }



        override fun getLayout(): Int {
            return R.layout.fragment_home_item
        }


    }

}
