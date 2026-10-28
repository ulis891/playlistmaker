package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(expression)
                if (tracks.isNotEmpty()) {
                    consumer.consume(tracks)
                } else {
                    consumer.consume(emptyList())
                }
            } catch (e: Exception) {
                consumer.onError(e)
            }
        }
    }
}
