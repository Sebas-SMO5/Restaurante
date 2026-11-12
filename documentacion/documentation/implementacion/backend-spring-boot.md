---
description: Implementación del backend Java con Spring Boot y su organización por capas.
icon: globe
---

# Backend con Spring Boot

## Descripción

El backend está desarrollado en Java con Spring Boot. Recibe solicitudes del frontend, procesa los datos, ejecuta las operaciones correspondientes y se comunica con MySQL.

## Organización por paquetes

| Paquete      | Responsabilidad                                          |
| ------------ | -------------------------------------------------------- |
| `Controller` | Exponer operaciones HTTP y coordinar las solicitudes.    |
| `Model`      | Representar los datos usados por la aplicación.          |
| `DAO`        | Ejecutar consultas y operaciones sobre la base de datos. |

## Ejemplo: módulo de platos

El controlador `PlatoController` utiliza la ruta base `/api/platos`. Expone una operación GET para listar platos, POST para registrar y PUT con identificador para actualizar.

El controlador delega las operaciones a `PlatoDAO`. Este DAO usa `JdbcTemplate` y ejecuta consultas sobre la tabla `plato`.

## Conversión JSON

Las solicitudes POST y PUT reciben información JSON mediante `@RequestBody`. En el modelo `Plato` se usa `@JsonCreator` con `@JsonProperty` para indicar explícitamente cómo construir el objeto a partir de las propiedades recibidas.

## Configuración

La conexión a MySQL se define en `application.properties`. Las credenciales y otros secretos no deben publicarse ni copiarse en esta documentación.

## Ejecución local

Desde la carpeta raíz del backend, en PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Para compilar:

```powershell
.\mvnw.cmd clean compile
```

En el entorno revisado, la aplicación se ejecutó en el puerto 8080 y se obtuvo una compilación satisfactoria.
