# HomeForge - Backend API

API REST para HomeForge, una plataforma CRM completa para inmobiliarias, constructoras, desarrolladores y equipos comerciales de vivienda.

## Desarrollo local

Requisitos: Java 21 y [Rancher Desktop](https://rancherdesktop.io/) con `dockerd (moby)` como
container engine (Kubernetes puede ir desactivado).

1. Copia la configuración de ejemplo. Sus valores de base de datos ya coinciden con `compose.yml`:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Levanta PostgreSQL y el backend:

   ```powershell
   docker compose up -d postgres
   ./mvnw spring-boot:run
   ```

   El backend lee `.env` automáticamente (`spring.config.import`) y Flyway aplica las migraciones al arrancar.
   API en `http://localhost:8080`, PostgreSQL en `localhost:5432`.

`.env` está en `.gitignore`: **nunca subas credenciales al repositorio**. En producción las variables
vienen del entorno de la plataforma.

### Todo en contenedores

Para probar el despliegue completo (PostgreSQL, backend y frontend en `http://localhost:5174`):

```powershell
docker compose up --build -d
```

La imagen del backend arranca con el perfil `prod`, así que antes define `JWT_SECRET` en `.env`
(32+ caracteres, por ejemplo con `openssl rand -base64 48`).

Los datos persisten en los volúmenes `homeforge_postgres_data` y `homeforge_uploads_data`.
`docker compose down` detiene los servicios sin borrar datos; no uses `docker compose down -v`
salvo que quieras eliminar también la base de datos.

### Pagos con Stripe (modo de prueba)

1. Pon `STRIPE_SECRET_KEY` y `STRIPE_PUBLISHABLE_KEY` del sandbox en `.env`.
2. Con el [Stripe CLI](https://docs.stripe.com/stripe-cli), obtén el secreto de los webhooks y guárdalo como `STRIPE_WEBHOOK_SECRET`:

   ```powershell
   stripe listen --api-key $env:STRIPE_SECRET_KEY --print-secret
   ```

3. Deja corriendo el reenvío de eventos mientras pruebas:

   ```powershell
   stripe listen --api-key $env:STRIPE_SECRET_KEY --forward-to localhost:8080/api/webhooks/stripe
   ```

Los productos, precios (MXN, `PLAN_*_PRICE`) y el portal de clientes se crean solos en Stripe la primera vez.
Paga con la tarjeta de prueba `4242 4242 4242 4242`, cualquier fecha futura y cualquier CVC.
El plan de la empresa solo cambia cuando Stripe lo confirma por webhook.

### Pruebas

```powershell
./mvnw test
```

Las pruebas de integración levantan su propio PostgreSQL con Testcontainers
(`TestcontainersConfiguration`), así que Rancher Desktop debe estar corriendo.

En GitHub, cada PR y cada push a `master` corren las pruebas y construyen la imagen Docker
(`.github/workflows/ci.yml`).

## Producción

El `Dockerfile` genera la imagen del backend. Arranca con el perfil `prod`
(`application-prod.yml`), que:

- **exige `JWT_SECRET`**: sin ella el backend no arranca. Si cambia, se cierran todas las sesiones;
- lee la IP real del cliente detrás del proxy de la plataforma (`X-Forwarded-For`), que necesita el bloqueo
  por intentos fallidos del login;
- expone solo `GET /actuator/health` (también `/liveness` y `/readiness`), sin detalles, para los chequeos de salud.

Variables mínimas en la plataforma:

| Variable | Ejemplo / nota |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Postgres administrado: `jdbc:postgresql://host:5432/homeforge?sslmode=require` |
| `JWT_SECRET` | `openssl rand -base64 48`; guárdala en los secretos de la plataforma |
| `FRONTEND_URL` | `https://homeforge.mx` (redirecciones de pago y enlaces) |
| `CORS_ALLOWED_ORIGINS` | `https://homeforge.mx` |
| `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET` | Claves **live** y el secreto del webhook creado en el panel de Stripe, apuntando a `https://<api>/api/webhooks/stripe` |
| `UPLOADS_DIRECTORY` | Volumen persistente (hasta migrar los archivos a almacenamiento en la nube) |

Si la plataforma **no** pone un proxy delante, define `FORWARD_HEADERS_STRATEGY=none`.

## Documentación de la API (Swagger)

Con el backend corriendo en local, abre **http://localhost:8080/swagger-ui.html**. Ahí están todos los endpoints
agrupados por tema (Prospectos, Propiedades, Citas…), con sus campos, validaciones y ejemplos, y se pueden probar:

1. Ejecuta `POST /api/auth/login` con tu correo y contraseña de desarrollo y copia el `token` de la respuesta.
2. Pulsa **Authorize** y pégalo. Desde ese momento las rutas privadas usan tu sesión.

El JSON OpenAPI está en `/v3/api-docs` (sirve para generar clientes o importarlo en Postman/Insomnia).
En producción la documentación está apagada; para revisarla en un entorno de pruebas usa `API_DOCS_ENABLED=true`.

Reglas generales de la API:

- **Autenticación:** JWT de 8 horas en `Authorization: Bearer <token>`. Cambiar la contraseña invalida los tokens anteriores.
- **Aislamiento por empresa:** cada usuario solo ve datos de su inmobiliaria. La empresa sale siempre del token; si una ruta
  recibe `companyId` (en la ruta, en la consulta o en el cuerpo) y no coincide, responde **403**.
- **Rutas públicas** (sin token): catálogo y detalle de propiedades, perfil público de empresas, catálogos, formulario
  "Me interesa", vista previa para compartir, webhooks y `/actuator/health`.
- **Errores:** cuerpo `{"error": "mensaje en español"}`. 400 validación · 401 sin sesión o token inválido · 403 sin permiso ·
  402 suscripción vencida · 429 demasiados intentos (con `Retry-After`).

## Arquitectura en breve

Un paquete por módulo de negocio, cada uno con `controller`, `service`, `repository`, `domain` (entidades) y `dto`:

| Paquete | Qué hace |
|---|---|
| `auth` | Registro de inmobiliaria, login, recuperación de contraseña, límite de intentos |
| `security` | JWT, usuario de la petición (`CurrentUser`), aislamiento por empresa (`TenantGuard`, `@TenantId`), rutas públicas |
| `company` | Perfil de la empresa, perfil público y horario de atención |
| `user` | Usuarios, roles (ADMIN / AGENT), equipos, Mi cuenta |
| `property` | Propiedades, fotos, catálogo público y vista previa para compartir |
| `lead` | Prospectos, historial, seguimientos automáticos, asignación automática, puntuación, formulario del sitio |
| `match` | Coincidencias entre lo que busca un prospecto y las propiedades |
| `appointment` | Citas, disponibilidad de asesores y recordatorios |
| `document` | Archivos subidos, plantillas de contrato, contratos generados y firmas |
| `notification` | Plantillas y envío de mensajes a prospectos (correo, WhatsApp) |
| `billing` | Suscripciones con Stripe (Checkout, portal, webhook idempotente) |
| `subscription` | Plan y estado de la suscripción; vencimiento de pruebas y suscripciones |
| `payment` | Integración anterior con Mercado Pago (su webhook está apagado) |
| `catalog`, `dashboard`, `shared` | Catálogos, métricas del tablero y utilidades comunes |

**Aislamiento por empresa:** las entidades con `@TenantId` se filtran automáticamente por la empresa del token
(`TenantIdentifierResolver`). Sin usuario (sitio público, login, tareas programadas) la sesión es "root" y no se filtra; esos
flujos restringen sus consultas por sí mismos. `TenantGuard` además rechaza peticiones cuyo `companyId` no sea el del token.

### Tareas programadas

| Tarea | Cuándo |
|---|---|
| Recordatorios de citas (`AppointmentReminderService`) | Cada 5 minutos |
| Seguimientos automáticos: tareas vencidas y prospectos sin respuesta (`FollowUpAutomationService`) | Cada hora |
| Recalcular la puntuación de prospectos (`LeadScoringService`) | Diario, 2:00 |
| Marcar suscripciones vencidas / pruebas vencidas (`SubscriptionExpirationScheduler`) | Diario, 2:00 y 3:00 |

## Planes y suscripciones

Los cobros van por **Stripe** (ver "Pagos con Stripe" arriba). Los productos y precios se crean solos en Stripe.

| Plan | Precio (MXN/mes, `PLAN_*_PRICE`) | Usuarios |
|---|---|---|
| Starter | $299 | 2 |
| Pro | $999 | 10 |
| Business | $3,999 | 50 |

- Toda empresa nueva empieza con **14 días de prueba**. Si contrata durante la prueba, el primer cobro llega al terminarla,
  y los cambios de plan en ese periodo no cobran nada.
- Fuera de la prueba, los cambios de plan se hacen en el portal de Stripe con cobro proporcional.
- El plan de la empresa solo cambia cuando Stripe lo confirma por webhook.
- **Límites:** el backend valida el número de usuarios por plan. Los límites de propiedades y prospectos que muestran las
  tarjetas de planes en la app **todavía no se validan**.

## Base de datos

PostgreSQL 16. Flyway aplica al arrancar las migraciones de `src/main/resources/db/migration/` (`V1` … `Vn`).
Hibernate solo **valida** el esquema (`ddl-auto: validate`): todo cambio de tablas va en una migración nueva; nunca se edita
una migración ya aplicada.

## Pruebas y build

```powershell
./mvnw test                    # pruebas (Testcontainers levanta su propio Postgres)
./mvnw verify                  # pruebas + reporte de cobertura en target/site/jacoco/index.html
./mvnw package -DskipTests     # genera target/homeforge-backend-0.1.0.jar
```

## Variables de entorno

Ver `.env.example` (desarrollo) y la tabla de la sección **Producción**. Las principales:

| Variable | Para qué |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexión a PostgreSQL |
| `JWT_SECRET` | Firma de las sesiones (obligatoria en producción) |
| `FRONTEND_URL`, `CORS_ALLOWED_ORIGINS` | URL de la app y orígenes permitidos |
| `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET` | Cobros con Stripe |
| `PLAN_STARTER_PRICE`, `PLAN_PRO_PRICE`, `PLAN_BUSINESS_PRICE` | Precios de los planes (MXN) |
| `UPLOADS_DIRECTORY` | Carpeta de archivos subidos |
| `SERVER_PORT` | Puerto (8080 por defecto) |
| `FORWARD_HEADERS_STRATEGY` | `framework` detrás de un proxy, `none` en local |
| `API_DOCS_ENABLED` | Publicar Swagger en producción (apagado por defecto) |

## Frontend

Repositorio `home-forge-frontend` (React + Vite). En desarrollo corre en http://localhost:5174 y llama a esta API en el
puerto 8080; en producción su nginx sirve la app y reenvía `/api` y `/uploads` a este backend en el mismo dominio.

## Licencia

Proyecto privado. Todos los derechos reservados.
