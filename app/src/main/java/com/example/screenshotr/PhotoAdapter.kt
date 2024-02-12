package com.example.screenshotr

import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val imageIds: List<Long>,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    private var centerPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageId = imageIds[position]

        Glide.with(holder.itemView)
            .load(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(imageId.toString()).build()
            )
            .into(holder.photoImageView)

        holder.itemView.setOnClickListener {
            onItemClick(imageId)
        }

        val context = holder.itemView.context
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams

        if (position == centerPosition) {
            layoutParams.width =
                context.resources.getDimensionPixelSize(R.dimen.dimen_65dp)
            layoutParams.height =
                context.resources.getDimensionPixelSize(R.dimen.dimen_65dp)
            layoutParams.topMargin =
                context.resources.getDimensionPixelSize(R.dimen.dimen_0dp)
        } else {
            layoutParams.width =
                context.resources.getDimensionPixelSize(R.dimen.dimen_55dp)
            layoutParams.height =
                context.resources.getDimensionPixelSize(R.dimen.dimen_55dp)
            layoutParams.topMargin =
                context.resources.getDimensionPixelSize(R.dimen.dimen_10dp)
        }

        holder.itemView.layoutParams = layoutParams

        val roundedCornersDrawable =
            ContextCompat.getDrawable(context, R.drawable.rounded_corners)
        holder.photoImageView.background = roundedCornersDrawable
        holder.photoImageView.clipToOutline = true
    }

    override fun getItemCount(): Int = imageIds.size

    fun setCenterPosition(centerPosition: Int) {
        this.centerPosition = centerPosition
        notifyDataSetChanged()
    }
}
