# Git-explorer-box

Приложение для Android, которое проверяет публичные репозитории GitHub и создаёт каталог по разделам для удобного поиска полезных программ и информации.

## Что делает приложение

- **Каталог по разделам** — Популярные, языки программирования (Kotlin, Java, Python, JavaScript, TypeScript, Go, Rust), темы (Машинное обучение, Инструменты разработки, Безопасность, Игры) и Избранное.
- **Списки репозиториев** — загрузка публичных репозиториев GitHub REST API (сортировка по звёздам) с описанием, языком, количеством звёзд и форков.
- **Поиск** — поиск репозиториев GitHub по произвольному запросу.
- **Карточка репозитория** — описание, лицензия, даты создания и обновления, кнопка «Открыть на GitHub» (открывает в браузере), добавление в избранное.
- **Избранное (закладки)** — хранятся локально на устройстве, доступны офлайн.
- **Кэш** — ответы GitHub кэшируются в приложении на 24 часа, partesially работает без сети.

## Стек технологий

- Kotlin, Android SDK (`minSdk 21`, `targetSdk 34`)
- Gradle, AndroidX (AppCompat, Material Components, RecyclerView, CardView, SwipeRefresh)
- Сеть: `java.net.HttpURLConnection` + `org.json` (встроено в Android, лишних зависимостей нет)
- GitHub REST API (`api.github.com`). Без токена лимит: 60 запросов в час; опциональный токен — строка `github_api_token` в `res/values/strings.xml`.

## Сборка

Требуется Android SDK (в этом сеансе沙бокса Java/Android SDK не установлен — сборка проверяется в GitHub Actions).

Локально:
```bash
./gradlew assembleDebug      # отладочная сборка
./gradlew assembleRelease    # релизная, нужна подпись (см. ниже)
```

## GitHub Actions

- `.github/workflows/ci.yml` — debug APK при каждом push/PR.
- `.github/workflows/release.yml` — подписанный release APK при теге `v*` или вручную (`workflow_dispatch`). APK попадает в артефакты workflow.

## Подпись и обновление поверх

Приложение подписывается **постоянным ключом**, чтобы обновление устанавливалось поверх уже установленной версии (Android требует совпадения `applicationId` и сертификата).

```bash
# 1. Сгенерировать ключ один раз (в модуле app)
cd app
RELEASE_STORE_PASSWORD=<пароль-хранилища> RELEASE_KEY_PASSWORD=<пароль-ключа> ../scripts/generate-keystore.sh

# 2. app/release-keystore.jks не коммитится (.gitignore).
# 3. Закодируйте ключ в base64 и добавьте в GitHub Secrets репозитория:
#    RELEASE_STORE_FILE, RELEASE_STORE_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_KEY_PASSWORD

base64 -w0 app/release-keystore.jks   # Linux
```

Никогда не меняйте `applicationId` (`com.gitexplorerbox.app`) и не заменяйте ключ подписи — иначе установка поверх старой версии будет невозможна.

## Версии и изменения

- `versionCode` / `versionName` задаются в `app/build.gradle` (`defaultConfig`).
- При каждом релизе увеличивайте `versionCode` на 1, обновляйте `versionName` (семантическое версионирование, например `1.0.0` → `1.0.1`).
- Все изменения фиксируются в `CHANGELOG.md`.
- Тег `vX.Y.Z` запускает релиз-сборку в GitHub Actions.

## Карта репозитория (чтобы любой агент продолжил работу)

```
.github/workflows/ci.yml        — CI сборка debug APK
.github/workflows/release.yml   — подписание и сборка release APK по тегу v*
app/build.gradle                — версия, подпись, зависимости
app/src/main/AndroidManifest.xml — манифест, пакет com.gitexplorerbox.app
app/src/main/res/               — макеты, строки (ru), темы, иконки
app/src/main/java/com/gitexplorerbox/app/
    Repo.kt                     — модель репозитория + JSON
    Section.kt                  — разделы каталога (Section.seeds)
    Bookmarks.kt                — локальные закладки (SharedPreferences)
    Cache.kt                    — файловый кэш GitHub API (24 ч)
    GitHubApi.kt                — клиент GitHub REST API
    SectionAdapter.kt           — адаптер разделов
    RepoAdapter.kt              — адаптер репозиториев
    MainActivity.kt             — главный экран каталога
    RepoListActivity.kt         — список репозиториев раздела / избранного
    RepoDetailActivity.kt       — карточка репозитория
    SearchActivity.kt           — поиск
scripts/generate-keystore.sh    — генерация постоянного ключа подписи
CHANGELOG.md                    — история изменений
AGENTS.md                       — инструкция для следующего агента
README.md                       — этот файл
```

## Как продолжить работу (для любого агента)

1. Работайте **только в ветке `arena/be9d8a70-git-explorer-box`** (не переключайтесь, не создавайте новые ветки, пуш只 в неё).
2. См. `AGENTS.md` — там команды сборки, порядок версий, секреты подписи и куда править код.
3. Перед любым релизом: запись в `CHANGELOG.md` → `versionCode` +1 → тег `vX.Y.Z` → `git push origin arena/be9d8a70-git-explorer-box --tags`.

## Лицензия

Проект открыт для продолжения любым агентом — документируйте все изменения в `CHANGELOG.md` и `AGENTS.md`, чтобы следующий исполнитель мог собрать и выпустить приложение без уточнений.
