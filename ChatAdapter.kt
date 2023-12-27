package com.Capstone.musicPlayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChatAdapter(private val context: Context) : BaseAdapter() {

    private val messages = ArrayList<Message>()

    fun addMessage(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(position: Int): Any {
        return messages[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val message = getItem(position) as Message
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.senderTextView.text = message.sender
        viewHolder.contentTextView.text = message.content

        return view
    }

    private class ViewHolder(view: View) {
        val senderTextView: TextView = view.findViewById(R.id.senderTextView)
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
    }
}