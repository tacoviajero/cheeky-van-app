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

Versión documentada: 0.1.0. El propietario aprobó la fundación del hito 0 el
2026-11-16. La integración de la PR corresponde al chat de control de versiones.
La evidencia y los pendientes se mantienen en [validación](docs/validation.md).

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

Los criterios siguientes describen cómo verificar cada requisito; su presencia en
esta tabla no significa que la verificación haya sido ejecutada.

| ID | Requisito y etapa | Criterio de aceptación / verificación |
| --- | --- | --- |
| BASE-01 | Hito 0: pantalla inicial oscura con estado de disponibilidad y aviso de seguridad. | Prueba de lanzamiento e inspección en Android; comprobar orientación, fuente grande, áreas seguras y desplazamiento según `docs/milestone-0.md`. |
| BASE-02 | Hito 0: base sin permisos sensibles; extensión Bluetooth autorizada posteriormente. | Pruebas de privacidad y manifiesto empaquetado: solo conexión Bluetooth emparejada y permiso interno de firma AndroidX. Copia y transferencia excluyen datos. |
| BASE-03 | Hito 0: compilación y validación reproducibles. | Formato, lint estricto, pruebas JVM, APK de aplicación y de instrumentación; ejecución en dispositivo y controles alojados según las puertas del hito. |
| SIM-01 | Primera entrega del hito 1: fuente de telemetría simulada con procedencia explícita. | Secuencia y cambios de escenario comprobados con pruebas JVM; revisión del nuevo tablero en el teléfono pendiente. |
| DASH-01 | Tablero solicitado: RPM, velocidad del vehículo, carga, refrigerante, temperatura de aceite y datos accesibles de admisión/turbo. | Identificar la fuente; distinguir valores simulados y lecturas reales, y marcar las lecturas pendientes sin sustituirlas por cero. Priorizar el catálogo de 201 parámetros observado con Car Scanner. |
| NAV-01 | Cinco tabs inferiores: Inicio, Tablero, Mapa, Diagnóstico y Ajustes. | Orientación única horizontal para el estéreo Android del coche; tabs inferiores en todas las vistas y retorno desde detalles. Diseño en `docs/propuesta-visual.md`; implementación pendiente. |
| DASH-02 | Tablero general completo inspirado en las referencias: añadir voltaje, posición del acelerador, consumo instantáneo, datos de mezcla/corrección y combustible restante según disponibilidad en la T5. | Verificar origen, unidades e interpretación de cada dato; distinguir datos ausentes y estimaciones. Combustible abre Autonomía; admisión/turbo abre su detalle. |
| DASH-03 | Inicio y Tablero: hora, temperatura exterior, Trip A y B, kilómetros totales, horas de motor, coordenadas y altitud. Escalas físicas: RPM 0–6.000 y velocidad 0–240 km/h. | Inicio centra el velocímetro; tabs inferiores con línea verde. Rojo de RPM según foto (5.000–6.000); otros umbrales requieren validación. Identificar fuentes reales y distinguir totales del vehículo de contadores de la app. Mockups actualizados; implementación pendiente. |
| MAP-01 | Vista Mapa, con interés en elevación, subidas y bajadas. | Fuente cartográfica, perfil de elevación y cálculo de pendiente pendientes de definición antes de implementar esas funciones. |
| DIAG-01 | Diagnóstico: catálogo completo de valores legibles de la ECU de nuestra T5, además de averías y estados. | Descubrir/listar todas las lecturas accesibles, con nombre, unidad, valor y disponibilidad. Usar los 201 parámetros observados por el propietario en Car Scanner como referencia de cotejo; catálogo concreto y lecturas específicas pendientes de integrar. |
| GEO-01 | Coordenadas y altitud disponibles del Android, independientes de OBD y de la simulación del motor. | Permiso en primer plano, actualización reciente y origen del dispositivo explícito; altitud ausente permanece sin valor. Detener al salir de la actividad y retirar lecturas de más de 60 s. No usar coordenadas ficticias como sustituto. |
| DATA-01 | Simulación y conexión futura: distinguir disponibilidad y validez de las lecturas. | Pruebas que diferencien cero válido, ausencia, incompatibilidad, respuesta malformada y dato desactualizado; limpiar muestras al cambiar de fuente o conexión. |
| CON-01 | Implementado, pendiente de hardware: elegir un dispositivo ya emparejado y solicitar el permiso de conexión cuando corresponda. | Pruebas de permisos, selección, cancelación y desconexión, más validación del adaptador y vehículo estacionado. |
| OBD-01 | Implementado, pendiente de hardware: descubrir PIDs compatibles antes de leer RPM, velocidad, temperatura de refrigerante y carga calculada. | Pruebas del descubrimiento y parser; comprobar disponibilidad y valores con hardware. La compatibilidad de cada PID en este T5 está pendiente de validación. |
| OBD-02 | Toda conexión: ejecutar únicamente operaciones de lectura y configuración segura permitidas. | Pruebas de la lista de comandos, límites de entrada, respuestas inesperadas y selección de ECU; revisión previa a habilitar el transporte real. |
| CON-02 | Conexión: cerrar recursos al cancelar o desconectar y detener la sesión al terminar el ciclo de vida visible. | Pruebas de transporte simulado y hardware para cierre de socket, revocación de permisos y reconexión; reconectar explícitamente al regresar. |

## Alcance posterior y exclusiones

El roadmap existente menciona tableros configurables, GPS, mapas, viajes,
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
- Integrar la fundación aprobada mediante el chat de control de versiones. El APK pasó la prueba de inicio y la revisión visual en el Xiaomi con Android 16, en ambas orientaciones y con texto al 200 %; CI también pasó. Consultar [evidencia vigente](docs/validation.md).
- Confirmar el identificador de paquete antes de distribuir una versión firmada.
- Revisar en el teléfono la [primera entrega del hito 1](docs/milestone-1.md), con los escenarios y las lecturas solicitadas.
- Con hardware, verificar compatibilidad, permisos, errores y rendimiento con el adaptador. Las respuestas de varias ECU con valores distintos se rechazan como ambiguas; las idénticas se aceptan.
- Obtener el catálogo de Car Scanner de esta T5 (nombres, unidades y valores disponibles); comprobar allí aceite, presión, temperaturas y RPM del turbo antes de profundizar en PIDs específicos. Si falta un dato accesible, informar y avanzar con las demás lecturas.
- El Yaris R 2016 del propietario muestra marcha engranada y marcha recomendada. Esa referencia queda anotada para un futuro indicador; las reglas de recomendación aún requieren definición.

Cada cambio de alcance debe actualizar los requisitos afectados. La estrategia de
pruebas está en [testing](docs/testing.md); las reglas de entrega, en
[CONTRIBUTING.md](CONTRIBUTING.md).
