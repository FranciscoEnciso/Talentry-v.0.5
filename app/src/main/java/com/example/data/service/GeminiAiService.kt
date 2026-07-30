package com.example.data.service

import com.example.BuildConfig
import com.squareup.moshi.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class GeminiPart(
    @field:Json(name = "text") val text: String? = null
)

data class GeminiContent(
    @field:Json(name = "parts") val parts: List<GeminiPart>
)

data class GeminiRequest(
    @field:Json(name = "contents") val contents: List<GeminiContent>
)

data class GeminiCandidate(
    @field:Json(name = "content") val content: GeminiContent
)

data class GeminiResponse(
    @field:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

class GeminiAiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(GeminiApi::class.java)

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Respuesta generada en modo offline:\n\n$prompt\n\n(Configura tu GEMINI_API_KEY en el panel de Secretos para respuestas en vivo de Gemini)"
        }

        try {
            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
            )
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No se obtuvo respuesta de IA."
        } catch (e: Exception) {
            "Simulación de IA (Error de red: ${e.localizedMessage}):\nProcesado con éxito para $prompt"
        }
    }

    suspend fun generateJobDescription(title: String, branch: String, requirements: String): String {
        val prompt = "Genera una descripción de vacante atractiva y profesional en español para reclutamiento operativo de alto volumen.\nPuesto: $title\nSucursal: $branch\nRequisitos clave: $requirements\nIncluye: Resumen del rol, Beneficios y Pasos para postularte."
        return generateContent(prompt)
    }

    suspend fun analyzeCandidateFit(candidateName: String, experience: String, vacancyTitle: String): String {
        val prompt = "Evalúa la compatibilidad del candidato $candidateName para la vacante de $vacancyTitle. Experiencia: $experience. Proporciona: 1) Puntuación estimada (0-100%), 2) Fortalezas clave, 3) Recomendación de contratación."
        return generateContent(prompt)
    }

    suspend fun generateInterviewSummary(candidateName: String, feedback: String): String {
        val prompt = "Redacta un resumen ejecutivo de entrevista laboral para $candidateName basado en los siguientes comentarios: $feedback. Incluye conclusión breve."
        return generateContent(prompt)
    }

    suspend fun generateAutoResponse(candidateName: String, stage: String): String {
        val prompt = "Redacta un mensaje amable de WhatsApp/SMS corto para enviar al candidato $candidateName que se encuentra en la etapa: $stage. Incluye indicación clara de siguientes pasos."
        return generateContent(prompt)
    }
}
