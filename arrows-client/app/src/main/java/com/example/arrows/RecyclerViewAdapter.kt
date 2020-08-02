package com.example.arrows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.TextView

class RecyclerViewAdapter(context: Context, itemsConstruct : ArrayList<RoomData>) :androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerViewAdapter.RoomViewHolder>() {

    var myListener: MyItemClickListener? = null
    val items = itemsConstruct
    var lastPosition = -1

    interface MyItemClickListener {
        fun onItemClickedFromAdapter(room : RoomData)
        fun onItemLongClickedFromAdapter(position : Int)
    }
    fun setMyItemClickListener ( listener: MyItemClickListener){
        this.myListener = listener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RoomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context) // p0 is parent
        val view : View

        view = layoutInflater.inflate(R.layout.list_card_view, p0, false)
        return RoomViewHolder(view)
    }
    // MUST DO!!
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = items[position]
        holder.roomTitle.text = room.title
        holder.roomUpvotes.text = "Number Upvotes: " + room.numUpvotes
        holder.roomDownvotes.text = "Number Downvotes: " + room.numDownvotes
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(view: View, position: Int){
        if(position != lastPosition){
            val animation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
            animation.duration = 700
            animation.startOffset = position * 100L
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    fun getItem(index: Int) : Any{
        return items[index]
    }



    inner class RoomViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){

        val roomTitle = view.findViewById<TextView>(R.id.rvTitle)
        val roomUpvotes = view.findViewById<TextView>(R.id.rvUpvotes)
        val roomDownvotes = view.findViewById<TextView>(R.id.numDownvotes)
        init{

            view.setOnClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                        myListener!!.onItemClickedFromAdapter(items[adapterPosition])
                    }
                }
            }
            view.setOnLongClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                        myListener!!.onItemLongClickedFromAdapter(adapterPosition)
                    }
                }
                true
            }
        }
    }
}