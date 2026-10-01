package com.seunome.multiverseradar.repository

import com.seunome.multiverseradar.database.CharacterDao
import com.seunome.multiverseradar.database.CharacterEntity
import com.seunome.multiverseradar.network.CharacterDto
import com.seunome.multiverseradar.network.RetrofitClient
import com.seunome.multiverseradar.network.RickAndMortyService
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val dao: CharacterDao,
    private val api: RickAndMortyService = RetrofitClient.api
) {
    fun observeCharacters(): Flow<List<CharacterEntity>> = dao.observeAll()

    suspend fun syncCharacters() {
        val resposta = api.getCharacters()
        dao.upsertAll(resposta.results.map { it.toEntity() })
    }
}

private fun CharacterDto.toEntity() = CharacterEntity(
    id = id,
    name = name,
    species = species,
    imageUrl = image
)
