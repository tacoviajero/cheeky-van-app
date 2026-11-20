# Propuesta visual — 2026-11-17

Estado: mockups de Inicio y Tablero general aprobados por el propietario el
2026-11-17, incluidos los estados del indicador de marcha. Implementación Android
implementada, validada e instalada en el Xiaomi en horizontal. Evidencia en
`docs/validation.md`. La resolución real del estéreo queda pendiente de comprobar.
Las otras vistas conservan su estado de boceto.
Referencia aprobada preservada en `.tools/design-proposal/aprobados-2026-11-17/`:
`inicio.png`, `tablero.png` y `estados-marcha.png`.
Las cuatro imágenes aportadas por el propietario son inspiración, sin exigencia de
copia exacta. Copias locales en `.tools/design-proposal/references/` (ignoradas por Git).
La lámina actual está en `.tools/design-proposal/cheeky-van-propuesta.png`, generada
con `render-v6.py` en esa carpeta. Incluye Inicio, Tablero y Autonomía. La lámina
`vistas-complementarias.png` contiene Mapa, Diagnóstico y Ajustes. También existen
imágenes individuales; todas son horizontales y usan las cinco tabs inferiores y
cifras ilustrativas. `motor-turbo.png` conserva ese detalle dentro de Tablero.
Destino confirmado: estéreo Android del coche, tipo tablet instalada en el tablero.
Las dimensiones y resolución concretas del estéreo todavía no se han proporcionado.
El regreso usa un icono en un hueco fijo a la derecha de la marca/título; el nombre
de la app conserva posición al abrir detalles. El velocímetro de Inicio crece al
114 %; los bordes interiores de ambos paneles siguen su circunferencia con separación
constante, también cuando cambia el ancho de pantalla.

La franja de coordenadas/altitud muestra ubicación del Android con permiso en primer
plano. Es independiente del origen OBD y permanece real durante la simulación del
motor. Activación desde la franja o Ajustes; estados de permiso, ubicación desactivada,
búsqueda y dato desactualizado. Una altitud ausente permanece como raya. Se prefiere
altura MSL si Android la entrega; la altura elipsoidal se identifica como GNSS.
Cabecera: indicador SIMULACIÓN centrado, como estado de texto. A la derecha, en orden,
hora de salida/puesta del sol con icono de sol en el horizonte de estilo iOS,
temperatura exterior y hora. En el mockup se ilustra una puesta de sol a las 18:24;
el evento y su hora reales requerirán fecha y ubicación al implementar la función.
La cabecera muestra siempre el siguiente evento: amanecer antes de la salida del
sol, atardecer durante el día y amanecer siguiente tras la puesta. Cambian juntos
icono y hora. La demostración usa 06:48/18:24; la agenda real sigue pendiente.

Refinamientos de la app: iconos inferiores de 14 dp; odómetro más alto y separador
superior corto; combustible verde solo hasta el nivel presente y continuación gris
fina para la parte vacía, con marcas de medios/cuartos y subdivisiones cerca de E.
Icono de surtidor centrado entre E y F; barra y fila inferior más juntas. En las
tarjetas, los valores quedan a la izquierda y las unidades a la derecha; etiquetas
a la izquierda. El propietario revirtió expresamente el agrupamiento a la derecha:
había solicitado una opinión, no aplicar ese cambio. La franja de contadores de
Inicio conserva su alineación.

La app ocupa el área horizontal disponible con modo inmersivo; las barras del sistema
reaparecen temporalmente mediante gesto. El cuerpo se distribuye según ancho y alto
reales. Se elimina el encuadre de proporción fija que dejaba márgenes laterales.
Los instrumentos circulares conservan escala uniforme y las zonas táctiles siguen
las mismas posiciones de la composición adaptable. Se respeta el recorte de cámara.

## Navegación principal acordada

| Sección | Contenido y función |
| --- | --- |
| Inicio | Vista de conducción: velocidad y RPM protagonistas, resumen de temperaturas y estado de conexión. |
| Tablero | Vista general completa de sensores; sus bloques abren detalles, incluido Motor y turbo y Autonomía. |
| Mapa | Contexto del recorrido y elevación, con interés particular en subidas y bajadas. Fuente de mapas, perfil de elevación y cálculo de pendiente por definir. |
| Diagnóstico | Catálogo completo de valores accesibles de la ECU, además de códigos y estados. Cotejar los 201 parámetros de Car Scanner; solo lectura, integración pendiente. |
| Ajustes | Adaptador Bluetooth, conexión, modo de prueba y preferencias que se incorporen. |

Orientación única horizontal. Las cinco tabs permanecen abajo en todas las vistas.
La tab seleccionada se indica con una línea verde y su icono/texto en verde, sobre
el mismo fondo de la barra. El contenido usa etiquetas de datos y acciones concretas;
se omiten explicaciones de interacción como «cada bloque abre su detalle».
Cada detalle tendrá retorno claro a su sección de origen.
Inicio muestra solo la marcha actual, incluida `R` para reversa. La flecha de
subida/bajada y la marcha objetivo aparecen simultáneamente con la marcha actual
únicamente cuando se recomienda cambiar. La marcha actual permanece centrada
horizontalmente respecto al semicírculo de RPM; el borde inferior del número queda
24 píxeles sobre el diámetro en el mockup, dentro del semicírculo. La marcha actual
usa 110 % del tamaño previo (también `R`); la sugerida y su flecha usan 75 % de sus
tamaños previos. Flecha y sugerencia forman un grupo compacto junto a la marcha
actual (centros a 32 y 60 píxeles de ella en el mockup).
Ambos se alinean verticalmente con el centro visual del glifo de la marcha actual,
calculado a partir de sus límites tipográficos, en lugar de compartir línea inferior.
Al recomendar bajar marcha, objetivo y flecha aparecen a la izquierda; al recomendar subir, a la
derecha. La marcha actual conserva su posición. `estados-marcha.png` muestra
los ejemplos normal, subir, bajar y reversa. Se eliminaron los rótulos «actual-estimada» y
«recomendada». La procedencia estimada de una marcha calculada se documentará en
Ajustes, sin añadir rótulos explicativos al cluster. Obtener la marcha de la T5 y
validar la lógica de recomendación siguen pendientes. Todos los datos del mockup
son ilustrativos.

## Tablero general y detalles

Actualización del propietario: la T5 tiene sensor de temperatura de aceite en el
cárter y Car Scanner lee ese dato. El mockup muestra temperatura de aceite con valor
simulado (92 °C) y su indicador; su disponibilidad en el vehículo queda confirmada
por el propietario. La integración de esa lectura en Cheeky Van App sigue pendiente.

Todas las tarjetas de mediciones del tablero usan valor, unidad, barra e indicador
de posición calculado a partir del valor y la escala. Refrigerante y aceite muestran
bandas térmicas de colores. Carga y acelerador usan barra verde de 0–100 %, sin
interpretar un porcentaje alto como avería. Presión y consumo usan escala verde.
Los datos ausentes se dibujan como raya y pista neutral, sin indicador de valor.
Trips, odómetro y horas conservan el formato de contador acumulativo.

La etiqueta es «Voltaje de batería». Su barra representa el sistema de 12 V en marcha,
con zona normal ilustrativa 13.7–14.7 V según [Interstate Batteries](https://www.interstatebatteries.com/support/faqs).
El estado de carga en reposo tiene otra interpretación: [VARTA](https://www.varta-automotive.com/en-gb/knowledge/articles/article-details/correct-assessment-of-battery-test-results)
indica 12.8 V para una batería cargada y recarga por debajo de 12.4 V en circuito
abierto. El diseño deberá distinguir motor parado/en marcha al implementar avisos;
el voltaje mostrado procederá de la lectura identificada del vehículo, sin presentar
una medición del adaptador como si fuera una medición directa en bornes.

Inicio adopta la propuesta visual externa aportada por el propietario, con el cluster
del Yaris como referencia: velocímetro central dominante y alas laterales integradas.
El ala izquierda contiene arco de RPM 0–6, marcha/flecha y odómetro bajo la barra;
se retiró la cifra de RPM, que se lee del arco. El ala derecha incluye refrigerante
y aceite encima de autonomía y combustible. Hora y temperatura exterior permanecen
en cabecera. La franja inferior muestra Trip A, Trip B, consumo y horas de motor;
coordenadas, altitud y tabs inferiores se conservan. Todos los arcos de velocidad
usan la misma conversión de valor/escala, incluidos los tramos iluminados.

La foto del tablero físico aportada el 2026-11-17 es referencia para las escalas:
cuentarrevoluciones de 0 a 6 (×1 000 rpm), con tramo rojo visible de 5 a 6, y
velocímetro de 0 a 240 km/h. Inicio centra el velocímetro en el ancho completo.
Tablero baja las cuatro tarjetas centrales y las alinea en una cuadrícula coherente
con las cinco tarjetas inferiores. Se eliminó «Lecturas del motor».

Inicio y Tablero incluyen hora, temperatura exterior, Trip A, Trip B, kilómetros
totales, horas de motor, coordenadas y altitud. Sus valores son ilustrativos. El
odómetro real, las horas acumuladas del motor y la temperatura exterior requieren
identificar su fuente; los contadores registrados por la app se distinguirán de los
totales del vehículo. Los dos trips y sus reglas de reinicio se definirán al implementar.

El rojo de RPM reproduce la referencia física. La franja térmica muestra el diseño
verde/ámbar/rojo, sin fijar todavía umbrales numéricos de alerta. Validar los umbrales
de refrigerante, aceite, voltaje y turbo para el CAAC antes de implementarlos; velocidad,
carga y acelerador no determinan por sí solos peligro mecánico. Las alertas de
velocidad requieren un límite configurado o conocido de la vía. Referencia de
[Volkswagen sobre el aviso rojo de refrigeración](https://www.volkswagen-vans.co.uk/en/owners-and-drivers/about-my-vehicle/warning-lights/engine-cooling-system-temperature.html).

Velocidad y RPM con instrumentos circulares; bloques compactos para refrigerante,
aceite, voltaje, carga, presión del múltiple, posición del acelerador, consumo
instantáneo, datos de mezcla/corrección de combustible y combustible restante.
Disponibilidad e interpretación se verificarán para nuestra T5 diésel. El catálogo
de Car Scanner guía la selección; una referencia visual no establece compatibilidad.
El voltaje deberá identificar su fuente de lectura del sistema eléctrico al implementarse.

Combustible restante → Autonomía: combustible disponible, alcance estimado, consumo
y su evolución. Las estimaciones deben identificar su base de cálculo. Registro
manual de cargas, reserva y reglas de alcance siguen pendientes de definición.
Presión del múltiple/turbo → Motor y turbo: presiones, temperaturas, caudal y demás
lecturas verificadas. Refrigerante/aceite → detalle de temperaturas y, cuando exista
registro, su evolución. No se simulará un historial como si fuera una medición real.

## Dirección y vistas

- Fondo negro/grafito, tipografía legible y acentos verdes. Indicadores de origen y
  conexión explícitos. Colores de alerta sujetos a límites verificados del vehículo.
- Inicio horizontal: velocímetro protagonista con ambos indicadores de marcha y RPM;
  sensores resumidos a los lados. Tablero: instrumentos separados de RPM y velocidad
  y bloques completos de sensores. Referencias principales: imágenes 1 y 3.
- Inicio horizontal: marcha actual y recomendada junto al velocímetro, con sensores
  a ambos lados y tabs inferiores persistentes.
- Motor y turbo: presión relativa protagonista, presión absoluta, temperatura de
  admisión, caudal y carga; lecturas no verificadas con estado explícito.
- Conexión: conservar selección Bluetooth, estado, desconexión y controles de prueba
  existentes dentro de Ajustes; estado resumido y acceso desde Inicio/Tablero.
- Mapas, diagnóstico y autonomía forman parte de la estructura aprobada. Su
  comportamiento real se entrega por incrementos con fuentes y cálculos definidos.

## Próxima entrega propuesta

Implementar los instrumentos y composiciones aprobados de Inicio y Tablero en Compose,
conectados a los estados y fuentes actuales. Comprobar lectura, disponibilidad,
navegación y adaptación horizontal al estéreo. El Xiaomi sirve como dispositivo
provisional de pruebas. Las escalas del boceto son
ilustrativas y no establecen límites seguros ni compatibilidad de sensores.
