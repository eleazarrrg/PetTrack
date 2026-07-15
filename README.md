# PetTrack 🐾

**La app que convierte a tu comunidad en una red de búsqueda para reunir a las mascotas perdidas con su familia.**

---

## El problema

Cuando una mascota se pierde, la búsqueda es caótica: mensajes sueltos en grupos de WhatsApp,
publicaciones que se pierden en redes sociales, carteles en postes. La información queda dispersa,
sin un lugar en el mapa donde se vio por última vez y sin una forma sencilla de que quien la
encuentre avise al dueño. Cada hora cuenta, y esa desorganización juega en contra.

## La solución

**PetTrack** centraliza toda esa búsqueda en un solo lugar. El dueño publica a su mascota con foto y
el **punto exacto del mapa** donde se perdió; las personas cercanas la ven, y cuando alguien la
encuentra reporta un **avistamiento** con la ubicación. El dueño recibe un aviso al instante y puede
contactar a quien la vio. Todo pasa dentro de la app, ordenado y en tiempo real.

---

## Qué puede hacer el usuario

**Reportar una mascota perdida.** Con su foto, especie, raza, edad, color, tamaño, señas
particulares y si tiene collar o chip. La ubicación se marca **en el mapa** —donde realmente se
perdió— aunque el dueño ya esté en otro lugar.

**Explorar la comunidad.** Un mapa muestra las mascotas perdidas de una zona. El usuario puede
filtrar por especie, distancia (1 a 25 km), estado (perdida / en búsqueda) y fecha, y **tocar el
mapa** para buscar en cualquier otro sector de la ciudad.

**Reportar un avistamiento.** Si alguien ve una mascota, marca en el mapa dónde la vio y deja una
nota. Esa información actualiza el rastro de la mascota para todos.

**Recibir avisos y contactar.** El dueño recibe una **notificación** apenas alguien reporta un
avistamiento de su mascota, y puede comunicarse directamente para coordinar el reencuentro.

**Ver estadísticas.** Un panel muestra cuántas mascotas se han reportado, cuántas se han
encontrado, la distribución por especie y raza, el tiempo promedio de búsqueda y las zonas con más
reportes.

**Gestionar su cuenta.** Cada usuario tiene su perfil (nombre, cédula, teléfono, dirección) y el
historial de todos sus reportes, que puede editar o eliminar.

---

## Cómo funciona, en 4 pasos

1. **Crea tu cuenta** e inicia sesión.
2. **Reporta** a tu mascota con su foto y el lugar del mapa donde la viste por última vez.
3. La **comunidad la encuentra**: quien la vea reporta el avistamiento con su ubicación.
4. **Te avisamos** con una notificación y contactas a quien la vio para recuperarla.

---

## Capturas de pantalla

Coloca las imágenes en `docs/screenshots/` con estos nombres para que se muestren aquí.

| Comunidad y mapa | Reportar mascota | Detalle y avistamientos |
|:---:|:---:|:---:|
| <img src="docs/screenshots/comunidad.png" width="240"/> | <img src="docs/screenshots/reportar.png" width="240"/> | <img src="docs/screenshots/detalle.png" width="240"/> |

| Estadísticas | Perfil |
|:---:|:---:|
| <img src="docs/screenshots/dashboard.png" width="240"/> | <img src="docs/screenshots/perfil.png" width="240"/> |

**Video demostración:** _(agrega aquí el enlace a tu video — YouTube, Drive, o un `docs/screenshots/demo.gif`)_

---

## Probar la aplicación

La app funciona apenas se instala, sin ninguna configuración.

- **Instalar el APK:** el archivo está en `app/build/outputs/apk/debug/app-debug.apk`.
- **O compilarla:** abre el proyecto en Android Studio y presiona *Run*.

**Requisitos:** un teléfono o emulador con **Android 7.0 o superior** y conexión a internet.

**Cuentas de demostración** (o puedes registrar una cuenta nueva desde la app):

| Correo | Contraseña |
|---|---|
| `ana@pettrack.test` | `Demo1234` |
| `luis@pettrack.test` | `Demo1234` |
| `marta@pettrack.test` | `Demo1234` |

---

## Confiabilidad y privacidad

- **Tus datos, protegidos:** cada usuario solo puede ver y editar su propia información; la sesión
  se guarda **cifrada** en el teléfono.
- **Ubicación con propósito:** la posición solo se usa para mostrar dónde se perdió o se vio una
  mascota y para buscar por cercanía.
- **Disponible en la nube:** la información se sincroniza en tiempo real para toda la comunidad.

---

## Hecho con

Aplicación **Android nativa** (Kotlin + Jetpack Compose, Material 3) con un backend en la nube
(Supabase) y mapas de OpenStreetMap.

---

<p align="center"><sub>Proyecto del Examen Semestral · Hecho para que ninguna mascota se quede sin volver a casa. 🐾</sub></p>
