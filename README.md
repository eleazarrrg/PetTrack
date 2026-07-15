# PetTrack 🐾

### Reúne a las mascotas perdidas con su familia.

**PetTrack** es una aplicación móvil que convierte a toda una comunidad en una red de búsqueda: si
tu mascota se pierde, la reportas con su foto y el punto exacto del mapa donde la viste por última
vez, y las personas cercanas pueden ayudarte reportando **avistamientos** en tiempo real hasta
encontrarla. 🐶🐱

---

## 📸 La app en imágenes

<!--
Para que el repositorio luzca ante el cliente, agrega tus capturas reales en docs/screenshots/
con estos nombres. Mientras no existan, estas imágenes aparecerán como "rotas".
-->

| Comunidad y mapa | Reportar mascota | Detalle y avistamientos |
|:---:|:---:|:---:|
| <img src="docs/screenshots/comunidad.png" width="240"/> | <img src="docs/screenshots/reportar.png" width="240"/> | <img src="docs/screenshots/detalle.png" width="240"/> |

| Dashboard de estadísticas | Perfil |
|:---:|:---:|
| <img src="docs/screenshots/dashboard.png" width="240"/> | <img src="docs/screenshots/perfil.png" width="240"/> |

> 🎥 **Video demo:** _(añade aquí el enlace a tu video — YouTube, Drive o un `docs/screenshots/demo.gif`)_

---

## ✨ ¿Qué puedes hacer con PetTrack?

- 🔎 **Reportar una mascota perdida** con foto, descripción (especie, raza, color, tamaño, señas) y
  la **ubicación en el mapa** donde se perdió — aunque ya no estés ahí.
- 🗺️ **Explorar la comunidad**: mira las mascotas perdidas cerca de cualquier zona, filtra por
  especie, distancia, estado o fecha, y **toca el mapa** para buscar en otro sector.
- 👀 **Reportar un avistamiento**: si viste a una mascota, marca dónde y avisas a su dueño.
- 🔔 **Recibir avisos**: el dueño recibe una **notificación** apenas alguien avista a su mascota.
- 📞 **Contactar al dueño** directamente para coordinar la entrega.
- 📊 **Dashboard** con estadísticas de la comunidad: mascotas perdidas vs. encontradas, por especie,
  tiempo promedio de búsqueda y las zonas con más reportes.

---

## 🧭 ¿Cómo funciona? (en 4 pasos)

1. **Crea tu cuenta** e inicia sesión.
2. **Reporta** a tu mascota perdida con su foto y el lugar donde la viste por última vez.
3. La **comunidad la ve** en el mapa y reporta avistamientos cuando la encuentra.
4. **Te avisamos** con una notificación y puedes contactar a quien la vio. 🎉

---

## 📲 Probar la aplicación

**Opción rápida:** instala el APK que está en `app/build/outputs/apk/debug/app-debug.apk`
(o compílalo desde Android Studio con *Run* ▶️). Funciona apenas se instala — sin configurar nada.

**Cuentas de demostración** (o regístrate desde la app):

| Correo | Contraseña |
|---|---|
| `ana@pettrack.test` | `Demo1234` |
| `luis@pettrack.test` | `Demo1234` |
| `marta@pettrack.test` | `Demo1234` |

> Requiere Android 7.0 (o superior) y conexión a internet.

---

## 🛠️ Hecho con

**Android nativo** en **Kotlin** + **Jetpack Compose** (Material 3), con un backend en la nube sobre
**Supabase** (base de datos, autenticación, mapas y almacenamiento de fotos). Mapas con
OpenStreetMap.

📄 **¿Detalles técnicos?** Todo el stack, la arquitectura, la estructura del código, el backend y
cómo compilar están explicados en **[`PRESENTACION_EXAMEN.md`](PRESENTACION_EXAMEN.md)** y, paso a
paso, en **[`DOCUMENTACION_PETTRACK.md`](DOCUMENTACION_PETTRACK.md)**.

---

## 👥 Equipo

Proyecto desarrollado para el **Examen Semestral** — _(agrega aquí los nombres del equipo)_.

<p align="center"><sub>Hecho con 🐾 para ayudar a que ninguna mascota se quede sin volver a casa.</sub></p>
