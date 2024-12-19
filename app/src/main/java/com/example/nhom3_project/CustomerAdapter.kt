package com.example.nhom3_project

import Customer
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageButton

class CustomerAdapter(
    private val context: Context,
    private val customerList: MutableList<Customer>,
    private val onDeleteClick: (Customer) -> Unit
) : BaseAdapter() {

    class ViewHolder(view: View) {
        val tvId: TextView = view.findViewById(R.id.tv_uid)
        val tvName: TextView = view.findViewById(R.id.tv_customer_name)
        val tvPhone: TextView = view.findViewById(R.id.tv_phone)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
        val tvRole: TextView = view.findViewById(R.id.tv_role)
        val btnDelete: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_customer, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val customer = customerList[position]

        holder.tvId.text = customer.uid
        holder.tvName.text = customer.name
        holder.tvPhone.text = customer.phone
        holder.tvEmail.text = customer.email
        holder.tvRole.text = customer.role

        holder.btnDelete.setOnClickListener {
            onDeleteClick(customer)
        }

        return view
    }

    override fun getCount(): Int {
        return customerList.size
    }

    override fun getItem(position: Int): Any {
        return customerList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
