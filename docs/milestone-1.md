# Hito 1 — primera entrega del tablero

Estado: en desarrollo, con el alcance de lecturas solicitado por el propietario.

## Comportamiento propuesto

- Tablero oscuro para nuestra VW T5, con RPM, velocidad en km/h, refrigerante y aceite en °C, y carga en %.
- Admisión/turbo: presión relativa, presión absoluta de admisión, atmosférica, temperatura de admisión y caudal de aire, inicialmente simulados.
- RPM y temperatura del turbo, presión solicitada y geometría variable aparecen sin valor hasta identificar datos accesibles en esta T5.
- Indicador permanente **SIMULACIÓN** y nombre del vehículo.
- Tres controles de escenario: **Motor apagado**, **En marcha** y **Sensor sin datos**.
- **En marcha** cambia los valores automáticamente mediante una secuencia repetible de demostración.
- **Motor apagado** presenta RPM y velocidad en cero como lecturas simuladas válidas.
- **Sensor sin datos** muestra el refrigerante como **Sin datos**, conservando las otras lecturas.
- Los valores son ficticios para desarrollar la interfaz y comprobar estados; los datos reales de la T5 se verificarán con el adaptador en el hito correspondiente.

## Aceptación y validación proporcional

- Las lecturas y sus unidades son legibles en el Xiaomi, en ambas orientaciones.
- Cambiar de escenario actualiza el tablero y conserva el indicador de simulación.
- Una lectura ausente se distingue de un cero válido.
- Pruebas focalizadas de la secuencia simulada y de las transiciones de estado; comprobación de la pantalla con los tres escenarios.
- Reutilizar la evidencia previa de la fundación cuando sus componentes no cambien; respetar las validaciones obligatorias de CI al integrar.

Esta entrega inicia la simulación. Los demás escenarios de fallos previstos se concretarán
en iteraciones posteriores antes de considerar completo el alcance de simulación.

La conexión real se guiará por el catálogo de 201 parámetros que el propietario
informa haber leído con Car Scanner. Falta compartir ese listado. Los candidatos
y límites de la consulta acotada están en [telemetría](telemetry.md).
