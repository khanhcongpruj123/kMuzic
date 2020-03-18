package com.icongkhanh.kmuzic.domain.models

interface DomainModelMapper<T> {
    fun mapToDomain(): T
}
