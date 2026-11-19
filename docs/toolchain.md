# Toolchain provenance

Verified against upstream documentation on 2026-11-16; dependency resolution and compilation
provide the project's compatibility check. Versions are deliberate stable pins, updated
through reviewed pull requests.

| Component | Version | Official reference |
| --- | --- | --- |
| Android Gradle Plugin | 8.13.2 | [AGP compatibility: Gradle 8.13, JDK 17, API up to 36.1](https://developer.android.com/build/releases/agp-8-13-0-release-notes) |
| Gradle | 8.13 | [Release notes](https://docs.gradle.org/8.13/release-notes.html), [checksums](https://gradle.org/release-checksums/) |
| Kotlin / Compose compiler | 2.3.10 | [KGP compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html), [compiler setup](https://developer.android.com/develop/ui/compose/compiler) |
| Compose BOM | 2025.08.00 | [Stable August release](https://android-developers.googleblog.com/2025/08/whats-new-in-jetpack-compose-august-25-release.html) |
| Activity Compose | 1.10.1 | [Release notes](https://developer.android.com/jetpack/androidx/releases/activity#1.10.1) |
| JUnit | 4.13.2 | [JUnit releases](https://github.com/junit-team/junit4/releases/tag/r4.13.2) |
| AndroidX test JUnit / runner | 1.2.1 / 1.6.2 | [AndroidX Test releases](https://developer.android.com/jetpack/androidx/releases/test) |
| Lifecycle / Coroutines | 2.9.0 / 1.9.0 | [Lifecycle releases](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.0), [Coroutines release](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.9.0) |
| Spotless / ktlint | 8.1.0 / 1.7.1 | [Plugin release](https://plugins.gradle.org/plugin/com.diffplug.spotless/8.1.0), [ktlint release](https://github.com/ktlint/ktlint/releases/tag/1.7.1) |

Kotlin 2.3.10's documented Gradle range is 7.6.3–9.0.0 and AGP range 8.2.2–9.0.0.
The Compose compiler plugin uses the same Kotlin version. Compose library versions are
managed together by the BOM. SDK 36 and Build Tools 35.0.0 follow the AGP requirements.

Gradle binary ZIP SHA-256:
`20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78`.
Wrapper JAR SHA-256:
`81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f`.
The ZIP checksum is enforced by `gradle-wrapper.properties`; CI independently validates
the JAR with Gradle's wrapper-validation action. Wrapper scripts/JAR came from Gradle's
official `v8.13.0` repository tag.

Validation tooling was downloaded locally from Adoptium's JDK 17 API and Google's
official SDK repository. The JDK's published SHA-256 and SDK archive checksums were
verified before extraction. Tools and caches are ignored and are not part of the product.

GitHub Action pins were resolved using `git ls-remote` on their official repositories.
Update pins using the process in [CONTRIBUTING](../CONTRIBUTING.md).
