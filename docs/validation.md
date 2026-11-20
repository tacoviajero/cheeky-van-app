# Evidencia de validación — fundación y desarrollo local

## Estado vigente y cierre documental — 2026-11-19

Revisión en la carpeta original, rama `codex/preserve-local-development`, HEAD
`92dc33e`, versión 0.1.0. La fundación fue aprobada el 2026-11-16 e integrada en
la PR #1, merge `07af106`. El incremento posterior está preservado en `5289953`;
`92dc33e` incorpora la integración de instrucciones globales. El propietario revisó
y aprobó el incremento actual y autorizó su publicación. Los controles alojados
quedan a cargo de control de versiones; la fusión requiere aprobación posterior.

**Separación de evidencia:** las secciones históricas inferiores registran cada
entrega y sus pendientes en aquel momento. La pantalla inicial del hito 0, sus
pruebas en vertical/texto al 200 % y su CI no certifican el tablero posterior.
Los permisos originales sin acceso sensible corresponden a esa base; el manifiesto
actual incluye las extensiones autorizadas de Bluetooth y ubicación en primer plano.
Los pendientes de instalación resueltos más adelante se conservan como cronología.
Este resumen y la última sección de ubicación describen el estado actual.

### Comprobaciones de este cierre y reutilización

Cambio documental de nivel 1, limitado a `PROJECT.md` y este registro. Se inspeccionó
el código de presentación, simulación/conexión, protocolo, manifiesto y ubicación,
y se cotejaron los artefactos locales existentes:

- Los **40 archivos versionados de código, pruebas, scripts y configuración de
  compilación** coinciden con la entrega `2026-11-17-5f63216-cluster-location-debug`.
  Para cambios locales se comparó SHA-256 con `source-state.json`; los demás se
  compararon con la revisión base `5f632161487fb9c8f8bf1ae540bbbc6d8d1f60cf`,
  normalizando únicamente CRLF/LF. La pantalla histórica eliminada sigue ausente.
- `git diff 5289953 HEAD` sobre esos componentes está vacío. La integración
  posterior conservó el desarrollo comprobado. Los documentos e instrucciones
  tienen su propia evolución y no se usan como igualdad de código ejecutable.
- Ambos APK existentes coinciden con los hashes de `delivery.json` de la última
  entrega, reproducidos en la sección final. Su build aprobado en 51 s y las
  comprobaciones locales de formato/lint/manifiesto se reutilizan para este código.
- Se leyeron los XML actuales: `DevicePositionTest` 3/3 y `ManifestSecurityTest`
  3/3, sin fallos, errores ni omisiones. Se verificaron los logs privados de
  instrumentación: `cluster-test.log` (`OK (3 tests)`, 10.325 s) y
  `cluster-location-test.log` (`OK (1 test)`, 3.256 s), entre otros incrementos.
- La entrega `cluster-debug/delivery.json` registra inspección de los XML de
  `ClusterPresentationTest` 3/3 y `SimulationTest` 3/3. La entrega
  `bluetooth-debug/delivery.json` registra 10/11 iniciales y el rerun focalizado
  corregido 1/1; el XML archivado original de `ObdConnectionTest` conserva aquel
  fallo de limpieza y **no representa una nueva regresión**. El resultado corregido
  está respaldado por el registro de entrega; su XML individual ya no está en el
  directorio actual, reemplazado por ejecuciones posteriores.

No se ejecutaron nuevas pruebas, compilaciones ni sesiones de dispositivo en este
cierre: la equivalencia comprobada permite reutilizar los resultados anteriores.
La revisión final comprueba enlaces locales, consistencia documental y whitespace.
Los resultados de dispositivo siguientes son los observados en las entregas de
noviembre 16–17, no una nueva conexión al teléfono o al vehículo el día 19.

### Cobertura actual y límites

| Componente | Implementado / listo para revisar | Evidencia reutilizada | Pendiente |
| --- | --- | --- | --- |
| Tablero horizontal | Inicio, Tablero, cinco tabs inferiores, detalles, encabezado estable, pantalla completa y curvas laterales ajustadas al velocímetro | Inspección de código y manifiesto landscape; pruebas UI y capturas Xiaomi Android 16, incluida última entrega de ubicación | Resolución/áreas seguras del estéreo definitivo; accesibilidad del nuevo cluster con texto grande y otras dimensiones |
| Simulación | Escenarios, datos ausentes, origen visible, instrumentos, marchas y sugerencias condicionales; incremento aceptado por el propietario | Pruebas JVM de simulación/presentación y tres pruebas UI del cluster; última prueba de navegación aprobada | Marchas reales y cálculo de recomendaciones |
| Bluetooth/OBD | Selección de emparejados, permiso, RFCOMM/SPP, descubrimiento y cuatro PIDs (RPM, velocidad, refrigerante, carga), cierre de sesión | Inspección de código; pruebas JVM con transporte simulado; conexión/desconexión del adaptador PRUEBA en Xiaomi | Permisos/selección/socket reales, firmware, ECU, compatibilidad, lecturas y rendimiento en T5; pérdida, revocación y reconexión con hardware |
| Ubicación | LocationManager GPS/red en primer plano, permisos COARSE/FINE, posición independiente de OBD/demo, caducidad 60 s y altitud opcional | Tres pruebas JVM de validez/caducidad; código de inicio/cierre inspeccionado; permiso preciso y coordenadas/altitud reales observados en Xiaomi con motor simulado | Revocación/denegación, cambios de proveedor, pérdida de señal y ciclo de vida comprobados manualmente; recepción en estéreo |
| Privacidad | Permisos acotados, backup deshabilitado y datos de ubicación en memoria | Tres pruebas actuales y manifiesto empaquetado de entrega aprobado; inspección actual de permisos/código | Comportamiento de permisos reales durante la validación de hardware |

La hora procede del Android. Temperatura exterior, combustible/autonomía, contadores,
horas de motor, aceite y mediciones ampliadas siguen simulados o ausentes en datos
de vehículo; su integración real sigue pendiente. La disponibilidad de aceite en la
T5 está confirmada por el propietario. El selector del próximo evento solar está
probado con horario demo; los horarios por fecha/ubicación siguen pendientes.
Mapa y Diagnóstico muestran estados vacíos; el catálogo de 201 parámetros observado
en Car Scanner queda pendiente de obtener e integrar. Las bandas térmicas no
constituyen umbrales de alarma ya validados en el vehículo.

**Cierre:** incremento implementado revisado y aprobado por el propietario, con
publicación autorizada. Quedan pendientes el hardware indicado, CI de la publicación,
la aprobación posterior de la fusión y la validación de distribución firmada/identificador de paquete.
La cobertura de dominio/protocolo del 80 % prevista en `docs/testing.md` todavía
carece de medición registrada; estas pruebas específicas no acreditan ese umbral
ni cierran todos los hitos funcionales posteriores.

## Archivo histórico: hito 0 — 2026-11-16

Scope: milestone 0, development version 0.1.0. Los estados de rama, CI y aceptación
de las siguientes secciones corresponden a sus fechas; el estado vigente está arriba.

## Automated results

The final full Gradle gate succeeded in 1m 31s (90 tasks: 17 executed, 73 up-to-date).

| Check | Result |
| --- | --- |
| Official Gradle distribution and wrapper SHA-256 | Passed |
| Kotlin and Gradle formatting | Passed |
| Strict Android lint | Passed |
| JVM privacy tests | 3 tests passed; 0 failures, errors or skipped tests |
| Debug application and instrumentation APK builds | Passed |
| Packaged-manifest privacy check | Passed |
| Invalid manifest mutations | INTERNET permission, enabled backup and weak custom permission rejected |
| Workflow static validation | Passed with checksum-verified actionlint 1.7.12 |
| PowerShell helper syntax | Passed |
| Connected Android device | Redmi Note 14 Pro+ 5G authorized in ADB; Android 16 / API 36 |
| Device launch instrumentation | Renamed APK passed: `OK (1 test)` in 1.578s; earlier APK passed in 2.425s |

Final Gradle command, with project-local toolchain configured:

```powershell
. .\scripts\Use-LocalToolchain.ps1
.\gradlew.bat spotlessApply spotlessCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest --no-daemon --console=plain
.\scripts\Test-PackagedManifest.ps1
```

Report locations: `app/build/reports/tests/testDebugUnitTest/index.html`,
`app/build/reports/lint-results-debug.html`, and `app/build/test-results/testDebugUnitTest/`.
APK locations: `app/build/outputs/apk/debug/app-debug.apk` and
`app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`.

## Repository and hosted CI

Local identity: Juan Nunez, using the email provided by the owner. The working branch
at that time was `feature/milestone-0-foundation`; foundation source commit: `8d89c5c`.
`origin` is `https://github.com/tacoviajero/cheeky-van-app.git`.

After explicit owner approval, the base `.gitignore` commit
`66392f4ef881a7176260c0e2319bffe1e02b49e2` was published and verified on remote `main`.
Terminal Git lacked push credentials; publication used the connected GitHub integration.
On 2026-11-16 the version-control chat reconciled the unpublished branch onto remote
`main`, preserving the source tree and a local backup reference. Original foundation
`8d89c5c` became `78468d3`; the name/scope correction is `a8f0447` and project/device
documentation is `f20a6ac`. Terminal authentication was subsequently configured and
the feature branch was published through Git.

PR: https://github.com/tacoviajero/cheeky-van-app/pull/1 (base `main`).
The initial PR workflow run `43957968079` failed during SDK setup because the action's
default package list includes the retired `tools` package. CI now explicitly selects
`platform-tools`; Android platform 36 and build tools 35.0.0 remain pinned below it.
Dependency review also failed because Dependency graph was disabled. The owner enabled
it and the subsequent PR run passed dependency review.

Hosted PR run: https://github.com/tacoviajero/cheeky-van-app/actions/runs/43958248302
for `a1183c3`. SDK setup, wrapper validation, dependency resolution and formatting passed.
`Validate Android` failed at strict lint with `OldTargetApi` in `app/build.gradle.kts:14`:
the hosted lint check considers target API 36 outdated. Packaged-manifest privacy and
APK upload steps were skipped after the lint failure. This hosted run does not establish
successful JVM tests or both APK builds. The prior local and device results above remain
separate evidence.

The development chat subsequently supplied the targeted lint-policy correction described
below. Hosted run `43962044697` verified the correction successfully on `17754f7`.
The earlier failing runs remain historical evidence.

Repository API inspection confirmed `main` is unprotected and the rulesets list is
empty. Owner configuration and approval remain required before merge.

## Android device session — results

On 2026-11-16, ADB started successfully outside the restricted sandbox and
`adb devices -l` returned no attached devices. After the owner connected the phone,
Windows detected `Redmi Note 14 Pro+ 5G` as a portable USB device without a reported
device error; ADB still returned no attached devices. USB debugging/authorization
was subsequently enabled/authorized. On retry, ADB recognized the phone and reported
Android 16 / API 36. The existing application APK initially received
`INSTALL_FAILED_USER_RESTRICTED: Install canceled by user` during streamed installation.
After the owner enabled USB installation and accepted the prompt, installation using
`adb install --no-streaming -r` succeeded for both existing debug APKs.

`FoundationLaunchTest` then passed on the physical phone: `OK (1 test)`, duration
2.425s. It verified the displayed title and connection status and the presence of
the safety notice. A subsequent explicit cold launch returned `Status: ok` and
`TotalTime: 1000` ms; the app was left open for owner visual review. That single
observed launch time is not a performance guarantee. Visual checks remain pending.

APK-chat handoff: prepare application and instrumentation debug APKs from foundation
source `8d89c5c`, recording any later source changes and the exact revision used.
Application ID: `com.machinedashboard`, version `0.1.0`, minimum API 23.
Instrumentation ID: `com.machinedashboard.test`; runner:
`androidx.test.runner.AndroidJUnitRunner`. Install both APKs for automated device testing.
APK generation/export is owned by that chat; this chat prepares and evaluates the tests.

After installation and USB authorization, the development test can run without invoking
Gradle packaging tasks:

```powershell
adb shell am instrument -w -r -e class com.machinedashboard.FoundationLaunchTest com.machinedashboard.test/androidx.test.runner.AndroidJUnitRunner
```

Observed automated result: one successful launch test. Device acceptance also requires
the remaining visual checks below.

| Device check | Expected result | Actual result |
| --- | --- | --- |
| Launch | Title, unavailable-connection status and safety notice accessible | Renamed APK passed the launch test; title verified in device captures |
| Portrait and landscape | Readable content; scrolling exposes all text | Passed at 1220×2712 and 2712×1220; all current content fits |
| Font scale 200% | Text and safety notice remain accessible | Passed in both orientations; all current content fits without scrolling |
| System bars and cutout | Content stays inside safe areas | Passed visual inspection of four device captures |
| Background and return | Screen returns without a crash | Passed: launcher opened, app resumed with `Status: ok` |
| Permissions | Foundation opens without sensitive permission prompts | Passed in the inspected launch; packaged manifest privacy check also passed |
| Instrumentation | `FoundationLaunchTest` passes on the recorded device/API | Renamed APK passed: 1 test, 1.578s, Android 16 / API 36 |

## Acceptance gates recorded at milestone 0 (historical)

- Owner review of the foundation; scrolling beyond the current content and additional device sizes remain future regression cases.
- Hosted CI passed on correction `17754f7`; verify the latest documentation revision in PR checks before merge.
- Review the changes and configure required checks/branch protection before merging.
- Confirm package identifier ownership before a signed distribution.

Vehicle/ELM327 hardware tests, refresh-rate measurements and release validation remain
pending. A native symbol-stripping notice for the upstream Compose graphics
library was informational; the library is packaged intact in the development APK.

At this point in the historical record, milestone 0 remained under review pending
these gates. Owner approval is recorded below; integration in `07af106` is confirmed
in the current summary. Hardware and release checks remain separate from that closure.

## Product name and scope correction — 2026-11-16

The owner confirmed the product name as **Cheeky Van App**, a personal Android app
optimized for our VW Transporter T5, 2.0 TDI CAAC, manual. The app label, screen title,
Gradle project name and theme now use Cheeky Van App. Project and architecture notes
prioritize this vehicle; multivehicle support is outside the approved scope.

The initial device result applied to the previously installed APK. The renamed APK
has since been exported, installed and checked as recorded below.
The existing internal Android package ID remains `com.machinedashboard` for continuity
with the installed development app. This internal identifier is separate from its label.
Source validation passed offline: `spotlessCheck`, `processDebugResources`,
`compileDebugKotlin`, `testDebugUnitTest` (3 passed), and `lintDebug`.
Gradle completed successfully in 1m 17s. The packaged-manifest privacy check also passed.

## Renamed APK export — 2026-11-16

The APK chat exported debug application and instrumentation APKs from
`8d89c5cfdfbf98194ead8361176cc1ca32d9de80` plus the local name and documentation
changes listed in the delivery manifest. The offline Gradle gate passed in 46s
(87 actionable tasks: 8 executed, 79 up-to-date): `spotlessCheck`, `lintDebug`,
`testDebugUnitTest`, `assembleDebug`, and `assembleDebugAndroidTest`.
JVM tests were up-to-date; their report records 3 passed, zero failures/errors/skips.
The packaged-manifest privacy check passed. AAPT verified the packaged application
label as `Cheeky Van App`, package `com.machinedashboard`, version `0.1.0` (1),
minimum API 23 and target API 36. Both exported APK signatures verified (v1/v2).
Instrumentation targets `com.machinedashboard` using
`com.machinedashboard.test/androidx.test.runner.AndroidJUnitRunner`.

Delivery directory: `app/build/exports/2026-11-16-8d89c5c-renamed-debug/`.
`delivery.json` records source hashes and checks; `SHA256SUMS.txt` records APK hashes.

- `CheekyVanApp-0.1.0-debug.apk`: SHA-256 `40056100828A0A6FE6C0663B807C08326DDD43F01A3A0E16EA2C1C2D7FD8EDA5`.
- `CheekyVanApp-0.1.0-debug-androidTest.apk`: SHA-256 `A728F06A8674F4BF55BCE0AB529B8DC8B46B35217E63C2635DAA709C157BDC73`.

## Renamed APK device verification — 2026-11-16

The development chat verified both exported APK SHA-256 hashes against the delivery
record and installed them using `adb install --no-streaming -r` on the Redmi Note
14 Pro+ 5G, Android 16 / API 36.

Two initial instrumentation attempts failed with `No compose hierarchies found`.
Device diagnostics then showed `mWakefulness=Dozing` and keyguard showing. After the
owner unlocked the phone, the same installed APKs passed `FoundationLaunchTest`:
`OK (1 test)` in 1.578s. This records the failed attempts and the successful retry;
the screen-lock condition is an instrumentation prerequisite for future sessions.

Visual inspection of private app captures confirmed the title **Cheeky Van App**,
connection status, description and full safety notice at font scales 1.0 and 2.0,
in portrait (1220×2712) and landscape (2712×1220). Content remained clear of system
bars. All current text fit the viewport in these four cases; an actual scroll gesture
was unnecessary and was not validated. Narrower windows remain a future regression case.

An initial large-text capture was interrupted by unsettled orientation and later
screen locking. The completed large-text pass waited for the requested display
orientation before capture and kept the phone awake over USB. Opening the launcher
and returning to the app succeeded. The completed pass restored its original font
scale 1.0, portrait lock (`lock 0`), and stay-awake setting 0.

Private screenshots, UI hierarchy dumps and local check results are under ignored
`.tools/device-validation/renamed/`; they are excluded from Git and CI artifacts.
ADB input-event injection was denied by the phone during diagnosis; no input-injection
permission was enabled. The completed checks used activity launches and temporary
display settings instead.

The initial handoff to the version-control chat was rejected by automatic approval
review. The owner subsequently explicitly authorized the handoff, commits, feature
branch publication, PR creation and CI verification on 2026-11-16. The version-control
chat accepted that work; publication and hosted results are recorded in the repository
section as they become available. Merge remains subject to explicit owner approval.

## OldTargetApi CI correction — 2026-11-16

The development chat inspected hosted run
https://github.com/tacoviajero/cheeky-van-app/actions/runs/43958929461 and the
`Validate Android` job log (`133620815048`). Dependency review passed; Android lint
failed with exactly one issue, `OldTargetApi`, at target SDK 36.

The correction retains compile/target API 36 and sets only `OldTargetApi` to
`informational` in the AGP lint DSL. The advisory remains visible as `Hint` in lint
reports when triggered. `warningsAsErrors` and `abortOnError` remain enabled.
Rationale and migration conditions are recorded in
[ADR 0002](decisions/0002-target-sdk-lint-policy.md).

Local gate passed offline in 1m 37s (50 tasks: 12 executed, 38 up-to-date):
`spotlessCheck`, `lintDebug`, `compileDebugKotlin`, `compileDebugAndroidTestKotlin`,
and `testDebugUnitTest`. JVM tests were up-to-date; their report records 3 passed,
zero failures/errors/skips. The packaged-manifest privacy check passed separately.

An isolated copy under ignored `.tools/lint-policy-probe/` used temporary target API
35 to trigger the same advisory with the locally installed SDK 36. Before the policy
change, lint failed with one `OldTargetApi` error. With the correction, lint passed
in 1m 14s and the XML/text reports retained one `OldTargetApi` hint (zero errors or
warnings). The real project's compile/target SDK stayed at 36 throughout.

A negative check removed the application icon only in that isolated copy. Lint
failed as expected in 1m 1s: `MissingApplicationIcon` and `UnusedResources` were
errors, while `OldTargetApi` remained a hint. The probe manifest was then restored.
The original/error, corrected/hint and unrelated-warning XML evidence is under
ignored `.tools/lint-policy-*.xml`; these fixtures are excluded from Git and CI.
The version-control chat published the correction as
`17754f7fda320eed846835cc0dc2170c4142ffdb` on the existing PR branch.
Hosted PR run https://github.com/tacoviajero/cheeky-van-app/actions/runs/43962044697
completed successfully: both `Validate Android` and `Review dependency vulnerabilities`
passed. Wrapper validation, formatting, strict lint with the documented advisory policy,
JVM tests, both debug APK builds and packaged-manifest privacy all passed. Validation
reports and debug APK artifacts were uploaded successfully by CI. This run does not
execute connected-device instrumentation; the recorded physical-device checks remain
separate evidence. The subsequent evidence-only commit triggers fresh PR checks.

The owner approved the foundation on 2026-11-16 and authorized moving to definition
of the simulated dashboard. Repository integration remains owned by the version-control
chat. The proposed first simulation delivery is in [milestone 1](milestone-1.md);
its controls and behavior await agreement before implementation.

## Desarrollo posterior al hito 0 — cronología de entregas

Las siguientes entradas conservan los resultados de cada incremento. Las menciones
de implementación futura o pruebas pendientes describen aquel momento; para el
estado consolidado consultar el resumen vigente al inicio de este documento.

## First dashboard implementation — 2026-11-16

Owner scope: display engine RPM, vehicle speed, calculated load, coolant/oil
temperatures and accessible turbo/intake measurements. The owner also clarified that
the Yaris R 2016 reference displays both current and recommended gear; that indicator
remains a future feature. The owner reports Car Scanner read 201 parameters from this
T5 through the Bluetooth adapter. Its actual catalog is the priority for live sensor
selection and has not yet been supplied here.

Implemented the initial simulated dashboard with explicit source indicator, three
scenario controls and unavailable placeholders for advanced turbo readings. Domain
models, simulated repository and ViewModel are separated. Lifecycle-aware collection
stops the simulation when the screen stops consuming samples. Details and standard
PID candidates are in [telemetry](telemetry.md).

Focused validation passed: `SimulationTest` executed 3 tests with zero failures,
errors or skips. The same Gradle invocation compiled production code and the updated
Android UI tests successfully in 1m 32s. The packaged-manifest privacy check passed.
One overlong formatting line was corrected before this successful validation.
Final `spotlessCheck` and `lintDebug` passed offline in 1m 23s. Documentation diff
checks also passed. No extra foundation test suite or repeated successful simulation
test run was required for this change.

This dashboard has not yet been installed, visually inspected or instrumented on the
phone. The prior foundation screenshots/results do not validate the new dashboard.
APK generation/export remains owned by the dedicated APK chat. No live vehicle data
or turbo-specific reading has been validated by this implementation.

## Simulated dashboard APK export — 2026-11-16

Exported application and instrumentation debug APKs from
`5f632161487fb9c8f8bf1ae540bbbc6d8d1f60cf` plus local dashboard changes.
Offline `assembleDebug` and `assembleDebugAndroidTest` passed in 2m 25s
(66 actionable tasks: 37 executed, 29 up-to-date). Production and Android test
Kotlin compilation were up-to-date. Existing simulation, formatting, lint and
privacy results above were reused. AAPT confirmed `Cheeky Van App`, version
`0.1.0` (1), package `com.machinedashboard`, minimum API 23 and target API 36.
Instrumentation is `com.machinedashboard.test/androidx.test.runner.AndroidJUnitRunner`.
Both exported signatures verified successfully.

Delivery directory: `app/build/exports/2026-11-16-5f63216-dashboard-debug/`.
Source hashes and build evidence are recorded in `delivery.json`.

- `CheekyVanApp-0.1.0-dashboard-debug.apk`: SHA-256 `9AD053006C121851E16A15E1EFE6A690F9D22C174CBEA104517D8B7C72CC260D`.
- `CheekyVanApp-0.1.0-dashboard-debug-androidTest.apk`: SHA-256 `7BABDB955808C808255128B220F4D480A85AC017F7060E2A559E2C73EE3C0BDA`.

Development owns installation, launch, visual acceptance and execution of the two
dashboard UI tests on the phone; those checks remain pending for this delivery.

## Dashboard installation — 2026-11-17

The application APK from the delivery above was checked against its recorded SHA-256
and installed on the Xiaomi with `adb install --no-streaming -r` (`Success`). Android
accepted the dashboard activity launch (`Status: ok`). Initially the keyguard remained
visible. After the owner unlocked the phone, ADB confirmed MainActivity at the front
and a device capture showed the actual dashboard with SIMULACIÓN, En marcha selected,
engine/oil readings and the intake/boost section. The private capture is at ignored
`.tools/device-validation/dashboard-current.png`. This confirms the installed portrait
screen; the updated instrumentation tests and broader layout checks remain pending.
No rebuild or device-setting change was needed for this installation.

## Bluetooth and test adapter — 2026-11-17

Implemented paired-device selection, Android connection permission, RFCOMM/SPP,
bounded ELM framing, supported-PID discovery and read-only RPM/speed/coolant/load
polling. The same coordinator/parser supports an explicit PRUEBA adapter with
disconnect and simulated link loss. Samples clear on source changes and session end.
Details and pending hardware checks: [Bluetooth](bluetooth.md).

The initial JVM run passed 10 of 11 tests: parser/allowlist, initialization/polling,
loss/reconnect, timeout/unsupported PIDs, three simulation and three privacy checks.
The remaining source-switch test met its assertions but failed during test-dispatcher
cleanup; cleanup was corrected and its focused rerun passed (1 test, zero failures).
Final `spotlessCheck` and `lintDebug` passed in the same run (1m 18s). Original XML evidence
is retained locally in ignored `.tools/device-validation/bluetooth/`.
Production and Android-test Kotlin compilation passed. Packaged-manifest privacy
checks passed with only paired-Bluetooth permissions and backup disabled.
Hardware connection and T5 readings remain unverified because the vehicle is away.

The APK chat exported application and instrumentation APKs successfully in 37s to
`app/build/exports/2026-11-17-5f63216-bluetooth-debug/`, from base `5f63216` plus
the current local changes. Both signatures were verified during export; development
independently verified the recorded SHA-256 hashes in `delivery.json`.
Application SHA-256: `8A2747C80E37AD553362DBC6A60592E650253449DDB6A850B112DB915F000B19`.
Instrumentation SHA-256: `F97947CB190E0449F96B1360DDB20507427E5E35DA63BC479C7DABC6427D4291`.
Installation, visual inspection and `testAdapterConnectsAndDisconnectClearsReadings`
remain pending: ADB reported no USB device at the final installation check. No phone
settings were changed during this task.

### Installation completed on Xiaomi — 2026-11-17

After the owner reconnected the unlocked Xiaomi (Android 16/API 36), both exported
APK hashes were verified and both installations returned Success using
`adb install --no-streaming -r`. The focused device test
`testAdapterConnectsAndDisconnectClearsReadings` passed: `OK (1 test)`, 3.477 seconds.
It verified PRUEBA, simulated RPM 1500, connection and clearing RPM on disconnect.
The activity then launched successfully. A private capture at ignored
`.tools/device-validation/bluetooth-installed.png` confirmed the installed dashboard
with Bluetooth and Probar adaptador controls visible. Temporary USB stay-awake was
restored to its original value 0 and verified. Real Bluetooth/T5 validation remains pending.

## Approved horizontal cluster — 2026-11-17

Moderate-risk UI/source-display change: implement approved Inicio and Tablero,
five lower tabs, horizontal orientation and move connection/scenario controls to
Ajustes. Preserve source isolation, missing values and the Bluetooth session.
Three `ClusterPresentationTest` and three `SimulationTest` JVM cases passed with
zero failures/errors: shared gauge scaling, conditional gear prompts and side,
reverse, supplemental demo values, scenario replacement and lifecycle collection.
Reports: `app/build/test-results/testDebugUnitTest/`.

The real OBD command set remains unchanged. Additional instrument fields, gear,
fuel/trips/GPS, solar time and outside temperature are demo-only until integrated;
vehicle and ELM test samples display missing fields as unavailable. Known oil-sensor
availability in the owner's T5 remains recorded separately from app integration.
Thermal bands are the approved visual proposal; numeric vehicle alarm thresholds
remain pending. There are no new automatic driving recommendations for real data.

Production and instrumentation compiled. Final `spotlessApply`, `lintDebug` and
`compileDebugAndroidTestKotlin` passed in 2m 11s. Packaged-manifest privacy passed.
The focused device checks are pending. UI tests cover horizontal launch/tab navigation, missing
sensor/detail navigation and ELM test connection/disconnection clearing values.
Only these three device tests and one visual capture per approved screen are planned.

First cluster APK export passed in 42s. Both application and test APK hashes were
verified against delivery.json, installed successfully on the Xiaomi Android 16,
and all three device tests passed: `OK (3 tests)`, 10.325s. Private log:
`.tools/device-validation/cluster-test.log`. Both actual screenshots were inspected.
That inspection identified inherited black header text on the dark surface; an
explicit light Surface content color fixes it. Tab touch ripple was also removed,
preserving the approved green selection line. No telemetry/session logic changed.
Final `spotlessApply`, `spotlessCheck` and `lintDebug` passed after that fix in 1m 8s.
Only the launch/navigation capture test will be repeated for this visual correction.

Final delivery: `app/build/exports/2026-11-17-5f63216-cluster-final-debug/`, generated
by the APK chat in 37s. Application SHA-256:
`8C2E5947F84E1B00D5F0FA7FB6BE7572CCC3C7BF544FC1E5EB5F5DC33971CCD5`.
Instrumentation SHA-256 is unchanged:
`22B42299CEAFF5D617B51A5F0974D9BDF1B85B54B6EC44C6D3ED9EBF042CDB14`;
the already-installed identical test APK was reused. Both final hashes were verified.
Application update returned Success. The focused final launch/navigation test passed:
`OK (1 test)`, 3.049s. Log: `.tools/device-validation/cluster-final-test.log`.
Actual Inicio and Tablero captures at `.tools/device-validation/cluster-final-inicio.png`
and `cluster-final-tablero.png` were visually inspected: header is light and legible,
lower tabs have only the selection line, instruments and cards render horizontally.
The body preserves the cluster aspect ratio and centers it on the wider Xiaomi;
head-unit resolution remains unknown and has not been tested. The app was left open
on Inicio. USB stay-awake was restored to original value 0 and verified.
This completes this UI increment. Vehicle data integration and remaining feature
tabs retain the limitations above; the actual T5 was not present.

## Cluster refinements and next solar event — 2026-11-17

Owner-requested visual refinements: 14dp tab icons, raised odometer/shorter divider,
proportional fuel fill with neutral empty track and fractional ticks, centered fuel
icon, tighter spacing and right-aligned value/unit groups in cards. Counter-strip
alignment is unchanged. No transport, permission or vehicle-command changes.

The header now selects the next event from the source's local solar schedule:
sunrise before dawn, sunset during daylight and following sunrise after sunset.
Icon and time switch together. The demo supplies 06:48/18:24; actual location/date
solar schedules are still pending integration. No fixed event times leak into live
samples. Three `SolarEventTest` cases passed, covering exact transition boundaries,
midnight and missing/invalid schedules. Format, lint and compilation passed in the
same run: `BUILD SUCCESSFUL in 1m 57s`. Visual verification/install pending.

Expanded diagnostic catalog requested by the owner is recorded as DIAG-01;
implementation and the concrete Car Scanner list remain pending.

Delivery exported by APK chat in 51s to
`app/build/exports/2026-11-17-5f63216-cluster-refinement-debug/`.
Application SHA-256 `9536825C12AA147B45061093DED0AF66A0FB2580F6E075ABDAE2CE19F61317D4`;
instrumentation remained byte-identical to the installed test APK and was reused.
Both exported hashes were checked; app installation returned Success.
Focused launch/navigation test passed: `OK (1 test)`, 2.93s. Private log:
`.tools/device-validation/cluster-refinement-test.log`.

One batched visual inspection of actual Inicio/Tablero screenshots confirmed raised
odometer, shorter divider, fuel icon/fractions/empty remainder, smaller tab icons,
right-aligned value/unit groups and the next-sunrise icon with 06:48 at device time
21:41, following the demo sunset. Private captures:
`.tools/device-validation/cluster-refinement-inicio.png` and `cluster-refinement-tablero.png`.
USB stay-awake returned to original value 0, verified. App left open on Inicio.

## Fullscreen adaptation and alignment restoration — 2026-11-17

Owner clarified that right alignment was an opinion request. Restored card values
to the left and units to the right. Replaced the fixed-aspect centered viewport
with width/height-aware placement. Panels/cards span the available width; circular
instrument groups retain uniform local scaling. Semantic/touch targets use the
same layout coordinates. Immersive mode hides system bars, with transient swipe
access; display-cutout clearance is preserved.

Moderate UI-layout risk: verify Inicio/Tablero captures and the fuel-card detail
action after reflow. The launch test now opens Autonomía and returns before
checking settings. Format, lint and production/instrumentation compilation passed
in 1m 49s. Existing data, solar and transport tests are reused because those paths
did not change. APK installation and focused visual/navigation check pending.

APK chat exported `2026-11-17-5f63216-cluster-fullscreen-debug` in 49s.
Application SHA-256 `110CA297A4A259930BA3D00F906A4870466FB3A637C4E57F618E50DB554ACB29`;
test APK SHA-256 `32BC89FBB5896EA62256D8DC80E6700A4DBBFFACA355E496A45A6DE5D35C5AD6`.
Both were verified and installed successfully. The updated launch/navigation test
passed: `OK (1 test)`, 3.399s, including the fuel-card action and return from Autonomía.
Private log: `.tools/device-validation/cluster-fullscreen-test.log`.

Actual Inicio and Tablero captures plus a full-device screenshot confirmed that
the layout uses the viewport width, system bars are hidden, circles remain circular
and card values are left-aligned with right-aligned units. Source captures:
`.tools/device-validation/cluster-fullscreen-inicio.png`, `cluster-fullscreen-tablero.png`
and `cluster-fullscreen-device.png`. GPS/altitude footer remains visible. Original
USB stay-awake value 0 was restored and verified; app left open on Inicio.

## Fixed header, larger cluster and device location — 2026-11-17

Owner-requested UI changes: stable app-name position with a back icon in a reserved
slot, 114% Inicio speed instrument and panel contours derived from its circumference.
Launch test additionally compares the app-name bounds before/after opening Autonomía
and activates the actual back control.

Location permissions expanded deliberately to foreground COARSE/FINE only. Native
LocationManager GPS/network updates are owned by the visible activity; onStop removes
listeners/timers and clears readings. Coordinates never use the engine demo values.
Reject invalid/future positions and expire fixes after 60s; altitude remains absent
unless provided. Prefer API34 MSL altitude where available, otherwise identify GNSS
ellipsoid altitude. Data remains in memory; app writes no coordinate logs/history.
References: [Android permissions](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime),
[LocationManager](https://developer.android.com/reference/android/location/LocationManager),
[altitude semantics](https://developer.android.com/reference/android/location/Location).

Three DevicePositionTest and three ManifestSecurityTest cases passed. Format, lint,
production and instrumentation compilation passed in 1m50s; packaged-manifest privacy
passed with Bluetooth plus foreground location only. Prior transport/solar tests
are reused unchanged.

APK chat exported `2026-11-17-5f63216-cluster-location-debug` in 51s.
Application SHA-256 `2B289444DEBA15EA604697B3CF4083DBEEDE69935AE6836B18EFD1934F6D2349`;
test APK SHA-256 `94843B48E676F943FC1C54E956DC463E80B0244459C87DFD046DC0E5875877E7`.
Both hashes were verified and both APKs installed successfully. Owner granted
precise foreground location through Android. Focused launch/navigation test passed:
`OK (1 test)`, 3.256s, including stable header bounds and the back-icon action.
Private log: `.tools/device-validation/cluster-location-test.log`.

Actual device capture confirmed Android coordinates and an available MSL altitude
while engine simulation remained active. Coordinate values are omitted here for
privacy. Visual inspection confirmed the larger circular instrument, matching side
contours and preserved card alignment. Private captures:
`.tools/device-validation/cluster-location-device.png` and `cluster-location-tablero.png`.
Original USB stay-awake value 0 was restored and verified. App left open on Inicio.
Vehicle telemetry remains simulated during this check; solar-event times remain
demo values pending their separate implementation.
