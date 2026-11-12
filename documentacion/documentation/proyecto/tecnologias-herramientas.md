---
description: Go from sign-up to your first deploy in under five minutes.
icon: bolt
---

# Tecnologías y herramientas

## Java y Spring Boot

Java se utiliza para implementar el backend y representar los modelos de información. Spring Boot permite configurar y ejecutar la aplicación, registrar componentes y exponer servicios HTTP.

## Spring Web

Se utiliza para definir controladores REST y asociar métodos con solicitudes HTTP mediante anotaciones como `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` y `@PutMapping`.

## Spring JDBC y JdbcTemplate

La capa DAO utiliza `JdbcTemplate` para ejecutar consultas SQL parametrizadas y operaciones de inserción y actualización en MySQL.

## MySQL

Motor de base de datos relacional utilizado para almacenar la información del restaurante.

## JavaScript, HTML y CSS

En los archivos de interfaz revisados se identifican módulos JavaScript, como `platos.js`, que obtienen valores de formularios, realizan validaciones y envían solicitudes al backend. HTML define la estructura de las vistas y CSS su presentación.

## Git y GitHub

Se utilizan para el control de versiones y la colaboración sobre el código fuente.

## GitBook y GitDiagram

GitBook se utiliza para publicar esta documentación y GitDiagram para generar una representación visual de la arquitectura a partir del repositorio.

## Versiones

Las versiones exactas deben verificarse en el archivo `pom.xml` y en la configuración del proyecto. No se fijan números de versión que no se hayan contrastado con la versión final.
