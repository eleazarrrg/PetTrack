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
- Un proyecto de **Supabase** con el esquema desplegado (tablas, RPCs y RLS de este proyecto).

## ⚙️ Configuración

1. **Clona** el repositorio.
2. Crea el archivo **`local.properties`** en la raíz (NO se sube al repo) con:
   ```properties
   sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
   SUPABASE_URL=https://TU-PROYECTO.supabase.co
   SUPABASE_ANON_KEY=TU_ANON_KEY
   ```
   > La `anon key` se obtiene en Supabase → **Project Settings → API**. Es pública por diseño (la seguridad real la dan las políticas RLS), pero **no se versiona** en el repo.
3. En Supabase → **Authentication → Providers → Email**: desactiva **"Confirm email"** para que el registro inicie sesión de inmediato (sin verificación por correo).
4. Abre el proyecto en **Android Studio** → Sync → **Run** en un emulador o dispositivo.
   - Por CLI: `./gradlew assembleDebug` (genera el APK en `app/build/outputs/apk/debug/`).

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
Incluye 28 pruebas unitarias (repositorios, ViewModels, refresh de token con MockWebServer y el centro de notificaciones).

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
