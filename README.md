# Демонстрация compile-time code generation в Kora 1.2.23

Небольшое приложение показывает, как Kora находит компоненты и их зависимости при компиляции, генерирует `ApplicationGraph`, а затем использует его при запуске.

## Что демонстрирует проект

В приложении есть два компонента:

- `ExampleRepository` возвращает строку `Привет`.
- `ExampleService` зависит от `ExampleRepository` и помечен `@Root`, поэтому включается в граф как корневой компонент.

Интерфейс `Application` помечен `@KoraApp`. Во время компиляции KSP-обработчик Kora анализирует приложение и генерирует `ApplicationGraph` и `$ApplicationImpl` в `build/generated/ksp/main/kotlin/`.

При запуске `Application.kt` получает `ApplicationGraphDraw`, передаёт его в `KoraApplication.run(...)`, находит узел `ExampleService` и получает экземпляр сервиса из графа. Сервис вызывает репозиторий; ожидаемый вывод программы:

```text
Привет
```

## Требования

- JDK 21 для Gradle и KSP.
- JDK 17 для компиляции проекта: он задан как Java toolchain в `build.gradle.kts`. Если он ещё не установлен, Gradle Toolchains Resolver может скачать его.
- Gradle Wrapper из репозитория; отдельно устанавливать Gradle не нужно.

Версия Kora задаётся в `gradle.properties` и равна `1.2.23`.

### Запуск в IntelliJ IDEA

В настройках Gradle выберите **Gradle JVM = JDK 21**. Java toolchain проекта при этом остаётся JDK 17.

### Запуск из терминала на macOS

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean run
```

`clean` удаляет старые результаты генерации, чтобы при демонстрации открыть актуальный `ApplicationGraph.kt`.

## Где смотреть генерацию

После сборки откройте:

```text
build/generated/ksp/main/kotlin/ApplicationGraph.kt
build/generated/ksp/main/kotlin/$ApplicationImpl.kt
```

В `ApplicationGraph.kt` найдите вызовы `ApplicationGraphDraw(...)` и `addNode0(...)`. Первый создаёт описание графа, последующие регистрируют фабрики компонентов и связи зависимостей. Для `ExampleService` сгенерированная фабрика получает `ExampleRepository` из его узла.

Сгенерированные файлы создаёт KSP; вручную их редактировать не нужно. После `clean` они появятся снова при компиляции.

## Файлы проекта

| Файл | Назначение |
| --- | --- |
| `src/main/kotlin/Application.kt` | Приложение `@KoraApp` и точка входа `main()` |
| `src/main/kotlin/ExampleService.kt` | Корневой компонент, зависящий от репозитория |
| `src/main/kotlin/ExampleRepository.kt` | Компонент-репозиторий |
| `build.gradle.kts` | KSP, зависимости Kora и Java toolchain |
| `gradle.properties` | Версия Kora и настройки Gradle |

## Последовательность работы

1. KSP находит интерфейс `Application` с `@KoraApp`.
2. Обработчик Kora анализирует компоненты и разрешает зависимость `ExampleService → ExampleRepository`.
3. Процессор генерирует `ApplicationGraph` с узлами и фабриками компонентов.
4. При запуске `KoraApplication.run(...)` вызывает переданную фабрику `ApplicationGraphDraw` и инициализирует runtime-граф.
5. `main()` получает `ExampleService` из графа и вызывает `greet()`.

Таким образом, описание и регистрация связей генерируются при компиляции, а экземпляры компонентов создаются при запуске.

Гультяев Илья
