# Base de datos `bd_restaurante`

## Archivos

| Archivo | Para qué sirve |
|---|---|
| `esquema.sql` | Estructura actual de todas las tablas (sin datos). Referencia o para crear la base desde cero. |
| `migraciones/001…006` | Cambios de estructura que se aplican **sobre una base existente**. |

## Si ya tienes la base de antes

Ejecuta las migraciones **una sola vez y en orden** (phpMyAdmin → `bd_restaurante` → pestaña SQL, o por consola):

1. `001_correcciones_estructura.sql` – utf8mb4, documento del cliente, categoría del plato, unidad y categoría de insumo, llaves primarias.
2. `002_estados.sql` – normaliza estados y evita repetidos.
3. `003_pedidos.sql` – fecha y hora del pedido, estado de cada plato ligado a `estado`.
4. `004_recibos_egresos.sql` – forma de pago en el recibo, cliente "Consumidor final", llave y relaciones de egresos.
5. `005_pqrs.sql` – textos de PQRS, origen, fecha y usuario de la respuesta.
6. `006_cierre_caja.sql` – tabla de cierres de caja.

Antes de empezar, haz una copia de seguridad:

```
C:\xampp\mysql\bin\mysqldump.exe -u root bd_restaurante --result-file=respaldo.sql
```

Por consola, cada migración se ejecuta así (usa `--default-character-set=utf8mb4` para que las tildes queden bien):

```
C:\xampp\mysql\bin\mysql.exe -u root --default-character-set=utf8mb4 bd_restaurante < database\migraciones\001_correcciones_estructura.sql
```

## Contraseñas de usuarios

Las contraseñas se guardan cifradas con BCrypt. Las que estaban en texto plano siguen funcionando y se cifran solas la primera vez que el usuario inicia sesión.

## Estados que usa el sistema

Los códigos de estado del flujo (pedido creado, plato en preparación, PQRS pendiente, etc.) están en `src/main/resources/application.properties`, en las propiedades `app.estados.*`. Si tu tabla `estado` tiene otros códigos, ajústalos ahí.
