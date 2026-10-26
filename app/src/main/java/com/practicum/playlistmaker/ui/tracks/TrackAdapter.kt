package com.practicum.playlistmaker.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter(
    private val trackList: List<Track>,
    private val onTrackClick: (Track) -> Unit): RecyclerView.Adapter<TrackViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track, parent, false)
            return TrackViewHolder(view)
        }
        override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            val track = trackList[position]
            holder.bind(track)
            holder.itemView.setOnClickListener {
                if (holder.clickDebounce()) {
                    onTrackClick(track)
                }
            }
        }

        override fun getItemCount(): Int {
            return trackList.size
        }
    }
