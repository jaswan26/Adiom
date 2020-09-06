package com.homeautomation.adiom.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.homeautomation.adiom.R
import com.homeautomation.adiom.models.DeviceModel
import com.homeautomation.adiom.models.RoomModel
import com.homeautomation.adiom.models.RoomModel.Companion.roomList
import kotlinx.android.synthetic.main.child_recycler_for_energy.view.*
import kotlinx.android.synthetic.main.content_scrolling_list_frag_energy.*
import kotlinx.android.synthetic.main.parent_recycler_for_energy.view.*


class EnergyFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    private var mAuth: FirebaseAuth? = null

    companion object {
        var instance : EnergyFragment = EnergyFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_energy, container, false)
        mAuth = FirebaseAuth.getInstance()

        var userId=mAuth!!.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/$userId/Rooms/Devices")

        return v
    }

    override fun onResume() {
        super.onResume()
//        roomList.clear()
        initRecycler()
    }

    private fun initRecycler(){

        recyclerView = rv_parent_energy
        if(roomList.isNotEmpty()) {
                 recyclerView . apply {
                layoutManager = LinearLayoutManager(context as AppCompatActivity, LinearLayout.VERTICAL, false)
//            adapter = ParentAdapter(ParentDataFactory.getParents(10))
                adapter = ParentAdapterEnergy(roomList.distinct())
                (adapter as ParentAdapterEnergy).notifyDataSetChanged()
            }
        }
        else{
            Handler().postDelayed({
                initRecycler()
            }, 1000)
        }
    }

}


class ChildAdapterEnergy(private val devices : List<DeviceModel>, private val roomName:String)
    : RecyclerView.Adapter<ChildAdapterEnergy.ViewHolder>(){



    lateinit  var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(parent.context)
                .inflate(R.layout.child_recycler_for_energy,parent,false)

        mContext=parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val child = devices[position]
//        holder.imageView.setImageResource(child.deviceName)
        holder.textView.text = child.deviceName

    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val textView : TextView = itemView.child_textViewEnergy


    }
}

class ParentAdapterEnergy(private var rooms : List<RoomModel>) : RecyclerView.Adapter<ParentAdapterEnergy.ViewHolder>(){

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.parent_recycler_for_energy,parent,false)
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
            adapter = ChildAdapterEnergy(parent.deviceList,parent.roomName)
            setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val recyclerView : RecyclerView = itemView.rv_child_energy
        val textView: TextView = itemView.textViewParentEnergy
    }
}

