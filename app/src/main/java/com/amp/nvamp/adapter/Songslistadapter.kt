package com.amp.nvamp.adapter

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.NvampApplication.Companion.context
import com.amp.nvamp.R
import com.amp.nvamp.utils.NvampUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.common.util.concurrent.ListenableFuture

class Songslistadapter(
    private val mediaitems: MutableList<MediaItem>,
    private val medcontroller: ListenableFuture<MediaController>
): RecyclerView.Adapter<Songslistadapter.MyViewHolder>() {


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.title)
        val songartist: TextView = view.findViewById(R.id.artist)
        val songDuration: TextView = view.findViewById(R.id.duration)
        val songalbumart: ImageView = view.findViewById(R.id.imageView)

        val options: MaterialButton = view.findViewById(R.id.options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.songs_card_view,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = mediaitems.size


    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.songTitle.text = mediaitems[position].mediaMetadata.title
        holder.songartist.text = mediaitems[position].mediaMetadata.artist
        holder.songDuration.text = mediaitems[position].mediaMetadata.durationMs?.let {
            NvampUtils().formatDuration(
                it
            )
        }

        Glide.with(holder.songalbumart.context)
            .load(mediaitems[position].mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_songs_foreground)
            .into(holder.songalbumart)

        holder.options.setOnClickListener { v ->
            showPopupMenu(v,position)
        }






        holder.itemView.setOnClickListener{
            if (medcontroller.isDone){
                var controller = medcontroller.get()
                controller.setMediaItems(mediaitems,position,0)
                controller.play()
                playerViewModel.setlastplayedpos(position)
            }

        }

    }

    fun showPopupMenu(view: View,position: Int) {
        val themedContext = ContextThemeWrapper(context, R.style.Base_Theme_Nvamp)
        val popupMenu = PopupMenu(themedContext,view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.song_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item -> handleMenuClick() }
        popupMenu.show()
    }


    fun handleMenuClick(): Boolean{

        return false
    }
}