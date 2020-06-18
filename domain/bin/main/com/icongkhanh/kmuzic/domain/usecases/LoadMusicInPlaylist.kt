package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow

class LoadMusicInPlaylist(val repository: MuzicRepository) {

    suspend operator fun invoke(playlistId: String): Flow<Result<List<Music>>> {
        return repository.loadMusicInPlaylist(playlistId)
    }
}
