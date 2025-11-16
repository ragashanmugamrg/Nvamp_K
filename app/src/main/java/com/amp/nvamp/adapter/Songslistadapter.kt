package com.amp.nvamp.adapter

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.R
import com.amp.nvamp.data.Song
import com.amp.nvamp.fragments.PlaylistFragment
import com.amp.nvamp.utils.NvampUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import java.io.File

class Songslistadapter(
    private val mediaitems: MutableList<MediaItem>,
    private val medcontroller: ListenableFuture<MediaController>
) : RecyclerView.Adapter<Songslistadapter.MyViewHolder>() {


    var songqueue = mutableListOf<MediaItem>()

    val playlistmap = mutableMapOf<String, List<Song>>()
    val dynamicChoice: Set<String> = playlistmap.keys



    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.title)
        val songartist: TextView = view.findViewById(R.id.artist)
        val songDuration: TextView = view.findViewById(R.id.duration)
        val songalbumart: ImageView = view.findViewById(R.id.imageView)

        val options: MaterialButton = view.findViewById(R.id.options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.songs_card_view, parent, false)
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
            showPopupMenu(v, position)
        }






        holder.itemView.setOnClickListener {
            songqueue.addAll(mediaitems)

            if (medcontroller.isDone && !medcontroller.isCancelled) {
                var controller = medcontroller.get()
                controller.setMediaItems(songqueue, position, 0L)
                controller.prepare()
                controller.play()
                playerViewModel.setlastplayedpos(position)
            }


        }

    }

    fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.song_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item -> handleMenuClick(item, position, view) }
        popupMenu.show()
    }


    @OptIn(UnstableApi::class)
    fun handleMenuClick(item: MenuItem, position: Int, view: View): Boolean {
        var playlistnames = mutableListOf<String>()
        playlistmap.putAll(playerViewModel.getplayListMusic())
        dynamicChoice.forEach { name ->
            playlistnames.add(name)
        }
        return when (item.title) {

            "Play Next" -> {
                if (songqueue.isNotEmpty()) {

//                    val lastplayedpos = playerViewModel.getlastplayedpos()
                    val mediaItem = MediaItem.Builder()
                        .setMediaId(mediaitems[position].mediaMetadata.description.toString())
                        .setUri(
                            (mediaitems[position].mediaMetadata.description.toString()
                                .let { it -> File(it) }).toUri()
                        )
                        .setMediaId("MediaStore:id")
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(mediaitems[position].mediaMetadata.title.toString())
                                .setArtist(mediaitems[position].mediaMetadata.artist.toString())
                                .setDurationMs(mediaitems[position].mediaMetadata.durationMs)
                                .setArtworkUri(mediaitems[position].mediaMetadata.artworkUri!!)
                                .setDescription(mediaitems[position].mediaMetadata.description.toString())
                                .build()
                        ).build()
//                    songqueue.add(lastplayedpos,mediaItem.build())
                    var controller = medcontroller.get()
                    controller.addMediaItem(
                        medcontroller.get().currentMediaItemIndex + 1,
                        mediaItem
                    )
                    controller.prepare()

                }
                return true
            }

            "Add to Playlist" -> {
                MaterialAlertDialogBuilder(view.context)
                    .setTitle("Save")
                    .setNeutralButton("ADD") { dialog, id ->
                        val editText = EditText(view.context)
                        MaterialAlertDialogBuilder(view.context)
                            .setTitle("playlist")
                            .setView(editText)
                            .setNegativeButton("cancle") { dialog1, which ->
                                dialog.dismiss()
                            }
                            .setPositiveButton("ok") { dialog, which ->
                                val playlistname = editText.text
                                val playlist = Song(
                                    mediaitems[position].mediaMetadata.title.toString(),
                                    mediaitems[position].mediaMetadata.artist.toString(),
                                    mediaitems[position].mediaMetadata.durationMs,
                                    mediaitems[position].mediaMetadata.description.toString(),
                                    "",
                                    "",
                                    0L,
                                    mediaitems[position].mediaMetadata.artworkUri!!,
                                    "",
                                    "",
                                    mediaitems[position].mediaMetadata.description.toString(),
                                    0,
                                    mediaitems[position].mediaMetadata.trackNumber,
                                    0L
                                )
                                var newplaylist = mutableListOf<Song>()
                                newplaylist.add(playlist)
                                playlistmap.put(playlistname.toString(), newplaylist)
                                playerViewModel.setplayListMusic(playlistmap)
                                PlaylistFragment.playernotify()
                            }
                            .show()
                    }
                    .setNegativeButton("cancle") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("ok", { dialog: DialogInterface?, which: Int ->
                        PlaylistFragment.playernotify()
                    })
                    .setMultiChoiceItems(
                        playlistnames.toTypedArray(),
                        null
                    ) { dialog, which, ischecked ->
                        val selectedItem = playlistnames[which]
                        if (ischecked) {
                            val playlist = Song(
                                mediaitems[position].mediaMetadata.title.toString(),
                                mediaitems[position].mediaMetadata.artist.toString(),
                                mediaitems[position].mediaMetadata.durationMs,
                                mediaitems[position].mediaMetadata.description.toString(),
                                "",
                                "",
                                0L,
                                mediaitems[position].mediaMetadata.artworkUri!!,
                                "",
                                "",
                                mediaitems[position].mediaMetadata.description.toString(),
                                0,
                                mediaitems[position].mediaMetadata.trackNumber,
                                0L
                            )
                            var newplaylist = mutableListOf<Song>()
                            val play = playlistmap.get(selectedItem)?.toMutableList()
                            if (play != null) {
                                play.forEach { data ->
                                    newplaylist.add(data)
                                }
                            }
                            newplaylist.add(playlist)
                            playlistmap.put(selectedItem, newplaylist)
                            playerViewModel.setplayListMusic(playlistmap)
                        }
                    }
                    .show()
                return true
            }

            else -> false
        }
    }
}