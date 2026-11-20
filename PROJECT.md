# Cheeky Van App — especificación del producto

## Propósito y estado

Cheeky Van App es una aplicación Android nativa de uso personal, diseñada y optimizada
para nuestra Volkswagen Transporter T5 2013, motor 2.0 TDI CAAC y transmisión manual.
El adaptador previsto es Bluetooth Classic compatible con ELM327. La camioneta y las
necesidades de sus propietarios guían las decisiones de interfaz, telemetría y rendimiento.
El alcance actual contempla esta T5 como único vehículo; el soporte multivehículo
queda fuera del alcance aprobado.

Este documento consolida el alcance registrado en el repositorio. Los requisitos de
hitos futuros representan planificación existente; su implementación queda sujeta a
la aprobación del hito correspondiente. Las propuestas nuevas requieren una decisión
del propietario.

Versión documentada: 0.1.0. Estado revisado el 2026-11-19 en la carpeta original,
rama `codex/preserve-local-development`, HEAD `92dc33e`.
El propietario aprobó la fundación del hito 0 el 2026-11-16; quedó integrada mediante
la PR #1 (merge `07af106`). El desarrollo posterior de tablero, simulación,
Bluetooth/OBD y ubicación está preservado en `5289953`; `92dc33e` integra además
las instrucciones globales. El propietario revisó y aprobó el incremento actual y
autorizó su publicación; la fusión de la PR requiere aprobación posterior.
La evidencia y sus límites se mantienen en [validación](docs/validation.md).

La fundación está cerrada. El incremento posterior está implementado parcialmente
respecto al producto completo y cuenta con comprobaciones automatizadas y en el
Xiaomi. La conexión real a la T5 y la adaptación al estéreo definitivo siguen
pendientes. Las pruebas históricas de la pantalla inicial en vertical y con texto
al 200 % pertenecen al hito 0; el tablero actual usa exclusivamente horizontal.

## Coordinación del trabajo

- Todas las sesiones deben aplicar la [política obligatoria de esfuerzo y validación proporcional](AGENTS.md#política-obligatoria-de-esfuerzo-y-validación-proporcional), incluida en las reglas de trabajo.
- Plataforma exclusiva: Android nativo.
- Destino de uso: estéreo Android del coche, tipo tablet instalada en el tablero; orientación única horizontal y cinco tabs inferiores. El Xiaomi es un dispositivo provisional de pruebas. Resolución del estéreo pendiente de conocer.
- Chat de desarrollo: implementación, arquitectura, pruebas y notas técnicas breves.
- Chat de control de versiones: commits, ramas, publicación, PR y seguimiento de CI.
- Chat de APK: generación y exportación de los APK para instalación y pruebas.
- Cada entrega identifica la revisión del código y sus verificaciones para coordinar los chats.

## Usuario y escenarios

- Propietario de la camioneta que consulta el estado disponible del vehículo.
- Desarrollo y comprobación del tablero mediante datos simulados.
- Sesiones de lectura mediante un adaptador previamente emparejado.
- El propietario informa que Car Scanner ya se conectó a esta T5 con el adaptador Bluetooth y leyó 201 parámetros. Su listado/exportación será la referencia prioritaria para seleccionar las lecturas reales; el catálogo todavía no se ha compartido en este proyecto.
- El propietario confirma sensor de temperatura de aceite en el cárter y lectura de ese dato mediante Car Scanner. La disponibilidad en su T5 queda registrada; conectar esa lectura a Cheeky Van App sigue pendiente.

La app es una herramienta complementaria. Los instrumentos y advertencias del
fabricante siguen siendo la referencia para operar el vehículo.

## Requisitos y aceptación

La columna Estado distingue implementación, evidencia ejecutada y pendientes.
Las verificaciones en Xiaomi corresponden a la entrega del 2026-11-17, cuya
equivalencia con el código actual se comprobó el 2026-11-19; no son pruebas del
estéreo definitivo ni del vehículo. Detalle y referencias en `docs/validation.md`.

| ID | Requisito y etapa | Estado y verificación |
| --- | --- | --- |
| BASE-01 | Hito 0: pantalla inicial oscura, disponibilidad y aviso de seguridad. | Cerrado y aprobado. Lanzamiento y revisión visual en Xiaomi, ambas orientaciones y texto al 200 %, sobre la pantalla histórica. |
| BASE-02 | Hito 0: base sin permisos sensibles; extensiones autorizadas posteriormente. | Base histórica comprobada. Actualmente: Bluetooth emparejado, ubicación COARSE/FINE en primer plano y permiso interno de firma AndroidX; backup/transferencia excluidos. Tres pruebas actuales de manifiesto y comprobación empaquetada aprobadas. |
| BASE-03 | Compilación y validación reproducibles. | Fundación integrada con CI aprobado. Desarrollo posterior con formato, lint, compilación, APK e instrumentación locales registrados; CI del incremento actual pendiente al publicarlo. |
| SIM-01 | Fuente de telemetría simulada con procedencia explícita. | Implementada y comprobada con tres pruebas JVM, escenarios y pruebas UI en Xiaomi; cero válido y dato ausente diferenciados. |
| DASH-01 | RPM, velocidad, carga, refrigerante, aceite y admisión/turbo. | Tablero implementado y revisado en Xiaomi. OBD implementa cuatro lecturas: RPM, velocidad, refrigerante y carga; falta comprobarlas con la T5. Aceite y datos ampliados tienen presentación simulada o ausencia explícita; integración real pendiente. |
| NAV-01 | Inicio, Tablero, Mapa, Diagnóstico y Ajustes; horizontal y tabs inferiores. | Navegación implementada; lanzamiento horizontal, detalles y retorno comprobados en Xiaomi. Mapa y Diagnóstico son estados vacíos. Ajustes permite escenarios, conexión y ubicación. |
| DASH-02 | Voltaje, acelerador, consumo, correcciones de combustible y combustible restante. | Tarjetas y barras implementadas; datos ampliados simulados o ausentes según escenario. Autonomía y admisión/turbo abren detalles; comprobaciones UI registradas. Cálculos y lecturas reales pendientes. |
| DASH-03 | Hora, temperatura exterior, dos trips, totales, horas de motor; RPM 0–6.000 y velocidad 0–240. | Instrumentos y escalas implementados y revisados en Xiaomi; velocímetro central ampliado y encabezado estable. Hora del Android; temperatura exterior, contadores y marchas son demostración. Rojo RPM 5.000–6.000 según referencia; umbrales térmicos requieren validación del vehículo. Próximo evento solar probado con horario demo; cálculo por fecha/ubicación pendiente. |
| MAP-01 | Mapa, elevación, subidas y bajadas. | Tab con estado vacío. Fuente cartográfica, perfil y cálculo de pendiente pendientes de definición e implementación. |
| DIAG-01 | Catálogo completo de lecturas de ECU, averías y estados. | Tab con estado vacío. Catálogo concreto de los 201 parámetros observados en Car Scanner y lecturas específicas pendientes de integrar. |
| GEO-01 | Coordenadas y altitud reales del Android, independientes de OBD/simulación. | Implementado. Tres pruebas JVM de validez/caducidad aprobadas; permiso preciso, coordenadas y altitud observados en Xiaomi durante simulación. Limpieza al salir y retiro tras 60 s inspeccionados en código; revocación, proveedores y estéreo pendientes de comprobación manual. |
| DATA-01 | Distinguir cero, ausencia, incompatibilidad, malformación y caducidad; aislar fuentes. | Modelos y pruebas JVM de simulación, protocolo y ubicación; UI de sensor ausente y limpieza al desconectar comprobadas con adaptador de prueba. Errores y datos reales requieren hardware. |
| CON-01 | Elegir adaptador ya emparejado y solicitar permiso de conexión. | Flujo y RFCOMM implementados; inspección de código y controles de prueba en Xiaomi. Permisos reales de Bluetooth, selección y conexión con el adaptador pendientes. |
| OBD-01 | Descubrir PIDs antes de leer RPM, velocidad, refrigerante y carga. | Parser/coordinador comprobados con transporte simulado. Compatibilidad, unidades y valores en esta T5 pendientes de hardware. |
| OBD-02 | Lista fija de lectura/configuración segura y entrada acotada. | Implementada e inspeccionada; pruebas de parser/allowlist registradas. Respuestas ambiguas de ECU rechazadas. Sesión real con adaptador pendiente. |
| CON-02 | Cancelación, desconexión, cierre de recursos y sesión ligada a actividad visible. | Pruebas JVM de sesión y prueba UI de desconexión con adaptador simulado aprobadas; cierre al salir inspeccionado en código. Revocación, pérdida y reconexión con socket real pendientes. |

## Alcance posterior y exclusiones

El roadmap existente menciona tableros configurables, mapas, viajes,
estimación de combustible e historial diagnóstico para nuestra T5. Sus reglas y criterios detallados
siguen pendientes de definición y aprobación.

Quedan excluidos la entrada arbitraria de comandos, el borrado de códigos DTC y las
escrituras en ECU. La base actual carece de funciones de IA; este documento no
incorpora ninguna al alcance.

## Restricciones y dependencias

- Android 6.0 / API 23 o posterior; Kotlin y Compose/Material 3.
- Un módulo Android con separación progresiva entre presentación, dominio, datos y transporte. Detalle en [arquitectura](docs/architecture.md) y [ADR inicial](docs/decisions/0001-foundation.md).
- Toolchain y dependencias fijadas según [README](README.md) y [procedencia de herramientas](docs/toolchain.md).
- Transporte Bluetooth Classic/RFCOMM y protocolo ELM327/OBD, sujetos a diferencias de firmware y disponibilidad real de sensores.
- La optimización para la T5 se basa en valores y comandos verificados con nuestro vehículo y adaptador; cada lectura específica de Volkswagen requiere comprobar disponibilidad e interpretación.
- Los límites de refresco y consumo deben medirse con hardware antes de comprometer valores de rendimiento.
- Preferencias y diagnósticos futuros locales; logs redactados con participación explícita del usuario, según el alcance documentado en el README.

## Decisiones pendientes y cierre de hitos

- Mockups de Inicio y Tablero general aprobados e implementados el 2026-11-17, incluidos los estados de marcha en simulación. La [referencia visual](docs/propuesta-visual.md) define orientación horizontal y tabs inferiores para el estéreo Android. APK final instalado y comprobado en Xiaomi; ver [evidencia](docs/validation.md). Faltan lecturas adicionales reales y comprobación en el estéreo; Mapa y Diagnóstico muestran estados vacíos.
- Fundación cerrada e integrada. Incremento preservado en `5289953` e integrado con las instrucciones globales en `92dc33e`, revisado y aprobado por el propietario; publicación y CI corresponden al chat de control de versiones. Fusión pendiente de aprobación posterior.
- Confirmar el identificador de paquete antes de distribuir una versión firmada.
- La simulación y el tablero horizontal ya tienen comprobaciones en Xiaomi y aceptación del incremento actual por el propietario. Queda revisión en el estéreo definitivo; el documento de [hito 1](docs/milestone-1.md) conserva la planificación de esa primera entrega.
- Con hardware, verificar compatibilidad, permisos, errores y rendimiento con el adaptador. Las respuestas de varias ECU con valores distintos se rechazan como ambiguas; las idénticas se aceptan.
- Obtener el catálogo de Car Scanner de esta T5 (nombres, unidades y valores disponibles); comprobar allí aceite, presión, temperaturas y RPM del turbo antes de profundizar en PIDs específicos. Si falta un dato accesible, informar y avanzar con las demás lecturas.
- Marcha actual y sugerida implementadas visualmente en simulación, incluida reversa y sugerencias condicionales a izquierda/derecha. Detección/estimación real y reglas de recomendación aún requieren definición y validación.

Cada cambio de alcance debe actualizar los requisitos afectados. La estrategia de
pruebas está en [testing](docs/testing.md); las reglas de entrega, en
[CONTRIBUTING.md](CONTRIBUTING.md).
