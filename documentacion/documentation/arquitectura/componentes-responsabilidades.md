---
description: Responsabilidades del frontend, controladores, modelos, DAO y base de datos.
icon: sitemap
---

# Componentes y responsabilidades

## Frontend

Es la capa de presentación. Captura los datos de los formularios, realiza validaciones de entrada, envía solicitudes HTTP y muestra la información obtenida desde el backend.

## Controladores

Los controladores reciben las solicitudes HTTP y actúan como punto de entrada a las funcionalidades del backend. En Spring Boot se definen mediante anotaciones como `@RestController` y se asocian a rutas mediante `@RequestMapping`.

## Modelos

Los modelos representan los datos de la aplicación. Por ejemplo, `Plato` contiene los atributos `idPlato`, `idEstado`, `descripcion` y `precio`, además de constructores, getters y setters.

En el registro de platos se incorporaron `@JsonCreator` y `@JsonProperty` para indicar explícitamente a Jackson cómo construir el objeto a partir del JSON recibido.

## DAO

Los DAO realizan las operaciones de acceso a datos. El ejemplo revisado, `PlatoDAO`, utiliza `JdbcTemplate` para consultar registros, validar descripciones repetidas y ejecutar inserciones y actualizaciones.

## Base de datos

MySQL conserva los registros persistentes del sistema. El acceso se realiza desde los DAO y la configuración de conexión se mantiene en las propiedades de la aplicación.

## Flujo de responsabilidad

La interfaz no ejecuta directamente sentencias SQL. Envía una solicitud al controlador, el controlador delega la operación al DAO y el DAO consulta o actualiza la base de datos. La respuesta regresa al cliente para presentar el resultado.
