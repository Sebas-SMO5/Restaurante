# Pruebas y validación

## Propósito

Las pruebas permiten verificar que las funcionalidades respondan de acuerdo con los requisitos y que el intercambio entre frontend, backend y base de datos se realice correctamente.

## Evidencia confirmada: módulo de platos

Durante el desarrollo se realizó una prueba de registro de un plato desde la interfaz web. Se identificó inicialmente un error de conversión JSON al construir el modelo Java. Se ajustó el modelo `Plato` mediante `@JsonCreator` y `@JsonProperty`, y posteriormente el registro funcionó. El nuevo elemento se visualizó tras actualizar el listado.

También se revisó la lógica de consulta, inserción y actualización del DAO de platos.

## Escenarios de prueba propuestos

| ID    | Escenario                                                  | Resultado esperado                                                     |
| ----- | ---------------------------------------------------------- | ---------------------------------------------------------------------- |
| PL-01 | Consultar listado de platos                                | Se muestran los registros existentes.                                  |
| PL-02 | Registrar plato con datos válidos                          | El backend responde con creación exitosa y el registro queda en MySQL. |
| PL-03 | Registrar descripción repetida                             | El sistema rechaza el registro duplicado.                              |
| PL-04 | Registrar precio vacío, no numérico o menor o igual a cero | El frontend impide el envío y presenta validación.                     |
| PL-05 | Actualizar un plato existente                              | Los datos se actualizan y el listado refleja el cambio.                |
| PL-06 | Actualizar un ID inexistente                               | El sistema informa que no pudo actualizar el registro.                 |

## Pruebas técnicas del backend

Para verificar la compilación se utilizó:

```powershell
.\mvnw.cmd clean compile
```

El resultado observado durante el desarrollo fue **BUILD SUCCESS**. Para ejecutar la aplicación se utilizó:

```powershell
.\mvnw.cmd spring-boot:run
```

## Evidencias pendientes

Para cerrar la matriz de pruebas se deben adjuntar capturas de cada escenario, datos de entrada, resultado obtenido, resultado esperado, estado de aprobación y observaciones. Los escenarios de esta página son una guía; no se presentan como ejecutados si todavía no existe evidencia.
