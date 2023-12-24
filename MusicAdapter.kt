package com.harshRajpurohit.musicPlayer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.harshRajpurohit.musicPlayer.API.DataItem
import com.harshRajpurohit.musicPlayer.API.Response
import com.harshRajpurohit.musicPlayer.MusicAdapter.MyHolder
import com.harshRajpurohit.musicPlayer.databinding.DetailsViewBinding
import com.harshRajpurohit.musicPlayer.databinding.MoreFeaturesBinding
import com.harshRajpurohit.musicPlayer.databinding.MusicViewBinding
import java.util.Collections.addAll

class MusicAdapter(private val context: Context, private var musicList: ArrayList<DataItem>, private val playlistDetails: Boolean = false,
                   private val selectionActivity: Boolean = false) : RecyclerView.Adapter<MyHolder>() {

    inner class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val artist = binding.songNameMV
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    fun getItemPosition(position: Int): Int {
        return position
    }
    private fun onItemClicked(position: Int) {
        val adapterPosition = getItemPosition(position)
        // Lakukan apa pun yang perlu dilakukan ketika item diklik
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.artist.text = musicList[position].artist!!.name
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album!!.title
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artist!!.picture)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        //for play next feature
        if (!selectionActivity) {
            holder.root.setOnLongClickListener {
                val customDialog =
                    LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))

                bindingMF.AddToPNBtn.setOnClickListener {
                    try {
                        if (PlayNext.playNextList.isEmpty()) {
                            PlayNext.playNextList.add(PlayerActivity.musicListPA[PlayerActivity.songPosition])
                            PlayerActivity.songPosition = 0
                        }
                        PlayNext.playNextList.add(musicList[position])
                        PlayerActivity.musicListPA = ArrayList()
                        PlayerActivity.musicListPA.addAll(PlayNext.playNextList)
                    } catch (e: Exception) {
                        Snackbar.make(holder.root, "Play A Song First!!", 3000).show()
                    }
                    dialog.dismiss()
                }

                bindingMF.infoBtn.setOnClickListener {
                    dialog.dismiss()
                    val detailsDialog = LayoutInflater.from(context)
                        .inflate(R.layout.details_view, bindingMF.root, false)
                    val binder = DetailsViewBinding.bind(detailsDialog)
                    binder.detailsTV.setTextColor(Color.WHITE)
                    binder.root.setBackgroundColor(Color.TRANSPARENT)
                    val dDialog = MaterialAlertDialogBuilder(context)
                        .setBackground(ColorDrawable(0x99000000.toInt()))
                        .setView(detailsDialog)
                        .setPositiveButton("OK") { self, _ -> self.dismiss() }
                        .setCancelable(false)
                        .create()
                    dDialog.show()
                    dDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    setDialogBtnBackground(context, dDialog)
                    dDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                    val str = SpannableStringBuilder().bold { append("DETAILS\n\nName: ") }
                        .append(musicList[position].title)
                        .bold { append("\n\nDuration: ") }
                        .append(DateUtils.formatElapsedTime(musicList[position].duration!!.toLong() / 1000))
                        .bold { append("\n\nLocation: ") }.append(musicList[position].link)
                    binder.detailsTV.text = str
                }

                return@setOnLongClickListener true
            }
        }


        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }

            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.cool_pink
                            )
                        )
                    else
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                }
            }

            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent(
                            ref = "MusicAdapterSearch",
                            pos = position
                        )

                        musicList[position].id == PlayerActivity.nowPlayingId->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)

                        else -> sendIntent(ref = "MusicAdapter", pos = position)
                    }
                }
            }
        }
    }

    private fun append(s: String) {

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<DataItem>){
        musicList.run { addAll(searchList) }
        notifyDataSetChanged()
    }
    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }
    private fun addSong(DataItem: DataItem): Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(DataItem.id == DataItem.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(DataItem)
        return true
    }
    fun refreshPlaylist(){
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.also { musicList}
        notifyDataSetChanged()
    }
}