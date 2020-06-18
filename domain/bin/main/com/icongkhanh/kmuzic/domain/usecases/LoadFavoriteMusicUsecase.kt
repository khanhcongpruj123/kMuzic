package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow

class LoadFavoriteMusicUsecase(val repository: MuzicRepository) {

    suspend operator fun invoke(): Flow<Result<List<Music>>> {
        return repository.loadFavoriteMuzic()
    }
}
