# Reglas de trabajo

La política de esfuerzo y validación proporcional incluida al final de este archivo es obligatoria para todas las tareas y sesiones del proyecto. Aplícala junto con las reglas siguientes.

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

### Principio general

El objetivo es entregar soluciones correctas con el mínimo esfuerzo necesario para alcanzar una confianza razonable.

No optimices por número de pruebas ejecutadas, cantidad de código producido ni exhaustividad aparente.

Cada actividad debe aportar valor demostrable a la tarea aprobada.

El tiempo de ejecución, los tokens, las llamadas al modelo y los recursos computacionales son recursos limitados que debes administrar responsablemente.

### 1. Clasificación del riesgo

Antes de comenzar, clasifica el cambio según su posible impacto.

**Nivel 1 — Bajo riesgo**

Cambios aislados de documentación, presentación, configuración no crítica o lógica simple sin dependencias significativas.

- Ejecuta únicamente las comprobaciones directamente relacionadas.
- No ejecutes suites completas por defecto.
- No repitas pruebas aprobadas si el código relevante no cambió.

**Nivel 2 — Riesgo moderado**

Cambios funcionales que afectan componentes, interfaces, persistencia o interacciones existentes.

- Ejecuta pruebas específicas del componente.
- Comprueba las integraciones que puedan verse afectadas.
- Amplía la validación únicamente cuando exista una razón técnica concreta.

**Nivel 3 — Alto riesgo**

Cambios en autenticación, seguridad, migraciones de datos, operaciones destructivas, concurrencia, arquitectura compartida o funcionalidades críticas.

- Identifica explícitamente los riesgos.
- Establece las verificaciones necesarias para cubrirlos.
- Ejecuta las pruebas relevantes de integración y regresión.
- Utiliza la validación completa cuando el impacto lo justifique o sea obligatoria para integrar el cambio.

El tamaño del cambio no determina por sí solo su riesgo.

### 2. Plan mínimo antes de actuar

Para cada tarea, identifica brevemente:

- Qué comportamiento o componente cambiará.
- Qué nivel de riesgo tiene.
- Qué comprobaciones son necesarias.
- Qué condición permitirá declarar terminada la tarea.

Para tareas triviales, este análisis debe ser mínimo y no convertirse en una nueva actividad extensa.

### 3. Ejecución de pruebas

Aplica las siguientes reglas:

1. Ejecuta primero la prueba más específica capaz de detectar el fallo relevante.
2. Amplía el alcance sólo si existe evidencia que lo justifique.
3. No ejecutes pruebas redundantes sobre el mismo estado del código.
4. No repitas una suite completa por costumbre.
5. No ejecutes compilaciones repetidas sin cambios relevantes.
6. No generes nuevas pruebas para comportamientos ajenos a la tarea.
7. No conviertas una advertencia no relacionada en una nueva tarea.
8. No ejecutes procesos indefinidos de análisis, reparación y nueva validación.

Las verificaciones obligatorias del repositorio o de CI deben respetarse. Si resultan excesivas, propone modificar esa política por separado en lugar de omitirlas silenciosamente.

### 4. Límites de autonomía

Puedes corregir errores directamente relacionados con la tarea aprobada.

Si una comprobación falla:

1. Identifica la causa probable.
2. Determina si el fallo se relaciona con tus cambios.
3. Realiza una corrección focalizada cuando corresponda.
4. Repite únicamente las comprobaciones invalidadas por esa corrección.

Después de dos intentos fallidos de corregir el mismo problema, detente y presenta el diagnóstico antes de continuar.

Si descubres un problema ajeno a la tarea, regístralo y repórtalo. No lo soluciones automáticamente.

Si una tarea aparentemente pequeña requiere una refactorización amplia, nuevas dependencias importantes o cambios arquitectónicos, solicita autorización antes de ampliar el alcance.

### 5. Condiciones de detención

Detén el trabajo cuando:

- Se cumplan los criterios de aceptación.
- Las verificaciones necesarias hayan pasado.
- No existan fallos conocidos relacionados que impidan completar la tarea.
- Se hayan documentado las limitaciones relevantes.

También debes detenerte cuando:

- La tarea requiera decisiones funcionales no autorizadas.
- Se alcance el límite de reintentos.
- El trabajo exceda sustancialmente el alcance aprobado.
- Una verificación necesaria no pueda ejecutarse y se requiera una decisión del propietario.

No busques problemas adicionales una vez cumplidas las condiciones de finalización.

### 6. Coordinación entre agentes y sesiones

Antes de repetir verificaciones, revisa si existe evidencia válida de ejecuciones anteriores.

Puedes reutilizar resultados cuando:

- La versión del código y las dependencias relevantes no hayan cambiado.
- Las condiciones de ejecución sigan siendo equivalentes.
- Los resultados sean verificables y estén disponibles.
- No exista una obligación de volver a ejecutar la comprobación.

No consideres como evidencia suficiente una afirmación no verificable de otro agente.

Evita duplicar trabajo entre sesiones cuando el estado existente permita continuar con seguridad.

### 7. Comunicación

No solicites autorización para cada operación rutinaria comprendida dentro del alcance aprobado.

Informa cuando una decisión afecte materialmente al alcance, riesgo, costo o comportamiento solicitado.

Al finalizar, comunica:

- Qué se modificó.
- Qué se comprobó y con qué resultado.
- Qué quedó sin verificar y por qué.
- Qué bloqueos existen, si los hay.

Sé breve. No conviertas cada tarea en un informe extenso.

### Regla final

**No confundas exhaustividad con calidad. Aplica el nivel de verificación necesario para controlar el riesgo real del cambio, demuestra los resultados y termina.**
