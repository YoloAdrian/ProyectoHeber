package com.example.wear.presentation

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class ParcialMensaje(val title: String, val subtitle: String, val timestamp: Long)

object NotificationRepository {
    private const val PREFS_NAME = "wear_prefs"
    private const val KEY_NOTIFICATIONS = "key_notifications"
    private var prefs: SharedPreferences? = null

    private val _mensajes = MutableStateFlow<List<ParcialMensaje>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadFromPrefs()
        }
    }

    private fun loadFromPrefs() {
        val jsonString = prefs?.getString(KEY_NOTIFICATIONS, null) ?: run {
            _mensajes.value = emptyList()
            return
        }
        val list = mutableListOf<ParcialMensaje>()
        try {
            val arr = JSONArray(jsonString)
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val title = obj.optString("title", "")
                val subtitle = obj.optString("subtitle", "")
                val ts = obj.optLong("timestamp", System.currentTimeMillis())
                list.add(ParcialMensaje(title, subtitle, ts))
            }
        } catch (e: Exception) {
            prefs?.edit()?.remove(KEY_NOTIFICATIONS)?.apply()
        }
        _mensajes.value = list
    }

    fun addMensaje(context: Context, title: String, subtitle: String) {
        init(context)
        val timestamp = System.currentTimeMillis()
        // Directamente en hilo IO
        val current = _mensajes.value.toMutableList()
        current.add(0, ParcialMensaje(title, subtitle, timestamp))
        if (current.size > 15) current.subList(15, current.size).clear()
        // Guardar en prefs
        val arr = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("title", item.title)
                put("subtitle", item.subtitle)
                put("timestamp", item.timestamp)
            }
            arr.put(obj)
        }
        // Puedes usar apply() o commit(): si observas que a veces no persiste, usa commit() aquí
        prefs?.edit()?.putString(KEY_NOTIFICATIONS, arr.toString())?.apply()
        _mensajes.value = current
    }

    fun removeMensaje(context: Context, mensaje: ParcialMensaje) {
        init(context)
        val current = _mensajes.value.toMutableList()
        val iterator = current.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.timestamp == mensaje.timestamp
                && item.title == mensaje.title
                && item.subtitle == mensaje.subtitle) {
                iterator.remove()
                break
            }
        }
        val arr = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("title", item.title)
                put("subtitle", item.subtitle)
                put("timestamp", item.timestamp)
            }
            arr.put(obj)
        }
        prefs?.edit()?.putString(KEY_NOTIFICATIONS, arr.toString())?.apply()
        _mensajes.value = current
    }

    fun clearAllMensajes(context: Context) {
        init(context)
        prefs?.edit()?.remove(KEY_NOTIFICATIONS)?.apply()
        _mensajes.value = emptyList()
    }
}
