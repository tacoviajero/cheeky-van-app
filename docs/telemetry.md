# Lecturas del tablero de la T5

El propietario informa que Car Scanner leyó **201 parámetros** de esta camioneta con
el adaptador Bluetooth. Su catálogo real tiene prioridad sobre las listas genéricas.
Falta recibir nombres, unidades y valores; el número por sí solo no identifica qué
sensores, cálculos o estados concretos están disponibles.

| Lectura solicitada | Vía estándar candidata | Estado en Cheeky Van App |
| --- | --- | --- |
| RPM / velocidad / carga / refrigerante | 01 0C / 0D / 04 / 05 | Simulados; lectura real pendiente |
| Temperatura de aceite | 01 5C como candidato estándar; identificar consulta usada por Car Scanner | El propietario confirma sensor en cárter y lectura en Car Scanner; integración real en Cheeky pendiente. Mockup con valor simulado. |
| Presión absoluta de admisión / atmosférica | 01 0B / 33 | Simuladas; disponibilidad y rango reales pendientes |
| Presión relativa de sobrealimentación | Admisión absoluta menos atmosférica | Derivada únicamente de las dos muestras simuladas |
| Temperatura de admisión / caudal de aire | 01 0F / 10 | Simulados; disponibilidad real pendiente |
| RPM del turbo | 01 74 | Sin valor; comprobar en catálogo/vehículo |
| Presión solicitada / control de geometría variable | 01 70 / 71 | Sin valor; comprobar en catálogo/vehículo |
| Temperaturas del turbo | 01 75 / 76 | Sin valor; sensores concretos por identificar |

Fuentes acotadas: [lista del fabricante OBDLink](https://www.obdlink.com/obd-apps/obdlink-app/)
y [parámetros de OBD Auto Doctor](https://www.obdautodoctor.com/help/articles/supported-obd-parameters/).
Son candidatos definidos por el estándar; su inclusión no confirma soporte en esta T5.
No se han añadido consultas OBD nuevas ni interpretaciones Volkswagen a partir de esta tabla.

La velocidad OBD futura será la comunicada por el vehículo. Su precisión respecto al
desplazamiento real requeriría una comprobación independiente; la simulación actual
es demostrativa. Las RPM del turbo requieren un dato propio: no se deducen de las RPM
del motor. Los parámetros sin vía confirmada se informan y se continúa con el resto.
