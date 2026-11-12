---
description: Persistencia relacional en MySQL y ejemplo de la tabla de platos.
icon: gear
tags:
  - tag: beta
    primary: true
---

# Base de datos MySQL

## Motor de almacenamiento

El proyecto utiliza MySQL como base de datos relacional. El backend se conecta mediante la configuración de la aplicación y realiza operaciones SQL a través de la capa DAO.

## Ejemplo verificado: tabla plato

El DAO de platos consulta los siguientes campos de la tabla `plato`:

| Campo             | Uso identificado                   |
| ----------------- | ---------------------------------- |
| `pla_id`          | Identificador del plato.           |
| `pla_descripcion` | Descripción del plato.             |
| `pla_precio`      | Precio del plato.                  |
| `est_id`          | Identificador del estado asociado. |

La consulta de listado obtiene estos campos. Las operaciones de inserción y actualización también utilizan descripción, precio y estado.

## Acceso a datos

La clase `PlatoDAO` usa `JdbcTemplate` para ejecutar consultas parametrizadas. La validación de descripción repetida se realiza antes de insertar o actualizar un plato.

## Seguridad y configuración

El archivo `application.properties` contiene parámetros de conexión. Las contraseñas, tokens y demás secretos deben mantenerse en variables de entorno o en una configuración privada y nunca incluirse en páginas públicas.

## Pendiente de completar

Para publicar el modelo de datos completo se debe adjuntar el diagrama entidad-relación o el script SQL definitivo. Esta página solo describe los campos que se han confirmado en el DAO revisado; no pretende ser el inventario completo de tablas.
