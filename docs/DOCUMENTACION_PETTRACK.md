# PetTrack — Documentación completa del proyecto

### Cómo se construyó la aplicación, explicado paso a paso (para personas no programadoras)

> **Proyecto:** PetTrack — aplicación Android para reportar y encontrar mascotas perdidas.  
> **Repositorio:** https://github.com/eleazarrrg/PetTrack  
> **Curso:** Examen Semestral (Desarrollo móvil / Android).

---

## Cómo usar este documento (nota para quien lo convierte a Word)

Este archivo está en formato Markdown y explica **todo** el funcionamiento del proyecto en lenguaje sencillo. Al convertirlo a Word conviene: mantener los títulos (`#`, `##`, `###`) como estilos de encabezado para que se genere el índice; conservar las tablas y las listas; y usar una portada con el título principal. El documento está ordenado de lo general a lo específico.

---

## Índice

1. Introducción y visión general
2. Glosario para no programadores
3. Cómo está organizado el código (arquitectura)
4. El servidor y la base de datos (Supabase)
5. Cómo la app se comunica con el servidor (la API y Retrofit)
6. Registro, inicio de sesión y seguridad (autenticación)
7. Reportar y gestionar mascotas (fotos y GPS)
8. Comunidad, mapa y avistamientos
9. Dashboard (estadísticas) y Notificaciones
10. Requisitos del curso, pruebas y cómo ejecutar

---

## Introducción y visión general

### ¿Qué es PetTrack?

**PetTrack** (que en inglés significa algo así como "rastreo de mascotas") es una aplicación para teléfonos Android que sirve para **reportar y encontrar mascotas perdidas o encontradas**. Imagina que tu perro se escapa una tarde: en lugar de imprimir carteles y pegarlos por el barrio, abres PetTrack, publicas la foto y los datos de tu mascota, marcas en un mapa dónde se perdió, y toda una comunidad de personas cercanas puede verlo. Si alguien la ve por la calle, reporta el "avistamiento" desde su propio teléfono y tú recibes un aviso al instante.

> **Nota rápida sobre las palabras raras que verás:** una **aplicación** (o "app") es simplemente un programa que se instala en el teléfono, como WhatsApp o Instagram. **Android** es el sistema de los teléfonos que no son iPhone (Samsung, Xiaomi, Motorola, etc.). Cada vez que aparezca un término técnico nuevo, lo explicaré con una comparación de la vida real.

PetTrack fue desarrollado como **Examen Semestral** de un curso de desarrollo móvil, así que no está hecho por una gran empresa, sino como proyecto de aprendizaje. Aun así, funciona de punta a punta: registro de usuarios, mapa real, fotos, avisos automáticos y estadísticas.

### El problema que resuelve

Cuando una mascota se pierde, el dueño se enfrenta a tres dificultades:

1. **Avisar rápido a mucha gente** que esté cerca de la zona.
2. **Recibir pistas** de quien la haya visto, sin tener que dar su teléfono personal a desconocidos de forma insegura.
3. **Coordinar la búsqueda** sabiendo por dónde anda el animal.

PetTrack ataca esos tres puntos: convierte a los vecinos con teléfono en una red de "ojos" que colaboran, guarda las pistas de forma ordenada en un mapa y conecta al que vio a la mascota con su dueño de manera controlada.

### La gran analogía: el mostrador y la oficina central

Para entender cómo está construido PetTrack, imagina un negocio con dos espacios muy distintos que trabajan juntos:

- **El mostrador de atención** = la app en tu teléfono. Es la parte bonita y visible: los botones, las pantallas, el mapa, las fotos. Es lo que tú tocas y ves. En el mundo de la programación a esto se le llama **frontend** ("la parte de adelante", la cara visible).

- **La oficina central con su archivador gigante y un guardia de seguridad en la puerta** = el servidor en la nube. Es la parte que tú *no* ves: donde de verdad se guardan todos los datos (usuarios, mascotas, fotos, avistamientos) de forma permanente y segura. A esto se le llama **backend** ("la parte de atrás", la trastienda).

> Un **servidor** es simplemente una computadora muy potente que está encendida las 24 horas en algún centro de datos, esperando peticiones. La **nube** solo quiere decir "en internet, en las computadoras de otra empresa", en lugar de dentro de tu teléfono. Así, aunque apagues tu celular, los datos siguen a salvo en la oficina central.

La oficina central de PetTrack la provee una empresa llamada **Supabase**. Supabase nos da, ya armados, tres elementos clave que en la analogía serían:

| En la vida real | En PetTrack (término técnico) | Para qué sirve |
|---|---|---|
| El archivador con carpetas ordenadas | La **base de datos** (PostgreSQL) | Guarda de forma ordenada todos los datos: usuarios, mascotas, fotos, avistamientos |
| El guardia que pide tu credencial en la puerta | La **autenticación** con **JWT** | Confirma quién eres y no deja entrar a extraños |
| El cajón de expedientes de cada persona, que solo su dueño puede abrir | La seguridad por filas o **RLS** | Se asegura de que cada quien solo vea y edite lo que le corresponde |

> Una **base de datos** es un archivador digital súper organizado: en vez de carpetas de papel, guarda la información en tablas, como hojas de Excel gigantes y conectadas entre sí. **PostgreSQL** es la marca concreta de ese archivador (una de las más usadas y confiables del mundo).

### ¿Cómo se hablan el mostrador y la oficina central?

El mostrador (la app) casi nunca guarda los datos por su cuenta; para casi todo, **le pregunta a la oficina central**. Esa conversación de ida y vuelta ocurre por internet, y funciona como pedir algo por teléfono a la oficina:

1. Tú tocas un botón en la app (por ejemplo, "Ver mascotas cercanas").
2. La app llama por internet a la oficina central y le dice: "Dame la lista de mascotas perdidas cerca de este punto".
3. El guardia (la autenticación) revisa tu credencial. Si eres un usuario válido, deja pasar la petición.
4. La oficina busca en el archivador y devuelve la respuesta.
5. La app recibe esa información y la dibuja bonita en la pantalla.

A esa forma estándar de "pedir cosas por internet" se le llama una **API REST**. Piensa en la API como el **menú de un restaurante con un mesero**: tú no entras a la cocina, solo pides del menú ("quiero esto") y el mesero te trae el plato. La app pide, el servidor cocina y responde. Las peticiones más comunes son de cuatro tipos, muy fáciles de recordar:

- **GET** = "muéstrame" (traer información, como leer).
- **POST** = "crea esto nuevo" (por ejemplo, registrar una mascota).
- **PUT / PATCH** = "modifica lo que ya existe" (editar un dato).

En PetTrack, la pieza que se encarga de hacer esas llamadas por internet desde el teléfono se llama **Retrofit**. Es como el **teléfono y la agenda de contactos** de la app: sabe a qué número (dirección de internet) llamar y cómo formular cada pedido.

### Un vistazo a las dos grandes partes (con nombres reales de carpetas)

El código de la app está organizado en carpetas, igual que ordenarías documentos en cajones separados por tema. Los nombres reales son estos:

- **`ui/`** → todo lo *visible*: las pantallas de inicio de sesión (`auth`), la comunidad y el mapa (`community`), el detalle de una mascota (`petdetail`), mis mascotas (`pets`), mi perfil (`profile`), las estadísticas (`dashboard`) y los avisos (`notifications`). Es el "mostrador" propiamente dicho.
- **`data/`** → la parte que *habla con la oficina central*: aquí viven las descripciones de las llamadas al servidor y los "traductores" que convierten la respuesta de internet en algo que la app entiende.
- **`domain/`** → los "moldes limpios" de la información (qué es una Mascota, qué es un Perfil), ya libres de tecnicismos.
- **`core/`** → las herramientas de fondo compartidas por toda la app: la conexión a internet, el guardado seguro de tu sesión, el GPS, el componente del mapa y las notificaciones.

No te preocupes por memorizar esto ahora: en las secciones siguientes cada carpeta se explica con calma. Por ahora basta la idea: **`ui` es la cara, `data` es el teléfono que llama a la oficina, y `core` son las herramientas de la casa.**

### Recorrido rápido: ¿qué puede hacer un usuario?

Este es el paseo completo por la app, tal como lo viviría cualquier persona:

- **Registrarse e iniciar sesión.** Creas una cuenta con tu correo y contraseña. El guardia (autenticación) te entrega una especie de **pulsera de festival con fecha de caducidad**: es el **JWT**, un pase temporal que demuestra que eres tú. Cuando está por vencer, la app pide una nueva sin molestarte (esto se llama *refresh* automático del token).
- **Reportar una mascota.** Subes su foto, especie, raza, edad, color, tamaño, señas particulares, si tiene collar o chip, y si está perdida, encontrada o en búsqueda.
- **Ver mascotas cercanas en el mapa.** La app usa el **GPS** de tu teléfono (el mismo sensor que usa Google Maps para saber dónde estás) y te muestra en un mapa las mascotas reportadas alrededor, con filtros por especie, zona, fecha y estado.
- **Reportar un avistamiento.** Si ves una mascota que aparece en la app, marcas dónde la viste. Ese punto queda guardado con su historial, como dejar una nota de "la vi aquí a tal hora".
- **Contactar al dueño.** Con botones para llamar o enviar correo, pero solo cuando corresponde (por ejemplo, si la mascota sigue activa como perdida), para proteger la privacidad de todos.
- **Ver estadísticas (Dashboard).** Un panel con números y gráficos: cuántas mascotas hay en total, cuántas perdidas, encontradas o en búsqueda, cuánto tarda en promedio una búsqueda y qué zonas concentran más reportes.
- **Recibir avisos.** Cuando alguien reporta que vio *tu* mascota, la oficina central genera automáticamente una notificación y tu teléfono te avisa, además de mostrarte una campanita con un contador de mensajes nuevos.

Todo esto se mueve por la app con una **barra inferior de cuatro pestañas** —Comunidad, Mis reportes, Dashboard y Perfil— como las pestañas de una carpeta que te dejan saltar de una sección a otra con un solo toque.

### La idea que debes llevarte

Si tuvieras que explicar PetTrack en una sola frase sería esta: **un teléfono con una app amigable (el mostrador) que se comunica por internet con una oficina central en la nube (Supabase), la cual guarda todo de forma ordenada y vigila con un guardia de seguridad quién puede ver o cambiar cada cosa.** Todas las funciones —el mapa, las fotos, los avisos, las estadísticas— son variaciones de esa misma conversación entre el mostrador y la oficina central. En las próximas secciones abriremos cada una de esas piezas para ver, paso a paso y sin tecnicismos, cómo se hicieron.

---

## Glosario para no programadores

Este glosario define, en palabras simples y con ejemplos del mundo real, todos los términos técnicos que aparecen en el resto del documento. La idea es que puedas leer cualquier sección de PetTrack y, si te tropiezas con una palabra "rara", vengas aquí y la entiendas de inmediato. Cada término trae una definición corta y una analogía cotidiana.

### 1. Las dos mitades del proyecto: frontend y backend

Casi todo lo que hace PetTrack se reparte entre dos "mundos" que conversan entre sí. Entender esta división es la llave para entender todo lo demás.

- **Frontend (la parte visible).** Es la aplicación que corre en el teléfono: las pantallas, los botones, el mapa, la campana de notificaciones. Es lo único que el usuario ve y toca.
  - *Analogía:* es el **salón de un restaurante**: las mesas, el menú, el mesero que te atiende. Es la cara bonita con la que interactúas.

- **Backend (la parte oculta).** Es el servidor y la base de datos que viven en internet. Ahí se guardan de verdad las mascotas, las fotos, los usuarios y los avistamientos. El teléfono no guarda esa información: se la pide al backend.
  - *Analogía:* es la **cocina y la despensa** del restaurante. No la ves, pero ahí se guarda todo y se prepara lo que pediste. En PetTrack, esa cocina es **Supabase** (lo explicamos más abajo).

### 2. Términos de la app (el frontend)

| Término | Qué es | Analogía |
|---|---|---|
| **App** | Programa que se instala y corre en el teléfono. En este proyecto, la app se llama PetTrack. | Un electrodoméstico que instalas en tu cocina: cumple una función concreta. |
| **APK** | El archivo empaquetado que instala la app en un Android. Es "la app metida en una cajita" lista para instalar. | El paquete cerrado de un mueble para armar: lo abres, lo instalas y ya funciona. |
| **Android** | El sistema operativo de la mayoría de los teléfonos (que no son iPhone). Es el "cerebro" del teléfono sobre el que corre la app. | El sistema eléctrico de una casa: todos los aparatos (apps) funcionan enchufados a él. |
| **Kotlin** | El lenguaje de programación con el que se escribió la app. Es el idioma en que se le dan las órdenes al teléfono. | El idioma en que está escrito un manual de instrucciones: si el aparato "habla" Kotlin, hay que escribirle en Kotlin. |
| **Jetpack Compose** | La herramienta moderna con la que se dibujan las pantallas de la app (botones, listas, tarjetas). Aquí se usa junto con el estilo visual **Material 3**. | Un juego de bloques de construcción: en vez de dibujar cada pantalla a mano, la armas juntando piezas ya hechas (un botón, una tarjeta, una lista). |

### 3. Cómo se guarda la información: base de datos

- **Base de datos.** Es el lugar organizado donde se guarda toda la información de forma permanente: usuarios, mascotas, fotos, avistamientos.
  - *Analogía:* un **archivador gigante lleno de gavetas y carpetas** perfectamente ordenadas.

- **Tabla, fila y columna.** La información se guarda en **tablas** (como hojas de cálculo de Excel). Cada **fila** es un registro completo (por ejemplo, *una* mascota), y cada **columna** es un dato de ese registro (nombre, especie, color, tamaño).
  - *Analogía:* la tabla es una **planilla de asistencia**; cada fila es un alumno y cada columna es un dato suyo (nombre, edad, sección).
  - En PetTrack las tablas principales son: **profiles** (datos del dueño), **pets** (mascotas), **pet_photos** (fotos), **sightings** (avistamientos) y **notifications** (avisos).

- **PostgreSQL.** Es el programa de base de datos concreto que usa el proyecto. Es muy conocido, gratuito y robusto.
  - *Analogía:* si "base de datos" es la idea de un archivador, PostgreSQL es una **marca específica y muy confiable** de archivador.

- **Supabase.** Es el servicio en internet que le da al proyecto, todo junto y listo para usar: la base de datos PostgreSQL, el sistema de usuarios, el guardado de fotos y la puerta de entrada para que la app pida datos. Es el backend completo de PetTrack.
  - *Analogía:* un **kit de cocina profesional ya instalado y conectado**: no tuvimos que construir la cocina desde cero, solo usarla.

### 4. Cómo conversan la app y el servidor

- **API.** Es el conjunto de "ventanillas" oficiales por donde la app le pide cosas al servidor. La app nunca entra a la base de datos directamente: siempre pasa por estas ventanillas.
  - *Analogía:* la **ventanilla de un banco**. No entras a la bóveda; le pides al cajero por la ventanilla y él te trae lo que necesitas.

- **API REST.** Es un estilo muy común de organizar esas ventanillas, basado en direcciones (parecidas a las de una página web) y en un puñado de acciones estándar. PetTrack usa este estilo.
  - *Analogía:* un **catálogo de pedidos con reglas fijas**: todos piden de la misma forma ordenada, así nadie se confunde.

- **PostGREST (PostgREST).** Es la herramienta de Supabase que crea esas ventanillas **automáticamente** a partir de las tablas de la base de datos, sin que nadie tenga que programarlas una por una.
  - *Analogía:* un **recepcionista que abre solito una ventanilla de atención para cada gaveta del archivador**, en cuanto la gaveta existe.

- **Verbos HTTP (GET, POST, PUT, PATCH, DELETE).** Son las cinco acciones básicas que la app le puede pedir al servidor. Cada una hace algo distinto:

  | Verbo | Qué hace | Ejemplo en PetTrack |
  |---|---|---|
  | **GET** | Traer / consultar datos | Ver la lista de mascotas de la comunidad |
  | **POST** | Crear algo nuevo | Reportar una mascota perdida |
  | **PUT / PATCH** | Modificar algo que ya existe | Editar los datos de tu mascota o tu perfil |
  | **DELETE** | Borrar | Eliminar un reporte de mascota |

  - *Analogía:* son los **verbos de un mesero**: *traer* (GET), *anotar un pedido nuevo* (POST), *cambiar un pedido* (PUT/PATCH) y *cancelar un pedido* (DELETE).

- **JSON.** Es el formato de texto ordenado en el que viajan los datos entre la app y el servidor. Usa parejas de "etiqueta: valor" que tanto el teléfono como el servidor entienden.
  - *Analogía:* una **etiqueta de producto** muy clara: "nombre: Firulais, especie: perro, color: marrón". Ambos lados leen lo mismo sin confundirse.

### 5. Seguridad: quién eres y qué puedes ver

- **Autenticación.** Es el proceso de comprobar que eres quien dices ser al iniciar sesión con tu correo y contraseña. En la app vive en la parte **ui/auth** (login y registro).
  - *Analogía:* mostrar tu **cédula en la entrada** para que te dejen pasar.

- **JWT / token.** Cuando inicias sesión, el servidor te entrega un **token**: un pase digital temporal que la app muestra en cada pedido para probar que ya estás identificado. Ese token **expira** (se vence) por seguridad, y la app lo **renueva automáticamente** por detrás (el "refresh") para que no tengas que volver a escribir tu clave todo el tiempo.
  - *Analogía:* la **pulsera de un evento**: te la ponen al entrar, sirve como prueba de que pagaste, y hay que renovarla cada cierto tiempo. En el código, esto lo maneja la parte **core/session** (que guarda los tokens de forma cifrada) y **core/network** (que los renueva).

- **RLS (Row Level Security, o "seguridad por filas").** Es una regla de la base de datos que decide, fila por fila, qué puede ver y tocar cada usuario. Así, aunque dos personas usen la misma app, cada una solo accede a lo suyo.
  - *Analogía:* un **casillero personal con llave**: aunque todos los casilleros estén en el mismo pasillo, tu llave solo abre el tuyo.
  - *Dato importante del proyecto:* PetTrack usa una "anon key" (clave pública) que es pública **a propósito**; la seguridad de verdad la dan estas políticas RLS, no el secreto de esa clave.

### 6. Cómo está organizada la app por dentro (arquitectura)

Todo el código de la app vive bajo el paquete **com.pettrack.app**, dividido en carpetas con roles claros:

- **Retrofit.** Es la herramienta que la app usa para hacer las llamadas por internet al servidor de forma ordenada. Las llamadas se describen en **data/remote/api**.
  - *Analogía:* el **teléfono con marcado rápido** de la app: ya tiene guardados todos los "números" del servidor y sabe exactamente a quién llamar para cada cosa.

- **Repositorio.** Es el intermediario que reúne y organiza los datos: pide al servidor, ordena la respuesta y se la entrega lista a las pantallas. Vive en **data/repository**.
  - *Analogía:* el **jefe de bodega**: tú le pides "necesito las mascotas cercanas" y él se encarga de ir a buscarlas y traértelas ordenadas, sin que tengas que saber de dónde salieron.

- **ViewModel / MVVM.** **MVVM** es la forma en que se organiza la app en tres capas: lo que se ve (las pantallas), lo que piensa (el **ViewModel**) y los datos (los modelos, en **domain/model**). El **ViewModel** es el "cerebro" de cada pantalla: recibe tus toques, pide datos al repositorio y decide qué mostrar. Cada sección de **ui/** (comunidad, dashboard, perfil, etc.) tiene el suyo.
  - *Analogía:* en una obra de teatro, la pantalla es el **escenario**, el ViewModel es el **director** que decide qué pasa, y los datos son el **guion**.

- **Inyección de dependencias (Hilt).** Es un ayudante que **arma y entrega automáticamente** las piezas que cada parte de la app necesita (por ejemplo, entregarle el repositorio correcto a un ViewModel), en vez de que cada parte tenga que construir sus herramientas a mano. Se configura en **core/di**.
  - *Analogía:* un **asistente de cocina** que, antes de que empieces a cocinar, ya te dejó en la mesa todos los ingredientes y utensilios listos.

### 7. Ubicación y mapas

- **GPS / sensor.** El **GPS** es el sensor del teléfono que sabe *dónde estás* en el mundo. PetTrack lo usa para capturar la ubicación de una mascota o del usuario. El código de ubicación está en **core/location** (usa el "FusedLocationProvider" de Android).
  - *Analogía:* la **brújula con dirección exacta** del teléfono: le preguntas "¿dónde estoy?" y te responde con coordenadas.

- **PostGIS / geolocalización.** **Geolocalización** significa trabajar con lugares y coordenadas. **PostGIS** es la extensión que le enseña a la base de datos PostgreSQL a entender mapas y distancias, para poder responder preguntas como "¿qué mascotas hay cerca de este punto?".
  - *Analogía:* es como **darle un GPS y un mapa al archivador** para que, además de guardar datos, sepa calcular cercanías y distancias.

- **Mapa (osmdroid / OpenStreetMap).** El mapa que se ve en la app se dibuja con una herramienta gratuita llamada osmdroid, sobre los mapas libres de OpenStreetMap. El componente del mapa está en **core/map**.
  - *Analogía:* como usar un **mapa comunitario gratuito** en lugar de comprar uno de pago.

### 8. Lógica que vive dentro de la base de datos

Además de guardar datos, la base de datos de PetTrack sabe **hacer cosas** por sí sola:

- **RPC (función en la base).** Una **RPC** es una función guardada dentro de la base de datos que hace un trabajo específico cuando la app la llama. En PetTrack existen varias: **pets_nearby** (buscar mascotas cercanas por radio), **dashboard_stats** (calcular estadísticas), **report_sighting** (registrar un avistamiento), **get_owner_contact** (dar el contacto del dueño, solo si la mascota sigue activa) y **set_pet_location** (guardar la ubicación GPS de una mascota).
  - *Analogía:* un **botón de "combo listo"** en la cocina: en vez de pedir cada ingrediente por separado, aprietas un botón y la cocina prepara todo el plato de una vez.

- **Trigger (disparador).** Un **trigger** es una acción automática que la base de datos ejecuta sola cuando pasa algo. PetTrack usa dos: **handle_new_user** (crea automáticamente el perfil del usuario en cuanto se registra) y **notify_owner_on_sighting** (crea un aviso para el dueño en cuanto alguien reporta un avistamiento de su mascota).
  - *Analogía:* un **sensor de luz con movimiento**: nadie aprieta el interruptor; la luz se enciende sola cuando detecta que alguien entró.

- **Storage.** Es el espacio del servidor donde se guardan **archivos** grandes, como las fotos de las mascotas. En PetTrack es un "bucket" (contenedor) público llamado **pet-photos**.
  - *Analogía:* el **álbum de fotos** del proyecto, separado del archivador de datos porque las fotos ocupan mucho y se guardan aparte.

### 9. Probar que todo funciona

- **Emulador.** Es un teléfono Android "de mentira" que corre dentro de la computadora, para probar la app sin necesitar un celular físico. PetTrack se probó de punta a punta en un emulador, conectado al servidor real, sin fallas.
  - *Analogía:* un **simulador de vuelo**: practicas y pruebas todo en un entorno seguro que se comporta igual que el real.

- **Prueba unitaria.** Es un pequeño examen automático que revisa que una parte concreta del código haga bien su trabajo. PetTrack tiene **28 pruebas unitarias** que revisan los repositorios, los ViewModels, la renovación del token y el centro de notificaciones.
  - *Analogía:* el **control de calidad en una fábrica** que revisa cada pieza por separado antes de armar el producto final, para atrapar fallas a tiempo.

---

## Cómo está organizado el código (arquitectura)

Cuando abres la app de PetTrack y tocas un botón, por dentro pasan muchas cosas: la pantalla le avisa a alguien, ese alguien pide información, otro va a buscarla al servidor y luego todo vuelve para mostrarse. Para que ese "ir y venir" no sea un caos, el código está **ordenado en capas**, donde cada pieza tiene un solo trabajo. En esta sección te explico esa organización con una analogía que todos entendemos: **un restaurante**.

### La idea general: el código como un restaurante

Imagina un restaurante bien organizado. El cliente (tú, usando la app) nunca entra a la cocina ni habla con los proveedores directamente. Solo habla con el mesero. El mesero lleva el pedido a la cocina, la cocina prepara el plato usando recetas, y si falta un ingrediente, la cocina lo pide a un proveedor externo. Cada persona hace **una sola cosa** y se pasan el trabajo entre ellos.

El código de PetTrack está dividido en cuatro grandes "áreas" (en programación se les llama **carpetas** o **paquetes**; son como los cajones donde guardas cosas del mismo tipo). Estas cuatro carpetas viven dentro de `com.pettrack.app`:

| Carpeta en el código | Rol en el restaurante | Qué hace en la app |
|---|---|---|
| `ui/` | El **mesero** y el salón | Todo lo que ves y tocas: las pantallas, los botones, los mapas. Atiende al usuario. |
| `data/repository/` | La **cocina** | Recibe los pedidos, decide de dónde sacar la información y arma la respuesta. |
| `domain/model/` | Las **recetas** | Define cómo debe verse cada "plato" (una mascota, un avistamiento) de forma limpia y ordenada. |
| `core/` | El **equipo de apoyo** (proveedores, luz, agua, teléfono) | Servicios que todos necesitan: la conexión a internet, el GPS, guardar contraseñas, los avisos. |

Veamos cada área con calma.

### La carpeta `ui/`: el mesero que atiende al cliente

`ui` viene de *user interface* (interfaz de usuario), que es simplemente **todo lo que aparece en la pantalla del teléfono**: los textos, las fotos, los botones, los filtros. Dentro de `ui/` hay una subcarpeta por cada zona de la app: `community` (la comunidad y el mapa), `pets` (tus mascotas), `dashboard` (las estadísticas), `profile` (tu perfil), etc.

El mesero (la pantalla) tiene una regla de oro: **no cocina**. No sabe cómo se conecta a internet ni cómo se guarda una foto. Solo muestra cosas y avisa cuando el usuario hace algo. ¿A quién le avisa? A su ayudante de confianza: el **ViewModel**. Ya llegaremos a él.

### La carpeta `data/`: la cocina y el trato con los proveedores

Aquí ocurre el trabajo pesado. Tiene tres partes:

- `data/remote/dto/` son los **moldes de datos**. Un dato que llega de internet viaja en un formato llamado **JSON** (piensa en JSON como un formulario de papel con casillas: nombre, especie, color...). Un **DTO** (*Data Transfer Object*, u "objeto para transferir datos") es el molde que le dice a la app: "cuando llegue este formulario, la casilla del nombre va aquí y la del color allá". Es como el envase en que llega un ingrediente del proveedor.
- `data/remote/api/` son las **interfaces**, que son como el **menú de pedidos que la cocina le pasa al proveedor**. No hacen el trabajo; solo describen qué se le puede pedir al servidor: "traer mascotas", "crear una mascota", "borrarla". En el código se llaman `PetApi`, `RpcApi`, `StorageApi`, etc.
- `data/repository/` son los **repositorios**: los verdaderos **cocineros**. Reciben un pedido sencillo del mesero ("dame las mascotas de este dueño") y se encargan de todo el enredo: pedirlo al servidor, subir la foto, traducir el formulario JSON a algo limpio y devolverlo.

### La carpeta `domain/`: las recetas limpias

`domain/model/` guarda los **modelos**: la versión "de la casa" de cada cosa, limpia y sin tecnicismos del servidor. Por ejemplo, existe un modelo llamado `Pet` (Mascota) con campos claros: nombre, especie, color, tamaño, estado.

¿Por qué separar el molde de internet (el DTO) de la receta limpia (el modelo)? Porque el proveedor a veces manda los ingredientes en envases raros. La cocina los pasa a un recipiente propio y ordenado antes de usarlos. Así, si mañana cambia el proveedor, solo se ajusta la cocina y el resto de la app ni se entera. En el archivo de la cocina de mascotas verás esta traducción hecha con una función llamada `toDomain()`, que agarra el molde de internet (`PetDto`) y lo convierte en la receta limpia (`Pet`).

### La carpeta `core/`: los servicios que todos comparten

`core` significa "núcleo". Son los servicios básicos que cualquier área puede necesitar, como la luz y el agua del restaurante:

- `core/network/` : la conexión a internet (aquí vive un archivo clave que veremos, `NetworkModule.kt`).
- `core/session/` : guarda de forma cifrada (protegida) tus "llaves de entrada" o tokens.
- `core/location/` : habla con el GPS del teléfono.
- `core/map/` : el componente del mapa.
- `core/notifications/` : los avisos.
- `core/di/` : el "armador" de piezas del que hablaremos en un momento.

### MVVM: mesero, ayudante y cocina

Ahora la parte importante. PetTrack usa un patrón (una "forma estándar de organizar") llamado **MVVM**. Las siglas asustan, pero la idea es simple: hay **tres personajes** que se reparten el trabajo para que la pantalla nunca se recargue de tareas.

1. **La Vista (la pantalla)** — el **mesero**. Solo muestra lo que hay y avisa cuando tocas algo. No piensa.
2. **El ViewModel** — el **ayudante personal del mesero**. Guarda "el estado" (qué se está mostrando ahora mismo: la lista de mascotas, si está cargando, si hubo un error) y decide qué hacer cuando el usuario actúa. Es el cerebro de la pantalla, pero **no cocina**: cuando necesita datos, se los pide a la cocina.
3. **El Repositorio** — la **cocina**, que ya conocimos.

La ventaja: si la cocina está ocupada trayendo datos de internet (algo lento), el mesero sigue atendiendo y la pantalla no se congela. Y como cada personaje hace poco, es fácil probar que cada uno funcione por separado (por eso el proyecto tiene 28 pruebas automáticas).

#### Ejemplo real del ViewModel de la Comunidad

Veamos al ayudante de la pantalla de Comunidad, en el archivo `CommunityViewModel.kt`. Cuando entras a esa pantalla, quieres ver mascotas cercanas y poder filtrarlas por especie, zona, fecha y estado.

Primero, el ViewModel guarda **el estado**: una "foto" de todo lo que la pantalla necesita mostrar en este instante. En el código es esta lista de casillas:

```
data class CommunityUiState(
    val loading: Boolean = true,      // ¿estamos cargando?
    val pets: List<NearbyPet> = ...,  // las mascotas encontradas
    val error: String? = null,        // ¿hubo un problema?
    val radiusKm: Int = 5,            // radio de búsqueda: 5 km
    val species: String = "",         // filtro por especie
    ...
)
```

En palabras simples: es una **tarjeta de pedido** con casillas. "Estoy cargando: sí/no", "estas son las mascotas que encontré", "el radio de búsqueda es 5 km". Cuando cambia cualquier casilla, la pantalla se vuelve a dibujar sola para reflejarlo. Fíjate que el radio arranca en 5 km: son valores por defecto para que la app muestre algo apenas abres.

Ahora mira qué pasa cuando el usuario **cambia el radio de búsqueda**. En el código es una sola línea:

```
fun setRadius(km: Int) { _state.update { it.copy(radiusKm = km) }; load() }
```

Traducido: "cuando el usuario elija un radio nuevo (`km`), (1) actualiza la casilla del radio en la tarjeta de pedido y (2) llama a `load()`, que es 've a buscar de nuevo las mascotas'". Dos pasos: anoto el cambio y vuelvo a pedir.

Y `load()` es donde el ayudante llama a la cocina:

```
community.nearby(
    lat = s.center.first,
    lng = s.center.second,
    radiusM = s.radiusKm * 1000.0,
    species = s.species,
    ...
)
    .onSuccess { pets -> ... muestra las mascotas ... }
    .onFailure { e -> ... muestra un mensaje de error ... }
```

En palabras simples: "Cocina (`community`), tráeme las mascotas cercanas (`nearby`) a esta ubicación, en este radio, de esta especie". Y luego hay dos caminos posibles: **si sale bien** (`onSuccess`), guarda las mascotas en la tarjeta de estado para que la pantalla las muestre; **si sale mal** (`onFailure`, por ejemplo se cayó el internet), guarda un mensaje de error amable. Nunca se rompe la app: siempre hay un plan B. Observa también que el ViewModel **no sabe** cómo se piden esas mascotas al servidor; solo le pide el plato a la cocina y espera el resultado. Esa es toda la gracia de MVVM.

#### Ejemplo real del Repositorio de Mascotas

Ahora entremos a la cocina, en `PetRepository.kt`. Aquí se ve el trabajo sucio que el ViewModel prefiere no hacer. Mira lo que pasa cuando **creas una mascota nueva** con foto y ubicación:

```
val created = petApi.createPet(input.toInsert(uid)).firstOrNull()
    ?: error("No se pudo crear la mascota")
if (lat != null && lng != null) rpcApi.setPetLocation(SetLocationRequest(created.id, lat, lng))
if (photo != null) uploadPhoto(uid, created.id, photo)
created.id
```

Paso a paso, en cristiano:

1. **Crea la mascota en el servidor** (`petApi.createPet`). Antes, `input.toInsert(uid)` convierte los datos que tú escribiste (nombre, color, especie) al molde JSON que el servidor entiende, y le pega tu identificador de usuario (`uid`) para saber que la mascota es tuya. Si el servidor no devuelve nada, lanza el error "No se pudo crear la mascota".
2. **Si diste una ubicación GPS** (`if (lat != null...`), la guarda aparte con una función especial del servidor (`setPetLocation`).
3. **Si adjuntaste una foto**, la sube (`uploadPhoto`).
4. Devuelve el identificador (`id`) de la mascota recién creada.

Un solo "pedido" del usuario se convirtió en **tres visitas al servidor**, coordinadas por la cocina. El mesero y su ayudante jamás tuvieron que enterarse de este enredo: para ellos solo fue "crear una mascota".

### Inyección de dependencias con Hilt: el "armador" que entrega las piezas listas

Aquí viene el concepto que más suena a chino, pero es de los más útiles. Fíjate que el ViewModel de la Comunidad necesita una cocina (`community`) y un acceso al GPS (`location`), y que la cocina de mascotas necesita varias herramientas (`petApi`, `rpcApi`, `storageApi`, la sesión...). La pregunta es: **¿quién le entrega esas herramientas a cada quien?**

Una opción mala sería que cada personaje fabrique sus propias herramientas. Sería como pedirle al mesero que, antes de atender, construya su propia libreta, su bolígrafo y hasta el teléfono para llamar a la cocina. Un desastre.

La solución se llama **inyección de dependencias**, y en PetTrack la maneja una herramienta llamada **Hilt**. Piensa en Hilt como el **encargado de bodega** o **"armador"** del restaurante: cuando alguien empieza su turno, el armador le entrega **todas sus piezas ya listas y funcionando**. El mesero recibe su libreta, la cocina recibe sus utensilios, y nadie pierde tiempo fabricando nada. En el código, esa entrega se reconoce por la etiqueta `@Inject` ("por favor, inyéctame esto ya armado"). Por ejemplo, el repositorio de mascotas empieza así:

```
class PetRepository @Inject constructor(
    private val petApi: PetApi,
    private val rpcApi: RpcApi,
    ...
)
```

Traducido: "Yo, la cocina de mascotas, **necesito** un acceso al menú de mascotas (`petApi`), otro para las funciones especiales (`rpcApi`), etc. No los voy a fabricar: que el armador (Hilt) me los entregue listos". Y el ViewModel de la Comunidad lleva una etiqueta parecida, `@HiltViewModel`, para que Hilt también sepa armarlo.

#### ¿Y de dónde saca Hilt esas piezas? Del archivo `NetworkModule.kt`

El armador no adivina cómo construir cosas complicadas, como "la conexión a internet". Alguien tiene que darle **las instrucciones de armado**. Esas instrucciones viven en archivos llamados **módulos**, y el de la conexión es `NetworkModule.kt`, dentro de `core/network/`.

Ese archivo es como un **recetario de montaje**: cada función marcada con `@Provides` ("yo sé fabricar esto") le enseña a Hilt cómo construir una pieza. Por ejemplo, ahí se arma el **cliente de internet** (`OkHttpClient`), que es el "teléfono" con el que la app llama al servidor. Y a ese teléfono se le enchufan dos accesorios importantes:

- Un **interceptor** (`headerInterceptor`). Un interceptor es como un **sello automático**: cada vez que la app manda una carta al servidor, este sello le pega tu "llave de entrada" (el token) sin que nadie tenga que acordarse de hacerlo.
- Un **autenticador** (`tokenAuthenticator`). Tu llave de entrada **caduca** cada cierto tiempo por seguridad. Este componente es como un **portero que renueva tu pase automáticamente**: si el servidor responde "tu pase venció", va, saca uno nuevo y reintenta la operación, todo sin molestarte.

Más abajo, otra receta arma **Retrofit**, que es la herramienta que traduce entre "el idioma de la app" y "el idioma de internet". Y al final del archivo hay una lista de recetas cortitas como esta:

```
fun providePetApi(retrofit: Retrofit): PetApi = retrofit.create(PetApi::class.java)
```

Traducido: "Armador, cuando alguien te pida un menú de mascotas (`PetApi`), fabrícalo usando Retrofit". Así, cada menú de pedidos (mascotas, perfil, avisos, almacenamiento...) queda disponible para quien lo necesite. Lo bonito: la cocina de mascotas nunca supo cómo se fabricó su teléfono ni su menú; **le llegaron ya listos**, gracias al recetario de `NetworkModule.kt`.

### El viaje completo de un dato: de tu dedo al servidor y de vuelta

Juntemos todo con un recorrido real, el que ocurre cuando estás en la Comunidad y **subes el radio de búsqueda de 5 a 10 kilómetros**:

1. **Tú tocas la pantalla.** El mesero (la Vista, en `ui/community`) detecta el toque. No piensa; solo avisa.
2. **El mesero llama a su ayudante.** Ejecuta `setRadius(10)` en el `CommunityViewModel`. El ayudante anota "radio = 10 km" en su tarjeta de estado y ordena buscar de nuevo (`load()`).
3. **El ayudante llama a la cocina.** Le pide al repositorio (`community.nearby(...)`) las mascotas dentro de 10 km de la ubicación actual, con los filtros elegidos.
4. **La cocina prepara el pedido.** El repositorio arma la llamada usando su menú (el `Api`) y su teléfono a internet.
5. **La llamada sale por internet.** El teléfono (`OkHttpClient` de `NetworkModule.kt`) le pega tu token con el sello automático (interceptor) y, si hiciera falta, el portero (autenticador) renueva tu pase. Retrofit lo manda al servidor en Supabase.
6. **El servidor responde.** Devuelve un formulario JSON con las mascotas cercanas.
7. **La cocina limpia el resultado.** El repositorio traduce ese JSON (los moldes DTO) a recetas limpias (los modelos de `domain/`), para entregar algo ordenado.
8. **El resultado vuelve al ayudante.** Si todo salió bien (`onSuccess`), el ViewModel guarda las mascotas en su tarjeta de estado; si algo falló (`onFailure`), guarda un mensaje de error.
9. **La pantalla se actualiza sola.** Como la tarjeta de estado cambió, el mesero vuelve a dibujar la lista y el mapa con las nuevas mascotas. Tú ves el resultado.

Todo eso ocurre en un instante, pero cada personaje hizo **solo su parte**: la pantalla mostró, el ViewModel decidió, el repositorio cocinó, el módulo de red conectó y el servidor respondió. Esa división de trabajo es, en resumen, la arquitectura de PetTrack: un restaurante donde cada quien sabe exactamente cuál es su labor, y donde el "armador" (Hilt) se asegura de que todos empiecen su turno con las herramientas ya listas.

---

## El servidor y la base de datos (Supabase)

Hasta ahora hablamos de la app que vive en el teléfono. Pero una app sola es como una tienda sin bodega: se ve bonita, pero no tiene dónde guardar las cosas. Toda la información de PetTrack (los usuarios, las mascotas, las fotos, los avistamientos, los avisos) tiene que guardarse en algún lugar que esté **siempre encendido y accesible desde internet**, para que la persona A pueda reportar a su perro perdido y la persona B, en otra parte de la ciudad, lo vea en su teléfono minutos después.

Ese "lugar central" es el **servidor**, y en PetTrack ese servidor es **Supabase**.

### ¿Qué es Supabase? (el "edificio de oficinas" del proyecto)

**Supabase** es un servicio en internet que nos entrega, ya montado, todo lo que un proyecto necesita para funcionar por detrás. En vez de comprar computadoras, instalar programas y configurarlo todo a mano, Supabase nos da un "edificio de oficinas" completo y llave en mano. Dentro de ese edificio hay varias oficinas, cada una con un trabajo:

- **La base de datos (PostgreSQL):** el gran archivo donde se guarda todo. Piénsalo como un enorme cuarto lleno de archiveros metálicos.
- **La autenticación (Auth):** la recepción con el registro de quién es quién y quién tiene permiso de entrar (esto se explica en la sección de login).
- **La API automática (PostgREST):** el mostrador de atención por el que la app pide y entrega información.
- **El almacenamiento de archivos (Storage):** la bodega donde se guardan las fotos.
- **PostGIS:** un "departamento de mapas" que sabe calcular distancias entre puntos del planeta.

Vamos a explicar cada una en palabras simples. Antes, dos términos que se repiten:

- **Base de datos:** un archivo gigante y muy ordenado donde se guarda información. No es un documento de Word: es más parecido a una colección de hojas de cálculo (tablas) conectadas entre sí.
- **PostgreSQL:** simplemente es la *marca* del programa de base de datos que usamos. Es como decir "el archivo está guardado en un mueble marca PostgreSQL". Es de los más usados y confiables del mundo.

### Las tablas: los archiveros donde se guarda todo

En una base de datos, la información se organiza en **tablas**. Una tabla es como una **hoja de cálculo**: tiene columnas (los tipos de dato, por ejemplo "nombre", "teléfono") y filas (cada registro concreto, por ejemplo "el usuario Ana", "el perro Toby").

La analogía que usaremos: **cada tabla es un archivero**, cada **fila es una carpeta** dentro de ese archivero, y cada **columna es un campo del formulario** que va escrito dentro de la carpeta.

PetTrack tiene cinco archiveros (tablas). Los definimos en unos "planos de construcción" llamados **migraciones** (más sobre eso al final). Veámoslos uno por uno.

#### 1. `profiles` — el archivero de los dueños

Guarda los datos de cada persona registrada en la app. Cuando alguien crea su cuenta, se abre una carpeta nueva aquí.

| Campo (columna) | Qué guarda | Por qué |
|---|---|---|
| `id` | Un código único de la persona | Es su "número de carpeta". Nunca se repite. |
| `full_name` | Nombre completo | Para mostrarlo y para que un dueño sepa con quién habla |
| `national_id` | Cédula | Dato personal sensible, se guarda protegido |
| `phone` | Teléfono | Para que otros puedan contactar al dueño de una mascota perdida |
| `email` | Correo | Contacto e identificación de la cuenta |
| `address` | Dirección | Dato del perfil |
| `avatar_url` | Dónde está su foto de perfil | Para mostrar su imagen |
| `created_at` / `updated_at` | Cuándo se creó / se editó por última vez | Historial de la carpeta |

Nota importante: cédula, teléfono y correo son **datos sensibles**. Más adelante verás que hay un "guardia" que impide que un desconocido husmee estas carpetas.

#### 2. `pets` — el archivero de las mascotas

El corazón de la app. Cada carpeta es una mascota reportada.

| Campo | Qué guarda | Por qué |
|---|---|---|
| `id` | Código único de la mascota | Su número de carpeta |
| `owner_id` | Código del dueño | Conecta la mascota con su carpeta en `profiles` (ver "relaciones") |
| `name`, `species`, `breed` | Nombre, especie, raza | Identificar al animal |
| `approx_age`, `color`, `size` | Edad aproximada, color, tamaño | Descripción para reconocerlo |
| `distinguishing_marks` | Señas particulares | "Tiene una mancha en la oreja" |
| `has_collar_chip`, `chip_number` | Si tiene collar/chip y su número | Ayuda a identificarlo |
| `status` | Estado: perdida, encontrada o en búsqueda | Define si es visible para la comunidad |
| `lost_at` | Fecha y hora en que se perdió | Para el mapa y las estadísticas |
| `last_known_location` | Última ubicación conocida (un punto en el mapa) | Para dibujarla en el mapa y buscar mascotas cercanas |
| `search_radius_m` | Radio de búsqueda en metros | La "zona" alrededor de donde se perdió |

Dos aclaraciones:

- El campo `status` solo puede tomar tres valores fijos: `perdida`, `encontrada` o `en_busqueda`. Eso se logra con un **enum** ("enumeración"), que es como un formulario con casillas predefinidas: no puedes escribir cualquier cosa, solo marcar una de las opciones válidas. Así nadie escribe "perdidoo" por error. Igual pasa con el tamaño (`pequeno`, `mediano`, `grande`).
- `last_known_location` no guarda "calle 50, edificio tal", sino un **punto geográfico** (latitud y longitud), como una chincheta en un mapa. Eso lo maneja PostGIS, que veremos abajo.

#### 3. `pet_photos` — el archivero de las fotos

Aquí no se guardan las fotos en sí (las imágenes pesadas van a la bodega de Storage), sino **una ficha por cada foto** que dice dónde encontrarla.

| Campo | Qué guarda |
|---|---|
| `id` | Código de la ficha |
| `pet_id` | A qué mascota pertenece la foto |
| `storage_path` | La "dirección" de la foto dentro de la bodega |
| `is_primary` | Si es la foto principal (la portada) |

Es como el índice de un álbum: la ficha dice "la foto de portada de Toby está en tal estante de la bodega". Además, una regla especial garantiza que **cada mascota tenga como máximo una foto marcada como principal**, para que no haya dos portadas peleando.

#### 4. `sightings` — el archivero de avistamientos

Cuando alguien de la comunidad ve una mascota perdida, crea un "avistamiento": un reporte de tipo "vi a este perro aquí, a esta hora".

| Campo | Qué guarda |
|---|---|
| `id` | Código del avistamiento |
| `pet_id` | Qué mascota fue vista |
| `reporter_id` | Quién la vio (qué usuario) |
| `location` | Dónde la vio (punto en el mapa) |
| `note` | Un comentario: "estaba cerca del parque" |
| `sighted_at` | Cuándo la vio |

Estos avistamientos alimentan el **historial** que se muestra en el detalle de la mascota y las chinchetas del mapa.

#### 5. `notifications` — el archivero de avisos

Guarda los avisos que le llegan a cada usuario. Por ejemplo: "¡Alguien vio a tu perro Toby!".

| Campo | Qué guarda |
|---|---|
| `id` | Código del aviso |
| `user_id` | A quién va dirigido el aviso |
| `pet_id`, `sighting_id` | A qué mascota y a qué avistamiento se refiere |
| `title`, `body` | El texto del aviso |
| `read` | Si el usuario ya lo leyó (para el contador de la campanita) |

### Las relaciones: cómo se conectan los archiveros

Los archiveros no están aislados; se referencian entre sí. Esto se llama una **relación**, y en la práctica es un campo de una carpeta que contiene el "número de carpeta" de otra tabla. Es como cuando en un expediente escribes "ver expediente #4472": no copias todo, solo apuntas dónde está.

Las relaciones de PetTrack, en lenguaje cotidiano:

- **Una mascota pertenece a un dueño.** La carpeta de la mascota (`pets`) guarda el `owner_id`, que es el número de carpeta del dueño en `profiles`. Un dueño puede tener varias mascotas; cada mascota tiene un solo dueño.
- **Una foto pertenece a una mascota** (`pet_photos.pet_id` apunta a `pets`).
- **Un avistamiento pertenece a una mascota** y **fue creado por un usuario** (`sightings.pet_id` y `sightings.reporter_id`).
- **Un aviso pertenece a un usuario** y suele referirse a una mascota y a un avistamiento.

Estas conexiones tienen una ventaja de limpieza automática: si se borra un dueño, se borran en cascada sus mascotas; si se borra una mascota, se borran sus fotos y avistamientos. Así no quedan "carpetas huérfanas" apuntando a algo que ya no existe. Es como si al eliminar un expediente principal, el sistema recogiera solito todos los papeles que dependían de él.

### La seguridad RLS: un guardia que revisa credenciales carpeta por carpeta

Aquí está una de las partes más importantes y más elegantes del proyecto.

El problema: la app se conecta al servidor usando una llave pública llamada **anon key** ("llave anónima"). Esta llave **es pública a propósito** y va incluida dentro de la app; cualquiera con conocimientos podría verla. Entonces, si la llave es pública, ¿qué impide que un curioso lea las cédulas y teléfonos de todos los usuarios?

La respuesta es **RLS (Row Level Security)**, que significa "seguridad a nivel de fila". Traducido a la analogía del archivero:

> Imagina que cada archivero tiene un **guardia parado al lado**. Cada vez que alguien pide una carpeta, el guardia revisa su credencial (quién eres) y decide, **carpeta por carpeta**, si te deja verla o no. No basta con entrar al edificio: el guardia revisa fila por fila.

La llave pública solo te deja llegar hasta el mostrador. **La seguridad de verdad la ponen estas reglas del guardia**, llamadas *políticas*. Las reglas de PetTrack, en palabras simples:

- **`profiles` (dueños):** solo puedes ver y editar **tu propia** carpeta. Los datos de otros usuarios (su cédula, su teléfono) son invisibles para ti si intentas leerlos directamente.
- **`pets` (mascotas):** puedes ver **todas tus mascotas** (sin importar su estado), y además puedes ver las mascotas de otros **solo si están perdidas o en búsqueda**. Una mascota marcada como "encontrada" desaparece de la vista de la comunidad. Crear, editar y borrar: solo las tuyas.
- **`pet_photos` (fotos):** puedes ver las fotos de una mascota si esa mascota es visible para la comunidad o es tuya; solo el dueño puede subir o borrar fotos.
- **`sightings` (avistamientos):** puedes reportar un avistamiento de una mascota que esté activamente perdida, y solo puedes editar o borrar **los avistamientos que tú reportaste**.
- **`notifications` (avisos):** solo ves y marcas como leídos **tus propios** avisos.

Un detalle bonito: para las mascotas se combinan **dos reglas con un "o"**: "esta carpeta es visible si la mascota está perdida/en búsqueda **o** si tú eres el dueño". Así, con una regla sencilla, se logra el comportamiento exacto que queríamos: la comunidad ve lo que está perdido, y cada dueño ve además todo lo suyo.

### Las funciones RPC: botones especiales del mostrador

Normalmente la app le pide cosas simples al servidor: "dame las mascotas", "guarda esta carpeta". Pero algunas tareas son demasiado complicadas o delicadas para hacerlas "a mano" desde el teléfono. Para esas, se crean **funciones RPC**.

**RPC** significa "llamada a un procedimiento remoto". En simple: es un **botón especial en el mostrador** que ejecuta una receta ya preparada en el servidor. La app solo aprieta el botón y le pasa unos datos; toda la lógica pesada ocurre del lado del servidor, no en el teléfono. PetTrack tiene cinco:

- **`pets_nearby` (mascotas cercanas):** le dices "estoy en este punto del mapa, muéstrame las mascotas perdidas en un radio de X metros" y te devuelve la lista ordenada de la más cercana a la más lejana, con la distancia calculada. Es el motor de la pantalla de Comunidad.
- **`dashboard_stats` (estadísticas):** aprietas el botón y el servidor **hace todas las cuentas** (cuántas perdidas, cuántas encontradas, promedio de tiempo de búsqueda, conteos por especie y por raza, zonas con más reportes) y te las devuelve empaquetadas. El teléfono no cuenta nada: solo recibe los números ya listos para pintar los gráficos.
- **`report_sighting` (registrar avistamiento):** cuando reportas que viste una mascota, este botón hace varias cosas de un tirón: verifica que la mascota siga perdida, guarda tu avistamiento a tu nombre, y actualiza la "última ubicación conocida" de la mascota a ese punto. Es como un formulario inteligente que, al entregarlo, dispara varias acciones ordenadas.
- **`get_owner_contact` (contacto del dueño):** este es el más delicado y muestra por qué las RPC son tan útiles para la privacidad. Recuerda que el guardia RLS **prohíbe** leer los datos de otros usuarios. Entonces, ¿cómo llama alguien al dueño de un perro perdido? Con este botón: le pasas el código de la mascota y **solo si esa mascota está perdida o en búsqueda**, te devuelve nombre, teléfono y correo del dueño. Si el dueño ya la marcó como "encontrada", el botón deja de entregar el contacto. Es como una recepcionista que te da el teléfono de alguien **solo si esa persona tiene un caso activo abierto**, y nunca de forma general.
- **`set_pet_location` (guardar ubicación GPS):** toma la latitud y longitud que capturó el GPS del teléfono y las guarda correctamente como un punto geográfico en la carpeta de la mascota. La app manda dos números sencillos y el servidor los convierte al formato de mapa que PostGIS entiende.

Un matiz técnico dicho en simple: algunas de estas funciones corren "con permisos de administrador" (en la jerga, `SECURITY DEFINER`) para poder hacer su trabajo, pero **cada una vuelve a revisar las reglas por su cuenta** (por ejemplo, `get_owner_contact` verifica que la mascota esté perdida). O sea, aunque el guardia general les abre la puerta, ellas traen su propio guardia interno. Están construidas con cuidado para que no se puedan usar de forma indebida.

### Los triggers: acciones automáticas que se disparan solas

Un **trigger** ("disparador" o "gatillo") es una **regla automática** que la base de datos ejecuta sola cuando pasa algo, sin que nadie la llame. La analogía perfecta es el **sensor de una puerta automática**: no aprietas ningún botón; caminas hacia la puerta y esta se abre sola porque un sensor detectó tu presencia.

PetTrack usa dos:

- **`handle_new_user` (crear el perfil al registrarse):** en el momento exacto en que una persona se registra, este disparador **crea automáticamente su carpeta** en `profiles` con su nombre y correo. Sin él, tendrías una cuenta pero ninguna carpeta de perfil. Es como si, al entregar tu solicitud de ingreso en recepción, apareciera al instante tu expediente ya abierto.
- **`notify_owner_on_sighting` (avisar al dueño ante un avistamiento):** cuando alguien registra un avistamiento de una mascota, este disparador **crea automáticamente un aviso** en el archivero `notifications` dirigido al dueño de esa mascota. El dueño no tuvo que pedir nada; el sistema reaccionó solo. Luego la app recoge ese aviso y lo muestra como notificación en el teléfono, con la campanita y su contador.

La diferencia clave entre RPC y trigger: la **RPC** es un botón que **tú aprietas**; el **trigger** salta **solo** cuando ocurre cierto evento.

### PostGIS: el "departamento de mapas" que sabe de distancias

Una base de datos normal sabe sumar números y comparar textos, pero no sabe geografía. No entiende que dos puntos en un mapa están a "420 metros" ni que la Tierra es curva.

**PostGIS** es un añadido que le da a PostgreSQL superpoderes geográficos. Con él, el servidor puede:

- Guardar **puntos del planeta** (latitud/longitud) como un tipo de dato de verdad, no como texto suelto.
- **Calcular distancias reales** entre dos puntos.
- Responder preguntas como "¿qué mascotas están dentro de este radio de 3 km?" de forma rápida y precisa.

Es exactamente lo que hace posible la pantalla de Comunidad ("mascotas cerca de mí") y el mapa de zonas con más reportes del Dashboard. Cuando `pets_nearby` te dice que un perro está "a 420 metros", ese cálculo lo hizo PostGIS.

Un detalle práctico de cómo se hizo: los puntos geográficos son incómodos de mandar por internet en su formato interno. Por eso, para **guardar** una ubicación, la app manda solo dos números simples (latitud y longitud) y el servidor arma el punto; y para **leer** ubicaciones se usan unas **vistas** llamadas `pets_geo` y `sightings_geo`. Una **vista** es como una "ventanilla de traducción": muestra la misma información del archivero, pero convirtiendo el punto geográfico de vuelta a latitud y longitud fáciles de usar. Así el teléfono nunca tiene que lidiar con el formato complicado.

### El Storage de fotos: la bodega de imágenes

Las fotos son archivos pesados, y meterlas dentro de los archiveros de datos sería como guardar cajas grandes en un fichero de papeles: no cabe y todo se vuelve lento. Por eso las imágenes van a una **bodega** aparte llamada **Storage**.

Dentro de Storage hay un **bucket** (literalmente "cubo"; piénsalo como una **bodega o carpeta grande** con nombre) llamado **`pet-photos`**. Sus reglas:

- Es de **lectura pública**: cualquiera puede *ver* las fotos de las mascotas perdidas (tiene sentido: mientras más gente vea la foto, más fácil encontrar al animal). La app las muestra descargándolas por internet.
- La **escritura está bloqueada**: solo el dueño puede subir fotos, y solo dentro de su propia "gaveta". Cada foto se guarda en una ruta ordenada con la forma `dueño / mascota / archivo`, y hay una regla que verifica que la primera parte de esa ruta sea **tu** identificador. Así nadie puede subir fotos haciéndose pasar por otro.

El flujo completo es: la app sube la imagen a la bodega `pet-photos`, la bodega devuelve la "dirección" del archivo, y esa dirección se guarda como una ficha en el archivero `pet_photos`. Cuando hay que mostrar la foto, la app lee la ficha, obtiene la dirección y descarga la imagen de la bodega.

### Aclaración importante: esto ES "la API propia" que pide el curso

El examen exige construir una **API propia**, y a veces se confunde eso con "programar un servidor entero desde cero". No hace falta. En este proyecto, **la API propia son precisamente estas tres cosas juntas**:

1. **El esquema** de la base de datos: los cinco archiveros (tablas), los enums, las vistas y sus relaciones.
2. **Las reglas RLS**: el guardia que decide quién ve y edita cada fila.
3. **Las funciones RPC**: `pets_nearby`, `dashboard_stats`, `report_sighting`, `get_owner_contact` y `set_pet_location`, más los triggers `handle_new_user` y `notify_owner_on_sighting`.

Todo eso, combinado, define un conjunto de "servicios" propios y a la medida de PetTrack, con su propia lógica de negocio y sus propias reglas de seguridad. Supabase se encarga de exponerlos automáticamente por internet (esa capa se llama **PostgREST**, que convierte las tablas y funciones en direcciones web que la app puede llamar). La app Android los consume desde su carpeta `data/remote/api` (por ejemplo el archivo `RpcApi.kt` para las funciones RPC, `PetsApi.kt` para las mascotas, etc.), usando distintos verbos de internet: GET para leer, POST para crear, PUT para reemplazar, PATCH para modificar un dato y DELETE para borrar. Es decir: aunque no escribimos un servidor "a mano", **sí diseñamos y programamos toda la lógica y la seguridad del backend**, que es justo lo que el requisito busca.

### Cómo se construyó todo esto, en orden (las migraciones)

Un último punto sobre "el cómo". Toda esta estructura no se armó haciendo clics al azar, sino con **migraciones**: son como los **planos de construcción de un edificio, aplicados por pasos y en orden**. Cada migración es un archivo con instrucciones que se corren una sola vez, y el orden importa porque cada paso se apoya en el anterior:

1. Encender PostGIS (el departamento de mapas).
2. Crear los enums (las opciones fijas como el estado de la mascota).
3. Crear los cinco archiveros (tablas) con sus índices.
4. Crear el trigger que abre el perfil al registrarse.
5. Encender RLS y poner todas las reglas del guardia.
6. Crear la función de contacto del dueño.
7. Crear la bodega de fotos y sus reglas.
8, 9, 10. Crear las funciones `pets_nearby`, `dashboard_stats` y `report_sighting`.

Al final se pasó un **revisor de seguridad automático** de Supabase (los "advisors") para confirmar que no quedaran huecos: que RLS esté activo en todas las tablas y que las funciones estén bien blindadas. En resumen: el backend de PetTrack no es una caja negra prestada, sino una estructura diseñada, ordenada y asegurada paso a paso, y esa es la parte del servidor que hace posible todo lo que se ve en el teléfono.

---

## Cómo la app se comunica con el servidor (la API y Retrofit)

Hasta ahora hemos hablado de la app que vive en el teléfono. Pero esa app, por sí sola, no sabe qué mascotas se reportaron ayer en tu barrio ni guarda tu usuario. Toda esa información vive en un **servidor** (una computadora potente encendida las 24 horas, en un servicio llamado Supabase). Esta sección explica el "cartero" que va y viene entre el teléfono y ese servidor.

### Primero, ¿qué es una API?

Imagina que vas a un restaurante. Tú no entras a la cocina a agarrar los ingredientes: le pides al mesero desde una **carta (el menú)**, y el mesero lleva tu pedido a la cocina y te trae el plato. Nunca ves lo que pasa adentro; solo pides cosas de una lista permitida y recibes el resultado.

Una **API** (siglas en inglés de "Interfaz de Programación de Aplicaciones") es exactamente ese mesero. Es la lista de cosas que la app tiene permitido pedirle al servidor: "dame las mascotas de este dueño", "guarda esta mascota nueva", "bórrala". La app nunca toca la base de datos directamente; siempre pasa por la API, igual que tú nunca entras a la cocina.

Cuando esa "carta" sigue un estilo ordenado y estándar que casi todo internet usa, se le llama **API REST**. No hay que memorizar qué significa la sigla; lo importante es que es una forma organizada y predecible de pedir cosas.

### Los verbos HTTP: las cinco formas de pedir

Cada pedido a un servidor de tipo REST lleva un **verbo HTTP**, que es simplemente la *intención* del pedido: no es lo mismo decir "muéstrame" que "bórralo". HTTP es el idioma que hablan los navegadores y las apps para conversar con servidores por internet. Piensa en los verbos como distintos tipos de trámite que llevas a una oficina:

| Verbo HTTP | Qué hace | Analogía del mundo real |
|------------|----------|--------------------------|
| **GET** | Leer / consultar información | Pedir *ver* un expediente (no lo cambias, solo lo lees) |
| **POST** | Crear algo nuevo | Llenar un formulario y entregarlo por primera vez |
| **PUT** | Reemplazar un registro completo | Rehacer todo el formulario desde cero y sustituir el viejo |
| **PATCH** | Actualizar solo una parte | Tachar un solo dato del formulario y corregirlo |
| **DELETE** | Borrar | Pedir que rompan el expediente |

Es la misma diferencia entre *leer* una carta, *escribir* una carta nueva, *reescribirla entera*, *corregir un renglón* o *tirarla a la basura*.

### Retrofit: el "traductor" entre Kotlin y el servidor

Aquí aparece la pieza clave. El servidor no entiende el lenguaje Kotlin en el que está escrita la app; el servidor entiende pedidos por internet (direcciones web, verbos HTTP, textos en formato JSON). Escribir esos pedidos a mano, uno por uno, sería tedioso y lleno de errores.

**Retrofit** es una herramienta (una librería) que actúa como **traductor automático**. Nosotros escribimos una función normal de Kotlin —por ejemplo `getPets()`— y Retrofit la convierte por detrás en un pedido de verdad por internet, espera la respuesta del servidor y nos la entrega ya lista. Es como tener un mesero bilingüe: tú le hablas en tu idioma (Kotlin) y él le habla a la cocina en el idioma de la cocina (HTTP), sin que tú tengas que aprenderlo.

Lo bonito es que a nosotros nos basta con **describir** qué queremos pedir; Retrofit se encarga del trabajo sucio.

### Las interfaces *Api: los "menús" de lo que se puede pedir

En el proyecto, esos "menús" están en la carpeta `data/remote/api`, y son tres archivos. Cada uno es una **interfaz** (en Kotlin, una interfaz es una simple lista de funciones sin el detalle interno; solo dice *qué* se puede hacer, y Retrofit rellena el *cómo*). Piénsalos como tres cartas distintas de un mismo restaurante:

- **`PetApi.kt`** — el menú de todo lo relacionado con mascotas: listarlas, ver una, crearla, reemplazarla, corregirla, borrarla, y también sus fotos y avistamientos.
- **`AuthApi.kt`** — el menú de la cuenta del usuario: registrarse, iniciar sesión y cerrar sesión.
- **`RpcApi.kt`** — un menú especial de "funciones inteligentes" que corren dentro del servidor (mascotas cercanas, estadísticas, reportar un avistamiento, etc.). Más abajo lo explicamos.

Veamos una línea real de `PetApi.kt`, traducida palabra por palabra:

```kotlin
@GET("rest/v1/pets_geo")
suspend fun getPets(...): List<PetDto>
```

- `@GET("rest/v1/pets_geo")` — esto es una **anotación**, una etiqueta que Retrofit lee. Le dice dos cosas: el verbo es **GET** (solo vengo a *leer*) y la dirección a la que hay que ir dentro del servidor es `rest/v1/pets_geo` (el "estante" donde viven las mascotas con su ubicación).
- `suspend fun getPets(...)` — declara una función llamada `getPets` ("dame las mascotas"). La palabra `suspend` significa que la función puede *esperar* la respuesta del servidor sin congelar el teléfono; mientras el pedido viaja por internet, la app sigue respondiendo con normalidad.
- `: List<PetDto>` — promete que la respuesta será una *lista de mascotas*. (Un `PetDto` es simplemente el molde de datos de una mascota; lo vimos en la sección de moldes de datos.)

Fíjate que **no escribimos ni una sola línea de "cómo conectarse a internet"**. Solo describimos el pedido con etiquetas, y Retrofit hace el resto. Eso es lo que lo vuelve un traductor tan cómodo.

### Cómo se ven los verbos en el código real

En `PetApi.kt` están los cinco verbos, cada uno con su etiqueta al inicio:

- `@GET("rest/v1/pets_geo")` en `getPets` y `getPet` → **leer** mascotas.
- `@POST("rest/v1/pets")` en `createPet` → **crear** una mascota nueva.
- `@PUT("rest/v1/pets")` en `replacePet` → **reemplazar** una mascota entera.
- `@PATCH("rest/v1/pets")` en `updatePet` → **corregir** un dato (por ejemplo, cambiar el estado de "perdida" a "encontrada").
- `@DELETE("rest/v1/pets")` en `deletePet` → **borrar** la mascota.

Algunas funciones llevan además la etiqueta `@Body`. Eso marca la información que *viaja adentro* del pedido, como el contenido de un sobre. Por ejemplo, al crear una mascota (`createPet`), el `@Body` lleva todos los datos de la mascota nueva (nombre, especie, color…). En cambio, un `@Query` es un dato pequeño que va pegado a la dirección, como poner el número de expediente en el asunto de la carta: `@Query("id")` le dice al servidor *cuál* mascota queremos.

### El menú especial: RpcApi (funciones que corren en el servidor)

`RpcApi.kt` es un caso curioso. Todas sus funciones usan **POST**, aunque algunas en realidad solo *consultan* datos (como `petsNearby`, que trae las mascotas cercanas, o `dashboardStats`, que trae las estadísticas). ¿Por qué POST si solo leen?

Porque estas no son consultas simples a una tabla, sino **funciones inteligentes que ya viven dentro del servidor** (se llaman funciones RPC). Es como pedirle a la cocina no un ingrediente, sino un plato preparado con una receta compleja: "calcúlame las mascotas perdidas en 5 kilómetros a la redonda de este punto". Como hay que *enviarle* al servidor los datos de entrada (tu ubicación, el radio de búsqueda, los filtros) dentro del sobre `@Body`, se usa POST. Cada función de este archivo apunta a una dirección que empieza con `rest/v1/rpc/`, que es la zona del servidor reservada para estas recetas.

### Los headers: las "credenciales" que se muestran en cada pedido

Falta un detalle de seguridad. El servidor no le abre la puerta a cualquiera: en **cada** pedido hay que mostrar una identificación, igual que enseñas tu carnet al entrar a un edificio. Esa identificación viaja en los **headers** ("encabezados"), que son etiquetas al principio de cada pedido con información sobre quién pide y con qué permiso.

Sería un fastidio pegar el carnet a mano en cada una de las funciones de las tres cartas. Por eso existe un archivo que lo hace automáticamente para *todos* los pedidos: `core/network/HeaderInterceptor.kt`.

Un **interceptor** es como un guardia en la puerta de salida: cada pedido que sale del teléfono pasa por él *antes* de irse a internet, y el guardia le grapa las credenciales sin que el resto del código tenga que preocuparse. Este guardia añade dos etiquetas:

- **`apikey`** — una llave pública que identifica *a qué aplicación/proyecto* pertenece el pedido. Es como el nombre del edificio al que quieres entrar: dice "vengo al proyecto PetTrack". No es secreta y está diseñada para ser pública.
- **`Authorization: Bearer <token>`** — esta es la credencial *personal*. El "token" es un pase temporal que el servidor te entregó cuando iniciaste sesión (lo vimos en la parte de autenticación). La palabra `Bearer` significa literalmente "portador": *quien porta este pase, entra*. Es como una pulsera de un evento: mientras la lleves puesta y no haya vencido, te dejan pasar.

Veamos el corazón del archivo, traducido:

```kotlin
val bearer = session.accessToken ?: BuildConfig.SUPABASE_ANON_KEY
```

- Esta línea decide *qué pase* mostrar. Si el usuario ya inició sesión, existe un `accessToken` (su pulsera personal) y se usa ese. El símbolo `?:` significa "y si no hay ninguno, usa esto otro": cuando nadie ha iniciado sesión todavía, se usa la llave pública anónima (`SUPABASE_ANON_KEY`), que es el permiso mínimo que el servidor exige incluso para pantallas públicas.
- Luego, las dos líneas siguientes graban las etiquetas `apikey` y `Authorization` en el pedido antes de dejarlo salir.

Gracias a este guardia central, ninguna de las funciones en `PetApi`, `AuthApi` o `RpcApi` tiene que acordarse de las credenciales: **se añaden solas a cada pedido, sin excepción.**

### El viaje completo, en una frase

Cuando tocas un botón en la app, ocurre esto: la app llama a una función de Kotlin (por ejemplo `getPets()`) → **Retrofit** la traduce a un pedido de internet con su verbo (GET) y su dirección → el **HeaderInterceptor** le grapa las credenciales (`apikey` y el pase `Bearer`) → el pedido viaja a Supabase → el servidor comprueba tus permisos, hace el trabajo y devuelve la respuesta en JSON → Retrofit la traduce de vuelta a objetos de Kotlin que la app puede mostrar en pantalla. Todo esto, en menos de un segundo y sin que el usuario vea nada de la maquinaria.

---

## Registro, inicio de sesión y seguridad (autenticación)

Antes de que alguien pueda reportar una mascota perdida, publicar un avistamiento o recibir avisos, PetTrack necesita saber **quién es esa persona**. A eso se le llama *autenticación*: el proceso de comprobar tu identidad (parecido a mostrar tu documento en la entrada de un edificio). En esta sección explicamos, paso a paso y sin tecnicismos, cómo la app te deja crear una cuenta, entrar, mantenerte dentro sin molestarte y salir de forma segura.

### Las cuatro piezas que trabajan en equipo

La autenticación de PetTrack no vive en un solo lugar del código; se reparte entre cuatro archivos que se pasan la pelota como un equipo bien coordinado. Antes de ver el flujo, conviene conocer a los jugadores:

| Archivo (pieza del código) | Qué es, en palabras simples | Su trabajo |
|---|---|---|
| `LoginViewModel.kt` | El "cerebro" de la pantalla de login | Recoge lo que escribes, valida que no esté vacío y le pide al repositorio que te haga entrar |
| `AuthRepository.kt` | El "mensajero" que habla con el servidor | Envía tu correo y contraseña a Supabase para registrarte, entrar o salir |
| `SessionManager.kt` | La "caja fuerte" del teléfono | Guarda de forma cifrada tus llaves de acceso y recuerda si estás dentro o fuera |
| `TokenAuthenticator.kt` | El "portero automático" | Cuando tu llave de acceso vence, consigue una nueva sin que te enteres |

Un par de términos que usaremos todo el tiempo:

- **ViewModel**: es la parte del código que conecta lo que ves en pantalla con la lógica de la app. Piénsalo como el *cerebro detrás de la pantalla*: la pantalla solo dibuja botones y cajas de texto, pero el ViewModel decide qué hacer cuando aprietas un botón. En PetTrack esa pieza es `LoginViewModel`.
- **Repositorio**: es el *mensajero oficial* de la app. Cuando la app necesita hablar con el servidor (por ejemplo, "quiero entrar con este correo"), no lo hace por su cuenta: se lo encarga al repositorio, que sabe exactamente a dónde llamar y cómo. Aquí es `AuthRepository`.

### Qué pasa cuando te registras (creas tu cuenta)

Imagina que llegas por primera vez. Llenas el formulario con tu correo, una contraseña, tu nombre, cédula, teléfono y dirección. Cuando aprietas "Registrarme", ocurre esto por dentro, en el archivo `AuthRepository.kt`, dentro de la función `register`:

1. **Se crea la cuenta.** La app le dice al servidor Supabase: "crea un usuario nuevo con este correo y contraseña". En el código es la línea `authApi.signUp(...)`. En ese momento, el servidor guarda tu correo y tu nombre.
2. **Se crea tu perfil automáticamente.** Aquí ocurre algo elegante: el servidor tiene un *disparador automático* (un **trigger**, que es como una regla del tipo "cada vez que pase X, haz Y automáticamente") llamado `handle_new_user`. En cuanto naces como usuario, ese disparador crea sin ayuda una fila en la tabla de perfiles con tus datos básicos. Nadie tiene que crearla a mano.
3. **Entras de inmediato.** Justo después de registrarte, la app inicia sesión por ti (la línea `authApi.signInWithPassword(...)`), para que no tengas que volver a escribir la contraseña. Así obtiene tus llaves de acceso (ahora veremos qué son).
4. **Se completan tus datos privados.** El registro inicial solo guardó correo y nombre. Los datos más personales —cédula, teléfono y dirección— se guardan en un segundo paso (`profileApi.updateProfile(...)`), rellenando la fila de perfil que el disparador ya había creado. Se hace después de entrar porque, para editar *tu* perfil, el servidor primero necesita confirmar que eres tú.

Un detalle importante que dejaron anotado en el propio código: para que el paso 3 funcione (entrar de inmediato tras registrarse), en la configuración de Supabase debe estar **desactivada** la confirmación por correo. Si estuviera activada, tendrías que ir a tu buzón y hacer clic en un enlace antes de poder entrar.

### Qué pasa cuando inicias sesión: la "pulsera de acceso temporal" (JWT)

Cuando ya tienes cuenta y escribes tu correo y contraseña, el recorrido es más corto. Empieza en la pantalla y termina en el servidor:

**Paso 1 — La pantalla habla con el cerebro (`LoginViewModel`).**
Cada vez que escribes una letra en el correo o la contraseña, la pantalla le avisa al ViewModel con las funciones `onEmailChange` y `onPasswordChange`. El ViewModel guarda esos valores en un pequeño paquete de información llamado `LoginUiState` (el *estado de la pantalla*: qué texto hay escrito, si está cargando, si hubo un error). Piénsalo como una pizarra que la pantalla y el cerebro comparten y mantienen siempre igual.

Cuando aprietas "Entrar", se ejecuta la función `submit()`. Lo primero que hace es una **validación**, es decir, una comprobación básica antes de molestar al servidor:

```kotlin
if (current.email.isBlank() || current.password.isBlank()) {
    _state.update { it.copy(error = "Ingresa tu correo y contraseña.") }
    return
}
```

En palabras simples: "si el correo está vacío **o** la contraseña está vacía, muestra el mensaje *Ingresa tu correo y contraseña* y no sigas". Así evitamos enviar una petición inútil al servidor y le damos al usuario un aviso inmediato.

**Paso 2 — El cerebro le pide al mensajero que haga el trabajo.**
Si los datos no están vacíos, el ViewModel enciende un indicador de "cargando" (para que veas una ruedita girando) y le pasa la tarea al repositorio con `repository.login(...)`. A partir de aquí puede pasar una de dos cosas:
- **Éxito** (`onSuccess`): marca la pantalla como "logrado" (`success = true`) y la app te lleva adentro.
- **Fallo** (`onFailure`): apaga la ruedita y muestra un mensaje de error entendible. Ese mensaje lo traduce una función auxiliar, `authErrorMessage`, que convierte errores técnicos en frases humanas (por ejemplo, "correo o contraseña incorrectos" en vez de un código raro).

**Paso 3 — El mensajero trae las llaves.**
En `AuthRepository.login`, la app le entrega tu correo y contraseña a Supabase (`authApi.signInWithPassword`). Si son correctos, el servidor devuelve dos "llaves":

- **Token de acceso (access token), un JWT.** Un **token** es simplemente un código que demuestra que ya te identificaste. El **JWT** (sigla en inglés de *JSON Web Token*) es el formato concreto de ese código. La mejor analogía es una **pulsera de acceso temporal**, como la de un concierto o un parque acuático: te la ponen al entrar y, a partir de ahí, no tienes que mostrar tu documento en cada puerta; basta enseñar la pulsera. Pero **caduca**: pasado un rato deja de valer.
- **Token de refresco (refresh token).** Es como un **cupón para pedir una pulsera nueva** cuando la anterior venza, sin tener que hacer toda la fila de la entrada otra vez (es decir, sin volver a escribir tu contraseña).

El repositorio toma esas llaves en la función `persist` y le pide a la caja fuerte que las guarde.

### Dónde se guardan las llaves: la "caja fuerte del teléfono"

Guardar tokens es delicado: son la prueba de tu identidad, así que no pueden quedar en cualquier parte donde otra app pudiera leerlos. Eso lo resuelve `SessionManager.kt`.

El corazón de ese archivo es algo llamado **EncryptedSharedPreferences**. Para entenderlo:
- Un teléfono Android tiene un lugar sencillo para que las apps guarden datos pequeños (preferencias, ajustes). El problema es que, por defecto, esos datos están *en texto plano*, es decir, legibles.
- **EncryptedSharedPreferences** es la versión blindada: guarda los datos **cifrados**. Cifrar significa convertir la información en un galimatías ilegible que solo puede volver a leerse con una llave secreta. Es, literalmente, la **caja fuerte del teléfono**.

En el código, `SessionManager` crea primero una **llave maestra** (`MasterKey`) usando un estándar de cifrado fuerte (AES-256), y luego abre esa caja fuerte con el nombre `pettrack_secure_prefs`. Dentro guarda cuatro cosas: el token de acceso, el token de refresco, tu identificador de usuario y tu correo.

`SessionManager` cumple además otro papel clave: es la **única fuente de la verdad** sobre si estás dentro o fuera. Mantiene un semáforo interno, `authState`, que solo tiene dos estados posibles: **Authenticated** (dentro) o **Unauthenticated** (fuera). El resto de la app *observa* ese semáforo para decidir qué mostrarte:
- Al guardar tu sesión (`saveSession`), el semáforo pasa a "dentro" y la app te lleva a las pantallas principales.
- Al borrarla (`clear`), pasa a "fuera" y la app te devuelve al login.

Y hay un detalle pensado a propósito, anotado en el propio código: la lectura de los tokens es **inmediata** (síncrona). Esto es necesario porque el "portero automático" que veremos a continuación necesita consultar las llaves justo en el momento de enviar una petición por internet, sin esperas.

### La magia invisible: el refresco automático del token

Aquí está la parte más ingeniosa, y la que hace que la app se sienta cómoda. Recuerda que la pulsera de acceso (el JWT) **caduca** a propósito, por seguridad: si alguien te la robara, solo le serviría un ratito. Pero, ¿qué pasa cuando vence mientras estás usando la app tranquilamente? Sin ninguna solución, la app te echaría al login a media tarea. Muy molesto.

La solución es `TokenAuthenticator.kt`, el **portero automático**. Su regla de oro está en su propia descripción: cuando el servidor responde a una petición con un error **401** (que en el lenguaje de internet significa "no autorizado", es decir, "tu pulsera ya no vale"), este portero entra en acción **antes** de que tú te enteres.

Esto es lo que hace, paso a paso:

1. **Detecta el rechazo.** El servidor dijo 401. El portero primero verifica que no lleve ya reintentando en círculos (`responseCount(response) >= 2`): si ya lo intentó una vez y falló, se rinde para no quedar atrapado en un bucle infinito.
2. **Busca el cupón.** Saca de la caja fuerte tu token de refresco. Si no hay ninguno, no hay nada que hacer y no reintenta.
3. **Pide una pulsera nueva.** En la función `refreshBlocking`, llama a una dirección especial del servidor (`/auth/v1/token?grant_type=refresh_token`) entregando el cupón. Si todo va bien, el servidor devuelve una pulsera nueva (y un cupón nuevo), que el portero guarda de inmediato en la caja fuerte con `updateTokens`.
4. **Reintenta la petición original.** Vuelve a lanzar la misma petición que había fallado, pero ahora con la pulsera nueva puesta (`.header("Authorization", "Bearer $newAccess")`). Para ti, todo esto es invisible: la acción que querías hacer simplemente funciona.
5. **Si el cupón también venció, cierra la sesión.** Si el refresco falla (por ejemplo, porque llevas demasiado tiempo sin usar la app), el portero limpia la caja fuerte con `session.clear()`, el semáforo pasa a "fuera" y la app, con elegancia, te lleva al login para que vuelvas a entrar.

Un par de detalles que muestran el cuidado con que se hizo:

- **Evita empujones simultáneos.** Si varias peticiones fallan a la vez y todas quieren refrescar el token al mismo tiempo, se pisarían. El código usa un "candado" (`synchronized(lock)`) para que solo una haga el refresco; las demás, al ver que ya hay una pulsera nueva lista, la aprovechan en vez de pedir otra. Es como una sola persona yendo a recoger las pulseras nuevas para todo el grupo.
- **No se muerde la cola.** El portero usa un cliente de red "pelado" (`bareClient`) para pedir la pulsera nueva. La razón, explicada en el propio archivo, es que ese cliente no tiene puesto al propio portero; si lo tuviera y la petición de refresco fallara con otro 401, el portero se llamaría a sí mismo una y otra vez sin fin.

### El cierre de sesión

Cerrar sesión es lo más sencillo. En `AuthRepository.logout()` pasan dos cosas:
1. Se le avisa al servidor que la sesión terminó (`authApi.logout()`), para que invalide las llaves de su lado.
2. Se vacía la caja fuerte del teléfono (`session.clear()`). Al hacerlo, el semáforo de `SessionManager` pasa a "fuera" y la app te devuelve automáticamente a la pantalla de inicio de sesión.

A partir de ahí no queda ningún rastro de tus llaves en el teléfono, y para volver a entrar habrá que escribir el correo y la contraseña de nuevo.

### El flujo completo, de un vistazo

Para amarrar todo, así viaja la información cuando inicias sesión:

1. **Tú** escribes correo y contraseña en la pantalla de login.
2. La **pantalla** avisa al **cerebro** (`LoginViewModel`), que valida que no estén vacíos.
3. El cerebro le pide al **mensajero** (`AuthRepository`) que inicie sesión.
4. El mensajero le pregunta al **servidor** (Supabase); si acierta, recibe la **pulsera (JWT)** y el **cupón (refresh token)**.
5. Ambas llaves se guardan en la **caja fuerte** (`SessionManager`), que enciende el semáforo en "dentro".
6. La app te muestra las pantallas principales.
7. Mientras usas la app, si la pulsera vence, el **portero** (`TokenAuthenticator`) consigue una nueva con el cupón, sin molestarte.
8. Cuando cierras sesión, se vacía la caja fuerte y vuelves al login.

El resultado es una experiencia que se siente simple y segura al mismo tiempo: tú solo escribes tu contraseña una vez, y por debajo el código se encarga de proteger tus llaves, renovarlas cuando hace falta y borrarlas cuando te vas.

---

## Reportar y gestionar mascotas (fotos y GPS)

Este es el corazón de PetTrack: aquí un usuario crea la "ficha" de su mascota (por ejemplo, cuando se le pierde), la edita si algo cambia, la borra si ya no la necesita, le pone una foto y guarda el lugar exacto donde se perdió usando el GPS del teléfono. Vamos a ver, en palabras sencillas, cómo se hizo todo esto por dentro.

### Primero, tres palabras que van a repetirse

Para que el resto se entienda, definamos tres términos con analogías:

- **Pantalla (Screen):** es lo que el usuario ve y toca (los cajitas para escribir, los botones). Piensa en ella como el **mostrador de una recepción**: donde el cliente llena el formulario y presiona botones.
- **ViewModel:** es el "cerebro" que está detrás de la pantalla. Guarda lo que el usuario va escribiendo y decide qué hacer cuando presiona un botón. Es como el **recepcionista** que anota todo en una libreta y sabe a quién pasarle el papel.
- **Repositorio (Repository):** es el "mensajero" que habla con el servidor por internet. La pantalla y el cerebro nunca hablan directo con el servidor; siempre le piden el favor al mensajero. Es como el **motorizado** que lleva y trae paquetes entre la oficina y la central.

En PetTrack, estos tres son archivos reales:

| Rol | Archivo | Qué hace |
|-----|---------|----------|
| Pantalla | `ui/pets/report/ReportPetScreen.kt` | Muestra el formulario para reportar/editar una mascota |
| Cerebro | `ui/pets/report/ReportPetViewModel.kt` | Guarda lo escrito y coordina guardar/editar |
| Cerebro de "Mis reportes" | `ui/pets/list/MyReportsViewModel.kt` | Pide y muestra la lista de tus mascotas |
| Mensajero | `data/repository/PetRepository.kt` | Habla con el servidor (crear, editar, borrar, subir foto, guardar GPS) |
| Sensor GPS | `core/location/LocationProvider.kt` | Le pregunta al teléfono dónde estás |

### El formulario: todos los campos de la mascota

La pantalla `ReportPetScreen.kt` es un formulario largo con una casilla por cada dato. Cada vez que el usuario escribe una letra, el "cerebro" la guarda al instante en una especie de **libreta de estado** llamada `ReportPetUiState` (piensa en ella como una **hoja de anotaciones** que siempre refleja lo que hay en pantalla ahora mismo).

Estos son los campos reales que se piden, tal como aparecen en el código:

- **Nombre** (`name`) y **Especie** (`species`, ejemplo: perro, gato) — son los dos únicos **obligatorios**. Van marcados con un asterisco.
- **Raza** (`breed`)
- **Edad aproximada** (`approxAge`)
- **Color** (`color`)
- **Tamaño** (`size`): pequeño, mediano o grande. No se escribe; se elige tocando un "chip" (un botoncito con forma de etiqueta). Si tocas el que ya estaba elegido, se des-selecciona.
- **Señas particulares** (`distinguishingMarks`): una manchita, una oreja caída, etc.
- **Collar/chip** (`hasCollarChip`): es un **interruptor** (Switch), como el de la luz: encendido o apagado. Solo si lo enciendes aparece la casilla para el **Número de chip** (`chipNumber`).
- **Estado** (`status`): perdida, encontrada o en búsqueda. También se elige con chips.
- **Hora de pérdida** (`lostAt`): hay un botón "Marcar hora de pérdida (ahora)". Al tocarlo, el cerebro toma la fecha y hora actuales del teléfono y las guarda con un formato estándar internacional (el método `nowIso()`), para que el servidor las entienda sin confusiones.

Cada casilla tiene su propia funcioncita en el cerebro que actualiza la libreta: por ejemplo `onName(...)` guarda el nombre, `onColor(...)` guarda el color, y así con cada campo. Es literalmente "el usuario tecleó algo → el cerebro lo anota".

Un detalle bonito de diseño: cuando el usuario escribe en el nombre o la especie, el cerebro además **borra cualquier mensaje de error** anterior (`error = null`), para que un aviso rojo viejo no se quede pegado en pantalla mientras la persona corrige.

### La FOTO: del teléfono al almacén en la nube

Subir la foto tiene dos momentos: **elegirla** (pasa en la pantalla) y **enviarla al servidor** (pasa en el mensajero).

**Momento 1 — Elegir la foto (en `ReportPetScreen.kt`):**
El usuario toca "Seleccionar foto" y se abre la galería del teléfono. Cuando escoge una imagen, el código hace tres cosas:

1. Averigua qué **tipo** de imagen es (`mime`, por ejemplo `image/jpeg`). El "mime" es como la **etiqueta del sobre** que le dice al servidor "esto es una foto JPG", no un texto ni un video.
2. Abre la imagen y la convierte en **bytes**, es decir, en el montoncito de números que realmente forma el archivo. Piensa en los bytes como los **ladrillos** con los que está hecha la foto.
3. Le pasa esos ladrillos al cerebro con `onPhotoPicked(...)`. El cerebro los guarda aparte (en una caja llamada `PhotoBytes`) y solo muestra en pantalla el **nombre** del archivo, para no llenar la libreta con la foto entera.

Importante: en este momento **la foto todavía no viajó a ningún lado**. Solo está cargada y lista, esperando a que el usuario presione "Reportar".

**Momento 2 — Subir la foto al Storage (en `PetRepository.kt`):**
"Storage" es el **almacén de archivos** de Supabase (el servidor). Imagínalo como un **casillero en la nube** donde se guardan las fotos, separado de la base de datos donde se guardan los textos. En PetTrack ese casillero se llama `pet-photos`.

La función `uploadPhoto(...)` hace el trabajo, y es un pequeño fragmento que vale la pena leer traducido:

```kotlin
val path = "$uid/$petId/${UUID.randomUUID()}.${photo.ext}"
storageApi.upload(path, photo.bytes.toRequestBody(photo.mime.toMediaType()), photo.mime)
petApi.addPhoto(PetPhotoInsert(petId = petId, storagePath = path))
```

Línea por línea, en simple:

1. **Se inventa una "dirección" única para la foto** (`path`). Se arma juntando el identificador del dueño (`uid`), el identificador de la mascota (`petId`) y un código al azar (`UUID.randomUUID()`, un número tan largo y aleatorio que jamás se repite). Es como ponerle a cada foto una **dirección postal única**: `dueño/mascota/códigoúnico.jpg`. Así dos fotos nunca chocan ni se pisan.
2. **Se sube la foto al casillero** con `storageApi.upload(...)`, mandando los ladrillos (bytes) y la etiqueta del sobre (mime).
3. **Se guarda solo la dirección, no la foto, en la base de datos** con `addPhoto(...)`. Esto es clave: en la tabla `pet_photos` no se guarda la imagen pesada, sino un papelito que dice "la foto de esta mascota está guardada en tal casillero". Es como no meter el mueble en tu agenda, sino solo apuntar en qué bodega está. Después, cuando otra pantalla quiera mostrar la foto, usa esa dirección para ir a buscarla.

### La UBICACIÓN: usar el GPS del teléfono

El **GPS** es el sensor que trae el teléfono para saber dónde estás en el mundo (la misma tecnología que usan Waze o Google Maps). En PetTrack sirve para marcar el punto exacto donde se perdió o se vio la mascota.

**Qué es el "permiso de ubicación" (en palabras simples):**
Android no deja que una app lea tu ubicación así porque sí. Primero el teléfono te pregunta "¿Le permites a PetTrack ver tu ubicación?" y tú dices sí o no. Eso es el **permiso**. Es como la **llave de una habitación**: la app no puede entrar hasta que tú le abres la puerta. Esto protege tu privacidad.

En la pantalla, el botón "Capturar ubicación (GPS)" es inteligente:

- Si **ya diste permiso**, llama directo a `captureLocation()` y toma tu posición.
- Si **todavía no lo has dado**, primero muestra la ventanita de Android pidiéndotelo (`launchPermissionRequest()`), y no antes.

Cuando el permiso está concedido y se captura la ubicación, entra en juego el sensor a través del archivo `LocationProvider.kt`. Este usa una herramienta de Google llamada **FusedLocationProvider**, que es como un **asistente experto en ubicación**: combina GPS, antenas de celular y wifi para darte la posición más certera con el menor gasto de batería. El método `currentLatLng()` pide la posición actual con **alta precisión**; y como truco de respaldo, si en ese instante no logra una lectura fresca, usa la **última ubicación conocida** (`lastLocation`) para no dejarte sin nada. Al final devuelve dos números: **latitud y longitud**, que juntos son como las **coordenadas de un tablero de batalla naval**: con esos dos números se señala un punto exacto en el mapa.

Mientras el teléfono está "pensando" la ubicación, el cerebro pone en la libreta `capturingLocation = true`, y por eso el botón cambia su texto a "Obteniendo ubicación…". Cuando llega la respuesta, guarda la latitud y longitud; y si algo falló (por ejemplo, GPS apagado), muestra el aviso "No se pudo obtener la ubicación GPS." sin que la app se caiga.

**Cómo se guarda la ubicación en el servidor — `set_pet_location`:**
Aquí hay un detalle técnico interesante. La ubicación **no** se guarda junto con los demás datos de la mascota en el mismo paso. El servidor usa **PostGIS**, que es una extensión de la base de datos especializada en geografía (piensa en ella como el **cartógrafo** de la base: sabe de mapas, distancias y coordenadas). Para guardar un punto geográfico de forma correcta, PetTrack llama a una función especial del servidor llamada `set_pet_location` (a través de `rpcApi.setPetLocation(...)` en el repositorio).

**RPC** significa, en cristiano, "pedirle al servidor que ejecute una tarea concreta que solo él sabe hacer bien". Es como llamar a un **especialista**: en vez de intentar guardar tú mismo la coordenada, le dices al cartógrafo del servidor "toma esta mascota y estas coordenadas, guárdalas tú como corresponde". Por eso, en el código, la ubicación se manda por separado solo **si hay latitud y longitud**:

```kotlin
if (lat != null && lng != null) rpcApi.setPetLocation(SetLocationRequest(created.id, lat, lng))
```

En palabras: "si el usuario capturó ubicación, envíasela al especialista del servidor junto con el identificador de la mascota; si no la capturó, no molestes al servidor con eso".

### Juntando todo: qué pasa al presionar "Reportar" o "Guardar cambios"

Cuando el usuario termina y presiona el botón grande, el cerebro (`submit()` en `ReportPetViewModel.kt`) hace esta secuencia:

1. **Revisa lo obligatorio.** Si falta el nombre o la especie, no envía nada y muestra un mensaje claro ("Ingresa el nombre de la mascota."). Es el **portero** que no te deja pasar con el formulario incompleto.
2. **Empaqueta todos los datos** en una cajita ordenada llamada `PetInput`, limpiando espacios sobrantes y convirtiendo campos vacíos en "nada" (para no guardar textos en blanco).
3. **Decide si es crear o editar.** El cerebro sabe si estás editando porque, al abrir la pantalla, recibió un `petId` (el identificador de una mascota que ya existe). Si hay `petId`, llama a `updatePet(...)`; si no, llama a `createPet(...)`.
4. **El mensajero hace el trabajo en orden** (en `PetRepository.kt`): primero crea o actualiza la ficha de la mascota, después envía la ubicación con `set_pet_location`, y por último sube la foto al casillero. Este orden importa: la foto y la ubicación necesitan saber **a qué mascota** pertenecen, así que la mascota debe existir primero.
5. **Avisa el resultado.** Si todo salió bien, la libreta marca `success = true`, la pantalla lo detecta y automáticamente te regresa a la lista. Si algo falló, muestra el error traducido a un mensaje entendible.

Dos aclaraciones útiles sobre crear vs. editar:

- Al **crear** (`createPet`), se manda al servidor la ficha nueva junto con el identificador del dueño (`ownerId`), para que quede claro de quién es la mascota.
- Al **editar** (`updatePet`), se usa un **reemplazo completo** (`replacePet`) de la ficha, es decir, se vuelve a mandar todo el conjunto de datos actualizado, no solo lo que cambió. Es como entregar el formulario entero corregido en vez de tachar una sola línea.

### "Mis reportes": la lista de tus mascotas

La pestaña **Mis reportes** muestra únicamente las mascotas que **tú** reportaste. Su cerebro es `MyReportsViewModel.kt`, y es muy sencillo:

- La función `load()` le pide al mensajero `repository.myPets()`, que trae solo las mascotas cuyo dueño eres tú. ¿Cómo sabe cuáles son tuyas? Porque el repositorio usa tu identificador de usuario guardado en la sesión y le pide al servidor "dame las mascotas donde el dueño sea igual a mi id" (eso es el `eq.$uid` que aparece en el código: **eq** significa "igual a"). Además, el servidor tiene una regla de seguridad llamada **RLS** (seguridad por filas) que actúa como un **portero de biblioteca**: aunque alguien intentara pedir las mascotas de otro, el servidor solo entrega las que legítimamente le pertenecen a quien pregunta.
- Mientras carga, la libreta marca `loading = true` para que la pantalla pueda mostrar un indicador de "cargando".
- La función `delete(id)` borra una mascota (llama a `repository.deletePet(...)`) y, si sale bien, vuelve a llamar a `load()` para **refrescar la lista** al instante, de modo que la mascota borrada desaparezca sin que tengas que salir y volver a entrar.

Desde esta lista, además, el usuario puede tocar una mascota para **editarla** (lo que reabre el mismo formulario de `ReportPetScreen.kt`, esta vez ya lleno con los datos existentes, gracias a que el cerebro los carga con `getPet(id)` al abrirse). Así, crear, ver, editar y borrar quedan todos conectados en un mismo flujo limpio.

---

## Comunidad, mapa y avistamientos

Esta es, probablemente, la parte más "viva" de PetTrack: la pantalla donde cualquier persona puede ver qué mascotas se han reportado cerca de ella, abrir la ficha de una en particular, avisar "¡yo la vi aquí!" y ponerse en contacto con el dueño. Vamos a recorrerla despacio.

Antes de empezar, dos palabras que se repetirán mucho:

- **Pantalla (o "Screen")**: es lo que el usuario ve y toca en el teléfono. En este proyecto cada pantalla es un archivo terminado en `Screen.kt`. Piensa en ella como el "escaparate" de una tienda: bonito y visible, pero no es donde se guarda la mercancía.
- **ViewModel**: es el "cerebro" que está detrás de la pantalla. Guarda los datos, hace los cálculos y le pide información al servidor. La pantalla solo muestra lo que el ViewModel le entrega. Son archivos terminados en `ViewModel.kt`. Analogía: la pantalla es el mesero que te atiende; el ViewModel es la cocina que prepara el plato.

### 1. La pantalla de Comunidad: "¿qué hay cerca de mí?"

El archivo principal aquí es `ui/community/CommunityScreen.kt`, y su cerebro es `ui/community/CommunityViewModel.kt`.

La idea central es sencilla: **muéstrame las mascotas que están dentro de un radio alrededor de un punto del mapa**. Esa búsqueda "por radio" no la hace el teléfono, sino el servidor, mediante una función especial llamada `pets_nearby`.

#### ¿Qué es una "función pets_nearby" y qué es un "radio"?

Imagina que pinchas un alfiler en un mapa de papel y atas a ese alfiler un cordel de, digamos, 5 kilómetros. Giras el cordel y dibujas un círculo. Todo lo que quede *dentro* de ese círculo es "lo cercano". Eso es exactamente un **radio de búsqueda**: la distancia máxima desde un punto central.

`pets_nearby` es una **función RPC**. RPC significa "llamada a un procedimiento remoto" (Remote Procedure Call); en cristiano: **un botón que aprietas en tu teléfono y que ejecuta un trabajo en el servidor, no en tu teléfono**. Es como marcar a una pizzería: tú solo dices "quiero una pizza a esta dirección" y ellos, allá, hacen todo el trabajo y te la traen lista. Aquí el teléfono dice "dame las mascotas a menos de X kilómetros de este punto" y el servidor le devuelve la lista ya filtrada.

#### El "estado" de la pantalla

El ViewModel guarda todo lo que la pantalla necesita en un solo paquete de datos llamado **estado** (en el código, `CommunityUiState`). Es como el "pedido" escrito en una comanda: recoge en un mismo papel qué se está pidiendo y cómo va. Estos son sus campos más importantes:

| Campo del estado | Qué significa en palabras simples |
|---|---|
| `center` | El punto central del mapa (el "alfiler"). Por defecto está en Ciudad de Panamá (`8.98, -79.52`). |
| `radiusKm` | El tamaño del círculo, en kilómetros. Empieza en 5. |
| `species` | El texto que escribió el usuario para filtrar por especie (perro, gato…). |
| `status` | Si solo quiere ver "Perdida", "En búsqueda", o todas. |
| `dateFilter` | Desde cuándo mirar: cualquiera, última semana o último mes. |
| `pets` | La lista de mascotas que devolvió el servidor. |
| `loading` / `error` | Si está cargando (rueda girando) o si algo falló (mensaje rojo). |

#### Los filtros: cuatro maneras de afinar la búsqueda

En la parte de arriba de la pantalla hay controles para acotar qué se muestra. Cada vez que el usuario toca uno, el ViewModel actualiza el estado y **vuelve a preguntarle al servidor** (esa "re-pregunta" es la función `load()`). Los cuatro filtros son:

1. **Especie** (una caja de texto): el usuario escribe "perro" o "gato" y pulsa buscar. En el código es `OutlinedTextField` (una caja de texto con borde) y al pulsar la lupa se ejecuta `applySpecies()`.
2. **Radio/zona** (botones de "chip"): las opciones son 1, 5, 10 y 25 km (`RADIUS_OPTIONS`). Un **chip** es un botoncito redondeado que se "enciende" cuando lo eliges, como marcar una casilla. Al tocar "10 km", se llama `setRadius(10)`.
3. **Estado**: chips de "Todas", "Perdida" y "En búsqueda".
4. **Fecha**: chips de "Cualquiera", "Última semana", "Último mes".

Hay además un botón especial, **"Usar mi ubicación"**, que enciende el GPS del teléfono para poner el "alfiler" donde realmente estás parado, en vez del centro por defecto. Si el GPS no responde, muestra el mensaje "No se pudo obtener tu ubicación GPS."

Un detalle bonito del filtro de fecha: el usuario elige "última semana", pero el servidor no entiende "semana", entiende **fechas concretas**. Por eso el ViewModel traduce "7 días" a una fecha exacta con esta pequeña función:

```kotlin
private fun fromIso(filter: DateFilter): String? {
    val days = filter.days ?: return null
    val millis = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date(millis))
}
```

Leído en voz de calle, línea por línea:
- `val days = filter.days ?: return null` → "¿Cuántos días abarca este filtro? Si es 'cualquiera' (sin límite), no calculo nada y devuelvo vacío."
- `val millis = System.currentTimeMillis() - days...` → "Tomo la hora de *ahora mismo* y le resto esos días" (todo se mide en milisegundos, de ahí la multiplicación por 24 horas × 60 minutos × 60 segundos × 1000). El resultado es "el instante de hace una semana".
- La última línea convierte ese instante a un **formato de fecha estándar** que el servidor sabe leer. Es como pasar de "hace una semana" a "10 de julio de 2026, 3:00 p.m." para que no haya malentendidos.

#### Cómo viaja la pregunta al servidor: el "repositorio"

El ViewModel no habla directamente con internet. Le pasa el encargo a un **repositorio**: `data/repository/CommunityRepository.kt`. Un repositorio es como el **encargado de compras** de una empresa: los demás le dicen "necesito esto", y él sabe a qué proveedor llamar y cómo pedirlo. Así, si mañana cambia el proveedor, solo se ajusta al encargado y nadie más se entera.

La función `nearby(...)` del repositorio arma el pedido y lo manda con `rpcApi.petsNearby(...)`. Un par de detalles cuidados que vale la pena mencionar:
- `species?.trim()?.ifBlank { null }` → "quita espacios sobrantes al texto; si quedó vacío, mándalo como *nada* en vez de mandar comillas vacías". Es como no anotar en la lista de compras un renglón en blanco.
- Cuando llega la respuesta, hace `.map { it.toDomain() }`. Esto es **traducir** los datos crudos del servidor (con nombres técnicos) al lenguaje limpio que usa la app (`NearbyPet`). Piénsalo como pasar la mercancía del camión del proveedor a las estanterías ordenadas de tu tienda.

#### Qué ve el usuario: mapa arriba, lista abajo

Con la lista `pets` ya en la mano, la pantalla muestra dos cosas:

- **El mapa** con un marcador (pin) por cada mascota y el círculo del radio dibujado (lo explicamos en la sección del mapa).
- **Una lista de tarjetas** debajo (`NearbyPetCard`): cada tarjeta muestra la foto (o un iconito de patita si no hay foto), el nombre, la especie/raza, el estado y **la distancia** ("a 340 m", "a 2.3 km"). Esa distancia la calcula el propio servidor y la app solo la redondea de forma amable con `formatDistance` (metros si es cerca, kilómetros si es lejos).

Si no hay resultados, aparece "No hay mascotas reportadas en esta zona." Si algo falla, un texto en rojo. Y mientras carga, una ruedita giratoria. Tocar cualquier tarjeta (o cualquier pin del mapa) abre el **detalle** de esa mascota.

### 2. El mapa de OpenStreetMap con marcadores y círculo

El mapa vive en un archivo reutilizable: `core/map/OsmMap.kt`. Es un solo componente que usan tanto la pantalla de Comunidad como la de detalle, para no repetir código.

#### ¿Qué es OpenStreetMap / osmdroid?

**OpenStreetMap** es un mapa mundial gratuito y colaborativo, tipo "la Wikipedia de los mapas". **osmdroid** es la pieza de software que permite mostrar ese mapa dentro de una app Android. Se eligió porque es gratuito y no exige tarjeta de crédito ni claves de pago como otras alternativas.

El componente `OsmMap` recibe cuatro cosas: el **centro** (dónde apuntar), la lista de **marcadores** (los pines), el **radio en metros** (opcional, para dibujar el círculo) y qué hacer cuando alguien **toca un pin** (`onMarkerClick`).

#### Marcadores y círculo

- Un **marcador** es cada alfiler clavado en el mapa. En Comunidad, la pantalla convierte cada mascota en un `MapMarker` con su latitud, longitud y nombre. Al tocar un pin, se abre el detalle de esa mascota (por eso `onMarkerClick = onOpenPet`).
- El **círculo del radio** se dibuja con una figura (`Polygon`) usando `Polygon.pointsAsCircle(centro, radio)`: literalmente calcula los puntos de un círculo de ese tamaño alrededor del centro, con un relleno verde translúcido y un borde verde. Es la representación visual del "cordel" del que hablábamos: el usuario *ve* hasta dónde llega su búsqueda.

**Latitud y longitud** son simplemente las dos coordenadas que ubican un punto exacto en la Tierra: la latitud es "qué tan al norte/sur" y la longitud "qué tan al este/oeste". Juntas son como la dirección postal del planeta.

#### El arreglo importante: que el mapa no se recentre solo

Aquí hubo un problema real que se corrigió, y está documentado en un comentario dentro del propio archivo. El síntoma: el usuario arrastraba el mapa para mirar otra zona y, de repente, el mapa **saltaba de vuelta al centro** él solo, de forma molesta.

La causa: en Android, la pantalla se "redibuja" muy seguido por motivos ajenos al mapa (por ejemplo, el contador de la campana de notificaciones que se actualiza en segundo plano). Cada redibujado hacía que el mapa se volviera a centrar, cancelando el movimiento del usuario. Es como si cada vez que sonara tu teléfono, alguien te girara la cabeza a la fuerza hacia el frente.

La solución fue enseñarle al mapa a tener memoria de "qué le mostré la última vez" y **solo re-centrar cuando el centro realmente cambió**, no en cada redibujado. En el código, esa memoria es la variable `applied`, y la comprobación clave es:

```kotlin
if (applied.value?.first != center) {
    map.controller.setCenter(GeoPoint(center.first, center.second))
}
```

En palabras simples: "¿el centro que tengo apuntado ahora es distinto del que ya había puesto antes? Solo entonces muevo el mapa." Si nada cambió, el mapa se queda quietecito donde el usuario lo dejó. Gracias a esto, el usuario puede pasear por el mapa con tranquilidad.

### 3. El detalle de una mascota

Al tocar una mascota se abre `ui/petdetail/PetDetailScreen.kt`, con su cerebro `ui/petdetail/PetDetailViewModel.kt`. Nada más abrirse, el ViewModel hace tres pedidos en cadena: los **datos de la mascota**, sus **fotos** y su **historial de avistamientos**.

#### Qué se muestra

- **Fotos**: una fila que se desliza horizontalmente (`LazyRow`) con todas las imágenes de la mascota. Las fotos se cargan desde internet con **Coil** (una herramienta que descarga y muestra imágenes sin congelar la app), a través de `AsyncImage`.
- **Información**: una tarjeta (`PetInfo`) con nombre, especie, raza, estado, tamaño, edad, color, señas particulares y si tiene collar/chip (y su número, si lo hay).
- **Mapa con el historial de avistamientos**: el mismo `OsmMap`, pero aquí muestra un pin para la "Última ubicación" conocida de la mascota **más un pin por cada avistamiento** que la gente haya reportado. Así se ve, de un vistazo, el rastro de por dónde la han visto. Debajo del mapa hay además una lista de avistamientos en texto (fecha + nota, como "10/07 08:15 — la vi cerca del parque").

Un detalle: si la mascota no tiene ubicación conocida, el mapa se centra por defecto en Ciudad de Panamá (`8.98, -79.52`) para no quedar en blanco.

#### El botón "Reportar avistamiento" (report_sighting)

Un **avistamiento** es un aviso del tipo "¡yo vi esta mascota aquí!". El botón abre una ventanita (un **diálogo**, esa cajita que aparece encima de la pantalla) donde el usuario:

1. Escribe una **nota** ("estaba cerca del supermercado").
2. Pulsa **"Capturar ubicación (GPS)"**, que le pide permiso para usar el GPS y toma sus coordenadas actuales. Si no captura nada, el sistema usa la última ubicación conocida de la mascota (así lo dice el propio aviso: "Si no capturas, se usará la última ubicación conocida.").
3. Pulsa **"Enviar"**.

Al enviar, el ViewModel llama a `community.reportSighting(...)`, que a su vez ejecuta en el servidor la función RPC `report_sighting`. Aquí ocurre algo muy útil "detrás del telón": cuando se registra un avistamiento, la propia base de datos avisa automáticamente al dueño de la mascota (mediante un **trigger** llamado `notify_owner_on_sighting` — un trigger es una regla del tipo "cuando pase X, haz Y automáticamente", como el sensor de una puerta que enciende la luz sola). Por eso, tras enviar, la app recarga el detalle para que el nuevo pin aparezca en el mapa.

#### El botón "Contactar dueño" (get_owner_contact)

El otro botón, "Contactar", llama a `community.ownerContact(...)`, que ejecuta la función RPC `get_owner_contact` en el servidor. Esta devuelve el nombre del dueño y, si están disponibles, su teléfono y su correo. La ventanita entonces ofrece acciones directas:

- **"Llamar: [número]"** → abre la app de teléfono con el número ya marcado, listo para llamar. En el código eso es un `Intent` de tipo `ACTION_DIAL` con `tel:...`. Un **Intent** en Android es como pasarle la batuta a otra app: "yo no sé llamar, pero le paso este número a la app de teléfono para que ella lo haga".
- **"Correo: [email]"** → abre la app de correo con el destinatario ya puesto (`ACTION_SENDTO` con `mailto:...`).

Es decir, PetTrack no llama ni envía correos por su cuenta; simplemente **prepara la acción y se la entrega a las apps del teléfono**. Esto es más seguro y más natural para el usuario.

#### Por qué el contacto solo aparece si la mascota sigue activa (privacidad)

Aquí hay una decisión de **privacidad** importante. El teléfono y el correo de una persona son datos sensibles: no se pueden repartir a la ligera. Por eso la función `get_owner_contact` solo devuelve esos datos **si la mascota todavía está activa** (perdida o en búsqueda). Si el caso ya se cerró (por ejemplo, la mascota apareció), el servidor **deja de entregar el contacto**, y la app muestra sencillamente "Contacto no disponible."

La lógica es de sentido común: tiene sentido compartir tu número mientras buscas a tu mascota, porque quieres que te llamen si la encuentran. Pero una vez resuelto el caso, no hay razón para que tu teléfono siga circulando entre desconocidos. Es como poner un cartel con tu número en un poste: útil mientras buscas, pero lo quitas cuando ya la encontraste.

Un punto clave para explicar el "cómo": **esta protección la decide el servidor, no el teléfono**. La app se limita a pedir el contacto; es la función del servidor la que decide si lo entrega o no. Esto es intencional: si el filtro estuviera solo en el teléfono, alguien astuto podría saltárselo. Al ponerlo en el servidor, la regla se cumple siempre, pase lo que pase con la app. En el código de la app esto se ve en que, si no llega nada, se construye un contacto vacío (`OwnerContact(null, null, null)`) y la pantalla muestra el mensaje de "no disponible" sin exponer ningún dato.

### Resumen del flujo, de principio a fin

Para que alguien pueda contarlo sin mirar el código, este es el viaje completo:

1. El usuario abre **Comunidad** → el ViewModel pide al servidor las mascotas cercanas con `pets_nearby`, usando el centro, el radio y los filtros elegidos.
2. Ajusta filtros (especie, radio, estado, fecha) o pulsa "Usar mi ubicación" → cada cambio vuelve a preguntar al servidor.
3. El **mapa** dibuja los pines y el círculo del radio; la **lista** muestra tarjetas con foto, datos y distancia. El mapa ya no se recentra solo gracias a la memoria del componente.
4. Toca una mascota → se abre el **detalle** con fotos, información, mapa con el rastro de avistamientos y la lista de avistamientos.
5. Pulsa **"Reportar avistamiento"** → `report_sighting` guarda el aviso y el servidor notifica automáticamente al dueño.
6. Pulsa **"Contactar dueño"** → `get_owner_contact` entrega el contacto **solo si la mascota sigue activa**, y el teléfono ofrece llamar o enviar correo con un toque.

---

## Dashboard (estadísticas) y Notificaciones

En esta parte del proyecto conviven dos funciones que, aunque parecen distintas, comparten una idea clave: **pedirle datos al servidor y mostrárselos a la persona de forma clara**. La primera es el **Dashboard**, una pantalla llena de números y gráficos que resume "cómo va todo" con las mascotas. La segunda son las **Notificaciones**, el sistema que avisa al dueño cuando alguien vio a su mascota. Vamos una por una.

Antes de empezar, dos palabras que se repetirán:

- **Servidor / base de datos:** piensa en un archivador central en internet donde se guarda toda la información (mascotas, avistamientos, avisos). La app del teléfono no guarda esos datos; se los pide al archivador cada vez que los necesita. Ese archivador se llama **Supabase**.
- **Pantalla (en código):** cada pantalla de la app es un archivo de Kotlin (el lenguaje en que está escrita). Kotlin usa una herramienta llamada **Jetpack Compose** para "dibujar" lo que ves. Compose funciona como armar con bloques de Lego: pones un texto aquí, una tarjeta allá, una fila de cosas más abajo, y la pantalla queda armada.

---

### Parte A — El Dashboard (estadísticas)

### ¿Qué es y de dónde saca los números?

El Dashboard es la pestaña de "estadísticas". Su trabajo es contar cosas: cuántas mascotas hay en total, cuántas perdidas, cuántas encontradas, etc. Pero **la app no hace esas cuentas ella misma**. Sería lento y complicado traerse miles de registros al teléfono solo para contarlos.

En vez de eso, le pide al servidor una función ya preparada que se llama **`dashboard_stats`**.

> **¿Qué es una función del servidor (RPC)?** Imagina que llamas a un empleado del archivador y le dices: "no me mandes todas las carpetas, solo hazme el resumen y mándame el total". `dashboard_stats` es ese empleado: hace todas las cuentas dentro del servidor y devuelve solo el resultado ya masticado. En términos técnicos a eso se le llama **RPC** (una "llamada a un procedimiento remoto", es decir, pedirle a otro computador que ejecute una tarea por ti).

El servidor devuelve un paquete de datos con todo lo necesario: los totales, el promedio de días, las cuentas por mes, por especie, por raza y las zonas. En el código ese paquete se representa con un molde llamado `DashboardStats` (un molde es simplemente la "forma" que tendrá la información: qué campos trae y de qué tipo).

### Cómo se organiza la pantalla

El archivo principal es:

`app/src/main/java/com/pettrack/app/ui/dashboard/DashboardScreen.kt`

Esta pantalla tiene tres estados posibles, y decide cuál mostrar según el momento:

| Estado | Qué ve la persona |
|---|---|
| `loading` (cargando) | Un círculo que gira, mientras se esperan los datos del servidor |
| `error` (falló algo) | Un mensaje en rojo explicando el problema |
| `stats` (llegaron los datos) | El contenido real: tarjetas y gráficos |

Esto es como un mesero: mientras la cocina prepara el plato te dice "ya viene" (cargando), si algo salió mal te avisa (error), y cuando está listo te lo sirve (contenido). En el código eso se ve en un bloque `when { ... }`, que significa "según cuál de estas situaciones sea cierta, muestra esto".

Cuando los datos ya llegaron, se llama a una parte llamada `DashboardContent`, que va colocando todo en una columna que se puede deslizar hacia abajo con el dedo (`verticalScroll`), porque hay más contenido del que cabe en una pantalla.

### 1. Las tarjetas KPI (los números grandes)

Lo primero que se ve son tarjetas con un número grande y una etiqueta debajo. A esos números clave se les llama **KPI** (siglas en inglés de "indicador clave"; en cristiano: "el dato importante de un vistazo", como el marcador de un partido).

Las tarjetas que se muestran son:

- **Total** de mascotas registradas
- **Perdidas**
- **Encontradas**
- **En búsqueda**
- **Días promedio de búsqueda** (cuánto tarda en promedio en resolverse un caso)

Cada tarjeta se dibuja con una piecita reutilizable llamada `KpiTile`, que vive en el archivo de gráficos (lo vemos abajo). Es "reutilizable" como un sello: se diseña una vez la forma de la tarjeta y luego se estampa cinco veces con distinto número y distinta etiqueta.

Un detalle bonito del código: el promedio de días puede no existir todavía (si no hay casos resueltos). Para ese caso, en lugar de mostrar un número inventado, la app muestra un guion "—". La línea que lo hace dice, en palabras simples: *"si hay un promedio, muéstralo con un decimal; si no hay, muestra una raya"*.

### 2. El gráfico de columnas: perdidas vs. encontradas por mes

Debajo viene un gráfico de barras verticales agrupadas por mes. Cada mes tiene tres columnitas juntas: una de perdidas, una de encontradas y una de "en búsqueda", cada una con su color. Así se ve de un vistazo, por ejemplo, si en marzo se perdieron muchas pero se encontraron pocas.

### 3. Las barras por especie y por raza

Más abajo hay dos listas de **barras horizontales**: una que compara cuántas mascotas hay por **especie** (perros, gatos, etc.) y otra por **raza**. Se muestran solo las 6 principales de cada una (con `take(6)`, que significa "toma las primeras seis"), para no saturar la pantalla.

### 4. El mapa de zonas con más reportes

Al final hay un **mapa** que marca las zonas geográficas donde más se han reportado mascotas. Aquí pasa algo interesante en el código: para centrar el mapa, la app calcula el punto medio de todas las zonas (suma todas las latitudes y las divide entre la cantidad, e igual con las longitudes; latitud y longitud son las dos coordenadas que ubican un punto en el planeta, como las "filas y columnas" del mapa). Si no hay ninguna zona todavía, usa unas coordenadas por defecto: `8.98, -79.52`, que corresponden a Panamá. Cada zona se convierte en un puntito en el mapa con una etiqueta tipo "5 reportes". El mapa en sí lo dibuja un componente aparte llamado `OsmMap`, que usa OpenStreetMap (un mapa gratuito y abierto, la alternativa libre a Google Maps).

### Lo importante: los gráficos se dibujaron "a mano"

Aquí está la decisión de diseño más relevante de esta sección, y conviene poder explicarla:

**No se usó ninguna librería de gráficos externa.** Todos los gráficos se dibujaron a mano con los bloques básicos de Compose. Están en un solo archivo:

`app/src/main/java/com/pettrack/app/ui/dashboard/components/SimpleCharts.kt`

> **¿Qué es una librería externa?** Es código ya hecho por otras personas que uno "enchufa" a su proyecto para no reinventar la rueda. Para gráficos existen varias muy potentes. Aquí, a propósito, **no** se usó ninguna. ¿Por qué? Porque el proyecto es un examen de estudiantes que no son programadores expertos, y una librería de gráficos suele ser complicada de configurar y agrega peso a la app. Era más sencillo y más fácil de entender construir barras simples con cajitas de colores.

¿Y cómo se dibuja una barra sin librería? El truco es elegante por lo simple: **una barra no es más que una caja de color a la que se le da una altura o un ancho proporcional al número**. Veámoslo con las barras horizontales (la función `HorizontalBars`):

1. Primero se busca el valor más grande de la lista (`max`). Ese será el 100%, la barra más larga.
2. Para cada dato se dibuja el nombre a la izquierda, luego una caja gris de fondo (el "carril" completo) y encima una caja de color que se rellena una fracción del carril.
3. Esa fracción es simplemente `count / max`: si un dato vale la mitad del máximo, su barra ocupa la mitad del carril. Al lado se escribe el número exacto.

Es literalmente como una regla de tres hecha con cajitas: la más grande llena todo, las demás llenan la parte que les toca.

Para las columnas verticales por mes (`GroupedColumns` y la piecita `Bar`) la idea es la misma, pero con la altura. Este mini-fragmento es el corazón del gráfico:

```kotlin
val heightDp = if (value <= 0) 2.dp else (120f * value / max).dp
```

Traducido palabra por palabra: *"la altura de esta columna será: si el valor es cero o menos, apenas 2 puntos (una rayita casi invisible, para que no desaparezca del todo); si no, 120 multiplicado por el valor y dividido entre el máximo"*. El `120` es la altura máxima en pantalla que puede tener la columna más alta. Así, la columna del mes con más casos llega arriba del todo y las demás quedan proporcionalmente más bajitas. `dp` es solo la unidad de medida en pantalla de Android (algo parecido a "puntos", pensada para que se vea igual en teléfonos con distinta resolución).

El mismo archivo también dibuja la **leyenda** (los puntitos de colores con su nombre: rojo = perdida, etc.) con una piecita llamada `LegendDot`, que no es más que un cuadradito de color al lado de un texto.

En resumen, todo el Dashboard es: **pedir el resumen ya calculado al servidor (`dashboard_stats`), y pintarlo con tarjetas, cajitas de colores proporcionales y un mapa**, sin depender de nada complicado.

---

### Parte B — Las Notificaciones

Este es quizás el flujo más "vivo" de la app: cómo el dueño de una mascota se entera de que alguien la vio, incluso sin tener la app abierta en esa pantalla. Y hay que subrayar algo desde el inicio para el examen:

**No se usó Firebase. Todo se hizo con Supabase.**

> **¿Qué es Firebase y por qué se aclara?** Firebase (de Google) es la herramienta más famosa para mandar notificaciones "push" a los teléfonos. Es lo que la mayoría usaría. En este proyecto se decidió **no** usarla y resolver todo con el mismo servidor que ya se tenía (Supabase), para no depender de un segundo servicio y mantener el proyecto más simple y en un solo lugar. La contraparte es que, en vez de que el servidor "empuje" el aviso al teléfono, es **la app la que pregunta cada cierto tiempo** si hay algo nuevo. Ese detalle es importante y lo explicamos abajo.

### El viaje completo de una notificación

Sigamos el recorrido paso a paso, desde que un vecino ve una mascota hasta que al dueño le suena el teléfono:

**Paso 1 — Alguien reporta un avistamiento.**
Un usuario cualquiera, usando la comunidad, dice "vi esta mascota aquí". Eso guarda una fila en la tabla de avistamientos del servidor.

**Paso 2 — El servidor crea el aviso automáticamente (un "trigger").**
En el momento en que se guarda ese avistamiento, se dispara solo un mecanismo del servidor llamado **`notify_owner_on_sighting`**, que crea una fila nueva en la tabla `notifications` dirigida al dueño de esa mascota.

> **¿Qué es un trigger?** Un "trigger" (gatillo, en inglés) es una regla automática dentro de la base de datos que dice: "cuando pase tal cosa, haz automáticamente esta otra". Es como el sensor de una puerta de supermercado: nadie tiene que apretar un botón; en cuanto alguien se acerca (se guarda el avistamiento), la puerta se abre sola (se crea el aviso). Lo valioso es que esto ocurre **dentro del servidor**, sin que la app tenga que hacer nada. El aviso queda esperando en el archivador con el nombre del dueño.

**Paso 3 — La app pregunta cada 15 segundos "¿hay algo nuevo?".**
Como no hay Firebase empujando, la app revisa por su cuenta. De eso se encarga el archivo:

`app/src/main/java/com/pettrack/app/ui/notifications/NotificationWatcherViewModel.kt`

Su corazón es un pequeño bucle que se repite mientras la app está viva:

```kotlin
while (isActive) {
    center.refresh()
    delay(POLL_INTERVAL_MS)
}
```

En palabras simples: *"mientras esta parte siga viva (`isActive`), pídele al centro de notificaciones que revise (`refresh`), luego espera un rato (`delay`) y vuelve a empezar"*. Ese "rato" está definido más abajo como `15_000L`, que son 15.000 milisegundos, es decir, **15 segundos**. A esta técnica de "preguntar una y otra vez cada cierto tiempo" se le llama **polling** (sondeo). Es como el niño en el auto preguntando "¿ya llegamos?" cada rato: no es lo más elegante, pero funciona perfectamente y es fácil de entender.

> **¿Qué es un ViewModel?** El nombre del archivo termina en "ViewModel". Un ViewModel es el "cerebro detrás de la pantalla": la pantalla solo muestra cosas, y el ViewModel es quien hace el trabajo pesado (pedir datos, contar, decidir). Se separan a propósito, igual que en un restaurante el mesero (la pantalla) atiende y la cocina (el ViewModel) cocina. A este estilo de organizar el código se le llama **MVVM**.

**Paso 4 — El "centro de notificaciones" hace la revisión inteligente.**
El `refresh()` que se llama cada 15 segundos vive en:

`app/src/main/java/com/pettrack/app/core/notifications/NotificationCenter.kt`

Este archivo es el más astuto de la sección. Cada vez que revisa, hace tres cosas:

1. **Pide al servidor la lista de avisos** del usuario.
2. **Actualiza el contador de no leídos** (`unreadCount`): cuenta cuántos avisos están sin leer. Ese número es el que se muestra en la campanita.
3. **Decide si suena una notificación del sistema nueva** — y aquí está el truco fino.

El problema que resuelve: si cada 15 segundos revisa la lista completa, ¿cómo evita volver a "sonar" por avisos viejos que ya vio antes? La solución es que el centro **recuerda cuáles avisos ya conocía** (guarda sus identificadores en `seenIds`, que sería como una lista de "estos ya los vi"). Entonces solo hace sonar el teléfono por los avisos que están sin leer **y** que no estaban en esa lista de conocidos.

Además, la **primera** vez que revisa (justo al abrir la app) no hace sonar nada; solo anota lo que ya existe como punto de partida (en el código, la variable `seeded`, que sería "ya sembré la base"). Esto evita que, al abrir la app, te bombardee con notificaciones de avisos viejos. Solo suena por lo genuinamente nuevo de ahí en adelante. El comentario del propio código lo dice: *"el primer refresco solo siembra la base, para que los items viejos no hagan spam"*.

> **Detalle técnico menor pero cuidado:** el centro usa un "candado" (`Mutex`) mientras hace estas cuentas. Es como poner el letrero de "ocupado" en el baño: garantiza que dos revisiones no se pisen entre sí si llegaran a coincidir en el tiempo.

**Paso 5 — Suena la notificación del sistema.**
Cuando el centro decide que hay algo nuevo, le pide a otro archivo que muestre la notificación de Android (esa que aparece arriba en la barra del teléfono):

`app/src/main/java/com/pettrack/app/core/notifications/AppNotifier.kt`

Este archivo construye la notificación clásica de Android: un ícono, un título, un texto (por ejemplo, "Nuevo avistamiento") y la propiedad de que al tocarla se cierre sola. También crea, al arrancar, un **canal de notificaciones** llamado "Avistamientos".

> **¿Qué es un canal de notificaciones?** Android obliga a agrupar las notificaciones en "canales" o categorías, para que el usuario pueda, desde los ajustes del teléfono, silenciar unas y dejar otras (por ejemplo, "quiero los avisos de avistamientos pero no los de promociones"). Es como los canales de la tele: cada tipo de aviso va por su canal.

Un detalle responsable del código: antes de intentar mostrar la notificación, revisa si el usuario dio permiso (`areNotificationsEnabled`), y si no lo dio, simplemente no hace nada en lugar de reventar la app. Desde Android moderno, mostrar notificaciones requiere permiso explícito del usuario, y el código lo respeta.

**Paso 6 — La campana con el contador y la pantalla de Notificaciones.**
Aparte de la notificación que suena, hay una **campanita** visible en la app con un numerito (el `unreadCount` que calcula el centro). Y hay una **pantalla dedicada** donde se listan todos los avisos, gobernada por:

`app/src/main/java/com/pettrack/app/ui/notifications/NotificationsViewModel.kt`

Ese cerebro de pantalla hace lo esperable:

- Al abrirse, **carga la lista** de avisos y muestra "cargando" mientras llegan.
- Tiene un botón **"marcar todo como leído"** (`markAllRead`), que le dice al servidor que ponga todo como visto y luego recarga.
- Cuando abres un aviso concreto (`onOpen`), lo **marca como leído** solo a él y recarga.

Cada vez que hace algo, también llama a `center.refresh()`, para que el contador de la campana quede sincronizado al instante (si marcaste todo como leído, la campana debe caer a cero de inmediato).

### Resumen del flujo de notificaciones en una frase

> Un vecino reporta un avistamiento → el servidor, solo, crea un aviso para el dueño (trigger) → la app pregunta cada 15 segundos si hay algo nuevo (polling, sin Firebase) → el "centro" detecta lo genuinamente nuevo y evita repetir → suena la notificación de Android y sube el número de la campana → el dueño abre la pantalla de Notificaciones y los marca como leídos.

Todo esto se apoya en un solo servidor (Supabase) y en piezas de código pequeñas y separadas, cada una con un trabajo claro: una vigila el tiempo, otra decide qué es nuevo, otra dibuja el aviso y otra maneja la pantalla. Esa separación es justamente lo que permite que un equipo que recién aprende pueda entender, probar y explicar cada parte por su cuenta.

---

## Requisitos del curso, pruebas y cómo ejecutar

En esta sección te muestro tres cosas: primero, una tabla que demuestra que el proyecto cumple TODO lo que el profesor pidió (y dónde se cumple); segundo, cómo comprobamos que la app funciona bien; y tercero, los pasos exactos para instalarla y ponerla a correr en tu propia computadora. Todo explicado para alguien que nunca ha programado.

### Parte 1: ¿Cumplimos con lo que pedía el examen?

El examen del curso tenía una lista de "requisitos obligatorios": funciones que la app SÍ o SÍ debía tener para aprobar. Piensa en esto como la lista de ingredientes de una receta: si falta uno, el plato no está completo. La siguiente tabla toma cada requisito y explica, en palabras simples, dónde vive dentro del proyecto y qué archivo o carpeta se encarga de él.

Antes de leerla, dos aclaraciones de vocabulario que se repiten mucho:

- **Carpeta `ui/`**: es donde vive todo lo que el usuario VE y toca en la pantalla (botones, listas, formularios). "UI" viene de *User Interface* (interfaz de usuario). Es como la vitrina y el mostrador de una tienda: la cara visible.
- **Carpeta `data/`**: es donde vive todo lo que habla con el servidor por internet para traer y guardar información. Es como la bodega y el personal de reparto de la tienda: no lo ves, pero trae y lleva la mercancía.

| Requisito obligatorio del examen | ¿Dónde y cómo se cumple en PetTrack? |
|---|---|
| **Login con validación de datos** (que la app revise que escribiste bien el correo y la contraseña antes de intentar entrar) | En la carpeta `ui/auth`, que es la pantalla de inicio de sesión. La app verifica que los campos no estén vacíos y que el correo tenga forma de correo antes de enviarlo. Es como el guardia de una discoteca que revisa tu identificación antes de dejarte pasar. |
| **Registro de usuarios** (crear una cuenta nueva) | También en `ui/auth`. Cuando alguien se registra, el servidor Supabase crea automáticamente su ficha de dueño gracias a un "disparador" llamado `handle_new_user` (una regla que se ejecuta sola al crearse una cuenta, como un formulario de bienvenida que se llena solo). |
| **JWT con expiración** (una "pulsera de acceso" digital que caduca a las pocas horas por seguridad) | El sistema de login de Supabase entrega ese token. La carpeta `core/session` lo guarda cifrado (protegido con candado) en el teléfono, y `core/network` se encarga de renovarlo solo cuando caduca, sin molestar al usuario. |
| **Logout / cerrar sesión** | En `ui/profile` (la pantalla de perfil) y en `ui/auth`. Al salir, se borra la pulsera de acceso guardada, igual que devolver la llave del hotel al irte. |
| **Dashboard con gráficos** (una pantalla de estadísticas visuales) | La carpeta `ui/dashboard` dibuja tarjetas con números clave (cuántas mascotas perdidas, encontradas, etc.), un gráfico de barras y un mapa de zonas. Los datos se los pide al servidor con la función `dashboard_stats`. |
| **API propia con operaciones GET/POST/PUT/PATCH** (un servidor propio que permite leer, crear, actualizar y modificar información) | El servidor Supabase genera esta "puerta de entrada" automáticamente (se llama PostgREST). Las instrucciones de qué pedir están descritas en `data/remote/api`. GET = leer, POST = crear, PUT/PATCH = actualizar. Es como una ventanilla de banco con distintos trámites. |
| **Consumo de la API con Retrofit** (que la app use una herramienta estándar para hablar con ese servidor) | Toda la conexión está montada en `core/network` usando **Retrofit** (la herramienta que convierte "quiero los datos de las mascotas" en una llamada real por internet). Es como un traductor telefónico entre la app y el servidor. |
| **Uso de un sensor del teléfono** | Usamos el **GPS**. La carpeta `core/location` obtiene tu ubicación con el localizador del teléfono (`FusedLocationProvider`). El GPS es el sensor; es como preguntarle al teléfono "¿dónde estoy parado ahora mismo?". |
| **Módulo Usuario (el dueño): registro, edición e historial** | En `ui/profile`. El dueño puede editar su nombre, cédula, teléfono y dirección, y ver el historial de todos los reportes que ha hecho. La información se guarda en la tabla `profiles` del servidor. |
| **Módulo Mascota completo** (crear, editar, borrar con todos sus datos) | En `ui/pets`. Permite dar de alta una mascota con foto, especie, raza, edad, color, tamaño, señas, collar/chip y estado (perdida, encontrada o en búsqueda). Los datos viven en las tablas `pets` y `pet_photos`. |
| **Geolocalización con mapa e historial de avistamientos** | En `ui/community` (que incluye el mapa) apoyado en `core/map`, que muestra un mapa de OpenStreetMap. Cada vez que alguien "avista" una mascota, queda registrado en la tabla `sightings`, formando un historial de dónde se ha visto al animal. |
| **Comunidad / búsqueda con filtros y contacto** | En `ui/community`. Puedes ver mascotas cercanas (con la función `pets_nearby`), filtrar por especie, zona, fecha y estado, y contactar al dueño con botones de llamar o escribir correo (usando la función `get_owner_contact`, que solo entrega el contacto si la mascota sigue activa, por privacidad). |
| **(Extra, no obligatorio) Notificaciones** | En `ui/notifications` y `core/notifications`. Cuando alguien reporta que vio tu mascota, el servidor crea el aviso solo (con el disparador `notify_owner_on_sighting`) y la app te lo muestra como notificación del teléfono y con una campanita con contador. |

En resumen: cada punto de la lista del profesor tiene un lugar concreto en el código donde se puede señalar y decir "aquí está, y así funciona".

### Parte 2: Pruebas y verificación (¿cómo sabemos que no está roto?)

Cuando construyes algo, no basta con que "parezca" que funciona: hay que comprobarlo. Nosotros lo comprobamos de dos maneras.

#### Las pruebas unitarias: revisiones automáticas

Una **prueba unitaria** es una pequeña "revisión automática" que verifica que una parte específica del programa hace exactamente lo que debe. Imagina una fábrica de galletas con un inspector que agarra una galleta, la mide y dice "sí, tiene el tamaño correcto" o "no, está mal". Una prueba unitaria es ese inspector, pero para un pedacito del código, y trabaja solo, en segundos, sin que nadie tenga que revisar a mano.

La gran ventaja es esta: si mañana alguien cambia algo y sin querer rompe una función, las pruebas lo detectan al instante y avisan, antes de que el error llegue a los usuarios. Es una red de seguridad.

PetTrack tiene **28 pruebas unitarias**. Revisan, entre otras cosas:

- **Los repositorios** (las piezas de la carpeta `data/repository` que piden y guardan datos): se comprueba que traen y entregan la información correctamente.
- **Los ViewModels** (los "cerebros" detrás de cada pantalla, que deciden qué mostrar): se comprueba que reaccionan bien a lo que hace el usuario.
- **El refresco del token**: se comprueba que, cuando la pulsera de acceso caduca, la app la renueva sola sin sacar al usuario. Esto se prueba con un servidor de mentira llamado *MockWebServer*, que finge ser Supabase para practicar sin tocar el servidor real.
- **El centro de notificaciones**: se comprueba que los avisos se cuentan y muestran bien.

Para correr estas 28 revisiones se usa un solo comando: `./gradlew testDebugUnitTest`. Al terminar, informa si todas pasaron.

#### La prueba de punta a punta en el emulador

Además de las revisiones automáticas, probamos la app COMPLETA de principio a fin. Para eso usamos un **emulador**: un teléfono Android "de mentira" que corre como una ventana dentro de la computadora, idéntico a uno real pero sin necesidad de tener el aparato físico. Es como un simulador de vuelo para pilotos: todo se siente real, pero es un entorno de práctica.

En ese emulador probamos la app conectada al **servidor real** de Supabase (no a uno de práctica): registrarse, iniciar sesión, crear una mascota con foto, verla en el mapa, reportar un avistamiento, recibir la notificación, ver el dashboard, etc. Todo el recorrido se completó **sin fallas**.

### Parte 3: Cómo instalar y ejecutar PetTrack

Aquí van los pasos para poner la app a funcionar en tu propia computadora, contados como una receta.

#### Lo que necesitas antes de empezar (ingredientes)

- **Android Studio** (versión Ladybug o más nueva). Es el programa oficial de Google para construir y ejecutar apps de Android; es como el taller completo con todas las herramientas. Trae incluido lo necesario para compilar (que significa "convertir el código escrito por humanos en una app que el teléfono entiende", como traducir una partitura en música que suena).
- El **Android SDK** (compileSdk 35), que es el kit de piezas y reglas de Android; normalmente se instala junto con Android Studio.
- Un proyecto de **Supabase** con la base de datos ya montada (las tablas, funciones y reglas de seguridad de este proyecto).

#### Los pasos

1. **Descarga el proyecto** desde el repositorio en GitHub (`https://github.com/eleazarrrg/PetTrack`).

2. **Crea el archivo de claves `local.properties`.** Este archivo va en la carpeta principal del proyecto y guarda las "direcciones y llaves" para conectarse a tu servidor. Es como la libreta privada con la dirección y la clave de tu casa: por seguridad NO se sube al repositorio público. Dentro escribes tres líneas:

   ```properties
   sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
   SUPABASE_URL=https://TU-PROYECTO.supabase.co
   SUPABASE_ANON_KEY=TU_ANON_KEY
   ```

   Línea por línea, en simple:
   - `sdk.dir` le dice a Android Studio dónde está instalado el kit de piezas de Android en tu computadora.
   - `SUPABASE_URL` es la dirección de internet de tu servidor, como la dirección de una tienda.
   - `SUPABASE_ANON_KEY` es la llave pública de entrada. Se llama "anon" (de *anónimo*) y es pública a propósito: por sí sola no da acceso a datos privados, porque quien realmente protege la información son las reglas de seguridad del servidor (las políticas RLS, que deciden fila por fila quién puede ver qué). La URL y la llave se copian desde Supabase, en la sección **Project Settings → API**.

3. **Desactiva "Confirm email" en Supabase.** Entra a tu proyecto de Supabase, ve a **Authentication → Providers → Email** y apaga la opción **"Confirm email"** (confirmar correo). Normalmente, al registrarse, el sistema envía un correo pidiendo que hagas clic en un enlace para verificar tu cuenta antes de poder entrar. Al desactivarlo, quien se registra puede entrar de inmediato, sin esperar ese correo. Esto es cómodo para la demostración del examen. Es como quitar el paso de "confirma tu asistencia" y dejar entrar directo.

4. **Abre y ejecuta la app.** Abre el proyecto en **Android Studio**, deja que haga el "Sync" (que es cuando ordena y descarga todas las piezas necesarias, como acomodar los ingredientes antes de cocinar) y luego presiona **Run** para correrla en un emulador o en un teléfono real conectado.
   - Alternativa por línea de comandos: `./gradlew assembleDebug` genera el instalable (el archivo APK) en la carpeta `app/build/outputs/apk/debug/`.

#### Cuentas de prueba (demo) para entrar sin registrarte

Para que puedas ver la app funcionando de inmediato, ya dejamos sembradas tres cuentas de ejemplo con datos cargados. Todas usan la misma contraseña:

| Correo | Contraseña |
|---|---|
| `ana@pettrack.test` | `Demo1234` |
| `luis@pettrack.test` | `Demo1234` |
| `marta@pettrack.test` | `Demo1234` |

También puedes **crear tu propia cuenta nueva** desde la pantalla de registro de la app; gracias al paso 3, entrarás al instante.

---

