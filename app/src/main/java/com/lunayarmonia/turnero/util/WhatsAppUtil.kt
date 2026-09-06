package com.lunayarmonia.turnero.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.lunayarmonia.turnero.data.Paciente
import com.lunayarmonia.turnero.data.Turno
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val FORMATO_LEGIBLE = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM, HH:mm", Locale("es", "AR"))

/**
 * Arma el mensaje de recordatorio y abre WhatsApp con el chat de la
 * paciente ya abierto y el texto pre-cargado (envío manual: la usuaria
 * decide cuándo tocar "enviar", tal como se definió).
 */
fun enviarRecordatorioWhatsApp(context: Context, turno: Turno, paciente: Paciente) {
    val fechaLegible = turno.inicio.format(FORMATO_LEGIBLE)
    val mensaje = "Hola ${paciente.nombre.substringBefore(" ")}! Te recordamos tu turno de " +
        "${turno.nombreTratamientoSnapshot} en Luna y Armonía el $fechaLegible. Te esperamos!"

    val telefono = paciente.telefono.filter { it.isDigit() }
    val uri = Uri.parse("https://wa.me/$telefono?text=${Uri.encode(mensaje)}")

    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}
