package com.gitexplorerbox.app

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit-тесты модели Repo: JSON-сериализация / десериализация.
 * Не требуют Android SDK — запускаются обычным `./gradlew test`.
 */
class RepoTest {

    @Test
    fun toJsonAndBackRoundTrip() {
        val original = Repo(
            id = 12345,
            fullName = "owner/repo-name",
            name = "repo-name",
            description = "A test repository",
            htmlUrl = "https://github.com/owner/repo-name",
            stars = 42,
            forks = 7,
            language = "Kotlin",
            license = "MIT",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-02-02T00:00:00Z",
            ownerLogin = "owner",
            ownerAvatarUrl = "https://avatars.githubusercontent.com/u/1?v=4"
        )

        val json = original.toJson()
        val restored = Repo.fromJson(json) ?: fail("fromJson returned null")

        assertEquals(original.id, restored.id)
        assertEquals(original.fullName, restored.fullName)
        assertEquals(original.name, restored.name)
        assertEquals(original.description, restored.description)
        assertEquals(original.htmlUrl, restored.htmlUrl)
        assertEquals(original.stars, restored.stars)
        assertEquals(original.forks, restored.forks)
        assertEquals(original.language, restored.language)
        assertEquals(original.license, restored.license)
        assertEquals(original.createdAt, restored.createdAt)
        assertEquals(original.updatedAt, restored.updatedAt)
        assertEquals(original.ownerLogin, restored.ownerLogin)
        assertEquals(original.ownerAvatarUrl, restored.ownerAvatarUrl)
    }

    @Test
    fun fromJsonWithMissingOptionalFields() {
        val json = """{"id":1,"full_name":"o/r","name":"r","html_url":"https://github.com/o/r","stargazers_count":0,"forks_count":0}"""
        val repo = Repo.fromJson(json) ?: fail("fromJson returned null")

        assertEquals(1, repo.id)
        assertEquals("o/r", repo.fullName)
        assertEquals("r", repo.name)
        assertNull(repo.description)
        assertNull(repo.language)
        assertNull(repo.license)
        assertEquals(0, repo.stars)
        assertEquals(0, repo.forks)
    }

    @Test
    fun fromItemUsesLicenseSpdxId() {
        val o = org.json.JSONObject().apply {
            put("id", 9)
            put("full_name", "a/b")
            put("name", "b")
            put("html_url", "https://github.com/a/b")
            put("stargazers_count", 5)
            put("forks_count", 1)
            put("language", "Java")
            put("license", org.json.JSONObject().put("spdx_id", "Apache-2.0"))
            put("owner", org.json.JSONObject().put("login", "a").put("avatar_url", "https://avatar.example/a.png"))
        }

        val repo = Repo.fromItem(o)
        assertEquals("Apache-2.0", repo.license)
        assertEquals("Java", repo.language)
        assertEquals("a", repo.ownerLogin)
        assertEquals("https://avatar.example/a.png", repo.ownerAvatarUrl)
    }
}
