---
description: Documentación técnica y funcional del sistema de gestión para restaurantes.
icon: hand-wave
---

# Sistema de Gestión para Restaurante

**Documentación técnica y funcional del proyecto**

Bienvenido a la documentación del Sistema de Gestión para Restaurante. Este sitio reúne la descripción del proyecto, sus objetivos, arquitectura, tecnologías, módulos funcionales, implementación del frontend y backend, base de datos, API REST y criterios de pruebas.

El sistema busca apoyar procesos operativos y administrativos de un restaurante mediante una aplicación web conectada a un backend y a una base de datos relacional.

## Contenido

* **Descripción del proyecto:** propósito, objetivos y alcance.
* **Arquitectura del sistema:** capas, componentes y flujo de datos.
* **Tecnologías:** herramientas utilizadas en el desarrollo.
* **Módulos funcionales:** platos, pedidos, inventario y administración.
* **Frontend:** interfaz y comunicación con el backend.
* **Backend:** Spring Boot, controladores, modelos y DAO.
* **Base de datos:** MySQL y persistencia.
* **API REST:** rutas y ejemplos confirmados.
* **Pruebas:** escenarios y validaciones.

## Tecnologías principales

| Componente               | Tecnología                 |
| ------------------------ | -------------------------- |
| Backend                  | Java y Spring Boot         |
| Servicios HTTP           | Spring Web / API REST      |
| Acceso a datos           | Spring JDBC y JdbcTemplate |
| Base de datos            | MySQL                      |
| Interfaz revisada        | JavaScript, HTML y CSS     |
| Control de versiones     | Git y GitHub               |
| Documentación            | GitBook                    |
| Diagrama de arquitectura | GitDiagram                 |

## Arquitectura general

La solución separa la presentación, los controladores REST, los modelos de datos, los objetos de acceso a datos (DAO) y la persistencia en MySQL.

> La documentación distingue entre funcionalidades comprobadas en código o pruebas y módulos identificados únicamente en el diagrama. Los detalles no verificados se señalan como pendientes, sin inventar rutas ni reglas de negocio.
