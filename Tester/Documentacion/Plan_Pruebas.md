# Plan de Pruebas

## 1. Objetivo

Validar el funcionamiento del sistema de gestión de restaurante, verificando que sus funcionalidades respondan correctamente a los datos ingresados y cumplan con los comportamientos esperados.

## 2. Alcance

Se realizarán pruebas sobre las funcionalidades del sistema, la interfaz de usuario, los servicios del backend y la interacción con la base de datos.

## 3. Tipos de pruebas

### Pruebas funcionales
Verificar que cada funcionalidad cumpla con su objetivo.

### Pruebas de validación
Comprobar el comportamiento ante datos vacíos, incorrectos o incompletos.

### Pruebas de integración
Validar la comunicación entre frontend, backend y base de datos.

### Pruebas automatizadas
Ejecutar las pruebas implementadas mediante herramientas de automatización.

## 4. Herramientas

- Postman
- JUnit
- Spring Boot Test
- Maven
- MySQL
- Navegador web

## 5. Criterios de evaluación

Una prueba será aprobada cuando el resultado obtenido coincida con el resultado esperado.

Una prueba será fallida cuando el sistema presente un comportamiento diferente al esperado.

Una prueba será bloqueada cuando no pueda ejecutarse debido a una dependencia, configuración o error externo.

## 6. Registro de resultados

Cada prueba debe registrar:

- Identificador.
- Módulo.
- Escenario.
- Datos de entrada.
- Resultado esperado.
- Resultado obtenido.
- Estado.
- Evidencia.