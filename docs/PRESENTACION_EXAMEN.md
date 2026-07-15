# PetTrack 🐾 — Documento de Presentación (Examen Semestral)

> Guía técnica del proyecto: **de qué trata la app, con qué herramientas la hicimos, cómo está
> organizado el repositorio parte por parte, cómo funciona el backend, los requisitos y cómo
> correrla.** Para la explicación narrativa "paso a paso" ver también `DOCUMENTACION_PETTRACK.md`.

---

## 1. ¿De qué trata la aplicación?

**PetTrack** es una app **Android nativa** para **reportar y encontrar mascotas perdidas**. Un dueño
publica su mascota con foto, datos y la **ubicación en el mapa** donde se perdió; la comunidad ve las
mascotas cercanas, reporta **avistamientos** (dónde la vieron) y puede **contactar al dueño**. Incluye
un **dashboard de estadísticas** y **notificaciones** cuando alguien avista tu mascota.

**Problema que resuelve:** cuando se pierde una mascota, la información queda dispersa (grupos de
WhatsApp, redes, papelitos). PetTrack la centraliza en un mapa colaborativo con ubicación real.

### Funcionalidades principales
| Módulo | Qué hace |
|---|---|
| **Autenticación** | Registro, login y logout con **JWT**; refresco automático de token; sesión cifrada en el dispositivo. |
| **Reportar mascota** | Alta/edición/borrado con foto, especie, raza, edad, color, tamaño, señas, collar/chip y estado (perdida/encontrada/en búsqueda). Ubicación por **GPS o eligiendo un punto en el mapa**. |
| **Comunidad** | Mascotas cercanas a un punto (radio configurable), filtros por especie/estado/fecha, **tocar el mapa para buscar en otra zona**. |
| **Detalle + Avistamientos** | Ficha de la mascota, mapa con última ubicación y avistamientos, "reportar avistamiento" y "contactar dueño". |
| **Dashboard** | KPIs, perdidas vs. encontradas por mes, barras por especie/raza, tiempo promedio de búsqueda y zonas con más reportes. |
| **Notificaciones** | Un *trigger* en la base de datos avisa al dueño cuando reportan un avistamiento → notificación local del sistema + bandeja in-app. |
| **Perfil** | Datos del usuario (nombre, cédula, teléfono, dirección) e historial de reportes. |

---

## 2. Herramientas y tecnologías (con versiones)

Todo se gestiona con el **catálogo de versiones** `gradle/libs.versions.toml`.

### Plataforma
| | Valor |
|---|---|
| Lenguaje | **Kotlin 2.0.21** |
| Build | **Gradle** + **Android Gradle Plugin 8.7.3** (requiere **JDK 17+**) |
| SDK | `minSdk 24` · `targetSdk 35` · `compileSdk 35` |
| UI | **Jetpack Compose** (BOM 2024.12.01) + **Material 3** |

### Librerías Android
| Herramienta | Versión | Para qué la usamos |
|---|---|---|
| Jetpack Compose + Material 3 | BOM `2024.12.01` | Toda la interfaz (UI declarativa). |
| Navigation-Compose | `2.8.5` | Navegación entre pantallas + barra inferior. |
| Lifecycle / ViewModel + StateFlow | `2.8.7` | Patrón **MVVM** y estado reactivo. |
| **Hilt** (Dagger) | `2.52` | Inyección de dependencias. |
| **Retrofit** | `2.11.0` | Cliente HTTP hacia la API de Supabase (PostgREST/Auth/Storage/RPC). |
| **OkHttp** | `4.12.0` | Interceptores (apikey/Bearer), refresh de token, logging. |
| **kotlinx.serialization** | `1.7.3` | Serializar/deserializar JSON (DTOs). |
| Coroutines | `1.9.0` | Asincronía (red, IO, GPS). |
| **security-crypto** | `1.1.0-alpha06` | **EncryptedSharedPreferences** para guardar los tokens cifrados. |
| **Coil** | `2.7.0` | Carga de imágenes (fotos de mascotas). |
| **osmdroid** | `6.1.20` | Mapas **OpenStreetMap** (sin API key). |
| Play Services Location | `21.3.0` | GPS (`FusedLocationProvider`). |
| Accompanist Permissions | `0.36.0` | Permisos de ubicación/notificaciones en Compose. |

### Pruebas
| Herramienta | Versión | Para qué |
|---|---|---|
| JUnit4 | `4.13.2` | Framework de pruebas unitarias. |
| Coroutines-Test | `1.9.0` | Probar código con corrutinas. |
| MockWebServer | `4.12.0` | Servidor HTTP falso (probar el refresh de token). |
| Turbine | `1.1.0` | Probar `Flow`/`StateFlow`. |

### Backend
**Supabase** (Postgres gestionado): **Auth** (GoTrue, JWT) · **PostgREST** (API REST automática) ·
**PostGIS** (geolocalización) · **Row Level Security (RLS)** · **Storage** (fotos) · **RPC + triggers**.

---

## 3. Arquitectura

Arquitectura por **capas** con patrón **MVVM** y flujo de datos unidireccional:

```
┌──────────────────────── ANDROID (app) ────────────────────────┐
│  ui/          Pantallas Compose + ViewModels (estado con        │
│               StateFlow). No conocen HTTP ni JSON.              │
│                     │ llama                                     │
│  data/repository/   Orquesta la lógica y traduce DTO ⇄ modelo.  │
│                     │ usa                                        │
│  data/remote/api/   Interfaces Retrofit (endpoints).            │
│  data/remote/dto/   Objetos JSON (entrada/salida).              │
│  domain/model/      Modelos limpios que usa la UI.              │
│  core/              Servicios transversales: red, sesión, DI,   │
│                     ubicación, mapa, notificaciones, utilidades.│
└─────────────────────────────┬──────────────────────────────────┘
                              │ HTTPS (Retrofit/OkHttp)
┌─────────────────────────────▼──────────────────────────────────┐
│                    SUPABASE (backend)                           │
│  Auth (JWT) · PostgREST · PostGIS · RLS · Storage · RPC/triggers│
└─────────────────────────────────────────────────────────────────┘
```

**Flujo de un dato** (ej. reportar mascota): `ReportPetScreen` (UI) → `ReportPetViewModel` →
`PetRepository` → `PetApi`/`RpcApi`/`StorageApi` (Retrofit) → Supabase → respuesta → modelo →
`StateFlow` → la UI se recompone.

---

## 4. Estructura del repositorio (parte por parte)

```
Examen Semestral/
├─ app/                         Módulo Android
│  ├─ build.gradle.kts          Dependencias, config de build, BuildConfig (SUPABASE_URL/KEY)
│  └─ src/
│     ├─ main/
│     │  ├─ AndroidManifest.xml  Permisos + Activity
│     │  ├─ java/com/pettrack/app/  (código Kotlin, ver abajo)
│     │  └─ res/                 Recursos (íconos, tema, drawables como ic_map_pin)
│     └─ test/                   Pruebas unitarias (JVM)
├─ gradle/libs.versions.toml    Catálogo central de versiones
├─ build.gradle.kts             Config raíz (plugins)
├─ settings.gradle.kts          rootProject "PetTrack", módulo :app
├─ local.properties(.example)   Override opcional de SUPABASE_URL/KEY (NO se versiona)
├─ README.md                    Guía rápida
├─ DOCUMENTACION_PETTRACK.md    Documentación narrativa detallada
└─ PRESENTACION_EXAMEN.md       Este documento
```

### Código Kotlin — `app/src/main/java/com/pettrack/app/`

**Raíz**
- `MainActivity.kt` — punto de entrada; monta el tema y el `PetTrackNavHost`.
- `PetTrackApp.kt` — clase `Application` con `@HiltAndroidApp` (arranca la inyección de dependencias).
- `ui/RootViewModel.kt` — expone el estado de sesión (autenticado / no) al NavHost.

**`core/` — servicios transversales**
| Paquete | Archivos clave | Responsabilidad |
|---|---|---|
| `core/network` | `NetworkModule`, `HeaderInterceptor`, `TokenAuthenticator` | Retrofit/OkHttp; añade `apikey` + `Bearer`; refresca el token en 401 (y **conserva la sesión ante errores de red**). |
| `core/session` | `SessionStore`, `SessionManager`, `AuthState` | Guarda tokens **cifrados** (EncryptedSharedPreferences) y expone el estado de autenticación. |
| `core/di` | `SessionModule`, `NotificationModule`, `LocationModule`, `DispatcherModule` | Módulos de **Hilt** (proveen dependencias). |
| `core/location` | `LocationSource` (interfaz), `LocationProvider` | GPS vía `FusedLocationProvider`, testeable por interfaz. |
| `core/map` | `OsmMap`, `LocationPickerDialog` | Mapa osmdroid en Compose (marcadores, círculo de radio, **toque para elegir punto**) y diálogo de mapa a pantalla completa. |
| `core/notifications` | `Notifier`, `AppNotifier`, `NotificationCenter` | Notificaciones del sistema + conteo de no leídas (badge). |
| `core/common` | `ErrorMapper`, `HttpExt`, `AppLog`, `ImageDownscaler`, `StorageUrls` | Mensajes de error legibles, verificación de respuestas HTTP, logging, compresión de imagen y URL pública de fotos. |

**`data/` — acceso a datos**
- `data/remote/api/` — interfaces **Retrofit**: `AuthApi`, `ProfileApi`, `PetApi`, `RpcApi`, `StorageApi`, `NotificationApi`.
- `data/remote/dto/` — objetos JSON (`AuthDtos`, `ProfileDto`, `PetDtos`, `CommunityDtos`, `DashboardDtos`, `NotificationDtos`).
- `data/repository/` — **repositorios** que orquestan la lógica: `AuthRepository`, `PetRepository`, `CommunityRepository`, `ProfileRepository`, `DashboardRepository`, `NotificationsRepository`.

**`domain/model/` — modelos limpios** que consume la UI: `Pet`, `CommunityModels`, `DashboardStats`, `AppNotification`.

**`ui/` — pantallas (Compose) + ViewModels**
| Paquete | Pantalla | ViewModel |
|---|---|---|
| `ui/auth/login`, `ui/auth/register` | Login / Registro | `LoginViewModel`, `RegisterViewModel` |
| `ui/community` | Comunidad (mapa + lista + filtros) | `CommunityViewModel` |
| `ui/petdetail` | Detalle + avistamientos + contacto | `PetDetailViewModel` |
| `ui/pets/report` | Reportar/editar mascota | `ReportPetViewModel` |
| `ui/pets/list` | Mis reportes | `MyReportsViewModel` |
| `ui/dashboard` (+ `components/SimpleCharts`) | Dashboard con gráficos propios en Compose | `DashboardViewModel` |
| `ui/notifications` | Bandeja + watcher del badge | `NotificationsViewModel`, `NotificationWatcherViewModel` |
| `ui/profile` | Perfil | `ProfileViewModel` |
| `ui/navigation` | `Routes`, `PetTrackNavHost` (rutas + barra inferior + guardas de sesión) | — |
| `ui/theme` | Colores, tipografía y tema Material 3 | — |

---

## 5. Backend (Supabase)

**Tablas** (todas con **RLS** por usuario):
- `profiles` — datos del dueño (PII: nombre, cédula, teléfono, dirección). SELECT/UPDATE **solo la fila propia**.
- `pets` — mascotas. SELECT: perdidas/en búsqueda (públicas) o las propias. UPDATE/DELETE solo del dueño (`owner_id = auth.uid()`).
- `pet_photos` — fotos (ruta en Storage). Insert solo si eres dueño de la mascota.
- `sightings` — avistamientos. Insert solo si estás autenticado y la mascota está perdida.
- `notifications` — avisos por usuario.

**Enums:** `pet_status (perdida | encontrada | en_busqueda)` · `pet_size (pequeno | mediano | grande)`.

**Vistas:** `pets_geo`, `sightings_geo` — proyectan `latitude`/`longitude` desde PostGIS. Creadas con
`security_invoker = true` (respetan la RLS del usuario que consulta).

**RPCs (funciones):** `pets_nearby` (búsqueda por radio con PostGIS) · `report_sighting` · `set_pet_location`
· `get_owner_contact` (solo mascotas perdidas) · `dashboard_stats`. Son `SECURITY DEFINER` con
`search_path` fijo y validan `auth.uid()` + propiedad/estado internamente.

**Triggers:** al insertar un avistamiento se crea automáticamente una `notification` para el dueño.

**Storage:** bucket **público** `pet-photos` (límite 5 MB; mime `jpeg/png/webp`). Políticas de
lectura/escritura por **carpeta del dueño** (`<uid>/<petId>/archivo`). La app lee las fotos por la URL
pública y las sube autenticado.

---

## 6. Cómo la hicimos (proceso)

1. **Diseño**: definición de módulos (usuario, mascota, comunidad, dashboard, notificaciones) y del
   esquema de datos en Supabase (tablas, enums, PostGIS).
2. **Backend primero**: tablas + **RLS** + vistas geográficas + **RPCs** + triggers + bucket de fotos.
3. **App por capas**: `core` (red/sesión/DI) → `data` (APIs/DTOs/repositorios) → `domain` → `ui`
   (Compose + MVVM), pantalla por pantalla.
4. **Integración**: Auth con JWT + refresh, mapa osmdroid, GPS, subida de fotos, notificaciones.
5. **Calidad**: pruebas unitarias, manejo de errores robusto (nada de fallos silenciosos), revisión de
   seguridad de la RLS y pulido de UX (selección de ubicación en el mapa, mensajes claros).

---

## 7. Requisitos

- **Android Studio** (Ladybug o superior) — trae su propio JDK (JBR 17/21).
- **Android SDK** con `compileSdk 35`.
- Un emulador o dispositivo con **Android 7.0 (API 24)** o superior.
- **Conexión a internet** (el backend es Supabase en la nube).
- *(Solo si compilas por consola)* **JDK 17+** — el `java` por defecto del sistema puede ser Java 8;
  usa el JDK de Android Studio (p. ej. `JAVA_HOME` → `...\Android Studio\jbr`).

> ✅ **Funciona al clonar, sin configurar nada.** La app trae embebidos el `SUPABASE_URL` y la
> `SUPABASE_ANON_KEY` del backend compartido (la *anon key* es pública por diseño; la seguridad real
> la da la RLS). Solo si quieres usar tu propio Supabase, copia `local.properties.example` a
> `local.properties` y sobrescribe los valores.

---

## 8. Cómo compilar y correr

**Android Studio:** abrir el proyecto → *Sync* → *Run* ▶️ en un emulador/dispositivo.

**Por consola (Windows):**
```bash
# JDK del sistema puede ser Java 8; apunta al JBR de Android Studio:
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"   # PowerShell
./gradlew.bat assembleDebug        # genera app/build/outputs/apk/debug/app-debug.apk
```

**Cuentas de prueba** (o regístrate desde la app): `ana@pettrack.test`, `luis@pettrack.test`,
`marta@pettrack.test` — contraseña `Demo1234`.

---

## 9. Pruebas

```bash
./gradlew.bat testDebugUnitTest
```
**41 pruebas unitarias** (0 fallos) que cubren repositorios, ViewModels, el refresco de token con
MockWebServer, el centro de notificaciones, el mapeo de errores y las guardas de subida (HTTP).

---

## 10. Seguridad (resumen)

- **Tokens cifrados** en el dispositivo (EncryptedSharedPreferences, clave en Android Keystore).
- **RLS** en todas las tablas (cada quien ve/edita solo lo suyo; las mascotas perdidas son públicas).
- **JWT** con expiración + refresh; ante error de **red** no se cierra sesión (se reintenta).
- Logs de red con cuerpo **solo en debug** (en release no se filtra información).
- La `anon key` es pública **por diseño**; la protección real vive en la RLS del servidor.

---

## 11. Permisos de Android

`INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`,
`POST_NOTIFICATIONS` (Android 13+). La ubicación y las notificaciones se piden en tiempo de ejecución.

---

## 12. Guion sugerido para la demo

1. **Registrarse** (o iniciar sesión con una cuenta de prueba).
2. **Reportar** una mascota perdida con **foto** y ubicación **elegida en el mapa**.
3. Ir a **Comunidad**, tocar el mapa para **buscar en otra zona**, abrir una mascota.
4. **Reportar un avistamiento** desde otra cuenta → el dueño recibe una **notificación**.
5. Mostrar el **Dashboard** (estadísticas) y el **Perfil**.

---

_Proyecto académico — Examen Semestral. Backend en Supabase; app Android en Kotlin + Jetpack Compose._
