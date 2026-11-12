---
description: Resumen de los módulos operativos y administrativos identificados.
icon: lock
---

# Módulos funcionales

El sistema se organiza en áreas operativas y administrativas. Esta clasificación combina los nombres identificados en el diagrama GitDiagram y los archivos revisados durante el desarrollo.

## Operación del restaurante

### Platos y menú

Permite administrar la información básica de los platos. El módulo revisado incluye descripción, precio e identificador del estado. Se comprobó la consulta de registros, el registro de un nuevo plato y la actualización de información.

### Pedidos y detalles

El diagrama identifica componentes separados para pedidos y detalles de pedidos, así como un DAO de pedidos denominado `PedidoDAO.java`. Las rutas y reglas de negocio específicas deben verificarse en los controladores y DAO correspondientes.

### Inventario e insumos

El diagrama identifica un recurso de inventario y un DAO asociado a insumos, representado como `InsumoDAO.java`. La lista definitiva de campos y operaciones debe comprobarse en el código y en el esquema SQL.

## Administración

### Usuarios

El diagrama identifica recursos y un DAO de usuarios, asociado al nombre `UsuarioDAO.java`.

### Clientes

El diagrama identifica recursos para la gestión de clientes. Las operaciones específicas deben confirmarse en el código de ese módulo.

### Egresos

Se identifica un área para gastos o egresos y un DAO asociado. Deben confirmarse los campos y las operaciones disponibles en la implementación.

### Recibos de caja

El diagrama identifica recursos para recibos de caja. Su flujo funcional debe contrastarse con los controladores y la estructura de datos.

### PQRS

Se identifica un área para peticiones, quejas, reclamos y sugerencias. Deben documentarse los tipos, estados y reglas definidos en la versión final del módulo.

### Recursos de referencia

Agrupan catálogos o datos auxiliares requeridos por otras funcionalidades.

## Ficha recomendada para cada módulo

La documentación detallada de cada módulo debe incluir propósito, actores, campos, validaciones, operaciones disponibles, endpoint, DAO relacionado, tablas utilizadas y evidencia de pruebas. Las rutas aún no verificadas se dejan pendientes para evitar publicar información incorrecta.
