---
description: Rutas REST y ejemplos verificados del módulo de platos.
icon: book-open
---

# API REST y endpoints

## Comunicación HTTP

El backend expone recursos mediante una API REST. En el módulo de platos revisado se identifican operaciones de consulta, registro y actualización.

## Endpoints verificados: platos

| Método | Ruta               | Descripción                                |
| ------ | ------------------ | ------------------------------------------ |
| GET    | `/api/platos`      | Lista los platos registrados.              |
| POST   | `/api/platos`      | Registra un plato.                         |
| PUT    | `/api/platos/{id}` | Actualiza el plato identificado por su ID. |

## Ejemplo de registro

```http
POST /api/platos
Content-Type: application/json
```

Cuerpo:

```json
{
  "descripcion": "Lechona",
  "precio": 15000,
  "idEstado": 6
}
```

La respuesta satisfactoria del controlador es HTTP 201 (Created) con un mensaje de registro. Si el DAO no logra registrar el plato, el controlador devuelve una respuesta HTTP 400.

## Validaciones observadas

* La descripción no debe estar repetida según la comparación realizada por el DAO.
* El precio debe ser positivo según la validación del frontend.
* El identificador del estado debe ser un entero positivo en el formulario.

## Otros endpoints

El diagrama de GitDiagram identifica áreas para pedidos, detalles, inventario, usuarios, egresos, clientes, PQRS y recibos de caja. Las rutas exactas de esos recursos deben extraerse de sus controladores antes de publicarse; no se inventan en esta referencia.
