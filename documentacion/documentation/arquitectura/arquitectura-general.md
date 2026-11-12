---
description: Descripción de las capas que componen la arquitectura del sistema.
icon: book
---

# Arquitectura general

## Descripción

El sistema sigue una organización cliente-servidor. El frontend envía solicitudes HTTP al backend; el backend procesa los datos, ejecuta la lógica de la operación y accede a MySQL cuando necesita consultar o modificar información.

## Capas principales

### 1. Presentación (Frontend)

Contiene las pantallas, formularios, tablas y elementos con los que interactúa el usuario. Sus responsabilidades incluyen capturar información, validar los campos de entrada, enviar solicitudes HTTP y mostrar los resultados recibidos.

### 2. Controladores (Controller)

Reciben las solicitudes HTTP y definen las rutas disponibles. Los controladores convierten la entrada en parámetros o modelos, delegan el trabajo a los componentes de persistencia y devuelven respuestas HTTP.

### 3. Modelos (Model)

Representan las estructuras de datos utilizadas por la aplicación. Incluyen atributos y métodos de acceso. Los modelos se utilizan para transportar información entre las capas.

### 4. Acceso a datos (DAO)

Las clases DAO encapsulan las consultas y operaciones sobre la base de datos. En el backend revisado utilizan Spring JDBC mediante `JdbcTemplate`.

### 5. Persistencia (MySQL)

La base de datos relacional almacena los registros del sistema y permite consultarlos y actualizarlos mediante sentencias SQL ejecutadas desde los DAO.

## Ejemplo comprobado: platos

El controlador `PlatoController` está asociado a `/api/platos`. Delega las operaciones a `PlatoDAO`, que consulta la tabla `plato` y trabaja con los campos `pla_id`, `pla_descripcion`, `pla_precio` y `est_id`.

## Interpretación del diagrama GitDiagram

El diagrama agrupa componentes en gestión de pedidos, operaciones del restaurante y administración. En la parte inferior representa una capa de persistencia con DAO y una base de datos relacional.

La representación ayuda a entender las dependencias, pero los nombres y relaciones automáticos deben validarse con el código fuente. No debe asumirse que todos los componentes representados tienen el mismo nivel de implementación.
