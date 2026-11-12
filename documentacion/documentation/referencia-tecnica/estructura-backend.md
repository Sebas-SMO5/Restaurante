---
description: Paquetes, clases y archivos de configuración del backend.
icon: sliders
---

# Estructura del backend

## Organización por paquetes

La estructura revisada del backend sigue una separación por responsabilidades:

```
com.restaurante.backend_restaurante
├── Controller
├── DAO
└── Model
```

### Controller

Contiene los controladores REST. Por ejemplo, `PlatoController.java` declara la ruta `/api/platos` y delega las operaciones en `PlatoDAO`.

### DAO

Contiene clases responsables de acceder a la base de datos. Por ejemplo, `PlatoDAO.java` utiliza `JdbcTemplate` para ejecutar consultas SQL sobre la tabla `plato`.

### Model

Contiene las clases que representan los datos. Por ejemplo, `Plato.java` define los campos del plato y sus métodos de acceso.

## Archivos de configuración

* `pom.xml`: define las dependencias y configuración de compilación de Maven.
* `src/main/resources/application.properties`: contiene parámetros de configuración de la aplicación y conexión a la base de datos.

## Convenciones

Mantener los controladores enfocados en HTTP, los DAO en persistencia y los modelos en representación de datos. Las credenciales deben configurarse de forma privada y no incluirse en commits públicos.
