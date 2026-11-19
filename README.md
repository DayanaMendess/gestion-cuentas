# Gestión de cuentas

Aplicación web para administrar clientes, cuentas financieras y transacciones.

## Funcionalidades

- Registro, consulta, actualización y eliminación de clientes.
- Validación de mayoría de edad, correo válido, documento único y nombres/apellidos de mínimo dos caracteres.
- Creación de cuentas de ahorros y corriente.
- Números de cuenta únicos de diez dígitos:
  - Ahorros: inicia en `53`.
  - Corriente: inicia en `33`.
- Activación, inactivación y cancelación de cuentas.
- Consignaciones, retiros y transferencias.
- Consulta de movimientos por cuenta.
- Interfaz web organizada en Clientes, Cuentas y Transacciones.
- Alertas visuales para cuentas inactivas, saldo insuficiente y transferencias inválidas.

## Tecnologías

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL 16
- Liquibase
- Maven
- Docker y Docker Compose
- JUnit 5 y Mockito
- HTML, CSS y JavaScript

## Arquitectura

El proyecto usa una arquitectura MVC por capas:

```text
Frontend
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

- `controller`: recibe las solicitudes HTTP.
- `service`: contiene las reglas de negocio.
- `repository`: consulta y guarda información.
- `entity`: representa las tablas de la base de datos.
- `dto`: define los datos que recibe o responde la API.

También se aplican los patrones Repository y DTO.

## Ejecución con Docker

### 1. Definir la contraseña local de PostgreSQL

En PowerShell:

```powershell
$env:DB_PASSWORD = Read-Host "Escribe la contraseña de la base de datos"
```

La contraseña no se guarda en GitHub.

### 2. Construir e iniciar la aplicación

```powershell
docker compose up -d --build
```

Docker inicia dos contenedores:

- `gestion-cuentas-postgres`: base de datos PostgreSQL.
- `gestion-cuentas-api`: aplicación Spring Boot.

### 3. Abrir la aplicación

```text
http://localhost:8080
```

## Pruebas

Para ejecutar las pruebas JUnit:

```powershell
& .\mvnw.cmd test
```

Las pruebas cubren reglas de la capa `service` y delegación de la capa `controller`.

## Rutas principales de la API

| Acción | Método | Ruta |
|---|---|---|
| Estado de la API | GET | `/api/health` |
| Crear cliente | POST | `/api/clients` |
| Consultar clientes | GET | `/api/clients` |
| Consultar cliente | GET | `/api/clients/{id}` |
| Actualizar cliente | PUT | `/api/clients/{id}` |
| Eliminar cliente | DELETE | `/api/clients/{id}` |
| Crear cuenta | POST | `/api/accounts` |
| Consultar cuentas | GET | `/api/accounts` |
| Cambiar estado de cuenta | PATCH | `/api/accounts/{id}/status?status=ACTIVE` |
| Cancelar cuenta | DELETE | `/api/accounts/{id}` |
| Consignar | POST | `/api/transactions/deposits` |
| Retirar | POST | `/api/transactions/withdrawals` |
| Transferir | POST | `/api/transactions/transfers` |
| Consultar movimientos | GET | `/api/transactions/accounts/{id}/movements` |

## Reglas de negocio

### Clientes

- El documento de identificación no se puede repetir.
- El cliente debe ser mayor de edad.
- No se puede eliminar un cliente con cuentas vinculadas.
- Las fechas de creación y modificación se generan automáticamente.
- El `id` es un UUID generado automáticamente por Java.

### Cuentas

- Solo existen cuentas de ahorros y corriente.
- Toda cuenta pertenece a un cliente.
- Las cuentas nacen activas.
- Las cuentas de ahorros no pueden quedar con saldo negativo.
- Una cuenta solo se cancela si su saldo es cero.
- Una cuenta inactiva o cancelada no permite movimientos.
- Las cuentas de ahorros inician en `53`; las corrientes, en `33`.

### Transacciones

- Solo se permiten consignaciones, retiros y transferencias.
- Cada movimiento actualiza saldo y saldo disponible.
- Una transferencia genera dos movimientos:
  - Débito en la cuenta de origen.
  - Crédito en la cuenta de destino.
- No se permite transferir a la misma cuenta.
- Las transacciones usan `@Transactional` para conservar la consistencia de los datos.

## Base de datos

Liquibase crea automáticamente las tablas:

- `clients`
- `accounts`
- `movements`

Las migraciones SQL están en:

```text
src/main/resources/db/changelog
```

Liquibase aplica DDL para crear tablas, restricciones e índices. JPA realiza las operaciones DML para insertar, consultar, actualizar o eliminar datos.

## Control de versiones

El proyecto utiliza Git y GitHub con ramas por funcionalidad:

- `feature/clientes`
- `feature/cuentas`
- `feature/transacciones`
- `feature/tests`
- `feature/docker`
- `feature/frontend`
- `feature/documentacion`