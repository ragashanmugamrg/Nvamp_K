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

class Generlistadapter(
    private val folderitems: Map<String, List<Song>>,
    private val medcontroller: ListenableFuture<MediaController>,
) :
    RecyclerView.Adapter<Generlistadapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val nooftracks: TextView = view.findViewById(R.id.nooftracks)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_card_view, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return folderitems.size
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
    ) {
        val foldername = mutableListOf(folderitems.keys)
        var splitname = foldername[0].elementAt(position).substring(0, foldername[0].elementAt(position).length - 1).split("/")
        holder.title.text = splitname[splitname.size - 1]
        holder.nooftracks.text = folderitems.get(foldername[0].elementAt(position))?.size.toString() + " Items"

        val bundle =
            Bundle().apply {
                putInt("position", position)
                putString("genername", foldername[0].elementAt(position).toString())
                putString("fromfragment", "gener")
            }
        val folderSongList =
            AlbumSongList().apply {
                arguments = bundle
            }

        holder.itemView.setOnClickListener {
            customFragmentManager.beginTransaction().replace(R.id.fragmentcontainer, folderSongList)
                .addToBackStack(null)
                .commit()
        }
    }
}
