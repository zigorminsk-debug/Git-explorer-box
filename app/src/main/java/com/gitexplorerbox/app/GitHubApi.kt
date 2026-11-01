package com.gitexplorerbox.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

object GitHubApi {
    private const val BASE = "https://api.github.com"
    private const val USER_AGENT = "GitExplorerBox/1.0"
    private val executor = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())

    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(error: String)
    }

    /**
     * Поиск репозиториев по запросу GitHub.
     * При ошибке сети пробует кэш; если и кэша нет — ошибка.
     */
    fun searchRepositories(
        context: Context,
        query: String,
        page: Int,
        callback: Callback<List<Repo>>
    ) {
        executor.execute {
            val encoded = URLEncoder.encode(query, Charsets.UTF_8.name())
            val urlString = "$BASE/search/repositories?q=$encoded&sort=stars&order=desc&per_page=20&page=$page"
            val json = try {
                fetchString(urlString, context)
            } catch (e: Exception) {
                null
            } ?: Cache.load(context, urlString) ?: run {
                mainHandler.post { callback.onError("Сеть недоступна и кэш пуст") }
                return@execute
            }
            try {
                val root = JSONObject(json)
                val items = root.getJSONArray("items")
                val list = ArrayList<Repo>(items.length())
                for (i in 0 until items.length()) {
                    Repo.fromItem(items.getJSONObject(i)).let { list.add(it) }
                }
                Cache.save(context, urlString, json)
                mainHandler.post { callback.onSuccess(list) }
            } catch (e: Exception) {
                mainHandler.post { callback.onError("Ошибка разбора ответа GitHub: ${e.message}") }
            }
        }
    }

    /** Один репозиторий по полному имени (owner/repo). */
    fun repository(
        context: Context,
        fullName: String,
        callback: Callback<Repo>
    ) {
        executor.execute {
            val urlString = "$BASE/repos/$fullName"
            val json = try {
                fetchString(urlString, context)
            } catch (e: Exception) {
                null
            } ?: Cache.load(context, urlString) ?: run {
                mainHandler.post { callback.onError("Репозиторий не найден") }
                return@execute
            }
            try {
                val repo = Repo.fromItem(JSONObject(json))
                Cache.save(context, urlString, json)
                mainHandler.post { callback.onSuccess(repo) }
            } catch (e: Exception) {
                mainHandler.post { callback.onError("Ошибка разбора ответа GitHub: ${e.message}") }
            }
        }
    }

    private fun fetchString(urlString: String, context: Context): String? {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("User-Agent", USER_AGENT) // GitHub требует User-Agent
            setRequestProperty("Accept", "application/vnd.github+json")
            val token = context.getString(R.string.github_api_token).trim()
            if (token.isNotEmpty()) {
                setRequestProperty("Authorization", "Bearer $token")
            }
        }
        return try {
            connection.inputStream.use { is ->
                BufferedReader(InputStreamReader(is, Charsets.UTF_8)).use { br ->
                    br.readText()
                }
            }
        } finally {
            connection.disconnect()
        }
    }
}
