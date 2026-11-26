---
description: Navegación, ejemplos prácticos, mensajes de error, guía de referencia, preguntas frecuentes y ayudas.
---

# 6. Anexos

## 6.1 Navegación por la aplicación

La tabla resume cómo se pasa de una pantalla a otra, ordenada por módulos. Desde cualquier pantalla se puede ir a otra con el menú principal.

| Módulo | Pantalla inicial | Acción del usuario | Pantalla de salida |
| --- | --- | --- | --- |
| Acceso | Inicio de sesión | Escribir usuario y contraseña y pulsar **Ingresar** | Inicio |
| Acceso | Inicio de sesión | Pulsar **Radica tu PQRS aquí** | Página pública de PQRS |
| Acceso | Cualquier pantalla | Pulsar **Cerrar sesión** o 60 minutos sin uso | Inicio de sesión |
| Acceso | Cualquier pantalla | Pulsar **Mi contraseña** | Ventana Cambiar mi contraseña |
| Inicio | Inicio | Pulsar **+ Nuevo pedido** | Pedidos, formulario abierto |
| Inicio | Inicio | Pulsar **Ver cocina** | Cocina / Comandas |
| Inicio | Inicio | Pulsar **Cobrar** | Recibos de caja |
| Inicio | Inicio | Pulsar **Ver materias primas** | Materias primas |
| Pedido | Pedidos | Pulsar **+ Nuevo pedido** | Formulario Nuevo pedido |
| Pedido | Formulario Nuevo pedido | Pulsar **Enviar a cocina** | Pedidos (el pedido aparece en Cocina) |
| Pedido | Pedidos | Pulsar **Ver** en un pedido | Detalle del pedido |
| Pedido | Detalle del pedido | Pulsar **Finalizar pedido** | Pedidos (el pedido pasa a Recibos de caja) |
| Pedido | Cocina / Comandas | Pulsar **Listo** en un plato | Cocina / Comandas (el plato sale de la lista) |
| Recibo de caja | Recibos de caja | Pulsar **Cobrar** en un pedido | Formulario Cobrar pedido |
| Recibo de caja | Formulario Cobrar pedido | Pulsar **Generar recibo** | Recibo de caja para imprimir |
| Recibo de caja | Recibos de caja | Pulsar **Ver** en un recibo | Recibo con **Imprimir** y **Anular** |
| Recibo de caja | Cierre de caja | Pulsar **Registrar cierre** | Cierre de caja con el cierre guardado |
| Comprobante de egreso | Comprobantes de egreso | Pulsar **+ Nuevo egreso** | Formulario Registrar comprobante de egreso |
| PQRS | Bandeja de PQRS | Pulsar **Responder** | Ventana con la PQRS y el campo de respuesta |
| PQRS | Bandeja de PQRS | Pulsar **Ver página pública** | Página pública de PQRS |
| Catálogos | Cualquier catálogo | Pulsar **+ Nuevo** o **Editar** | Formulario del registro |

## 6.2 Ejemplos prácticos

**Ejemplo 1. Atender la mesa 8 de principio a fin** (perfil: auxiliar)

1. El mesero abre **Pedido** → **Pedidos**, pulsa **+ Nuevo pedido** y escribe la mesa `8`.
2. Agrega 1 Chuleta de cerdo, 2 Tajadas de maduro y 1 Limonada natural, y pulsa **Enviar a cocina**.
3. En **Cocina / Comandas**, el cocinero pulsa **Listo** en cada plato cuando lo termina.
4. Cuando la mesa termina, el mesero abre el pedido con **Ver** y pulsa **Finalizar pedido**.
5. En **Recibos de caja**, el cajero pulsa **Cobrar** en el pedido de la mesa 8 (total $ 40.000), deja **Consumidor final**, elige **Efectivo**, escribe que recibió $ 50.000 y ve el cambio de $ 10.000.
6. Pulsa **Generar recibo** e **Imprimir** para entregarle el recibo al cliente.

**Ejemplo 2. Cerrar la caja del día** (perfil: administrador o auxiliar)

1. En **Recibos de caja**, revisa que **Pedidos por cobrar** esté vacío. Si no, cobra lo que falte.
2. Abre **Cierre de caja**. Supongamos que dice que debe haber $ 23.000 en efectivo.
3. Cuenta el dinero. Si hay $ 23.000, escríbelo y pulsa **Registrar cierre**.
4. Si hay $ 20.000, escríbelo y explica en **Observación** el faltante de $ 3.000, por ejemplo «se pagó un domicilio con efectivo de la caja». Luego pulsa **Registrar cierre**.
5. Pulsa **Imprimir** para guardar el resumen del día.

## 6.3 Mensajes de error

El sistema explica en la misma pantalla por qué no deja hacer algo. Estos son los mensajes que el usuario puede ver, ordenados por módulo.

| Código | Error (mensaje en pantalla) | Detección | Causas y/o soluciones |
| --- | --- | --- | --- |
| ACC-01 | Usuario o contraseña incorrectos. | Inicio de sesión | Revisa mayúsculas y que el teclado no tenga Bloq Mayús. Si la olvidaste, pide al administrador una nueva |
| ACC-02 | Tu usuario está inactivo. Comunícate con el administrador. | Inicio de sesión | El administrador inactivó tu usuario. Solo él puede activarlo |
| ACC-03 | El rol asignado a tu usuario está inactivo. | Inicio de sesión | El rol fue inactivado. El administrador debe activarlo en **Rol** |
| ACC-04 | Debes iniciar sesión para continuar. | Cualquier pantalla | La sesión se cerró tras 60 minutos sin uso. Vuelve a entrar |
| ACC-05 | Tu rol solo permite consultar esta información. | Catálogos y egresos | Esa tarea es del administrador. Pídesela |
| ACC-06 | La contraseña actual no es correcta. | Mi contraseña | Escribe tu contraseña de hoy, no la nueva |
| ACC-07 | La nueva contraseña debe tener entre 6 y 72 caracteres. | Mi contraseña y Usuario | Usa una contraseña más larga |
| PED-01 | La mesa X ya tiene el pedido #N abierto. | Pedidos | Abre ese pedido con **Ver** y usa **+ Agregar platos** |
| PED-02 | Uno de los platos no está disponible para la venta. | Pedidos | El plato está Agotado o Inactivo. El administrador revisa su estado en **Platos** |
| PED-03 | La cantidad de cada plato debe estar entre 1 y 50. | Pedidos | Corrige la cantidad. Para más de 50, agrégalo en dos líneas |
| PED-04 | La mesa admite máximo 4 caracteres (letras o números). | Pedidos | Escribe la mesa sin espacios ni símbolos, por ejemplo `T2` |
| PED-05 | Aún hay N plato(s) en preparación. | Detalle del pedido | Espera a que cocina pulse **Listo**, o cancela el plato |
| PED-06 | El pedido no tiene platos entregados. | Detalle del pedido | Todos los platos se cancelaron. Cancela el pedido en vez de finalizarlo |
| PED-07 | El pedido ya está cerrado y no se puede modificar. | Detalle del pedido | Un pedido finalizado o cancelado no cambia. Crea uno nuevo |
| CAJ-01 | Solo se pueden cobrar pedidos finalizados. | Recibos de caja | Finaliza el pedido primero en **Pedidos** |
| CAJ-02 | El pedido #N ya se cobró con el recibo #M. | Recibos de caja | No cobres dos veces. Si el recibo está mal, el administrador lo anula |
| CAJ-03 | Solo un administrador puede anular recibos de caja. | Recibos de caja | Pídele al administrador que lo anule |
| CAJ-04 | Escribe el motivo de la anulación (mínimo 5 caracteres). | Recibos y egresos | Explica por qué se anula |
| CAJ-05 | La identificación del tercero debe tener entre 4 y 15 caracteres. | Egresos | Escribe el NIT o cédula sin puntos |
| CAJ-06 | El valor del egreso debe ser mayor que cero. | Egresos | Revisa el valor |
| CAJ-07 | Hay una diferencia en el efectivo. Escribe una observación. | Cierre de caja | Explica el sobrante o faltante en **Observación** |
| CAJ-08 | La caja del AAAA-MM-DD ya fue cerrada. | Recibos, egresos y cierre | Ese día ya no admite cobros, egresos ni anulaciones |
| PQR-01 | Describe la PQRS con al menos 15 caracteres. | PQRS | Escribe más detalle de lo que pasó |
| PQR-02 | No encontramos una PQRS con ese número y correo. | Página pública | Revisa el número de radicado y usa el mismo correo con que se registró |
| PQR-03 | Has enviado varias PQRS en poco tiempo. | Página pública | Límite de 5 por hora. Espera una hora |
| CAT-01 | Ya existe un cliente con ese tipo y número de documento. | Cliente | El cliente ya está registrado. Búscalo en el listado |
| CAT-02 | Ya existe otro usuario con ese login. | Usuario | Elige otro nombre de usuario |
| SIS-01 | La pantalla no carga datos o muestra un error al abrir | Cualquier pantalla | MySQL o la aplicación están apagados. Enciéndelos en el computador del sistema |

## 6.4 Guía de referencia

**Índice alfabético**

| Término | Sección |
| --- | --- |
| Activar / Inactivar | 3.4, 5.8 |
| Agregar platos a un pedido | 5.2 |
| Anular recibo | 5.4 |
| Anular egreso | 5.5 |
| Cambio (vueltas) | 5.4 |
| Cancelar pedido o plato | 5.2 |
| Categorías | 5.8 |
| Cerrar sesión | 3.1 |
| Cierre de caja | 5.6, 6.2 |
| Clientes | 5.8 |
| Cobrar un pedido | 5.4, 6.2 |
| Cocina / Comandas | 5.3 |
| Consumidor final | 5.4 |
| Contraseña | 3.1, 5.9 |
| Egresos | 5.5 |
| Entrar al sistema | 3.1 |
| Estados | 5.8 |
| Finalizar pedido | 5.2 |
| Inicio (resumen) | 5.1 |
| Materias primas | 5.8 |
| Menú principal | 3.3 |
| Mensajes de error | 6.3 |
| Modo consulta | 4 |
| Perfiles y permisos | 4 |
| Platos | 5.8 |
| PQRS | 5.7 |
| Página pública de PQRS | 5.7 |
| Usuarios y roles | 5.9 |

**Índice de figuras**

| Figura | Contenido | Sección |
| --- | --- | --- |
| 1 | Recorrido de un pedido durante el día | 2 |
| 2 | Pantalla de inicio de sesión | 3.1 |
| 3 | Pantalla principal con cabecera, menú y área de trabajo | 3.2 |
| 4 | Platos vista por un auxiliar, en modo consulta | 4 |
| 5 | Listado de pedidos abiertos | 5.2 |
| 6 | Formulario de nuevo pedido | 5.2 |
| 7 | Detalle de un pedido | 5.2 |
| 8 | Cocina con las comandas por mesa | 5.3 |
| 9 | Recibos de caja | 5.4 |
| 10 | Cobro de un pedido | 5.4 |
| 11 | Comprobantes de egreso | 5.5 |
| 12 | Cierre de caja | 5.6 |
| 13 | Bandeja de PQRS | 5.7 |
| 14 | Página pública de PQRS | 5.7 |
| 15 | Gestión de clientes | 5.8 |
| 16 | Gestión de platos | 5.8 |
| 17 | Gestión de materias primas | 5.8 |
| 18 | Gestión de usuarios | 5.9 |
| 19 | Cambio de contraseña | 5.9 |

## 6.5 Preguntas frecuentes

**Acceso**

* **Olvidé mi contraseña, ¿qué hago?** Pídele al administrador que te asigne una nueva desde **Usuario** → **Editar**, y cámbiala al entrar con **Mi contraseña**.
* **¿Por qué me sacó del sistema?** Después de 60 minutos sin usarlo, la sesión se cierra por seguridad. Vuelve a entrar.
* **¿Por qué no veo algunos botones?** Tu perfil solo permite consultar esa pantalla (capítulo 4).

**Pedidos y cocina**

* **Un plato no aparece al tomar el pedido.** No está en estado Disponible. El administrador lo revisa en **Platos**.
* **La mesa ya tiene un pedido, ¿creo otro?** No. Agrega los platos al pedido abierto con **+ Agregar platos**.
* **Se acabó un ingrediente con el plato ya pedido.** Mientras esté en preparación, cancélalo con **Cancelar plato** en el detalle del pedido.

**Caja**

* **Cobré con la forma de pago equivocada.** El administrador anula el recibo con un motivo y el pedido vuelve a **Pedidos por cobrar** para cobrarlo bien.
* **Ya cerré la caja y falta un cobro.** El día cerrado queda bloqueado para que el cierre no cambie. Habla con el administrador.
* **El efectivo no cuadra.** Registra lo que contaste y explica la diferencia en **Observación**.

**PQRS**

* **¿Cómo sabe el cliente que le respondimos?** En la página pública, pestaña **Consultar respuesta**, con su número de radicado y su correo.
* **¿Cuánto tiempo hay para responder?** El plazo de referencia es de 15 días hábiles. La bandeja marca las que lo superan.

**Sistema**

* **Las pantallas no cargan datos.** MySQL o la aplicación están apagados en el computador del sistema. Enciéndelos desde XAMPP.
* **Una pantalla se ve rara después de una actualización.** Recarga la página con **F5**.

## 6.6 Ayudas

El sistema no tiene un módulo de ayuda aparte. La ayuda está en la propia pantalla:

* Cada pantalla dice arriba en qué módulo estás y para qué sirve.
* Los campos muestran ejemplos de lo que se debe escribir, como «Ej. Sin cebolla» o «Mínimo 6 caracteres».
* Cuando algo no se puede hacer, un mensaje explica la causa y qué hacer (ver 6.3).
* Las pantallas de solo consulta muestran el aviso _Modo consulta_.

Para cualquier otra duda, consulta este manual o al administrador del sistema.
