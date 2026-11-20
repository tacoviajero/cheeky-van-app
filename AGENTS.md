# Reglas de trabajo

La política global de esfuerzo y validación proporcional y los complementos locales de este archivo son obligatorios para todas las tareas y sesiones del proyecto.

## Alcance y decisiones

- El propietario decide el comportamiento y alcance del producto. Resuelve los detalles técnicos menores dentro del alcance autorizado y comunica las suposiciones relevantes.
- Usa `PROJECT.md` como índice del alcance, requisitos y criterios de aceptación. Consulta los documentos enlazados para los detalles técnicos y la evidencia.
- Distingue requisitos aprobados, restricciones, propuestas y decisiones pendientes. Las instrucciones explícitas posteriores del propietario prevalecen sobre la documentación anterior; actualiza los documentos afectados.
- Pregunta cuando falte una decisión que cambie el comportamiento del producto, la arquitectura fundamental o un riesgo relevante. Continúa con el trabajo independiente de esa decisión.
- Mantén los cambios limitados a la tarea. Registra propuestas adicionales por separado y solicita autorización para ampliar el alcance.

## Implementación y calidad

- Antes de editar, inspecciona la estructura, las convenciones, las instrucciones aplicables y el estado de Git. Preserva los cambios preexistentes.
- Ajusta la planificación al tamaño del cambio. Para funcionalidades relevantes, identifica comportamiento, componentes afectados, errores, criterios de aceptación y verificaciones antes de implementar.
- Mantén una arquitectura proporcional y responsabilidades claras. Documenta decisiones importantes en `docs/decisions/` y justifica dependencias importantes.
- Define criterios verificables. Vincula requisitos con pruebas o verificaciones manuales; añade pruebas apropiadas para comportamiento nuevo, errores y regresiones.
- Sigue los comandos de validación y las convenciones de Git de `CONTRIBUTING.md` y `README.md`.
- Informa qué cambió, qué verificaste, los resultados reales y los pendientes relevantes. Distingue compilación, pruebas automatizadas, revisión visual y validación con hardware.
- Declara completa una tarea únicamente cuando satisfaga sus criterios obligatorios. Mantén `docs/validation.md` actualizado cuando cambie la evidencia del hito.

## Política global de esfuerzo y validación proporcional

La [política obligatoria compartida](https://github.com/tacoviajero/engineering-standards/blob/main/policies/effort-validation.md) es la fuente canónica. Aplica estas obligaciones operativas en **cada tarea**, incluso si no puedes consultar el otro repositorio durante la sesión:

- **Antes de actuar**, identifica de forma proporcional al tamaño de la tarea qué cambiará, su nivel de riesgo, la validación necesaria y la condición de finalización. Para tareas triviales basta una nota breve.
- **Clasifica por impacto, no por número de archivos:** nivel 1 para cambios aislados y no críticos; nivel 2 para cambios funcionales que afecten componentes o integraciones; nivel 3 para seguridad/autenticación, migraciones, operaciones destructivas, concurrencia, arquitectura compartida o funcionalidades críticas.
- **Valida según el riesgo:** nivel 1, comprobación puntual; nivel 2, pruebas específicas y de integraciones afectadas; nivel 3, pruebas relevantes de integración y regresión, ampliadas a la suite completa cuando el impacto o las reglas obligatorias lo exijan.
- **Empieza por la verificación más específica** capaz de detectar el fallo. No repitas suites, compilaciones ni pruebas sobre un mismo estado sin justificación. No agregues pruebas ajenas al cambio ni transformes advertencias no relacionadas en tareas nuevas. Respeta siempre los controles obligatorios del repositorio y de CI; si son excesivos, propón revisarlos por separado, sin omitirlos.
- **Limita la autonomía al alcance autorizado.** Puedes corregir fallos causados por tu trabajo y repetir sólo la verificación invalidada. Después de **dos intentos fallidos** sobre el mismo problema, detente y presenta el diagnóstico. Reporta fallos ajenos sin corregirlos; solicita autorización para refactorizaciones amplias, dependencias importantes o cambios arquitectónicos no aprobados.
- **Reutiliza evidencia verificable** de pruebas previas sólo si código, dependencias y condiciones relevantes permanecen equivalentes y ninguna obligación exige repetirlas. Una afirmación no verificable de otro agente no basta.
- **Detente** cuando se cumplan los criterios de aceptación y las verificaciones necesarias, o cuando exista un bloqueo o decisión no autorizada. No busques más problemas por costumbre.
- **Al cerrar**, informa brevemente qué cambió, qué comprobaste y su resultado, qué quedó sin verificar y los bloqueos pertinentes.

`docs/validation.md` registra evidencia específica del proyecto y sus hitos; preserva esa evidencia histórica. Esta sección aplica la política global sin sustituir sus normas técnicas particulares. Las actualizaciones futuras de la política canónica **no se sincronizan automáticamente**: revisa su impacto y actualiza estas instrucciones de forma explícita. Requisitos de seguridad, CI e instrucciones de mayor prioridad siguen siendo obligatorios.

## Seguridad de la app

- Aplica mínimo privilegio y valida entradas externas, incluidas las respuestas del adaptador.
- La comunicación con el vehículo debe respetar las listas explícitas de comandos de lectura y configuración segura descritas en la arquitectura. La entrada arbitraria de comandos, el borrado de DTC y las escrituras en ECU están excluidos.
- Conserva la distinción entre lectura válida de cero, dato ausente, incompatible, malformado y desactualizado. Identifica el origen simulado de las muestras.
- Mantén secretos, material de firma, identificadores y capturas privadas fuera de Git y de los artefactos de CI.
- Las operaciones destructivas, los cambios sobre información real o producción y las acciones de publicación requieren la autorización aplicable del propietario. Respeta las aprobaciones ya concedidas para su alcance.
- Si se aprueban funcionalidades de IA en el producto, define entradas, salidas, permisos, evaluación, privacidad, límites y comportamiento ante fallos antes de implementarlas.

## Windows y PowerShell

- Usa sintaxis compatible con Windows PowerShell 5.1 y PowerShell 7; evita sintaxis de Bash y encadenamiento con `&&` o `||`.
- Usa `rg` para buscar archivos o texto y cmdlets de PowerShell para filtrar y procesar resultados.
- Usa `curl.exe`, `Invoke-WebRequest` o `Invoke-RestMethod` para solicitudes HTTP.
- Protege las variables de scripts incrustados con cadenas o here-strings de comillas simples. Si necesitas escapar comillas dobles en PowerShell, usa el acento grave de PowerShell.
- Usa `--%` cuando sea adecuado para argumentos nativos complejos; revisa el tratamiento de variables y rutas.
- Escribe archivos con codificación UTF-8 explícita mediante herramientas de edición o `Set-Content`/`Out-File -Encoding utf8`.

## Comunicación

- Sé directo y concreto. Explica consecuencias y decisiones relevantes con detalle proporcional al cambio.
- Evita autoelogios, comentarios sobre la profundidad del análisis y contrastes con enfoques inferiores.
- Mantén la documentación breve y actualizada; enlaza la información existente para evitar duplicaciones.

## Política obligatoria de esfuerzo y validación proporcional

La sección [Política global de esfuerzo y validación proporcional](#política-global-de-esfuerzo-y-validación-proporcional) establece las obligaciones comunes. Se aplican además estos complementos locales:

- Entrega soluciones correctas con el mínimo esfuerzo necesario para alcanzar una confianza razonable. Cada actividad debe aportar valor a la tarea aprobada; administra responsablemente tiempo, tokens, llamadas al modelo y recursos computacionales. No optimices por cantidad de pruebas, código o exhaustividad aparente.
- Resuelve las operaciones rutinarias comprendidas en el alcance aprobado sin solicitar autorización para cada una. Informa cuando una decisión afecte materialmente al alcance, riesgo, costo o comportamiento solicitado.
- Declara terminada la tarea cuando se cumplan sus criterios de aceptación, pasen las verificaciones necesarias, no existan fallos conocidos relacionados que impidan completarla y estén documentadas las limitaciones relevantes.
