package com.amp.nvamp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.customFragmentManager
import com.amp.nvamp.R
import com.amp.nvamp.data.Song
import com.amp.nvamp.fragments.AlbumSongList
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture

class Albumlistadapter(
    private val albumitems: Map<String, List<Song>>,
    private val medcontroller: ListenableFuture<MediaController>) :
    RecyclerView.Adapter<Albumlistadapter.MyViewHolder>() {


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val albumTitle: TextView = view.findViewById(R.id.albumsongsname)
        val albumart: ImageView = view.findViewById(R.id.albumartimage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.album_card_view,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumitems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val albumname = mutableListOf(albumitems.keys)
        val songs = albumitems.values
        holder.albumTitle.text = albumname[0].elementAt(position).toString()
        try {
            Glide.with(holder.albumart.context)
                .load("content://media/external/audio/albumart/"+
                        albumitems.get(albumname[0].elementAt(position).toString())?.get(0)?.album_id)
                .placeholder(R.drawable.ic_songs_foreground)
                .into(holder.albumart)
        }catch (_: Exception){

        }

        val bundle = Bundle().apply {
            putInt("position",position)
            putString("albumname",albumname[0].elementAt(position).toString())
            putString("fromfragment","album")
        }
        val albumSongList = AlbumSongList().apply {
            arguments = bundle
        }

        holder.itemView.setOnClickListener {
            customFragmentManager.beginTransaction().replace(R.id.fragmentcontainer,albumSongList)
                .addToBackStack(null)
                .commit()
        }

    }
}