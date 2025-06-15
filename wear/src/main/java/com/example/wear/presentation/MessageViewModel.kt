package com.example.wear.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MensajeNotificacion(val titulo: String, val subtitulo: String)

class MessageViewModel : ViewModel() {
    private val _mensajes = MutableStateFlow<List<MensajeNotificacion>>(emptyList())
    val mensajes: StateFlow<List<MensajeNotificacion>> = _mensajes

    fun agregarMensaje(mensaje: MensajeNotificacion) {
        _mensajes.value = _mensajes.value + mensaje
    }
}