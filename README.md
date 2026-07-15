# PetTrack 🐾

App Android para **reportar y encontrar mascotas perdidas/encontradas**, con comunidad, mapa, avistamientos, dashboard de estadísticas y notificaciones. Proyecto del Examen Semestral.

Backend en **Supabase** (Auth con JWT, PostgREST, PostGIS, RLS, Storage, RPC/triggers), consumido desde Android con **Retrofit**.

---

## ✨ Características

- **Autenticación**: registro, inicio y cierre de sesión con **JWT** (expiración + refresh automático de token).
- **Módulo Usuario**: perfil editable (nombre, cédula, teléfono, dirección) e historial de reportes.
- **Módulo Mascota**: alta/edición/borrado con **foto**, especie, raza, edad, color, tamaño, señas particulares, collar/chip y estado (perdida / encontrada / en búsqueda).
- **Geolocalización (GPS)**: captura de ubicación, mapa (OpenStreetMap), avistamientos e historial, radio/zona de búsqueda.
- **Comunidad**: mascotas cercanas a una ubicación, filtros (especie, zona, fecha, estado), "reportar avistamiento" y contacto con el dueño.
- **Dashboard**: KPIs (total/perdidas/encontradas/en búsqueda), gráfico de perdidas vs. encontradas por mes, barras por especie y raza, tiempo promedio de búsqueda y mapa de zonas con más reportes.
- **Notificaciones**: aviso cuando alguien reporta un avistamiento de tu mascota (trigger en la base de datos → notificación local del sistema + bandeja in-app).

## 🧱 Tecnologías

- **Android**: Kotlin · Jetpack Compose (Material 3) · MVVM + StateFlow · Hilt · Navigation-Compose · Retrofit/OkHttp · kotlinx.serialization · Coil · osmdroid · FusedLocationProvider. `minSdk 24`, `targetSdk 35`.
- **Backend**: Supabase — Auth (JWT), PostgREST (GET/POST/PUT/PATCH/DELETE), PostGIS, Row Level Security, Storage y RPC/triggers.
- **Arquitectura**: capas `core / data / domain / ui`.

## ✅ Requisitos previos

- Android Studio (Ladybug o superior) con su JBR incluido.
- Android SDK (compileSdk 35, build-tools instaladas).
- *(Opcional)* Un proyecto de **Supabase** propio — solo si no quieres usar el backend compartido que ya viene embebido (ver Configuración).

## ⚙️ Configuración

1. **Clona** el repositorio.
2. Abre el proyecto en **Android Studio** → Sync → **Run** en un emulador o dispositivo.
   - Por CLI: `./gradlew assembleDebug` (genera el APK en `app/build/outputs/apk/debug/`).

> ✅ **Funciona al clonar, sin pasos extra.** La app trae embebidos por defecto el `SUPABASE_URL` y la `SUPABASE_ANON_KEY` del backend compartido, así que tus amigos pueden **registrarse e iniciar sesión** apenas compilan. La `anon key` es pública por diseño (la seguridad real la dan las políticas RLS de Supabase).

### (Opcional) Usar tu propio Supabase

Solo si quieres apuntar la app a **otro** proyecto de Supabase, copia la plantilla y sobrescribe los valores:

```bash
cp local.properties.example local.properties
```

```properties
SUPABASE_URL=https://TU-PROYECTO.supabase.co
SUPABASE_ANON_KEY=TU_ANON_KEY
```

`local.properties` está en `.gitignore` (no se versiona). Si usas tu propio proyecto, despliega también el esquema (tablas, RPCs, RLS) y en **Authentication → Providers → Email** desactiva **"Confirm email"** para que el registro inicie sesión de inmediato.

## 🗄️ Base de datos (Supabase)

- **Tablas**: `profiles`, `pets`, `pet_photos`, `sightings`, `notifications`.
- **Enums**: `pet_status (perdida | encontrada | en_busqueda)`, `pet_size (pequeno | mediano | grande)`.
- **RPCs**: `pets_nearby`, `dashboard_stats`, `report_sighting`, `get_owner_contact`, `set_pet_location`.
- **Vistas**: `pets_geo`, `sightings_geo` (proyectan lat/lng con PostGIS).
- **Seguridad**: RLS por usuario en todas las tablas; funciones sensibles como `SECURITY DEFINER` con `search_path` fijo. Fotos en el bucket público `pet-photos`.

## 👤 Cuentas de prueba (demo)

Datos de ejemplo sembrados para la demostración:

| Correo | Contraseña |
|---|---|
| `ana@pettrack.test` | `Demo1234` |
| `luis@pettrack.test` | `Demo1234` |
| `marta@pettrack.test` | `Demo1234` |

También puedes **registrar una cuenta nueva** desde la app.

## 🧪 Tests

```bash
./gradlew testDebugUnitTest
```
Incluye **41 pruebas unitarias** (repositorios, ViewModels, refresh de token con MockWebServer, el centro de notificaciones, el mapeo de errores y las guardas de subida HTTP).

## 📁 Estructura

```
app/src/main/java/com/pettrack/app/
├─ core/        (network, session, di, location, map, notifications, common)
├─ data/        (remote/dto, remote/api, repository)
├─ domain/      (modelos)
└─ ui/          (auth, community, petdetail, pets, profile, dashboard, notifications, navigation, theme)
```

## 📄 Licencia

Proyecto académico (Examen Semestral).
