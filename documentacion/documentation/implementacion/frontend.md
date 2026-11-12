---
description: Interfaz web, validaciones y comunicación HTTP con el backend.
icon: graduation-cap
---

# Frontend

## Responsabilidad

El frontend es la parte del sistema con la que interactúa el usuario. Presenta formularios y listados, captura los datos, aplica validaciones de entrada y envía solicitudes al backend.

## Comunicación con el backend

En el módulo de platos revisado, el archivo `platos.js` utiliza la ruta relativa `/api/platos`. La petición de registro envía un cuerpo JSON con los campos `descripcion`, `precio` e `idEstado`.

Ejemplo del cuerpo enviado:

```json
{
  "descripcion": "Lechona",
  "precio": 15000,
  "idEstado": 6
}
```

El frontend utiliza solicitudes HTTP y procesa respuestas del backend. En el desarrollo se comprobó el registro de un plato; para visualizar el nuevo registro fue necesario actualizar el listado en la interfaz.

## Validaciones del formulario de platos

* La descripción no debe estar vacía.
* El precio debe ser numérico y mayor que cero.
* El identificador del estado debe ser un entero positivo.
* Los valores se convierten a número antes de construir el cuerpo JSON.

## Respuesta del servidor

El frontend interpreta el código HTTP y el mensaje devuelto por el backend. Cuando el registro se realiza correctamente, el servidor responde con estado 201. Cuando el DAO informa que no pudo registrar, el controlador devuelve un error 400.

## Pendiente de completar

Para documentar el frontend completo deben incorporarse las tecnologías exactas, la estructura definitiva de carpetas, las rutas de navegación y capturas de los demás módulos. No se atribuye un framework que no se haya verificado.
