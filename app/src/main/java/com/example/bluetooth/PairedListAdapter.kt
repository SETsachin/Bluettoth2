package com.example.bluetooth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PairedListAdapter(val context: Context, val paireddevices: ArrayList<PairedListModel>) :
    RecyclerView.Adapter<PairedListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.paired_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return paireddevices.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val currentPosition  = paireddevices[position]

        holder.deviceName.text = currentPosition.deviceName
        holder.deviceAddress.text = currentPosition.deviceHardwareAddress

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val deviceName = itemView.findViewById<TextView>(R.id.device_name)
        val deviceAddress = itemView.findViewById<TextView>(R.id.device_address)

    }
}