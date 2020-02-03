package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow

class LoadMusicInPlaylist (val repository: MuzicRepository) {

    suspend operator fun invoke(playlistId: String): Flow<Muzic> {
        return repository.loadMusicInPlaylist(playlistId)
    }
}