package com.icongkhanh.kmuzic.domain.usecases

import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository

class GetNowPlaylistUsecase(val repo: MuzicRepository) {

    suspend operator fun invoke() = repo.getNowPlaylist()
}
