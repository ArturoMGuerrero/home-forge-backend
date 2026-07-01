# HomeForge - Backend API

API REST para HomeForge, un CRM para constructoras, desarrolladores inmobiliarios y equipos comerciales de vivienda.

## 🚀 Tecnologías

- **Framework**: Spring Boot 3.3.5
- **Lenguaje**: Java 21
- **Base de datos**: PostgreSQL, H2 (desarrollo) o SQL Server
- **ORM**: Spring Data JPA + Hibernate
- **Migraciones**: Flyway
- **Build**: Maven
- **Pagos**: MercadoPago SDK
- **Suscripciones**: Sistema integrado con renovación automática

## 📋 Requisitos

- Java 21 o superior ([Descargar Adoptium JDK](https://adoptium.net/))
- Maven (incluido como wrapper - `mvnw`)
- Base de datos (elige una):
  - H2 (incluida - recomendada para desarrollo)
  - PostgreSQL 14+ (vía Docker o instalación local)
  - SQL Server Express o superior

## 🔧 Configuración Inicial

### 1. Clonar el repositorio

```bash
git clone <tu-repositorio-backend>
cd HomeForge-backend
```

### 2. Configurar variables de entorno

Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

Edita `.env` según tus necesidades:

```bash
# Puerto del servidor
SERVER_PORT=8080

# CORS - Agrega la URL de tu frontend
CORS_ALLOWED_ORIGINS=http://localhost:5174

# Directorio para archivos subidos
UPLOADS_DIRECTORY=uploads

# MercadoPago (IMPORTANTE: Usa credenciales de prueba para desarrollo)
MERCADOPAGO_ACCESS_TOKEN=tu_access_token_aqui
MERCADOPAGO_PUBLIC_KEY=tu_public_key_aqui
```

## 🚀 Iniciar el Proyecto

### Opción 1: H2 Database (Desarrollo rápido)

```bash
# Windows
start-h2.cmd

# Linux/Mac/Git Bash
./start-h2.sh
```

**Consola H2**: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:file:./data/casaflow`
- Usuario: `sa`
- Password: (vacío)

### Opción 2: SQL Server

```bash
# Windows
start-sqlserver.cmd

# Linux/Mac/Git Bash
./start-sqlserver.sh
```

**Configuración**: Edita `src/main/resources/application-sqlserver.yml` si necesitas cambiar la conexión.

Ver guía completa: [SQL_SERVER_SETUP.md](../HomeForge/SQL_SERVER_SETUP.md)

### Opción 3: PostgreSQL (via Docker)

```bash
# Iniciar PostgreSQL
docker-compose up -d

# Iniciar backend
mvnw spring-boot:run
```

### Opción 4: Ejecutar manualmente con Maven

```bash
# H2
mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# SQL Server
mvnw spring-boot:run -Dspring-boot.run.profiles=sqlserver

# PostgreSQL (default)
mvnw spring-boot:run
```

## 📡 API Endpoints

Una vez iniciado, el backend estará disponible en: **http://localhost:8080**

### Principales endpoints:

#### Propiedades
- `GET /api/properties` - Listar propiedades
- `POST /api/properties` - Crear propiedad

#### Prospectos (Leads)
- `GET /api/leads` - Listar prospectos
- `POST /api/leads` - Crear prospecto
- `PATCH /api/leads/{id}/status` - Cambiar estado (trigger de automatización)
- `GET /api/leads/{id}/activities` - Ver actividades de un prospecto
- `POST /api/leads/{id}/calculate-score` - Calcular score del lead

#### Pipeline de Ventas
- `GET /api/leads?status=QUALIFIED` - Filtrar por estado (Kanban)
- `PATCH /api/leads/{id}/status` - Mover entre columnas del pipeline

#### Tareas de Seguimiento
- `GET /api/follow-up-tasks` - Listar tareas
- `GET /api/follow-up-tasks/lead/{leadId}` - Tareas de un prospecto
- `GET /api/follow-up-tasks/overdue` - Tareas vencidas
- `POST /api/follow-up-tasks` - Crear tarea manual
- `PATCH /api/follow-up-tasks/{id}` - Actualizar tarea

#### Scoring de Leads
- `POST /api/leads/{id}/calculate-score` - Recalcular score
- `GET /api/leads/{id}/score-history` - Historial de cambios de score

#### Asignación Automática
- `GET /api/assignment-rules` - Listar reglas de asignación
- `POST /api/assignment-rules` - Crear regla
- `PATCH /api/assignment-rules/{id}` - Actualizar regla

#### Documentos y Contratos
- `GET /api/document-templates` - Listar plantillas
- `POST /api/document-templates` - Crear plantilla
- `GET /api/documents` - Listar documentos generados
- `POST /api/documents` - Generar documento desde plantilla
- `POST /api/documents/{id}/send-for-signature` - Enviar para firma
- `GET /api/document-signatures/document/{id}` - Firmas de un documento
- `POST /api/document-signatures/{id}/sign` - Firmar documento

#### Notificaciones y Comunicación
- `GET /api/notifications` - Listar notificaciones
- `POST /api/notifications` - Crear notificación
- `POST /api/notifications/{id}/send` - Enviar notificación manualmente
- `PATCH /api/notifications/{id}/status` - Actualizar estado
- `GET /api/message-templates` - Listar plantillas de mensajes
- `POST /api/message-templates` - Crear plantilla
- `GET /api/message-templates/by-type` - Filtrar por tipo
- `PATCH /api/message-templates/{id}/toggle-active` - Activar/desactivar plantilla

#### Calendario y Citas
- `GET /api/appointments` - Listar citas (con filtros por fecha, usuario, lead)
- `POST /api/appointments` - Crear cita
- `GET /api/appointments/{id}` - Obtener cita
- `PUT /api/appointments/{id}` - Actualizar cita
- `DELETE /api/appointments/{id}` - Eliminar cita (soft delete)
- `PATCH /api/appointments/{id}/status` - Cambiar estado
- `POST /api/appointments/{id}/reminder-sent` - Marcar recordatorio enviado
- `POST /api/appointments/{id}/sync-google` - Sincronizar con Google Calendar

#### Disponibilidad de Agentes
- `GET /api/agent-availability` - Listar disponibilidad (por usuario/día)
- `POST /api/agent-availability` - Crear horario disponible
- `PUT /api/agent-availability/{id}` - Actualizar disponibilidad
- `DELETE /api/agent-availability/{id}` - Eliminar disponibilidad

#### Empresas
- `GET /api/companies` - Listar empresas
- `GET /api/companies/{id}` - Obtener empresa por ID

#### Suscripciones
- `GET /api/subscriptions` - Listar planes de suscripción
- `POST /api/subscriptions/subscribe` - Crear suscripción
- `PUT /api/subscriptions/{id}/renew` - Renovar suscripción

#### Pagos (MercadoPago)
- `POST /api/payments/create-preference` - Crear preferencia de pago
- `POST /api/payments/webhook` - Webhook de notificaciones
- `GET /api/payments/status/{paymentId}` - Consultar estado de pago

#### Dashboard
- `GET /api/dashboard/metrics` - Métricas generales del sistema

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## 🗄️ Base de Datos

### Migraciones

Las migraciones se ejecutan automáticamente con Flyway al iniciar la aplicación.

Ubicación:
- PostgreSQL: `src/main/resources/db/migration/`
- H2: `src/main/resources/db/migration/h2/`
- SQL Server: `src/main/resources/db/migration/sqlserver/`

### Tablas principales

- `companies` - Empresas (multi-tenant)
- `users` - Usuarios del sistema
- `properties` - Inventario de propiedades
- `leads` - Prospectos/clientes potenciales (con scoring)
- `lead_activities` - Seguimiento extendido de actividades
- `lead_score_history` - Historial de cambios de score
- `follow_up_tasks` - Tareas automatizadas de seguimiento
- `lead_assignment_rules` - Reglas de asignación automática de leads
- `developments` - Desarrollos inmobiliarios
- `tasks` - Tareas y seguimiento
- `documents` - Archivos adjuntos
- `document_templates` - Plantillas de contratos reutilizables
- `documents` - Documentos generados (contratos, acuerdos)
- `document_signatures` - Firmas electrónicas de documentos
- `message_templates` - Plantillas de mensajes para notificaciones
- `notifications` - Notificaciones enviadas (Email, WhatsApp, Push, SMS)
- `communication_channels` - Configuración de canales de comunicación
- `push_subscriptions` - Suscripciones a notificaciones push
- `notification_preferences` - Preferencias de notificaciones por usuario
- `appointments` - Citas y eventos del calendario
- `agent_availability` - Disponibilidad de agentes por día/hora
- `calendar_blocks` - Bloqueos de calendario
- `appointment_reminders` - Recordatorios de citas
- `subscription_plans` - Planes de suscripción (Trial, Básico, Profesional, Empresarial)
- `subscriptions` - Suscripciones activas de empresas
- `payments` - Registro de pagos procesados
- `property_lead_match` - Matching entre propiedades y prospectos

## 🧪 Tests

```bash
# Ejecutar todos los tests
mvnw test

# Ejecutar tests con reporte de cobertura
mvnw test jacoco:report
```

Reporte de cobertura: `target/site/jacoco/index.html`

## 📦 Build

### Crear JAR ejecutable

```bash
mvnw clean package -DskipTests
```

El JAR se generará en: `target/casaflow-backend-0.1.0.jar`

### Ejecutar JAR

```bash
java -jar target/casaflow-backend-0.1.0.jar
```

## 🔐 Autenticación

El sistema incluye autenticación básica con:

- Registro de usuarios
- Login con email y password
- Hashing de contraseñas con BCrypt
- Multi-tenant por `company_id`

## 💳 Sistema de Suscripciones y Pagos

### Planes Disponibles

1. **Trial (Prueba)** - 14 días gratis
   - 10 propiedades máximo
   - 20 prospectos máximo
   - Funcionalidades básicas

2. **Básico** - $299 MXN/mes
   - 50 propiedades
   - 100 prospectos
   - Soporte por email

3. **Profesional** - $599 MXN/mes
   - 200 propiedades
   - 500 prospectos
   - Soporte prioritario
   - Reportes avanzados

4. **Empresarial** - $999 MXN/mes
   - Propiedades ilimitadas
   - Prospectos ilimitados
   - Soporte 24/7
   - API access
   - Dashboard personalizado

### Integración con MercadoPago

- Pagos procesados mediante MercadoPago SDK
- Webhooks para actualización automática de suscripciones
- Soporte para pagos recurrentes
- Validación automática de límites según plan

### Renovación Automática

El sistema incluye un scheduler que:
- Verifica suscripciones próximas a vencer
- Envía notificaciones de renovación
- Marca como expiradas las suscripciones vencidas
- Se ejecuta diariamente a las 00:00

## 🌍 CORS

Configurado en `.env` con la variable `CORS_ALLOWED_ORIGINS`.

Por defecto permite:
- http://localhost:5174
- http://127.0.0.1:5174

Agrega más orígenes separados por coma si es necesario.

## 📁 Estructura del Proyecto

```
HomeForge-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/casaflow/
│   │   │       ├── controller/    # REST Controllers
│   │   │       ├── service/       # Lógica de negocio
│   │   │       ├── repository/    # JPA Repositories
│   │   │       ├── model/         # Entidades JPA
│   │   │       ├── dto/           # Data Transfer Objects
│   │   │       └── config/        # Configuración
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-h2.yml
│   │       ├── application-sqlserver.yml
│   │       └── db/migration/      # Migraciones Flyway
│   └── test/                      # Tests
├── pom.xml                        # Dependencias Maven
├── .env                           # Variables de entorno
└── README.md
```

## 🛠️ Comandos Útiles

```bash
# Limpiar proyecto
mvnw clean

# Compilar sin tests
mvnw clean install -DskipTests

# Ver dependencias
mvnw dependency:tree

# Actualizar wrapper de Maven
mvnw wrapper:wrapper

# Ejecutar con perfil específico
mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

## 🔧 Configuración Avanzada

### Cambiar puerto

Edita `.env`:
```bash
SERVER_PORT=8081
```

### Variables de entorno disponibles

- `SERVER_PORT` - Puerto del servidor (default: 8080)
- `CORS_ALLOWED_ORIGINS` - Orígenes CORS permitidos
- `UPLOADS_DIRECTORY` - Directorio para archivos subidos
- `DB_URL` - URL de base de datos (para PostgreSQL)
- `DB_USERNAME` - Usuario de BD (para PostgreSQL/SQL Server)
- `DB_PASSWORD` - Contraseña de BD (para PostgreSQL/SQL Server)
- `MERCADOPAGO_ACCESS_TOKEN` - Access Token de MercadoPago (prueba o producción)
- `MERCADOPAGO_PUBLIC_KEY` - Public Key de MercadoPago

## 🐳 Docker (Opcional)

Para PostgreSQL local:

```bash
# Iniciar
docker-compose up -d

# Detener
docker-compose down

# Ver logs
docker-compose logs -f
```

## 📚 Documentación Adicional

- [Configuración SQL Server](../HomeForge/SQL_SERVER_SETUP.md)
- [Comandos Rápidos](../HomeForge/COMANDOS_RAPIDOS.md)
- [Cambios Realizados](../HomeForge/CAMBIOS_REALIZADOS.md)

## 🤝 Frontend

Este backend está diseñado para trabajar con el frontend de HomeForge:

- Repositorio Frontend: `HomeForge-frontend`
- URL por defecto: http://localhost:5174

Asegúrate de configurar `CORS_ALLOWED_ORIGINS` con la URL del frontend.

## 📝 Licencia

Proyecto privado - Todos los derechos reservados

## 🆘 Soporte

Para problemas o preguntas:

1. Revisa la documentación
2. Verifica los logs en consola
3. Consulta los archivos de configuración

---

**Desarrollado con ☕ y Spring Boot**
