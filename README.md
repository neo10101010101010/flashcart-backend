# FlashCart Backend API

## 1. Descripción

FlashCart Backend es una API REST desarrollada con Spring Boot para la gestión de usuarios, autenticación, 
productos y carrito de compras.

### Tecnologías

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL (Supabase)
- Swagger / OpenAPI
- Maven

---

# 2. Arquitectura

## Arquitectura utilizada

- Arquitectura en capas
    - Controller
    - Service
    - Repository
    - Entity
    - DTO

## Patrones de diseño

- DTO Pattern
- Repository Pattern
- Service Layer
- Builder Pattern (Lombok)
- Dependency Injection
- Singleton (Beans de Spring)

---

# 3. Autenticación

JWT Bearer Token

Ejemplo

Authorization

Bearer eyJhbGc....

---

# 4. Variables de entorno

application.properties

```properties
spring.datasource.url=${SUPABASE_DB_URL}
spring.datasource.username=${SUPABASE_DB_USER}
spring.datasource.password=${SUPABASE_DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

Variables

| Variable | Descripción |
|----------|-------------|
| SUPABASE_DB_URL | URL PostgreSQL |
| SUPABASE_DB_USER | Usuario |
| SUPABASE_DB_PASSWORD | Contraseña |
| JWT_SECRET | Llave JWT |

---

# 5. Base de datos

## Tablas

### usuarios

| Campo | Tipo |
|-------|------|
| id | bigint |
| username | varchar |
| email | varchar |
| password | varchar |

---

### productos

| Campo | Tipo |
|-------|------|
| id | bigint |
| nombre | varchar |
| descripcion | text |
| precio | numeric |
| stock | integer |

---

### carrito

| Campo | Tipo |
|-------|------|
| id | bigint |
| usuario_id | FK |
| fecha | timestamp |

---

### carrito_items

| Campo | Tipo |
|-------|------|
| id | bigint |
| carrito_id | FK |
| producto_id | FK |
| cantidad | integer |
 | precio_unitario | bigInteger

---

# 6. Diagrama ER

![Diagrama ERD](ERDFlashCart.png)

---

# 7. Endpoints

## Productos

### GET /productos

Obtiene todos los productos.

Respuesta

```json
[
  {
    "id":1,
    "nombre":"Laptop",
    "precio":15000,
    "stock":10
  }
]
```

---

### GET /productos/{id}
Obtiene productos por id
```json
{
  "id":1,
  "nombre":"Laptop",
  "precio":15000,
  "stock":10
}
```

---

### POST /productos

Crea un nuevo producto

```json
{
  "nombre":"Laptop",
  "descripcion":"Core i7",
  "precio":15000,
  "stock":5
}
```

Response

```json
{
  "id":1,
  "nombre":"Laptop",
  "precio":15000,
  "stock":5
}
```

---

### PUT /productos/{id}
Actualiza un producto por id
```json
{
  "nombre":"Laptop Lenovo",
  "precio":16000,
  "stock":8
}
```

---

### DELETE /productos/{id}

Elimina un producto de la lista

---

## Usuarios

POST /auth/register

POST /auth/login

---

## Carrito

GET /carrito/{usuarioId}

POST /carrito/{usuarioId}/productos

DELETE /carrito/{usuarioId}/productos/{productoId}

POST /carrito/{usuarioId}/procesar

---

# 8. Manejo de concurrencia

La aplicación implementa manejo de concurrencia para evitar inconsistencias en el inventario cuando múltiples 
usuarios intentan comprar el mismo producto simultáneamente.

Se utilizan:
- Transacciones (@Transactional)
- Validación de stock antes de confirmar la compra
- Confirmación de cambios únicamente cuando la transacción finaliza correctamente
- Rollback automático ante cualquier excepción
- Actualización atómica del inventario

Esto evita ventas duplicadas y cantidades negativas en stock.

---

# 9. Pruebas

Pruebas unitarias realizadas con

- JUnit 5
- Mockito



---

# 10. Swagger

Swagger UI

http://localhost:8080/swagger-ui/index.html

OpenAPI

http://localhost:8080/v3/api-docs

---

# 11. Ejecución

Clonar

git clone ...

Instalar

mvn clean install

Ejecutar

mvn spring-boot:run

---

# 12. Ejecutar pruebas

Backend

mvn test


---
