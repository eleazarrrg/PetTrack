# Guion de presentación — PetTrack 🐾

Guía de **qué decir en cada diapositiva**, por integrante. Viñetas breves y hablables: léelas y
dilas con naturalidad, no las recites palabra por palabra.

## Agenda y reparto

| # | Tema | Presenta | ~Tiempo |
|---|------|----------|:---:|
| 1 | Portada | Juan | 0.5 min |
| 2 | Introducción | Juan | 1.5 min |
| 3 | Propuesta de Valor y Modelo de Negocio | Juan | 2 min |
| 4 | Equipos y Roles | Octavio | 1.5 min |
| 5 | Fases del Proyecto | Octavio | 2 min |
| 6 | Arquitectura y Patrones de Diseño | María | 3 min |
| 7 | Diseño de la Base de Datos | María *(ajustable)* | 2 min |
| 8 | Diseño y Desarrollo del API | María *(ajustable)* | 2 min |
| 9 | App Android (capas, JWT, Retrofit, sensor) | Rafael | 3 min |
| 10 | Dashboard | Jere | 1.5 min |
| 11 | Pruebas y Verificación | Jere | 1.5 min |
| 12 | Demostración en vivo *(ajustable)* | Jere / Rafael | 3 min |
| 13 | Conclusión | Jere | 1.5 min |

**Total ≈ 25 min** — recorten tiempos si el examen es más corto. *(Los "ajustable" son supuestos:
7–8 las asigné a María y la 12 la puse como demo; cámbienlo si no es así.)*

## Consejos rápidos
- Cada quien **cierra con su "Transición"** para dar pie al siguiente sin silencios.
- Hablen del proyecto **real**: nombres de tablas, RPCs y librerías dan credibilidad.
- Tengan la **demo lista de antes**: app instalada, datos sembrados y cuentas de prueba abiertas.
- **Plan B:** si falla el internet, muestren las capturas del README (`docs/screenshots/`).
- Cuentas demo: `ana@pettrack.test`, `luis@pettrack.test`, `marta@pettrack.test` — clave `Demo1234`.

---

# Juan · Diapositivas 1–3

### Diapositiva 1 — Portada · Juan · ~0.5 min
- "Buenas, somos el equipo de **PetTrack** y les vamos a presentar nuestro proyecto del examen semestral."
- "PetTrack es una app para **reunir a las mascotas perdidas con su familia**."
- Presenta al equipo: Juan, Octavio, María, Rafael y Jere.

**Términos clave:** PetTrack, mascotas perdidas, examen semestral, equipo.
**Transición:** "Primero, ¿por qué hicimos esta app? Déjenme contarles el problema."

### Diapositiva 2 — Introducción · Juan · ~1.5 min
- "Cuando se pierde una mascota, la búsqueda es un **caos**: mensajes sueltos en grupos de WhatsApp, publicaciones que se pierden en redes, carteles en postes."
- "La información queda **dispersa**, sin un lugar en el mapa donde se vio por última vez y sin forma sencilla de que quien la encuentre avise al dueño. Y cada hora cuenta."
- "**PetTrack lo centraliza todo:** el dueño publica a su mascota con foto y el punto exacto del mapa; la comunidad la ve, y quien la encuentre reporta un **avistamiento** con su ubicación. El dueño recibe un aviso al instante."
- "Es, en resumen, una **red comunitaria de búsqueda** con ubicación real y en tiempo real."

**Términos clave:** problema, información dispersa, mapa, comunidad, avistamiento, tiempo real.
**Transición:** "Con el problema claro, veamos por qué esto tiene valor y cómo podría sostenerse."

### Diapositiva 3 — Propuesta de Valor y Modelo de Negocio · Juan · ~2 min
- **Propuesta de valor:** "Lo que nos diferencia es combinar **ubicación real en el mapa + comunidad + tiempo real**. No es una red social genérica: está diseñada para una sola misión, encontrar mascotas."
- **¿A quién sirve?** "A los **dueños** que buscan, a la **comunidad** que ayuda, y a **refugios y veterinarias** que manejan casos de mascotas perdidas."
- **Modelo de negocio (propuesta):** dejen claro que es una propuesta a futuro:
  - **Freemium:** la app es gratis; funciones premium (alertas de mayor alcance, radio ampliado, destacar un reporte).
  - **Alianzas** con veterinarias, refugios y **municipios** (integrar sus campañas de mascotas perdidas).
  - **Patrocinios** de tiendas y marcas de mascotas dentro de la comunidad.
- "El foco del proyecto fue el **producto funcional**; el modelo de negocio es la visión de cómo escalaría."

**Términos clave:** propuesta de valor, freemium, alianzas, refugios/veterinarias, patrocinios.
**Transición:** "Ahora Octavio les cuenta quiénes somos y cómo organizamos el trabajo."

---

# Octavio · Diapositivas 4–5

### Diapositiva 4 — Equipos y Roles · Octavio · ~1.5 min
- "Gracias, Juan. Somos cinco y **cada uno defiende la parte que construyó**, así que verán la app contada por quien la hizo."
- **Juan** — introducción, propuesta de valor y modelo de negocio.
- **Octavio (yo)** — gestión del proyecto: organización, roles y fases.
- **María** — el corazón técnico: arquitectura, patrones de diseño y el backend (base de datos y API).
- **Rafael** — la aplicación Android: capas, seguridad con JWT, consumo de la API con Retrofit y el sensor GPS.
- **Jere** — dashboard de estadísticas, pruebas y verificación, y conclusiones.
- "Dividimos por especialidad, pero **todos entendemos el conjunto**: por eso cada quien responde por lo suyo y por cómo encaja con el resto."

**Términos clave:** roles, gestión, arquitectura, backend, Android, JWT, dashboard, pruebas.
**Transición:** "Con el equipo presentado, les cuento cómo llevamos PetTrack de idea a app funcional: las fases."

### Diapositiva 5 — Fases del Proyecto · Octavio · ~2 min
- "No fuimos directo a programar; seguimos un **orden por fases**, y cada fase preparaba la siguiente."
- **1. Análisis y requisitos** — definimos el problema y los módulos: usuario, mascota, comunidad, dashboard y notificaciones.
- **2. Diseño de base de datos y arquitectura** — el esquema en Supabase (tablas, enums, geolocalización con PostGIS) y la arquitectura por capas con MVVM.
- **3. Backend primero (Supabase)** — antes de tocar Android montamos las tablas con **seguridad a nivel de fila (RLS)**, las vistas geográficas, las funciones **RPC** (`pets_nearby`, `report_sighting`, `dashboard_stats`), los triggers de notificación y el bucket de fotos. "Así la app solo tenía que consumir un backend firme."
- **4. Desarrollo Android por capas** — de adentro hacia afuera: `core` (red, sesión, DI), luego `data` (APIs, DTOs, repositorios), `domain`, y por último la `ui` en Jetpack Compose.
- **5. Integración** — login con JWT y refresco de token, mapa con osmdroid, GPS, subida de fotos y notificaciones.
- **6. Pruebas y QA** — 41 pruebas unitarias, manejo de errores robusto (sin fallos silenciosos) y una revisión de seguridad de la RLS.
- **7. Documentación y entrega** — README para el cliente, documentación técnica y esta presentación. "El proyecto **compila y corre al clonar**, sin configurar nada."

**Términos clave:** fases, requisitos, PostGIS, MVVM, backend primero, RLS, RPC, por capas, integración, QA.
**Transición:** "Ese diseño por capas y ese esquema son la base de todo. María lo explica a fondo."

---

# María · Diapositivas 6–8

### Diapositiva 6 — Arquitectura y Patrones de Diseño · María · ~3 min
- Apertura: "PetTrack no es un montón de pantallas sueltas: está organizado en **capas**, cada una con una sola responsabilidad, y encima aplicamos **patrones de diseño** reconocidos."
- **Capas (señalando el diagrama, de arriba hacia abajo):**
  - `ui/` — pantallas en Jetpack Compose y sus ViewModels. "Es lo único que el usuario ve; no sabe nada de HTTP ni de JSON, solo pinta el estado."
  - `data/repository/` — los repositorios. "El cerebro: orquestan la lógica y traducen el JSON del servidor a nuestros modelos limpios."
  - `data/remote/api/` y `data/remote/dto/` — las interfaces **Retrofit** (los endpoints) y los **DTO** (objetos JSON).
  - `domain/model/` — modelos limpios que usa la UI: `Pet`, `NearbyPet`, `DashboardStats`.
  - `core/` — servicios transversales: red, sesión, DI, ubicación, mapa, notificaciones.
  - Regla de oro: "las dependencias apuntan **hacia abajo**: la UI llama al repositorio; el repositorio usa las APIs; nunca al revés."
- **Patrones (nómbralos):**
  - **MVVM + StateFlow (flujo unidireccional):** el ViewModel expone un **estado inmutable**; los eventos suben desde la pantalla, el ViewModel calcula el nuevo estado con `.copy()` y Compose se recompone solo.
  - **Repository:** única puerta a los datos; devuelve `Result` (éxito o error explícitos).
  - **Inyección de dependencias con Hilt:** nadie hace `new`; Hilt provee (arranca en `PetTrackApp` con `@HiltAndroidApp`; módulos como `NetworkModule`).
  - **Programación contra interfaces:** `SessionStore`/`LocationSource` son interfaces con implementación aparte → fáciles de **probar** con dobles falsos.
  - **Observer (StateFlow) y Singleton** (repositorios y servicios viven como instancias únicas vía Hilt).

**Términos clave:** capas, MVVM, StateFlow, Repository, Hilt, inyección de dependencias, inmutabilidad, Observer, Singleton.
**Transición:** "Esa capa de datos habla con un backend en Supabase. Veamos primero la base de datos."

### Diapositiva 7 — Diseño de la Base de Datos · María · ~2 min
- "El backend es **Supabase**, que es PostgreSQL gestionado. Diseñamos **cinco tablas**:"
  - `profiles` (datos del dueño), `pets` (mascotas), `pet_photos` (fotos), `sightings` (avistamientos), `notifications` (avisos).
- **Enums:** `pet_status` (perdida / encontrada / en_busqueda) y `pet_size` (pequeño / mediano / grande).
- **Geolocalización con PostGIS:** la ubicación se guarda como tipo geográfico; sobre las tablas creamos **vistas** `pets_geo` y `sightings_geo` que proyectan latitud y longitud, con `security_invoker` para que **respeten los permisos** del usuario.
- **Seguridad — Row Level Security (RLS):** "Cada usuario solo ve y edita **lo suyo**. Las mascotas perdidas son públicas para la comunidad; los perfiles, solo su propia fila."
- **Triggers:** "Cuando alguien inserta un **avistamiento**, un trigger crea automáticamente la **notificación** para el dueño."
- **Storage:** las fotos van a un bucket público `pet-photos` (límite de 5 MB, solo imágenes).

**Términos clave:** Supabase/PostgreSQL, tablas, enums, PostGIS, vistas geo, RLS, triggers, Storage.
**Transición:** "Y toda esa base se expone como una API. Así la construimos."

### Diapositiva 8 — Diseño y Desarrollo del API · María · ~2 min
- "Supabase nos da una **API REST automática** sobre las tablas, llamada **PostgREST**: con `GET`, `POST`, `PATCH`, `PUT` y `DELETE` operamos las mascotas, perfiles, fotos y avistamientos."
- **Autenticación con JWT (GoTrue):** registro e inicio de sesión devuelven un **token JWT**; cada petición viaja firmada, y la **RLS** usa ese token para saber quién eres.
- **Funciones RPC** (lógica en el servidor) para lo que no es un CRUD simple:
  - `pets_nearby` — mascotas dentro de un radio (con PostGIS).
  - `report_sighting` — registra un avistamiento.
  - `set_pet_location` — fija la ubicación de una mascota.
  - `get_owner_contact` — contacto del dueño (solo si la mascota está perdida).
  - `dashboard_stats` — todas las estadísticas del panel en una sola llamada.
- **Storage API** para subir y servir las fotos.
- "Desde Android **consumimos todo con Retrofit**: una interfaz por API (`PetApi`, `RpcApi`, `AuthApi`, `StorageApi`) y `kotlinx.serialization` para el JSON."

**Términos clave:** PostgREST, REST, JWT/GoTrue, RPC, Storage API, Retrofit, kotlinx.serialization.
**Transición:** "Así consume Rafael esta API desde la app Android."

---

# Rafael · Diapositiva 9

### Diapositiva 9 — App Android (capas, JWT, Retrofit, sensor) · Rafael · ~3 min
- **UI:** "La app está hecha 100 % en **Kotlin** con **Jetpack Compose** (Material 3) y navegación con Navigation-Compose. Cada pantalla tiene su ViewModel, como explicó María."
- **Seguridad con JWT:**
  - "Al iniciar sesión guardamos el token de forma **cifrada** con `EncryptedSharedPreferences` (clave en el Android Keystore) — no en texto plano."
  - "Un `HeaderInterceptor` de OkHttp agrega a cada petición la `apikey` y el `Authorization: Bearer` con el token."
  - "Un `TokenAuthenticator` detecta el **401**, **refresca el token** automáticamente y reintenta; y algo importante: ante un **error de red no cierra la sesión**, solo cuando el token de verdad expiró."
- **Retrofit / OkHttp:** "El acceso a la API son interfaces Retrofit (Retrofit 2.11.0, OkHttp 4.12.0); la conversión de JSON con `kotlinx.serialization`; los interceptores manejan cabeceras y logging (solo en debug)."
- **Sensor GPS:** "Usamos el **FusedLocationProvider** de Play Services para la ubicación. El permiso se pide **en contexto**, cuando tocas 'Usar mi ubicación'."
  - "Y como a veces la perdiste en otro lado, puedes **elegir el punto en el mapa** (OpenStreetMap con osmdroid) — lo mismo para reportar avistamientos."
- Cierre: "Todo esto —UI, seguridad, red y sensor— trabaja sobre las capas que vieron: la pantalla nunca habla con la red directamente."

**Términos clave:** Kotlin, Jetpack Compose, JWT, EncryptedSharedPreferences, HeaderInterceptor, TokenAuthenticator, Retrofit, OkHttp, FusedLocationProvider, osmdroid, permiso en contexto.
**Transición:** "Con los datos ya en la app, Jere les muestra qué hacemos con ellos: el dashboard, y cómo verificamos que todo funciona."

---

# Jere · Diapositivas 10–13

### Diapositiva 10 — Dashboard · Jere · ~1.5 min
- "El **dashboard** resume la actividad de la comunidad en un vistazo."
- **KPIs:** total de mascotas, perdidas, encontradas y en búsqueda.
- **Gráficos:** perdidas vs. encontradas por mes, barras por **especie** y por **raza**, y el **tiempo promedio** de búsqueda.
- **Zonas con más reportes:** un mapa con las áreas de mayor actividad.
- Dato técnico: "Todo el panel se llena con **una sola llamada** a la RPC `dashboard_stats`, y los gráficos los dibujamos nosotros en Compose (no una librería externa pesada)."

**Términos clave:** dashboard, KPIs, dashboard_stats, gráficos en Compose, zonas.
**Transición:** "Y para confiar en todo esto, lo probamos. Aquí va la verificación."

### Diapositiva 11 — Pruebas y Verificación · Jere · ~1.5 min
- "Tenemos **41 pruebas unitarias** (JUnit) que corren en verde."
- "Prueban lo importante: **repositorios**, **ViewModels**, el **mapeo de errores** y las guardas de red."
- Herramientas: "**MockWebServer** para simular el servidor y probar el **refresco del token**; **Turbine** para probar los flujos de estado (`StateFlow`)."
- Verificación adicional: "El proyecto **compila** limpio, pasa el **lint** de Android y genera el APK; además hicimos **QA manual** en equipo probando cada pantalla."
- "El principio que seguimos: **ningún error se traga en silencio** — si algo falla, la app lo dice."

**Términos clave:** 41 pruebas unitarias, JUnit, MockWebServer, Turbine, lint, QA, sin fallos silenciosos.
**Transición:** "Mejor que contarlo, se los mostramos. Vamos a la demo."

### Diapositiva 12 — Demostración en vivo · Jere / Rafael · ~3 min
Guion de la demo (que alguien narre mientras otro maneja el teléfono):
1. **Registrarse** o iniciar sesión (`ana@pettrack.test` / `Demo1234`).
2. **Reportar** una mascota perdida: foto, datos y **marcar la ubicación en el mapa**.
3. Ir a **Comunidad**: ver la mascota, **tocar el mapa** para buscar en otra zona, abrir su detalle.
4. Desde **otra cuenta** (`luis@pettrack.test`), **reportar un avistamiento**.
5. Mostrar que al primer usuario le llegó la **notificación**.
6. Cerrar con el **Dashboard** actualizado.
- **Plan B:** si falla la red, usar las capturas de `docs/screenshots/`.

**Términos clave:** demo, reportar, avistamiento, notificación, dashboard.
**Transición:** "Con eso visto en acción, cerramos con las conclusiones."

### Diapositiva 13 — Conclusión · Jere · ~1.5 min
- **Logros:** "Una app **funcional de punta a punta**: autenticación segura, mapa, avistamientos, notificaciones y estadísticas, con un backend protegido por **RLS** y **pruebas automatizadas**."
- **Aprendizajes:** trabajo por capas, diseñar el backend primero, seguridad con RLS/JWT y manejo serio de errores.
- **Mejoras futuras:** notificaciones push reales, límite de peticiones (rate-limit) en el contacto del dueño, y ofuscar la ubicación pública (snap a una cuadrícula) por privacidad.
- Cierre: "Gracias por su atención — ¿preguntas?"

**Términos clave:** logros, RLS, pruebas, mejoras futuras, push, privacidad.

---

## Checklist antes de presentar
- [ ] Teléfono/emulador cargado y con la app instalada (APK actualizado).
- [ ] Datos de demo sembrados y cuentas de prueba abiertas (`ana` / `luis` / `marta` · `Demo1234`).
- [ ] Internet probado; capturas de `docs/screenshots/` listas por si acaso.
- [ ] Cada quien leyó su sección y **cronometró** su parte.
- [ ] Confirmar quién presenta las diapositivas **7–8** y quién maneja la **demo (12)**.
