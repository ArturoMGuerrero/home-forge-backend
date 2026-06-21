# 🎯 Testing Guide - Multi-upload de Imágenes

## 📦 Nuevas Funcionalidades Implementadas

### 1. DELETE Propiedad Completa ✅
**Endpoint:** `DELETE /api/properties/{propertyId}?companyId={companyId}`

**Descripción:** Elimina una propiedad y TODAS sus imágenes del filesystem automáticamente.

**Ejemplo:**
```bash
curl -X DELETE "http://localhost:8080/api/properties/7477b5f3-b056-4fe3-bc18-d069a1d05478?companyId=c8ee6c02-3112-45c5-ab46-59a74634076b"
```

**Comportamiento:**
- ✅ Elimina todos los archivos de imágenes del directorio `uploads/properties/{propertyId}/`
- ✅ Elimina los registros de `property_images` de la BD
- ✅ Marca la propiedad como eliminada (soft delete)
- ✅ No falla si alguna imagen no se puede borrar (log de error pero continúa)

---

### 2. PUT Reordenar Imágenes ✅
**Endpoint:** `PUT /api/properties/{propertyId}/images/reorder?companyId={companyId}`

**Descripción:** Permite cambiar el orden de visualización de las imágenes (drag & drop).

**Request Body:**
```json
{
  "images": [
    { "id": "2d17443c-9aca-4749-820a-3524db8c9c69", "sortOrder": 0 },
    { "id": "9689532e-999e-4c47-a023-3b11d4350571", "sortOrder": 1 },
    { "id": "d20b046f-62a6-4e0f-8f4a-0fcaddf3640f", "sortOrder": 2 }
  ]
}
```

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8080/api/properties/7477b5f3-b056-4fe3-bc18-d069a1d05478/images/reorder?companyId=c8ee6c02-3112-45c5-ab46-59a74634076b" \
  -H "Content-Type: application/json" \
  -d '{
    "images": [
      { "id": "2d17443c-9aca-4749-820a-3524db8c9c69", "sortOrder": 2 },
      { "id": "9689532e-999e-4c47-a023-3b11d4350571", "sortOrder": 0 },
      { "id": "d20b046f-62a6-4e0f-8f4a-0fcaddf3640f", "sortOrder": 1 }
    ]
  }'
```

**Validaciones:**
- ✅ Debe incluir TODAS las imágenes de la propiedad
- ✅ Los `sortOrder` deben estar entre 0 y N-1 (donde N = total de imágenes)
- ✅ No puede faltar ninguna imagen en la lista

---

### 3. Mensajes de Error Mejorados ✅

#### Antes:
```json
{ "error": "Cada propiedad puede tener máximo 12 imágenes." }
```

#### Ahora:
```json
{ "error": "Ya tienes 10 imágenes. Solo puedes subir 2 más (máximo 12)." }
```

---

#### Validación de Tamaño - Antes:
```json
{ "error": "Cada imagen debe pesar máximo 8 MB." }
```

#### Ahora:
```json
{ "error": "La imagen pesa 15 MB. El tamaño máximo es 8 MB." }
```

---

#### Validación de Formato - Antes:
```json
{ "error": "Solo se permiten imágenes JPG, PNG o WebP." }
```

#### Ahora:
```json
{ "error": "El archivo 'foto-casa.bmp' no es válido. Solo se permiten imágenes JPG, PNG o WebP." }
```

---

### 4. Validación de Extensión de Archivo ✅

**Nueva validación:** Ahora se valida tanto el `Content-Type` como la **extensión del archivo**.

**Comportamiento:**
- ✅ Valida el header `Content-Type` (image/jpeg, image/png, image/webp)
- ✅ Valida la extensión del archivo (.jpg, .jpeg, .png, .webp)
- ✅ Rechaza archivos con extensión incorrecta aunque el Content-Type sea válido
- ✅ Mensaje de error específico con el nombre del archivo

**Ejemplo de error:**
```json
{
  "error": "El archivo 'casa-hermosa.exe.jpg' tiene una extensión no permitida. Solo se permiten: .jpg, .jpeg, .png, .webp"
}
```

---

## 🧪 Casos de Prueba

### Test 1: Subir imágenes cuando ya hay 11
```bash
# Si la propiedad tiene 11 imágenes
curl -X POST "http://localhost:8080/api/properties/{id}/images?companyId={companyId}" \
  -F "files=@foto1.jpg" \
  -F "files=@foto2.jpg"

# Respuesta esperada:
{
  "error": "Ya tienes 11 imágenes. Solo puedes subir 1 más (máximo 12)."
}
```

---

### Test 2: Subir archivo muy grande
```bash
# Subir imagen de 15 MB
curl -X POST "http://localhost:8080/api/properties/{id}/images?companyId={companyId}" \
  -F "files=@foto-grande.jpg"

# Respuesta esperada:
{
  "error": "La imagen pesa 15 MB. El tamaño máximo es 8 MB."
}
```

---

### Test 3: Subir archivo con extensión inválida
```bash
# Subir archivo .gif
curl -X POST "http://localhost:8080/api/properties/{id}/images?companyId={companyId}" \
  -F "files=@animacion.gif"

# Respuesta esperada:
{
  "error": "El archivo 'animacion.gif' tiene una extensión no permitida. Solo se permiten: .jpg, .jpeg, .png, .webp"
}
```

---

### Test 4: Reordenar con imagen faltante
```bash
# Propiedad tiene 3 imágenes pero solo enviamos 2
curl -X PUT "http://localhost:8080/api/properties/{id}/images/reorder?companyId={companyId}" \
  -H "Content-Type: application/json" \
  -d '{
    "images": [
      { "id": "uuid1", "sortOrder": 0 },
      { "id": "uuid2", "sortOrder": 1 }
    ]
  }'

# Respuesta esperada:
{
  "error": "Debes especificar el orden de todas las imágenes."
}
```

---

### Test 5: Eliminar propiedad con 12 imágenes
```bash
# Verificar que las imágenes existen en el filesystem
ls uploads/properties/7477b5f3-b056-4fe3-bc18-d069a1d05478/

# Eliminar propiedad
curl -X DELETE "http://localhost:8080/api/properties/7477b5f3-b056-4fe3-bc18-d069a1d05478?companyId=c8ee6c02-3112-45c5-ab46-59a74634076b"

# Verificar que el directorio se vació
ls uploads/properties/7477b5f3-b056-4fe3-bc18-d069a1d05478/
# Debería estar vacío o no existir
```

---

## 📊 Resumen de Mejoras

| Característica | Antes | Ahora |
|----------------|-------|-------|
| Eliminar propiedad | ❌ No existía | ✅ Limpia filesystem |
| Reordenar imágenes | ❌ No existía | ✅ Endpoint completo |
| Mensajes de error | ⚠️ Genéricos | ✅ Descriptivos y específicos |
| Validación de extensión | ⚠️ Solo Content-Type | ✅ Content-Type + extensión |
| Límite de imágenes | ✅ Validado | ✅ Mensaje mejorado |

---

## 🔧 Archivos Modificados

1. **PropertyController.java** - Nuevos endpoints
   - `DELETE /{propertyId}` - Eliminar propiedad
   - `PUT /{propertyId}/images/reorder` - Reordenar imágenes

2. **PropertyService.java** - Lógica de negocio
   - `deleteProperty()` - Elimina propiedad + limpieza de imágenes
   - `reorderImages()` - Actualiza sortOrder de imágenes
   - `addImages()` - Mejoras en validación de límite

3. **PropertyImageStorage.java** - Validaciones mejoradas
   - Validación de extensión de archivo
   - Mensajes de error descriptivos con nombre de archivo y tamaño

4. **Property.java** - Modelo de dominio
   - `reorderImages()` - Método para cambiar orden

5. **PropertyImage.java** - Modelo de imagen
   - Agregado getter `getProperty()`

6. **ReorderImagesRequest.java** - DTO nuevo
   - Request para endpoint de reordenamiento

---

## ✅ Checklist de Implementación

- [x] DELETE propiedad con limpieza de filesystem
- [x] PUT reordenar imágenes
- [x] Validación de extensión + Content-Type
- [x] Mensajes de error descriptivos
- [x] Validación de todas las imágenes en reorder
- [x] Validación de rangos de sortOrder
- [x] Manejo de errores al borrar archivos del filesystem
- [x] Verificación de permisos por companyId en todos los endpoints

---

## 🚀 Próximos Pasos Opcionales

1. **Thumbnails**: Generar versiones optimizadas (200x200, 800x800)
2. **Compresión**: Comprimir automáticamente al 80-90% de calidad
3. **S3/Cloudinary**: Migrar storage para producción
4. **Límite configurable**: Hacer el límite de 12 imágenes configurable por plan

---

**Última actualización:** 2026-06-20
**Backend corriendo en:** http://localhost:8080
**Perfil activo:** sqlserver
