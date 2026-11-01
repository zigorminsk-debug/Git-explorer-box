# Инструкция для агента — продолжение работы

Репозиторий: **Git-explorer-box**. Ветка (фиксированная, не переключайте и не создавайте другие):
`arena/be9d8a70-git-explorer-box`. Пушите только в эту ветку:
`git push origin arena/be9d8a70-git-explorer-box`

## Что сделано (v1.0.0)
Android-приложение, которое каталогизирует публичные репозитории GitHub по разделам.
- Источники: `app/src/main/java/com/gitexplorerbox/app/*.kt`
- Ресурсы: `app/src/main/res/` (макеты, строки на русском, темы, иконки)
- Манифест: `app/src/main/AndroidManifest.xml` (пакет `com.gitexplorerbox.app`, `INTERNET`)
- Сборка: Gradle (Kotlin + AndroidX), `app/build.gradle` — `minSdk 21`, `targetSdk 34`
- CI: `.github/workflows/ci.yml` (debug APK), `.github/workflows/release.yml` (подписанный release APK по тегу `v*`)
- Подпись: `scripts/generate-keystore.sh` (один ключ на всё время жизни приложения — обновление поверх)
- Документация: `README.md`, `CHANGELOG.md`, этот файл

## Как собирать (требуется Android SDK — в沙боксе нет Java)
```
./gradlew assembleDebug          # debug APK
./gradlew assembleRelease        # release, нужен RELEASE_* (см. ниже)
```
Отладочная и релизная сборки проверяются в GitHub Actions.

## Подпись (обновление поверх установленной версии)
1. `cd app && RELEASE_STORE_PASSWORD=.. RELEASE_KEY_PASSWORD=.. ../scripts/generate-keystore.sh`
   → `app/release-keystore.jks` (не коммитить, в `.gitignore`).
2. GitHub Secrets: `RELEASE_STORE_FILE` (base64 ключа), `RELEASE_STORE_PASSWORD`,
   `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.
3. Тег `vX.Y.Z` → `release.yml` собирает `app-release.apk` (артефакт workflow).

## Версии и изменения (обязательно при каждой правке)
1. Внесите запись в `CHANGELOG.md` (формат `## [X.Y.Z] — дата`).
2. Увеличьте `versionCode` на 1 и обновите `versionName` в `app/build.gradle`.
3. Закоммитьте, создайте тег `vX.Y.Z`, пушните — релиз-сборка запустится автоматически.
   Никогда не меняйте `applicationId` и не меняйте ключ подписи — иначе обновление поверх не установится.

## Куда править ключевое
- Разделы каталога: `Section.seeds` в `app/src/main/java/com/gitexplorerbox/app/Section.kt` (добавляйте Section(title, query)).
- GitHub API: `GitHubApi.kt` (без токена лимит 60 запросов/час; опциональная строка `github_api_token` в `res/values/strings.xml`).
- Строки интерфейса (русский): `app/src/main/res/values/strings.xml`.
- Макеты: `app/src/main/res/layout/`.
- Адаптеры/экраны: `SectionAdapter`, `RepoAdapter`, `MainActivity`, `RepoListActivity`, `RepoDetailActivity`, `SearchActivity`.
- Закладки: `Bookmarks.kt`; кэш сети: `Cache.kt`; модель: `Repo.kt`.

## Следующие шаги (по желанию)
- Unit-тесты (JUnit) для `Repo`, `Bookmarks`, `Cache`.
- Пагинация списков (page > 1), загрузка аватарок владельцев (Coil/Glide).
- Темная/светлая тема уже есть (DayNight); можно добавить Настройки.
- Публикация подписанного APK в GitHub Releases (`actions/upload-release-asset`).

## Окружение этого сеанса
В沙боксе нет Java/Android SDK — локально `./gradlew` не запустить. Проверка сборки — через GitHub Actions (actions/setup-java + android-actions/setup-android). Если Actions не проходит — читайте логи workflow, правьте `*.yml` и Gradle-файлы, коммитьте в ветку `arena/be9d8a70-git-explorer-box`.
