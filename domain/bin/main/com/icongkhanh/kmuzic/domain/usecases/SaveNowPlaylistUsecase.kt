package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository

class SaveNowPlaylistUsecase(val repository: MuzicRepository) {

    suspend operator fun invoke(list: List<Music>) {
        repository.saveAllMusicToNowPlaylist(list)
    }
}
