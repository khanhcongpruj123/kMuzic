package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow

class LoadAllMusicUseCase(val repository: MuzicRepository) {

    suspend operator fun invoke(isFirstTimeOpenApp: Boolean): Flow<List<Music>> {
        return repository.loadAllMuzic(isFirstTimeOpenApp)
    }
}