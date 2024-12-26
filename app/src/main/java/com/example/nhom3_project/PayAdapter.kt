package com.example.nhom3_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class PayAdapter(context: Context, private val datalist: MutableList<PayData>):
    ArrayAdapter<PayData>(context, 0, datalist){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.pay_detail_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val datals = datalist[position]
        holder.productName.text = datals.propductname
        holder.quality.text = datals.quality.toString()

        return view
    }

    private class ViewHolder(view: View) {
        val productName: TextView = view.findViewById(R.id.tvPayIContent)
        val quality: TextView = view.findViewById(R.id.tvPayIQuality)
    }
}