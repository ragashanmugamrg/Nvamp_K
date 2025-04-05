package com.amp.nvamp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.customFragmentManager
import com.amp.nvamp.R
import com.amp.nvamp.data.Song
import com.amp.nvamp.fragments.AlbumSongList
import com.google.common.util.concurrent.ListenableFuture
import java.util.Collections

class Artistlistadapter(
    private val artistitems: Map<String, List<Song>>,
    private val medcontroller: ListenableFuture<MediaController>):
    RecyclerView.Adapter<Artistlistadapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.title)
        val nooftracks: TextView = view.findViewById(R.id.nooftracks)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_card_view,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return artistitems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var foldername = mutableListOf(artistitems.keys)
        var folder = foldername[0].sorted()
        holder.title.text = folder[position]
        holder.nooftracks.text = artistitems.get(folder[position])?.size.toString()+" Items"

        val bundle = Bundle().apply {
            putInt("position",position)
            putString("artistname",folder[position])
            putString("fromfragment","artist")
        }
        val folderSongList = AlbumSongList().apply {
            arguments = bundle
        }

        holder.itemView.setOnClickListener {
            customFragmentManager.beginTransaction().replace(R.id.fragmentcontainer,folderSongList)
                .addToBackStack(null)
                .commit()
        }
    }
}