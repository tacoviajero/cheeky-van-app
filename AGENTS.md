# Reglas de trabajo

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
