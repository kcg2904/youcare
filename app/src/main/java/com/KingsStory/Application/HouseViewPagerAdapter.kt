package com.KingsStory.Application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HouseViewPagerAdapter:ListAdapter<HouseModel,HouseViewPagerAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(val view : View): RecyclerView.ViewHolder(view){
        fun bind(houseModel: HouseModel){
            val titleTextView:TextView = view.findViewById(R .id.titleTextView)
            val priceTextView:TextView = view.findViewById(R.id.priceTextView)
            val thumbnailImageView:ImageView = view.findViewById(R.id.thumbnailImageView1)

            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            Glide
                .with(thumbnailImageView.context)
                .load(houseModel.imgUrl)
                .fallback(R.drawable.ic_launcher_background)
                .into(thumbnailImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.itme_house_detail,parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val differ = object : DiffUtil.ItemCallback<HouseModel>(){
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}