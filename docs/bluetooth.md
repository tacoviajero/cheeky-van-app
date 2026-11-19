# Primera conexión Bluetooth

El botón Bluetooth solicita CONNECT en Android 12+ y lista dispositivos Classic ya
emparejados. No realiza descubrimiento. Probar adaptador usa un transporte ELM simulado
con el mismo coordinador y parser; su indicador PRUEBA identifica los datos ficticios.

La sesión RFCOMM/SPP ejecuta exclusivamente ATZ, ATE0, ATL0, ATS0, ATH0, ATSP0,
0100 y ATDPN; después consulta solo los PIDs compatibles entre 010C (RPM), 010D
(velocidad), 0105 (refrigerante) y 0104 (carga). Las demás métricas quedan sin verificar.
El socket conecta con límite de 12 segundos; cada respuesta tiene 8 segundos y un
máximo de 4096 bytes ASCII. Los datos malformados se distinguen de NO DATA y cero.
Las respuestas idénticas de varias ECU se aceptan; las contradictorias se rechazan.

Desconectar, cambiar de fuente, perder conexión o salir de la actividad limpia las
lecturas y cierra la sesión. Regresar requiere conexión explícita. Simular pérdida
permite comprobar ese estado sin vehículo. La cadencia real depende del adaptador;
el intervalo entre rondas es de un segundo y todavía no se ha medido en la T5.

Pendiente de hardware: permiso/selección real, enlace RFCOMM, firmware ELM, protocolo
detectado y valores de los cuatro PIDs en nuestra T5. El catálogo de 201 parámetros
de Car Scanner ayudará a ampliar sensores, pero no bloquea esta primera conexión.

Referencias: [permisos Android](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions),
[RFCOMM](https://developer.android.com/develop/connectivity/bluetooth/connect-bluetooth-devices),
[ELM327](https://www.elmelectronics.com/wp-content/uploads/2016/07/ELM327DS.pdf).
