# 🚀 Inicio Rápido - HomeForge Backend

## Pre-requisitos

- ✅ Java 21 instalado
- ✅ Base de datos (H2/PostgreSQL/SQL Server)

## Instalación en 3 Pasos

### 1️⃣ Configurar Variables de Entorno

```bash
cp .env.example .env
```

Edita `.env` si es necesario (el default funciona con H2).

### 2️⃣ Iniciar el Backend

**Opción A - H2 (Más rápido):**
```bash
# Windows
start-h2.cmd

# Linux/Mac
./start-h2.sh
```

**Opción B - SQL Server:**
```bash
# Windows
start-sqlserver.cmd

# Linux/Mac
./start-sqlserver.sh
```

### 3️⃣ Verificar

Abre en el navegador: http://localhost:8080/actuator/health

Deberías ver: `{"status":"UP"}`

## ✅ Listo!

El backend está corriendo en **http://localhost:8080**

## Siguiente Paso

Inicia el frontend: [HomeForge-frontend](../HomeForge-frontend/README.md)

## Consola H2 (si usas H2)

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/casaflow`
- Usuario: `sa`
- Password: (vacío)

## Problemas?

Ver [README.md](README.md) para más detalles y solución de problemas.
