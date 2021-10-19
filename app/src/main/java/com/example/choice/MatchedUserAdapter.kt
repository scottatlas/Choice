package com.example.choice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

//Daniel Kathiresan
//Adapter to display matched users, uses cardItem, used for FriendsFragment
class MatchedUserAdapter: ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        //Set content on recycleviewer
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.user_name).text = cardItem.name
            view.findViewById<TextView>(R.id.user_bio).text = cardItem.bio
            view.findViewById<ImageView>(R.id.user_image)
            //Add image from url with glide
            Glide.with(view.findViewById<ImageView>(R.id.user_image))
                .load(cardItem.profilePic)
                .into(view.findViewById(R.id.user_image))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_user, parent, false))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        val currUser = currentList[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            var context = holder.itemView.context
            intent.putExtra("first_name" + " " + "last_name", currUser.name)
            intent.putExtra("uid", currUser.userId)

            context.startActivity(intent)
        }


    }
}