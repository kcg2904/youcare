package com.KingsStory.Application

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

// bottomsheet 어뎁터
class CaregiverListAdapter:ListAdapter<CaregiverModel,CaregiverListAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(val view : View): RecyclerView.ViewHolder(view){
        fun bind(caregiverModel: CaregiverModel){
            val nameTextView:TextView = view.findViewById(R .id.nameTextView)
            val addressTextView:TextView = view.findViewById(R.id.addressTextView)
            val ageTextView : TextView = view.findViewById(R.id.ageTextView)
            val genderTextView : TextView = view.findViewById(R.id.genderTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            nameTextView.text = caregiverModel.name
            addressTextView.text = caregiverModel.address
            ageTextView.text = caregiverModel.age
            genderTextView.text = caregiverModel.gender

            Glide
                .with(thumbnailImageView.context)
                .load(caregiverModel.imgUrl)
                .transform(CenterCrop(),RoundedCorners(dpToPx(thumbnailImageView.context,12)))
                .fallback(R.drawable.ic_launcher_background)
                .into(thumbnailImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.caregiverlist,parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    private fun dpToPx(context: Context, dp:Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(),context.resources.displayMetrics).toInt()
    }
    companion object{
        val differ = object : DiffUtil.ItemCallback<CaregiverModel>(){
            override fun areItemsTheSame(oldItem: CaregiverModel, newItem: CaregiverModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CaregiverModel, newItem: CaregiverModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}


