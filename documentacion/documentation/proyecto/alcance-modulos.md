---
description: Áreas funcionales identificadas en el sistema.
icon: compass
---

# Alcance y módulos

## Áreas funcionales identificadas

El proyecto contempla módulos para apoyar diferentes procesos del restaurante. La siguiente relación se basa en el diagrama GitDiagram y en los archivos revisados durante el desarrollo.

| Área                 | Descripción general                                        |
| -------------------- | ---------------------------------------------------------- |
| Platos y menú        | Administración de la información básica de los platos.     |
| Pedidos              | Registro y consulta de pedidos y sus detalles.             |
| Inventario e insumos | Recursos asociados al control de insumos.                  |
| Usuarios             | Administración de usuarios del sistema.                    |
| Clientes             | Gestión de información de clientes.                        |
| Egresos              | Registro de conceptos y movimientos de egreso.             |
| Recibos de caja      | Recursos para registrar recibos de caja.                   |
| PQRS                 | Gestión de peticiones, quejas, reclamos y sugerencias.     |
| Referencias          | Catálogos y datos auxiliares requeridos por otros módulos. |

## Nivel de verificación

El módulo de platos cuenta con operaciones confirmadas en el backend revisado: consulta, registro y actualización. Los demás módulos aparecen en la arquitectura y deben verificarse individualmente en sus controladores, DAO, pantallas y tablas antes de describir sus operaciones exactas.

## Criterio de documentación

Cada módulo debe documentarse con su objetivo, actores, campos, validaciones, endpoints, DAO, tablas relacionadas y casos de prueba. Esta estructura permite mantener la documentación alineada con la implementación real.
