# Restaurante

Sistema de gestión para restaurante: una aplicación web que reúne en un solo lugar los procesos diarios de un restaurante, desde que una mesa pide hasta que se cierra la caja.

## Qué hace

| Módulo | Funciones |
| --- | --- |
| Inicio | Resumen del día: ventas, pedidos abiertos, cobros pendientes, PQRS y materias primas por reponer |
| Pedido | Pedidos por mesa, comandas de cocina, platos, categorías, materias primas y unidades de medida |
| Recibo de caja | Cobro de pedidos, anulación de recibos, cierre de caja diario, clientes y tipos de identidad |
| Comprobante de egreso | Salidas de dinero, conceptos y formas de pago |
| PQRS | Bandeja interna, respuestas y página pública para que los clientes radiquen y consulten |
| Configuración | Usuarios, roles y estados |

El acceso es con usuario y contraseña (cifrada con BCrypt) y dos roles: **Administrador**, con acceso completo, y **Auxiliar** (mesero, cocina y caja), con permisos limitados.

## Tecnologías

- **Backend:** Java 25 y Spring Boot 4.1.1 (web y JDBC), compilado con Maven
- **Interfaz:** HTML, CSS y JavaScript, servidos por la misma aplicación
- **Base de datos:** MySQL (MariaDB 10.4 de XAMPP), base `bd_restaurante` con 22 tablas
- **Herramientas:** Visual Studio Code, Git y GitHub, GitBook, Figma

## Ramas del repositorio

| Rama | Contenido |
| --- | --- |
| `Backend` | Versión más reciente del sistema: código, vistas web, esquema y migraciones de la base de datos |
| `main` | Documentación sincronizada con GitBook (técnica y manual de usuario) |
| `Frontend` | Trabajo de las vistas del sistema |
| `tester` | Plan e informe de pruebas |

## Cómo ejecutarlo

1. Instala Java 25 y XAMPP, y enciende **MySQL** desde el panel de XAMPP.
2. Clona el repositorio y cámbiate a la rama `Backend`:

   ```bash
   git clone https://github.com/Sebas-SMO5/Restaurante.git
   cd Restaurante
   git checkout Backend
   ```

3. Crea la base de datos `bd_restaurante` con el archivo `database/esquema.sql`. Las instrucciones están en `database/README.md`.
4. Inicia la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Abre `http://localhost:8080` en el navegador e inicia sesión.

Antes de apagar el computador, detén MySQL con **Stop** en XAMPP para no dañar la base de datos.

## Documentación

- **Documentación técnica y manual de usuario:** https://sebasmo.gitbook.io/sebasmo-docs/
- **Diseño en Figma:** https://www.figma.com/design/5pBciyoj3ZLymqtQcfYdRp/Restaurante
- **Diagrama de la arquitectura (GitDiagram):** https://gitdiagram.com/sebas-smo5/restaurante

## Equipo MRGG

| Integrante | Rol |
| --- | --- |
| Sebastián Mora Osorio | Líder del equipo y desarrollador backend |
| María Camila Ramírez Caro | Diseñadora de sistemas y frontend |
| María de los Ángeles Gutiérrez López | Tester |
| Juan Sebastián Granados Gómez | Analista de sistemas |

Proyecto académico de la Universidad Santiago de Cali, 2026.
