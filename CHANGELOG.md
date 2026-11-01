# Changelog

## [1.0.4] — 2026-11-01

### Исправлено
- CI workflow очищен: убраны отладочные шаги (capture stacktrace, commit comments), сборка стабильно проходит (тесты + assembleDebug).
- `.gitignore` больше не исключает `build.gradle` файлы.

## [1.0.3] — 2026-11-01

### Исправлено
- CI workflow: `.gitignore` содержал `*.gradle`, что исключало `build.gradle` файлы из репозитория — сборка в Actions была невозможна. Убрано из игнорирования.
- CI устанавливает Android SDK вручную через `sdkmanager` (действие `android-actions/setup-android` не принимает нужные параметры на текущем раннере).
- `actions/setup-java` обновлён до v5, `gradle/actions/setup-gradle@v4` для кэширования.

## [1.0.2] — 2026-11-01

### Исправлено
- CI workflow больше не вызывает отсутствующий `./gradlew` (Gradle wrapper не был в репозитории).
- Используется системный Gradle через `gradle/actions/setup-gradle@v4` — команды `gradle test` / `gradle assembleDebug`.
- Оба workflow (`ci.yml`, `release.yml`) приведены к единому способу вызова Gradle.

## [1.0.1] — 2026-11-01

### Добавлено
- Unit-тесты модели `Repo` (JSON round-trip, опциональные поля, лицензия SPDX из `license.spdx_id`) — `app/src/test/.../RepoTest.kt`.
- Шаг `./gradlew test` в `.github/workflows/ci.yml` — тесты запускаются на каждый push/PR перед сборкой debug APK.

## [1.0.0] — 2026-11-01

### Добавлено
- Первая версия приложения Git Explorer Box (пакет `com.gitexplorerbox.app`).
- Каталог публичных репозиториев GitHub по разделам:
  Популярные, Kotlin, Java, Python, JavaScript, TypeScript, Go, Rust,
  Машинное обучение, Инструменты разработки, Безопасность, Игры, Избранное.
- Экран списка репозиториев раздела с описанием, языком, звёздами и форками
  (GitHub REST API, сортировка по звёздам).
- Экран поиска репозиториев GitHub по произвольному запросу.
- Карточка репозитория: описание, лицензия, дата создания/обновления,
  кнопка «Открыть на GitHub» (браузер), добавление/удаление из избранного.
- Избранное (закладки) хранится локально (SharedPreferences + JSON), работает офлайн.
- Файловый кэш ответов GitHub (TTL 24 ч) — частичная работа без сети.
- Сборка на GitHub Actions: `ci.yml` (debug APK на push/PR),
  `release.yml` (подписанный release APK по тегу `v*` или вручную).
- Подпись постоянным ключом (`scripts/generate-keystore.sh`, секреты GitHub),
  обновление поверх установленной версии сохраняется.
- Версионирование: `versionCode` + `versionName` в `app/build.gradle`.
