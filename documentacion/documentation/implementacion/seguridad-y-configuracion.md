# Seguridad y configuración

## Parámetros de conexión

La configuración de conexión a MySQL se gestiona desde `application.properties` o mediante variables de entorno. En la documentación pública no se deben incluir contraseñas, tokens, claves privadas ni datos personales.

## Buenas prácticas

* Mantener las credenciales fuera del repositorio público.
* Utilizar variables de entorno o configuración local protegida para los secretos.
* No publicar capturas que expongan usuarios o contraseñas de conexión.
* Validar los datos recibidos antes de ejecutar operaciones de persistencia.
* Utilizar consultas parametrizadas en el acceso a datos.
* Revisar que los mensajes de error no revelen información sensible.

## Validación observada

En el módulo de platos el frontend verifica la descripción, el precio y el ID del estado. El DAO también valida si ya existe un registro con la misma descripción antes de insertar o actualizar.

## Consideraciones

Esta página documenta prácticas y validaciones identificadas, pero no constituye una auditoría de seguridad. Los permisos, autenticación, autorización y protección de todos los endpoints deben revisarse en la implementación completa.
