package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "Activa", "Contratado", "Completada", "Oferta / Contratado", "Alta", "Compatible" -> EmeraldLight to EmeraldOnContainer
        "En Pausa", "Llamada / Filtro", "Llamada Pendiente", "Entrevista", "Documentos", "Media", "Revisar" -> AmberLight to AmberOnContainer
        "Cerrada", "Rechazado", "No Asistió", "Cancelada", "Descartado", "Baja", "No Compatible" -> RoseLight to RoseOnContainer
        else -> ElectricBlueContainer to ElectricBlueOnContainer
    }

    Text(
        text = status.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
