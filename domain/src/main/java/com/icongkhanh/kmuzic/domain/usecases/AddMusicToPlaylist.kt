package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository

class AddMusicToPlaylist (val repository: MuzicRepository) {

    suspend operator fun invoke(muzicId: String, playlistId: String) {
        return repository.addToPlaylist(muzicId, playlistId)
    }
}