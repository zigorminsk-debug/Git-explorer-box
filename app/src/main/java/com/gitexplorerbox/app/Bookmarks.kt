package com.gitexplorerbox.app

import android.content.Context
import android.content.SharedPreferences

/**
 * Звёзды/закладки репозиториев. Хранятся локально в SharedPreferences
 * (JSON каждого репозитория), поэтому работают офлайн и не тратят лимит GitHub API.
 */
object Bookmarks {
    private const val PREFS = "gitexplorerbox_bookmarks"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun key(fullName: String) = "repo_$fullName"

    fun isBookmarked(context: Context, fullName: String): Boolean =
        prefs(context).contains(key(fullName))

    fun toggle(context: Context, repo: Repo): Boolean {
        val p = prefs(context)
        val k = key(repo.fullName)
        val ed = p.edit()
        val added = !p.contains(k)
        if (added) {
            ed.putString(k, repo.toJson())
        } else {
            ed.remove(k)
        }
        ed.apply()
        return added
    }

    fun all(context: Context): List<Repo> =
        prefs(context).all.keys
            .filter { it.startsWith("repo_") }
            .mapNotNull { prefs(context).getString(it, "") }
            .mapNotNull { Repo.fromJson(it) }
}
