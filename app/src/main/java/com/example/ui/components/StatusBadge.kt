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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "Activa", "Contratado", "Completada", "Oferta / Contratado" -> EmeraldLight to EmeraldOnContainer
        "En Pausa", "Llamada / Filtro", "Llamada Pendiente", "Entrevista", "Documentos" -> PurpleAILight to PurpleAIOnContainer
        "Cerrada", "Rechazado", "No Asistió", "Cancelada", "Descartado" -> RoseLight to RoseOnContainer
        "Alta" -> RoseLight to RoseOnContainer
        "Media" -> AmberLight to AmberOnContainer
        "Baja" -> ElectricBlueContainer to ElectricBlueOnContainer
        else -> ElectricBlueContainer to ElectricBlueOnContainer
    }

    Text(
        text = status.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = textColor,
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
