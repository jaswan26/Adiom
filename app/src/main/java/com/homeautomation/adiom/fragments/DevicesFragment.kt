package com.homeautomation.adiom.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homeautomation.adiom.R
import com.homeautomation.adiom.models.*
import com.homeautomation.adiom.models.RoomModel.Companion.roomList

import kotlinx.android.synthetic.main.child_recycler.view.*
import kotlinx.android.synthetic.main.content_scrolling_list_frag_alldevices.*
import kotlinx.android.synthetic.main.parent_recycler.view.*


class DevicesFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    private var mAuth: FirebaseAuth? = null
    companion object {
        var instance : DevicesFragment = DevicesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v=inflater.inflate(R.layout.fragment_devices, container, false)
        mAuth = FirebaseAuth.getInstance()

        var userId=mAuth!!.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fetchRoomInfo()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                fetchRoomInfo()            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                fetchRoomInfo()            }

            override fun onChildRemoved(p0: DataSnapshot) {
                fetchRoomInfo()            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                fetchRoomInfo()
            }

        })
        return v
    }

    override fun onDetach() {
        super.onDetach()
//        roomList.clear()
    }

    override fun onResume() {
        super.onResume()
        fetchRoomInfo()

    }
    private fun initRecycler(){
        recyclerView = rv_parent
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context as AppCompatActivity, LinearLayout.VERTICAL, false)
//            adapter = ParentAdapter(ParentDataFactory.getParents(10))
            adapter=ParentAdapter(roomList.distinct())
            (adapter as ParentAdapter).notifyDataSetChanged()
        }

    }

    private fun fetchRoomInfo() {

        if (roomList.isNotEmpty())
        {
            roomList.clear()
        }
        val userId=mAuth!!.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Profile ","Failed")

            }

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("All Device Fragment","Dtasnapshot ${it.toString()}")
                    val roomInfo=it.key

                    var room: RoomModel = RoomModel(it.key!!, arrayListOf())
                    if (!roomList.contains(room))
                    {
                        roomList.add(room)
                    }

                }

                fetchMoreDetails()

            }
        })

    }

    private  fun fetchMoreDetails() {

        if(roomList.size>0) {
            val userId = mAuth!!.currentUser?.uid
            val ref2 = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices")
           for (item in roomList)
           {
               val query = ref2.child(item.roomName)
               query.addListenerForSingleValueEvent(object : ValueEventListener {
                   override fun onCancelled(p0: DatabaseError) {
                       Log.d("Profile ", "Failed")

                   }

                   override fun onDataChange(p0: DataSnapshot) {


                       var deviceList:ArrayList<DeviceModel>

                       deviceList= arrayListOf()
                       p0.children.forEach {
                           Log.d("All Device Fragment", "Dtasnapshot ${it.toString()}")
                           val device = DeviceModel(it.key!!, Integer.parseInt(it.value!!.toString()) )
                           deviceList.add(device)
                       }
                       item.deviceList=deviceList
                       initRecycler()
                   }
               })
           }
        }
    }
}
class ChildAdapter(private val devices : List<DeviceModel>,private val roomName:String)
    : RecyclerView.Adapter<ChildAdapter.ViewHolder>(){



    lateinit  var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(parent.context)
                .inflate(R.layout.child_recycler,parent,false)

        mContext=parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val child = devices[position]
//        holder.imageView.setImageResource(child.deviceName)
        holder.switch.text = child.deviceName
        when(child.status)
        {
            1-> holder.switch.isChecked=true
            else -> holder.switch.isChecked=false
        }

        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked)
            {

                updateRomsChild(child.deviceName,true)
            }
            else
                updateRomsChild(child.deviceName,false)

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

        val userId= FirebaseAuth.getInstance().currentUser?.uid
        val mRoomRef= FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices/$roomName").child(veh.toString())
        mRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
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


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val switch : Switch = itemView.child_switchBtn
//        val imageView: ImageView = itemView.child_imageView

    }
}

class ParentAdapter(private var rooms : List<RoomModel>) : RecyclerView.Adapter<ParentAdapter.ViewHolder>(){

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.parent_recycler,parent,false)
        Log.d("ParentAdapter",rooms.toString())
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = rooms[position]
        holder.textView.text = parent.roomName
        holder.recyclerView.apply {
            layoutManager = LinearLayoutManager(holder.recyclerView.context, LinearLayout.VERTICAL, false)
            adapter = ChildAdapter(parent.deviceList,parent.roomName)
            setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val recyclerView : RecyclerView = itemView.rv_child
        val textView: TextView = itemView.textView
    }
}