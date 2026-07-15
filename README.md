# FlashCart Backend API

Backend REST desarrollado con **Spring Boot** para una plataforma de comercio electrónico.

Implementa autenticación mediante JWT, gestión de productos, carrito de compras, control de inventario, pruebas automatizadas e integración continua.

---

# Tecnologías

| Tecnología | Versión |
|------------|----------|
| Java | 17 |
| Spring Boot | 3.x |
| Spring Security | JWT |
| Spring Data JPA | Hibernate |
| PostgreSQL | Supabase |
| H2 Database | Pruebas de integración |
| Maven | 3.x |
| Swagger / OpenAPI | springdoc |
| JUnit 5 | Testing |
| Mockito | Unit Testing |
| MockMvc | Integration Testing |
| JaCoCo | Cobertura |
| GitHub Actions | CI |

---

# Arquitectura

Arquitectura en capas.

```
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
```

## Patrones implementados

- DTO Pattern
- Repository Pattern
- Service Layer
- Builder Pattern (Lombok)
- Dependency Injection
- Singleton (Beans Spring)

---

# Autenticación

La autenticación se realiza mediante **JWT Bearer Token**.

```
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

Flujo:

```
Registro
      │
      ▼
BCrypt Password Encoder
      │
      ▼
Login
      │
      ▼
AuthenticationManager
      │
      ▼
JWT
      │
      ▼
Endpoints protegidos
```

---

# Variables de entorno

```properties
spring.datasource.url=${SUPABASE_DB_URL}
spring.datasource.username=${SUPABASE_DB_USER}
spring.datasource.password=${SUPABASE_DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

| Variable | Descripción |
|----------|-------------|
| SUPABASE_DB_URL | URL PostgreSQL |
| SUPABASE_DB_USER | Usuario |
| SUPABASE_DB_PASSWORD | Contraseña |
| JWT_SECRET | Llave JWT |
| JWT_EXPIRATION | Tiempo de expiración |

---

# Base de datos

## usuarios

| Campo | Tipo |
|-------|------|
| id | bigint |
| username | varchar |
| email | varchar |
| password | varchar |

---

## productos

| Campo | Tipo |
|-------|------|
| id | bigint |
| nombre | varchar |
| descripcion | text |
| precio | numeric |
| stock | integer |

---

## carrito

| Campo | Tipo |
|-------|------|
| id | bigint |
| usuario_id | FK |
| fecha | timestamp |

---

## carrito_items

| Campo | Tipo |
|-------|------|
| id | bigint |
| carrito_id | FK |
| producto_id | FK |
| cantidad | integer |
| precio_unitario | numeric |

---

# Modelo Entidad Relación

![ERD](ERDFlashCart.png)

---

# Endpoints

## Autenticación

| Método | Endpoint |
|---------|----------|
| POST | /auth/register |
| POST | /auth/login |

---

## Productos

| Método | Endpoint |
|---------|----------|
| GET | /productos |
| GET | /productos/{id} |
| POST | /productos |
| PUT | /productos/{id} |
| DELETE | /productos/{id} |

---

## Carrito

| Método | Endpoint |
|---------|----------|
| GET | /carrito/{usuarioId} |
| POST | /carrito/{usuarioId}/productos |
| DELETE | /carrito/{usuarioId}/productos/{productoId} |
| POST | /carrito/{usuarioId}/procesar |

---

# Manejo de concurrencia

Para evitar inconsistencias en el inventario se implementa:

- @Transactional
- Validación de stock
- Rollback automático
- Actualización atómica
- Confirmación únicamente cuando finaliza correctamente la transacción

---

# Pruebas

## Pruebas unitarias

Frameworks

- JUnit 5
- Mockito

Cobertura de:

- ProductosService
- Validaciones
- Manejo de excepciones
- Control de inventario
- Reglas de negocio

---

## Pruebas de integración

Frameworks

- Spring Boot Test
- MockMvc
- H2 Database
- @SpringBootTest
- @Transactional

### Casos implementados

### Autenticación

✔ Login correcto

✔ Login con contraseña incorrecta

✔ Registro exitoso

✔ Registro de usuario existente

Las pruebas verifican:

- Código HTTP
- Respuesta JSON
- Persistencia
- Flujo completo de autenticación
- Generación del JWT

---

# Cobertura de código

El proyecto utiliza **JaCoCo**.

Generar reporte:

```bash
mvn clean verify
```

Reporte:

```
target/site/jacoco/index.html
```

Incluye:

- Cobertura por líneas
- Cobertura por métodos
- Cobertura por clases
- Cobertura por paquetes

---

# Integración Continua

El proyecto utiliza **GitHub Actions**.

El pipeline ejecuta automáticamente:

- Compilación
- Tests unitarios
- Tests de integración
- Reporte JaCoCo

---

# Documentacion Swagger

Swagger UI

```
https://flashcart-backend-lbkd.onrender.com/swagger-ui/index.html#/Usuario/login
```

# Link de proyecto FlashCart 

```
https://flashcart-frontend-two.vercel.app/
```

---

# Instalación

Clonar repositorio

```bash
git clone https://github.com/USUARIO/flashcart-backend.git
```

Entrar al proyecto

```bash
cd flashcart-backend
```

Instalar dependencias

```bash
mvn clean install
```

Ejecutar

```bash
mvn spring-boot:run
```

---

# Ejecutar pruebas

Pruebas

```bash
mvn test
```

Pruebas + cobertura

```bash
mvn clean verify
```

---

# Estructura del proyecto

```
src
├── controllers
├── services
├── repositories
├── models
├── dto
├── config
├── security
├── exceptions
├── tests
└── resources
```

---

# Características implementadas

- Autenticación JWT
- Registro de usuarios
- Login seguro
- BCrypt Password Encoder
- CRUD de productos
- Carrito de compras
- Control de inventario
- Validaciones
- Manejo global de excepciones
- Swagger/OpenAPI
- Pruebas unitarias
- Pruebas de integración
- JaCoCo
- GitHub Actions
- Arquitectura en capas