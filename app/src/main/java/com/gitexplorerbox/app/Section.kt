package com.gitexplorerbox.app

data class Section(
    val title: String,
    val query: String,      // GitHub search query, пусто для локальных разделов
    val localOnly: Boolean = false
)

object Section {
    /** Каталог разделов. Добавлять новые разделы — сюда. */
    val seeds = listOf(
        Section("Популярные", "stars:>1000 sort:stars"),
        Section("Kotlin", "language:kotlin sort:stars"),
        Section("Java", "language:java sort:stars"),
        Section("Python", "language:python sort:stars"),
        Section("JavaScript", "language:javascript sort:stars"),
        Section("TypeScript", "language:typescript sort:stars"),
        Section("Go", "language:go sort:stars"),
        Section("Rust", "language:rust sort:stars"),
        Section("Машинное обучение", "topic:machine-learning sort:stars"),
        Section("Инструменты разработки", "topic:developer-tools sort:stars"),
        Section("Безопасность", "topic:security sort:stars"),
        Section("Игры", "topic:games sort:stars"),
        Section("Избранное", "", localOnly = true)
    )
}
