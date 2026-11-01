package com.gitexplorerbox.app

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Файловый кэш ответов GitHub API во внутреннем кэше приложения.
 * TTL = 24 часа. Используется как fallback, когда сеть недоступна.
 */
object Cache {
    private const val TTL_MS = 24L * 60 * 60 * 1000

    private fun fileFor(context: Context, url: String): File {
        val md5 = MessageDigest.getInstance("MD5").digest(url.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return File(context.cacheDir, "gh_$md5.json")
    }

    fun save(context: Context, url: String, json: String) {
        fileFor(context, url).writeText(json, Charsets.UTF_8)
    }

    fun load(context: Context, url: String): String? {
        val f = fileFor(context, url)
        return if (f.exists() && System.currentTimeMillis() - f.lastModified() < TTL_MS) {
            f.readText(Charsets.UTF_8)
        } else {
            null
        }
    }
}
