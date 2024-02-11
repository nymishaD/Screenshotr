package com.example.screenshotr

import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

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

        holder.photoImageView.setImageURI(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                .appendPath(imageId.toString()).build()
        )

        holder.itemView.setOnClickListener {
            onItemClick(imageId)
        }

        val context = holder.itemView.context
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams

        if (position == centerPosition) {
            layoutParams.width =
                context.resources.getDimensionPixelSize(R.dimen.center_item)
            layoutParams.height =
                context.resources.getDimensionPixelSize(R.dimen.center_item)
            layoutParams.topMargin =
                context.resources.getDimensionPixelSize(R.dimen.center_item_margin_top)
        } else {
            layoutParams.width =
                context.resources.getDimensionPixelSize(R.dimen.other_item)
            layoutParams.height =
                context.resources.getDimensionPixelSize(R.dimen.other_item)
            layoutParams.topMargin =
                context.resources.getDimensionPixelSize(R.dimen.other_item_margin_top)
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
