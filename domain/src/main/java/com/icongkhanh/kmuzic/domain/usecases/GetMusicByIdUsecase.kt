package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow

class GetMusicByIdUsecase(val repository: MuzicRepository) {

    suspend operator fun invoke(id: String): Flow<Result<Music>> {
        return repository.getMusicById(id)
    }
}
