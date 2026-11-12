---
description: Contexto, propósito y objetivos del sistema.
icon: rocket-launch
---

# Introducción y objetivos

## Introducción

El proyecto **Sistema de Gestión para Restaurante** consiste en el desarrollo de una aplicación de información que reúne funciones operativas y administrativas de un establecimiento gastronómico. La solución se estructura en una interfaz web, un backend que expone servicios HTTP y una base de datos relacional que centraliza la información.

## Propósito

Proporcionar una herramienta que facilite el registro, consulta y actualización de información necesaria para la gestión del restaurante, organizando los datos en módulos y permitiendo su consulta desde una interfaz conectada al backend.

## Objetivo general

Desarrollar un sistema de información para apoyar la gestión de procesos operativos y administrativos de un restaurante mediante una aplicación web, servicios backend y una base de datos relacional.

## Objetivos específicos

* Organizar las funcionalidades en módulos según su responsabilidad.
* Permitir que el frontend envíe solicitudes HTTP al backend.
* Implementar servicios REST para consultar, registrar y actualizar información.
* Representar los datos mediante modelos Java.
* Separar las operaciones de persistencia en clases DAO.
* Centralizar la información en MySQL.
* Facilitar las pruebas funcionales y técnicas.
* Documentar la arquitectura y la ejecución del sistema.

## Alcance

Las áreas identificadas incluyen platos y menú, pedidos y sus detalles, inventario e insumos, usuarios, clientes, egresos, recibos de caja, PQRS y recursos de referencia.

La implementación exacta de cada operación debe verificarse en la versión final del repositorio. El módulo de platos cuenta con evidencia concreta de consulta, registro y actualización; para los demás módulos se requiere contrastar controladores, DAO y pantallas.

## Público objetivo

Esta documentación está dirigida a los integrantes del equipo, docentes, personas encargadas de las pruebas y usuarios que necesiten comprender la organización técnica del sistema.
