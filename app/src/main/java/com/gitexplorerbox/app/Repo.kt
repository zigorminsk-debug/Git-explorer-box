package com.gitexplorerbox.app

import org.json.JSONObject

data class Repo(
    val id: Int,
    val fullName: String,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val stars: Int,
    val forks: Int,
    val language: String?,
    val license: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val ownerLogin: String?,
    val ownerAvatarUrl: String?
) {

    /** Сериализация репозитория в JSON для локальных закладок (SharedPreferences). */
    fun toJson(): String = JSONObject().apply {
        put("id", id)
        put("full_name", fullName)
        put("name", name)
        put("description", description)
        put("html_url", htmlUrl)
        put("stargazers_count", stars)
        put("forks_count", forks)
        put("language", language)
        put("license", license)
        put("created_at", createdAt)
        put("updated_at", updatedAt)
        put("owner_login", ownerLogin)
        put("owner_avatar_url", ownerAvatarUrl)
    }.toString()

    companion object {

        fun fromJson(json: String): Repo? {
            return try {
                val o = JSONObject(json)
                Repo(
                    o.optInt("id", 0),
                    o.optString("full_name", ""),
                    o.optString("name", ""),
                    o.optString("description", null).takeIf { it.isNotEmpty() },
                    o.optString("html_url", ""),
                    o.optInt("stargazers_count", 0),
                    o.optInt("forks_count", 0),
                    o.optString("language", null).takeIf { it.isNotEmpty() },
                    o.optString("license", null).takeIf { it.isNotEmpty() },
                    o.optString("created_at", null).takeIf { it.isNotEmpty() },
                    o.optString("updated_at", null).takeIf { it.isNotEmpty() },
                    o.optString("owner_login", null).takeIf { it.isNotEmpty() },
                    o.optString("owner_avatar_url", null).takeIf { it.isNotEmpty() }
                )
            } catch (e: Exception) {
                null
            }
        }

        fun fromItem(o: JSONObject): Repo {
            val license = o.optJSONObject("license")
            val owner = o.optJSONObject("owner")
            return Repo(
                o.optInt("id", 0),
                o.optString("full_name", ""),
                o.optString("name", ""),
                o.optString("description", null).takeIf { it.isNotEmpty() },
                o.optString("html_url", ""),
                o.optInt("stargazers_count", 0),
                o.optInt("forks_count", 0),
                o.optString("language", null).takeIf { it.isNotEmpty() },
                license?.optString("spdx_id", null)?.takeIf { it.isNotEmpty() },
                o.optString("created_at", null).takeIf { it.isNotEmpty() },
                o.optString("updated_at", null).takeIf { it.isNotEmpty() },
                owner?.optString("login", null)?.takeIf { it.isNotEmpty() },
                owner?.optString("avatar_url", null)?.takeIf { it.isNotEmpty() }
            )
        }
    }
}
