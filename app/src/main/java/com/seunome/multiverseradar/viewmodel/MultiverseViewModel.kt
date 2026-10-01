package com.seunome.multiverseradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seunome.multiverseradar.database.CharacterEntity
import com.seunome.multiverseradar.database.MultiverseDatabase
import com.seunome.multiverseradar.repository.CharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SyncState {
    object Idle : SyncState
    object Loading : SyncState
    object Success : SyncState
    data class Error(val message: String) : SyncState
}

class MultiverseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CharacterRepository(
        dao = MultiverseDatabase.getInstance(application).characterDao()
    )

    val characters: StateFlow<List<CharacterEntity>> = repository.observeCharacters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun sync() {
        if (_syncState.value is SyncState.Loading) return
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _syncState.value = try {
                repository.syncCharacters()
                SyncState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SyncState.Error("Não foi possível sincronizar agora. Mostrando personagens salvos.")
            }
        }
    }
}
