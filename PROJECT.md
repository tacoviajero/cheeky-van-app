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

Versión documentada: 0.1.0. La base del hito 0 está implementada y bajo revisión.
La evidencia y los pendientes se mantienen en [validación](docs/validation.md).

## Coordinación del trabajo

- Plataforma exclusiva: Android nativo.
- Chat de desarrollo: implementación, arquitectura, pruebas y notas técnicas breves.
- Chat de control de versiones: commits, ramas, publicación, PR y seguimiento de CI.
- Chat de APK: generación y exportación de los APK para instalación y pruebas.
- Cada entrega identifica la revisión del código y sus verificaciones para coordinar los chats.

## Usuario y escenarios

- Propietario de la camioneta que consulta el estado disponible del vehículo.
- Desarrollo y comprobación del tablero mediante datos simulados.
- Futuras sesiones de lectura mediante un adaptador previamente emparejado.

La app es una herramienta complementaria. Los instrumentos y advertencias del
fabricante siguen siendo la referencia para operar el vehículo.

## Requisitos y aceptación

Los criterios siguientes describen cómo verificar cada requisito; su presencia en
esta tabla no significa que la verificación haya sido ejecutada.

| ID | Requisito y etapa | Criterio de aceptación / verificación |
| --- | --- | --- |
| BASE-01 | Hito 0: pantalla inicial oscura con estado de disponibilidad y aviso de seguridad. | Prueba de lanzamiento e inspección en Android; comprobar orientación, fuente grande, áreas seguras y desplazamiento según `docs/milestone-0.md`. |
| BASE-02 | Hito 0: base sin permisos sensibles ni funciones de red, conexión o almacenamiento. | Pruebas de privacidad y verificación del manifiesto empaquetado; reglas de copia y transferencia excluyen datos. Se permite el permiso interno de firma aportado por AndroidX. |
| BASE-03 | Hito 0: compilación y validación reproducibles. | Formato, lint estricto, pruebas JVM, APK de aplicación y de instrumentación; ejecución en dispositivo y controles alojados según las puertas del hito. |
| SIM-01 | Hito 1 planificado: fuente de telemetría simulada con procedencia explícita. | Verificar que el estado presentado identifica la simulación y las muestras conservan su origen; detalle funcional por acordar al abrir el hito. |
| DATA-01 | Simulación y conexión futura: distinguir disponibilidad y validez de las lecturas. | Pruebas que diferencien cero válido, ausencia, incompatibilidad, respuesta malformada y dato desactualizado; limpiar muestras al cambiar de fuente o conexión. |
| CON-01 | Hito 2 planificado: elegir un dispositivo ya emparejado y solicitar el permiso de conexión cuando corresponda. | Pruebas de permisos, selección, cancelación y desconexión, más validación del adaptador y vehículo estacionado. |
| OBD-01 | Hito 2 planificado: descubrir PIDs compatibles antes de leer RPM, velocidad, temperatura de refrigerante y carga calculada. | Pruebas del descubrimiento y parser; comprobar disponibilidad y valores con hardware. La compatibilidad de cada PID en este T5 está pendiente de validación. |
| OBD-02 | Toda conexión futura: ejecutar únicamente operaciones de lectura y configuración segura permitidas. | Pruebas de la lista de comandos, límites de entrada, respuestas inesperadas y selección de ECU; revisión previa a habilitar el transporte real. |
| CON-02 | Conexión futura: cerrar recursos al cancelar o desconectar y detener la sesión al terminar el ciclo de vida visible. | Pruebas de transporte simulado y hardware para cierre de socket, revocación de permisos y reconexión; reconectar explícitamente al regresar. |

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
- Futuro transporte Bluetooth Classic/RFCOMM y protocolo ELM327/OBD, sujetos a diferencias de firmware y disponibilidad real de sensores.
- La optimización para la T5 se basa en valores y comandos verificados con nuestro vehículo y adaptador; cada lectura específica de Volkswagen requiere comprobar disponibilidad e interpretación.
- Los límites de refresco y consumo deben medirse con hardware antes de comprometer valores de rendimiento.
- Preferencias y diagnósticos futuros locales; logs redactados con participación explícita del usuario, según el alcance documentado en el README.

## Decisiones pendientes y cierre de hitos

- Cerrar la revisión del propietario y los controles de repositorio del hito 0. El APK con el nombre corregido pasó la prueba de inicio y la revisión visual en el Xiaomi con Android 16, en ambas orientaciones y con texto al 200 %. Consultar [criterios del hito](docs/milestone-0.md) y [evidencia vigente](docs/validation.md).
- Confirmar el identificador de paquete antes de distribuir una versión firmada.
- Al abrir el hito 1, concretar valores simulados, controles de interacción y criterios de aceptación detallados.
- Antes de conexión real, resolver la política de múltiples ECU y verificar compatibilidad, permisos, errores y rendimiento con el adaptador.

Cada cambio de alcance debe actualizar los requisitos afectados. La estrategia de
pruebas está en [testing](docs/testing.md); las reglas de entrega, en
[CONTRIBUTING.md](CONTRIBUTING.md).
