package com.example.wear.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationRepository {
    private val _mensajes = MutableStateFlow<List<ParcialMensaje>>(emptyList())
    val mensajes = _mensajes.asStateFlow()
    fun addMensaje(title: String, subtitle: String) {
        _mensajes.value = _mensajes.value + ParcialMensaje(title, subtitle)
    }
}

data class ParcialMensaje(val title: String, val subtitle: String)