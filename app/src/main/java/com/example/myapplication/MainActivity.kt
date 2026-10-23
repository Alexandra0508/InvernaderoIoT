package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos visuales (asegúrate de agregarlos en tu activity_main.xml)
        val btnConectar = findViewById<Button>(R.id.btnConectar)
        val txtDatos = findViewById<TextView>(R.id.txtDatos)

        // Al hacer clic en el botón, nos conectamos al ESP32 mediante corrutinas
        btnConectar.setOnClickListener {
            txtDatos.text = "Conectando al invernadero..."

            // lifecycleScope permite ejecutar corrutinas de manera segura en la Activity
            lifecycleScope.launch {
                conectarAlInvernaderoWifi { datosRecibidos ->
                    txtDatos.text = datosRecibidos
                }
            }
        }
    }

    // Función que realiza la conexión por Wi-Fi (Socket TCP) fuera del hilo principal
    private suspend fun conectarAlInvernaderoWifi(onDataReceived: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                // IP por defecto del ESP32 en modo Access Point y puerto 80
                val socket = Socket("192.168.4.1", 80)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // Leemos la trama enviada por el ESP32 (Temp, Hum, Luz)
                val response = reader.readLine() ?: "Sin datos del servidor"

                // Volvemos al hilo principal (UI) para actualizar la pantalla
                withContext(Dispatchers.Main) {
                    onDataReceived(response)
                }

                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onDataReceived("Error de conexión: ${e.localizedMessage}")
                }
            }
        }
    }
}