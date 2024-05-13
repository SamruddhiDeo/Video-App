package com.example.videoapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class RecyclerVideosAdapter(val context : Context, val arrVideos : ArrayList<VideosModel>) : RecyclerView.Adapter<RecyclerVideosAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val videoTitle = itemView.findViewById<TextView>(R.id.videoTitle);
        val channelName = itemView.findViewById<TextView>(R.id.channelName);
        val videoView = itemView.findViewById<VideoView>(R.id.videoView);
        val playPauseBtn = itemView.findViewById<ImageView>(R.id.playPauseBtn);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.video_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return arrVideos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoTitle.text = arrVideos[position].videoTitle;
        holder.channelName.text = arrVideos[position].channelName;
        holder.videoView.setVideoURI(arrVideos[position].videoUri);
        holder.videoView.setOnClickListener{
            if(holder.videoView.isPlaying){
    holder.videoView.pause()
    holder.playPauseBtn.setImageResource(R.drawable.baseline_play_circle_24)
}else{
    holder.videoView.start()
        holder.playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24)
}
        }
    }
}