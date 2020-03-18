package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository

class AddMusicToFavorite(val repository: MuzicRepository) {

    suspend operator fun invoke(muzicId: String) {
        return repository.addToFavorite(muzicId)
    }
}
