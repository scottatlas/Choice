package com.example.choice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView

class CardStackAdapter: ListAdapter<CardItem, CardStackAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        //Put into recycleview
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.item_name).text = cardItem.name
            view.findViewById<TextView>(R.id.item_bio).text = cardItem.bio
            Glide.with(view.findViewById<RoundedImageView>(R.id.item_image))
                .load(cardItem.profilePic)
                .into(view.findViewById<RoundedImageView>(R.id.item_image))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
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
}