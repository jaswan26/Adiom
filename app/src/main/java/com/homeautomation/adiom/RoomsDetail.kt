package com.homeautomation.adiom

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pedro.vlc.VlcListener
import com.pedro.vlc.VlcVideoLibrary
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_room_detail_item.view.*
import kotlinx.android.synthetic.main.activity_rooms_detail.*
import org.videolan.libvlc.MediaPlayer
import java.util.*


class RoomsDetail : AppCompatActivity() ,VlcListener {
    override fun onComplete() {
    }

    override fun onError() {

//        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show()
        vlcVideoLibrary.stop()

    }

    override fun onBuffering(event: MediaPlayer.Event?) {


    }

    public var mRoomRef: DatabaseReference? = null

    lateinit  var roomName:String
    private var mAuth: FirebaseAuth? = null
    lateinit var vlcVideoLibrary:VlcVideoLibrary
    private val options = arrayOf(":fullscreen")
    private var streamUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_detail)
        mAuth= FirebaseAuth.getInstance()
        val userId=mAuth!!.currentUser?.uid
        val In: Intent =intent
        roomName=In.getStringExtra("roomname")



        val surfaceView = findViewById<SurfaceView>(R.id.surfaceCCTV) as SurfaceView
        vlcVideoLibrary = VlcVideoLibrary(this, this,surfaceView)
        vlcVideoLibrary.setOptions(Arrays.asList(*options))


        var StringGenerated=when(roomName){
            "Livingroom"-> "livingroom"
            "Bedroom"-> "bedroom"
            "Kitchen"-> "kitchen"
            "Bathroom"-> "bathroom"
            "Garage"-> "garage"
            "Storeroom"-> "storeroom"
            else -> "livingroom"
        }
        var imageId=resources.getIdentifier("com.homeautomation.adiom:drawable/" + StringGenerated, null, null)
        relativeView.setBackgroundResource(imageId)
        closeSurfaceBtn.setOnClickListener(View.OnClickListener {
            surfaceFrame.visibility=View.INVISIBLE
        })

        cctvCameraTv.setOnClickListener(View.OnClickListener {
            val intent = intent
            finish()
            startActivity(intent)
        })

        turnOffBtn.setOnClickListener(View.OnClickListener {
            turnOffAll()
        })


        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices/$roomName")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                fetchroomInfo(roomName)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                fetchroomInfo(roomName)            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                fetchroomInfo(roomName)            }

            override fun onChildRemoved(p0: DataSnapshot) {
                fetchroomInfo(roomName)            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                fetchroomInfo(roomName)
            }

        })
        fetchroomInfo(roomName)


    }

    private fun turnOffAll() {

        val userId=FirebaseAuth.getInstance().currentUser?.uid
        val mRoomRef=FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices/$roomName")
        mRoomRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Profile ","Failed")
                Toast.makeText(this@RoomsDetail,"Upload Failed, Try again ", Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d("Profile ","Data fetched ${p0.child("uname").getValue().toString()}")

                p0.children.forEach {
                    Log.d("MyCars Fragment","Dtasnapshot ${it.toString()}")
                    val devicename=it.key
                    it.ref.setValue(0)
                }

                Toast.makeText(this@RoomsDetail,"Upload succesful ", Toast.LENGTH_SHORT).show()



            }
        })

    }
    override fun onBackPressed() {
        super.onBackPressed()
        vlcVideoLibrary.stop()
        finish()
    }

    private fun fetchroomInfo(roomName: String?) {

        val userId=mAuth!!.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices/$roomName")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Profile ","Failed")

            }

            override fun onDataChange(p0: DataSnapshot) {
                val myAdapter= GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("MyRoom Activity","Dtasnapshot ${it.toString()}")
                    val devicename=it.key
                    val devicevalue=Integer.parseInt(it.value.toString())
                    myAdapter.add(DeviceItem(roomName,devicename,devicevalue,this@RoomsDetail))
                }
                totalDeviceCount.text=myAdapter.itemCount.toString()
                recyclerview_activity_room_list?.adapter=myAdapter
            }
        })

        val urlRef=FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Urls")
        urlRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {


                if (p0.hasChild(roomName.toString())) {
                    streamUrl=p0.child("$roomName").value.toString()
                    if (streamUrl.toString().length > 0) {
                        vlcVideoLibrary.play(streamUrl)

                    }
                }
                else
                {
                    surfaceFrame.visibility=View.INVISIBLE
                    cctvCameraTv.visibility=View.GONE
                }
            }

        })


    }


    class DeviceItem(val roomName: String?,val veh:String?,val devicevalue:Int,val mContext:AppCompatActivity): Item<ViewHolder>()
    {
        override fun bind(viewHolder: ViewHolder, position: Int) {


            viewHolder.itemView.roomswitches.setText(veh)
            if (devicevalue.equals(1))
            {
                viewHolder.itemView.roomswitches.isChecked=true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.itemView.roomswitches.elevation= 12.0F
                }

            }
            else {
                viewHolder.itemView.roomswitches.isChecked = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.itemView.roomswitches.elevation = 0.0F
                }
            }


            viewHolder.itemView.roomswitches.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked)
                {

                    updateRomsChild(veh,true)
                }
                else
                    updateRomsChild(veh,false)

            }




        }

        private fun updateRomsChild(veh: String?, b: Boolean) {

            val x:Int
            if(b)
            {
                x=1
            }
            else
                x=0

            val userId=FirebaseAuth.getInstance().currentUser?.uid
            val mRoomRef=FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices/$roomName").child(veh.toString())
            mRoomRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Profile ","Failed")
                    Toast.makeText(mContext,"Upload Failed, Try again ", Toast.LENGTH_SHORT).show()

                }

                override fun onDataChange(p0: DataSnapshot) {

                    Log.d("Profile ","Data fetched ${p0.child("uname").getValue().toString()}")

                    p0.ref.setValue(x)

                    Toast.makeText(mContext,"Upload succesful ", Toast.LENGTH_SHORT).show()



                }
            })

        }


        override fun getLayout(): Int {
            return R.layout.activity_room_detail_item
        }


    }

}
