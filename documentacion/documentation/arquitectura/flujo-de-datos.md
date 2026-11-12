# Flujo de datos

## Patrón cliente-servidor

El flujo de datos sigue el patrón cliente-servidor. El usuario interactúa con el frontend, que prepara una solicitud HTTP y la envía al backend. El backend recibe la solicitud mediante un controlador, procesa los datos y utiliza los DAO para consultar o modificar la base de datos.

Cuando la operación termina, el backend construye una respuesta HTTP, normalmente con datos o mensajes. El frontend interpreta esa respuesta y actualiza la interfaz.

## Secuencia general

1. El usuario realiza una acción en una pantalla, por ejemplo registrar un plato.
2. El frontend obtiene los valores del formulario y valida los campos.
3. JavaScript construye el cuerpo JSON y envía una solicitud HTTP al endpoint.
4. Spring Boot recibe la solicitud en el controlador REST.
5. Jackson convierte el JSON en un objeto del modelo.
6. El controlador delega la operación al DAO.
7. El DAO ejecuta una consulta o actualización SQL en MySQL.
8. El backend devuelve el resultado mediante una respuesta HTTP.
9. El frontend presenta un mensaje y actualiza la tabla o vista correspondiente.

## Ejemplo: registro de platos

El formulario de platos envía una petición POST a `/api/platos` con descripción, precio e ID del estado. El controlador recibe el objeto Plato mediante `@RequestBody` y llama a `PlatoDAO`. El DAO valida si existe una descripción repetida y, si no existe, realiza la inserción en la tabla `plato`.

En las pruebas realizadas se comprobó que el registro se completaba correctamente y que era necesario actualizar el listado para visualizar el nuevo registro en pantalla.

## Formato de intercambio

El frontend y el backend intercambian información mediante JSON. Los nombres de las propiedades deben coincidir con las propiedades que Jackson pueda enlazar al modelo Java.
