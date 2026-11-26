# Informe de Pruebas

## Proyecto
Sistema de Gestión de Restaurante

## Rol - Maria Gutierrez (Tester)
Tester

## Objetivo
Documentar las pruebas realizadas sobre las funcionalidades del sistema y los resultados obtenidos.

## Entornos de prueba

### Backend
Validación de servicios y endpoints mediante solicitudes HTTP, verificando respuestas, datos y comportamiento de las operaciones.

### Frontend
Validación de la interfaz desde el navegador, verificando navegación, formularios, botones, mensajes y visualización de datos.

### Base de datos
Verificación de registros, consultas y persistencia de la información.

## Resultados
Los resultados se registrarán conforme se ejecuten los casos de prueba.

## Conclusiones
La evaluación del sistema se realizará a partir de los resultados de los casos de prueba, los errores encontrados y la verificación de las correcciones.


## PRUEBAS DE BACKEND

## BE-001 - Inicio del Backend

- **Tipo:** Prueba de humo (Smoke Test)
- **Herramienta:** Maven y Spring Boot
- **Comando ejecutado:** `mvn spring-boot:run`
- **Objetivo:** Verificar que el backend pueda compilar, inicializarse y comenzar a ejecutarse.
- **Resultado esperado:** La aplicación inicia correctamente y queda escuchando solicitudes.
- **Resultado obtenido:** Maven compiló el proyecto y Spring Boot inició correctamente. Tomcat quedó ejecutándose en el puerto 8080.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-001_Inicio_Backend.png`

## BE-002 - Consulta de platos

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** GET
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que el sistema permita consultar y obtener los platos registrados.
- **Resultado esperado:** Respuesta HTTP 200 OK y listado de platos en formato JSON.
- **Resultado obtenido:** El servidor respondió 200 OK y devolvió una lista de platos con sus identificadores, estados, descripciones y precios.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-002_Consulta_Platos_PASS.png`

## BE-003 - Registro de platos

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** POST
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que el sistema permita registrar nuevos platos.
- **Datos enviados:** ID de estado, descripción y precio del plato.
- **Resultado esperado:** El sistema registra el plato y responde con HTTP 201 Created.
- **Resultado obtenido:** El servidor respondió 201 Created con el mensaje "Plato registrado correctamente.".
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-003_Registro_Platos_PASS.png`

## BE-004 - Verificación del plato registrado

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** GET
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que el plato registrado previamente mediante POST quede almacenado y pueda recuperarse mediante una consulta.
- **Resultado esperado:** HTTP 200 OK y presencia del plato registrado en la respuesta JSON.
- **Resultado obtenido:** El servidor respondió 200 OK. Se encontró el plato "Plato Tester", con ID 5, estado 8 y precio de 17000.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-004_Verificar_Registro_PASS.png`

## BE-005 - Actualización de platos

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** PUT
- **Endpoint:** `http://localhost:8080/api/platos/5`
- **Objetivo:** Verificar que el sistema permita actualizar la información de un plato existente.
- **Resultado esperado:** HTTP 200 OK y mensaje de actualización exitosa.
- **Resultado obtenido:** El servidor respondió 200 OK con el mensaje "Plato actualizado correctamente.".
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-005_Actualizar_Plato_PASS.png`

## BE-006 - Verificación de actualización

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** GET
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que los cambios realizados mediante PUT se hayan almacenado correctamente.
- **Resultado esperado:** HTTP 200 OK y datos del plato con los valores actualizados.
- **Resultado obtenido:** Se encontró el plato con ID 5, descripción "Plato Tester Actualizado" y precio 20000.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-006_Verificar_Actualizacion_PASS.png`

## BE-007 - Validación de platos duplicados

- **Tipo:** Prueba funcional y de validación de API
- **Herramienta:** Postman
- **Método HTTP:** POST
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que el sistema impida registrar platos con descripciones que ya existen.
- **Resultado esperado:** Rechazo del registro duplicado y respuesta indicando que la descripción ya existe.
- **Resultado obtenido:** El servidor respondió 400 Bad Request y mostró el mensaje "No se pudo registrar el plato. Verifica si la descripción ya existe.".
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-007_Validacion_Duplicados_PASS.png`

## BE-008 - Validación de precio negativo

- **Tipo:** Prueba de validación de API
- **Herramienta:** Postman
- **Método HTTP:** POST
- **Endpoint:** http://localhost:8080/api/platos
- **Objetivo:** Verificar que el sistema rechace precios negativos al registrar platos.
- **Resultado esperado:** Rechazo del registro con precio negativo.
- **Resultado obtenido:** El servidor aceptó el registro y respondió 201 Created con el mensaje "Plato registrado correctamente.".
- **Estado:** FAIL
- **Observación:** El backend no está validando que el precio sea mayor o igual a cero.
- **Evidencia:** `Evidencias/Backend/BE-008_Precio_Negativo_FAIL.png`

## BE-009 - Consulta de endpoint inexistente

- **Tipo:** Prueba funcional de API
- **Herramienta:** Postman
- **Método HTTP:** GET
- **Endpoint:** http://localhost:8080/api/endpoint-inexistente
- **Objetivo:** Verificar el comportamiento del backend cuando se solicita una ruta que no existe.
- **Resultado esperado:** Respuesta HTTP 404 Not Found.
- **Resultado obtenido:** El servidor respondió 404 Not Found y devolvió un JSON indicando el error y la ruta solicitada.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Backend/BE-009_Endpoint_Inexistente_PASS.png`


## PRUEBAS DE BASE DE DATOS

## BD-001 - Verificación de estructura de la base de datos

- **Tipo:** Prueba de base de datos
- **Herramienta:** phpMyAdmin
- **Base de datos:** restaurante
- **Objetivo:** Verificar la existencia de la base de datos y sus tablas.
- **Resultado esperado:** Base de datos disponible y estructura de tablas creada correctamente.
- **Resultado obtenido:** Se confirmó la existencia de la base de datos restaurante, con 18 tablas de almacenamiento InnoDB.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Base_Datos/BD-001_Estructura_Base_Datos_PASS.png`

## BD-002 - Verificación de registros almacenados

- **Tipo:** Prueba de base de datos
- **Herramienta:** phpMyAdmin
- **Tabla:** plato
- **Objetivo:** Verificar que los registros creados y modificados durante las pruebas se encuentren almacenados en la base de datos.
- **Resultado esperado:** Los registros de prueba deben existir y presentar los valores enviados o actualizados.
- **Resultado obtenido:** Se visualizaron 6 registros en la tabla plato. Se confirmó la existencia de "Plato Tester Actualizado" con precio 20000 y "Plato Precio Negativo" con precio -5000.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Base_Datos/BD-002_Registros_Plato_PASS.png`

## BD-003 - Verificación de registro mediante consulta SQL

- **Tipo:** Prueba de base de datos
- **Herramienta:** phpMyAdmin / MySQL
- **Consulta ejecutada:** SELECT * FROM plato WHERE pla_descripcion = 'Plato Tester Actualizado';
- **Objetivo:** Verificar mediante una consulta SQL que el plato registrado y actualizado exista en la base de datos.
- **Resultado esperado:** La consulta devuelve el registro correspondiente con sus datos almacenados.
- **Resultado obtenido:** Se obtuvo 1 registro con ID 5, descripción "Plato Tester Actualizado", precio 20000 y estado 8.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Base_Datos/BD-003_Verificacion_Insercion_SQL_PASS.png`

## BD-004 - Verificación de actualización en MySQL

- **Tipo:** Prueba de base de datos
- **Herramienta:** phpMyAdmin / MySQL
- **Consulta ejecutada:** SELECT pla_id, pla_descripcion, pla_precio, est_id FROM plato WHERE pla_id = 5;
- **Objetivo:** Verificar que la actualización del plato se refleje directamente en la base de datos.
- **Resultado esperado:** El registro debe mostrar los valores modificados correctamente.
- **Resultado obtenido:** Se encontró el registro con ID 5, descripción "Plato Tester Actualizado", precio 20000 y estado 8.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Base_Datos/BD-004_Verificacion_Actualizacion_SQL_PASS.png`


## PRUEBAS DE FRONTEND

## FE-001 - Inicio y carga del sistema

- **Resultado:** La prueba se ejecutó nuevamente después del resultado inicial fallido.
- **Resultado obtenido:** El Dashboard cargó correctamente. Las tarjetas informativas, pedidos abiertos y platos más vendidos se visualizaron sin el error 404 detectado inicialmente.
- **Estado de la reprueba:** PASS
- **Evidencia:** `Evidencias/Frontend/FE-001_Inicio_Sistema_RETEST_PASS.png`

## FE-002 - Navegación entre módulos principales

- **Tipo:** Prueba funcional de interfaz
- **Herramienta:** Navegador web
- **Objetivo:** Verificar el acceso y la carga de los módulos principales del sistema.
- **Módulos evaluados:** Recibo de caja, Pedido, Comprobante de egreso, PQRS y Configuración.
- **Resultado esperado:** Los módulos deben abrir correctamente y mostrar sus respectivas interfaces.
- **Resultado obtenido:** Los cinco módulos principales cargaron correctamente y permitieron visualizar sus interfaces sin errores que impidieran la navegación.
- **Estado:** PASS
- **Evidencias:**
  - `Evidencias/Frontend/FE-002_Recibo_Caja_PASS.png`
  - `Evidencias/Frontend/FE-002_Pedido_PASS.png`
  - `Evidencias/Frontend/FE-002_Comprobante_Egreso_PASS.png`
  - `Evidencias/Frontend/FE-002_PQRS_PASS.png`
  - `Evidencias/Frontend/FE-002_Configuracion_PASS.png`

  ## FE-003 - Visualización de listados

- **Tipo:** Prueba funcional de interfaz
- **Herramienta:** Navegador web
- **Objetivo:** Verificar la visualización y organización de los registros en los listados de los módulos principales.
- **Módulos evaluados:** Recibo de caja, Pedido, Comprobante de egreso y PQRS.
- **Resultado esperado:** Los listados deben visualizarse correctamente, mostrando sus registros y columnas de forma organizada.
- **Resultado obtenido:** Se verificó visualmente la presentación de los listados de los módulos evaluados.
- **Estado:** Se determina según los resultados observados.
- **Evidencias:** Capturas de los listados evaluados.

## FE-004 - Registro de información

- **Tipo:** Prueba funcional de interfaz
- **Herramienta:** Navegador web
- **Módulo evaluado:** Pedido - Platos
- **Objetivo:** Verificar que el sistema permita acceder al formulario de registro de platos y visualizar los campos necesarios para ingresar información.
- **Resultado esperado:** El formulario de registro debe abrir correctamente y mostrar los campos requeridos para ingresar los datos del plato.
- **Resultado obtenido:** El formulario "Registrar plato" se visualizó correctamente, mostrando los campos descripción del plato, categoría, precio y estado, así como los botones para guardar y cancelar el registro.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Frontend/FE-004_Formulario_Registro_PASS.png`

## FE-005 - Registro de información desde el Frontend

- **Tipo:** Prueba funcional de interfaz
- **Herramienta:** Navegador web
- **Módulo evaluado:** Pedido - Platos
- **Objetivo:** Verificar que el sistema permita registrar un nuevo plato desde la interfaz y almacenar la información ingresada.
- **Resultado esperado:** El plato debe registrarse correctamente, mostrar una confirmación y aparecer en el listado de platos.
- **Resultado obtenido:** Se realizó el registro de un nuevo plato desde el formulario del módulo Platos y se verificó su incorporación al listado.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Frontend/FE-005_Registro_Plato_PASS.png`

## FE-006 - Validaciones de formularios

- **Tipo:** Prueba funcional de interfaz
- **Herramienta:** Navegador web
- **Módulo evaluado:** Pedido - Platos
- **Objetivo:** Verificar que el formulario valide los campos obligatorios y los valores ingresados.
- **Resultado esperado:** El sistema debe impedir registros incompletos o con datos inválidos y mostrar mensajes de validación.
- **Resultado obtenido:** Se realizaron pruebas con campos vacíos y valores inválidos para comprobar las validaciones del formulario.
- **Estado:** PASS
- **Evidencia:** `Evidencias/Frontend/FE-006_Validaciones_Formulario.png`