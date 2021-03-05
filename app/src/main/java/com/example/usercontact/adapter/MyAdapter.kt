package com.example.usercontact.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usercontact.R
import com.example.usercontact.listener.OnCallListener
import com.example.usercontact.model.Contact
import kotlinx.android.synthetic.main.list_item.view.*

class MyAdapter(private val contactList: ArrayList<Contact>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private var onCallListener: OnCallListener<Contact>? = null
    private val array = SparseBooleanArray()

    fun setListener(onCallListener: OnCallListener<Contact>) {
        this.onCallListener = onCallListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bindItems(contact)

        holder.itemView.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (onCallListener != null) {
                if (b){
                    onCallListener!!.onMessage(contact,"check")
                    array.put(position,true)
                } else {
                    onCallListener!!.onMessage(contact,"uncheck")
                    array.put(position,false)
                }

            }

        }

        if(array.get(position)){
            holder.itemView.checkbox.setChecked(true)
//            onCallListener!!.onMessage(contact,"check")
        }else {
            holder.itemView.checkbox.setChecked(false)
//            onCallListener!!.onMessage(contact,"uncheck")
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(contact: Contact) {
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
            val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
            tvName.text = contact.name
            tvNumber.text = contact.number
        }
    }
}