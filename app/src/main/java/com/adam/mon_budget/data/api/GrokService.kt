package com.adam.mon_budget.data.api

import com.adam.mon_budget.BuildConfig
import com.adam.mon_budget.data.model.Goal
import com.adam.mon_budget.data.session.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val GROK_URL   = "https://api.x.ai/v1/chat/completions"
private const val GROK_MODEL = "grok-3-mini"

object GrokService {

    suspend fun getGoalAdvice(goals: List<Goal>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val budget = UserSession.currentUserBudget
            val prenom = UserSession.currentUserPrenom.ifBlank { "l'utilisateur" }

            val prompt = buildString {
                appendLine("Tu es un conseiller financier personnel bienveillant pour $prenom.")
                appendLine("Réponds toujours en français, de manière courte et motivante.")
                appendLine()
                appendLine("Voici ses objectifs d'épargne :")
                if (goals.isEmpty()) {
                    appendLine("- Aucun objectif encore défini.")
                } else {
                    goals.forEach { g ->
                        val pct = if (g.montantCible > 0) ((g.montantActuel / g.montantCible) * 100).toInt() else 0
                        appendLine("- ${g.nom} : ${g.montantActuel}€ / ${g.montantCible}€ ($pct% atteint)")
                    }
                }
                if (budget > 0) appendLine("\nSon budget mensuel est de ${budget}€.")
                appendLine()
                appendLine("Donne exactement 3 conseils personnalisés, numérotés 1. 2. 3., courts et actionnables pour l'aider à atteindre ses objectifs plus vite.")
            }

            val requestBody = JSONObject().apply {
                put("model", GROK_MODEL)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("max_tokens", 400)
                put("temperature", 0.7)
            }.toString()

            val connection = (URL(GROK_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer ${BuildConfig.GROK_API_KEY}")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15_000
                readTimeout = 30_000
                doOutput = true
            }

            OutputStreamWriter(connection.outputStream).use { it.write(requestBody) }

            val code = connection.responseCode
            if (code != 200) {
                val errBody = try {
                    connection.errorStream?.bufferedReader()?.readText() ?: "pas de détails"
                } catch (_: Exception) { "lecture erreur impossible" }
                return@withContext Result.failure(Exception("HTTP $code — $errBody"))
            }

            val responseText = connection.inputStream.bufferedReader().readText()
            val content = JSONObject(responseText)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
