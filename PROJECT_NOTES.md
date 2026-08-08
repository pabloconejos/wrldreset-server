# wrldReset Server - Project Notes

Documento vivo de decisiones, contexto y apuntes importantes para guiar el desarrollo.

## Vision del proyecto

wrldResetArchive es un sistema self-hosted para importar una exportacion de Instagram y visualizarla despues desde una app SwiftUI.

No es una red social. Es un visor/archivo personal.

## Estructura del server

Estructura elegida:

```text
wrldresetServer/
  api/
  importer/
  database/
  storage/
  docker-compose.yml
```

Responsabilidades:

- `importer/`: lee ZIPs de Instagram, descomprime, parsea JSONs, copia media a storage y guarda metadatos en PostgreSQL.
- `api/`: futura API REST para SwiftUI; leera PostgreSQL y servira media desde storage.
- `database/`: datos/configuracion de PostgreSQL.
- `storage/imports/`: entrada original del usuario, ZIPs de Instagram.
- `storage/temp/`: descompresion temporal durante importaciones.
- `storage/media/`: archivos finales gestionados por wrldResetArchive.

## Filosofia de codigo

Prioridad del usuario:

- codigo limpio
- sintaxis sencilla
- facil de leer
- evitar complicaciones innecesarias
- construir por capas pequenas y comprensibles

Regla general:

```text
Codigo simple antes que codigo ingenioso.
```

Evitar al inicio:

- arquitectura demasiado abstracta
- builders innecesarios
- frameworks extra sin necesidad
- separar en modulos compartidos antes de que duela la duplicacion

## Importer

El importer recibira una carpeta de ZIPs, no rutas concretas a JSONs.

Entrada esperada:

```text
storage/imports/
  instagram-wrldreset-2026-07-25-pNeIe6Yn.zip
  instagram-wrldreset-2026-07-25-8trX8Sq2.zip
```

Flujo deseado:

1. leer configuracion de storage
2. encontrar ZIPs en `storage/imports`
3. crear una carpeta temporal unica en `storage/temp`
4. descomprimir todos los ZIPs en esa carpeta temporal
5. tratar todos los ZIPs como partes de una misma exportacion
6. localizar JSONs importantes dentro de la exportacion unificada
7. importar perfil, posts, stories, reels, IGTV, archivados
8. resolver media por `uri`
9. copiar media final a `storage/media`
10. guardar metadatos/rutas en PostgreSQL
11. registrar `ImportJob`

## Exportacion real analizada

Carpeta de referencia original:

```text
/Users/pabloconejos/Documents/wrldresetArchive
```

ZIPs originales:

```text
/Users/pabloconejos/Documents/wrldresetArchive/zips
```

Partes descomprimidas observadas:

```text
instagram-wrldreset-2026-07-25-pNeIe6Yn/
instagram-wrldreset-2026-07-25-8trX8Sq2/
instagram-wrldreset-2026-07-25-8trX8Sq2-1/
```

`8trX8Sq2-1` parece duplicado por descompresion repetida local, no una parte necesaria del flujo real.

Contenido de los ZIPs:

- `8trX8Sq2`: contiene `media/` y `your_instagram_activity/`.
- `pNeIe6Yn`: contiene `ads_information/`, `connections/`, `media/`, `personal_information/`, `your_instagram_activity/`, etc.

Al descomprimir ambos ZIPs dentro de una sola carpeta temporal, sus carpetas se fusionan correctamente:

```text
instagram-import-{uuid}/
  media/
  your_instagram_activity/
  personal_information/
  connections/
  ...
```

Esto confirma que el importer debe tratar los ZIPs como partes de una misma exportacion, no como importaciones separadas.

## JSONs importantes

Rutas principales esperadas tras descomprimir:

```text
personal_information/personal_information/personal_information.json
your_instagram_activity/media/stories.json
your_instagram_activity/media/posts_1.json
your_instagram_activity/media/posts.json
your_instagram_activity/media/reels.json
your_instagram_activity/media/igtv_videos.json
your_instagram_activity/media/archived_posts.json
```

Prioridad inicial:

1. perfil
2. stories
3. posts/carruseles
4. reels
5. IGTV
6. archived posts
7. followers/following
8. comments/likes
9. mensajes solo si se decide modulo separado

## Storage

No guardar binarios en PostgreSQL.

PostgreSQL guarda:

- metadatos
- relaciones
- fechas
- captions/titulos
- rutas relativas
- hashes

Disco local guarda:

- imagenes
- videos
- subtitulos

Diseño actual local-first, con posibilidad futura de migrar a S3/MinIO.

`storagePath` debe tratarse como ruta logica/relativa, no como ruta absoluta del sistema.

## Docker y PostgreSQL

Docker compose inicial levanta PostgreSQL.

Estructura recomendada:

```text
database/
  data/
  init/
storage/
  imports/
  media/
  temp/
```

`database/data/` no debe subirse a GitHub.

`storage/imports`, `storage/media` y `storage/temp` no deben subir contenido real.

## Git

Subir repo completo `wrldresetServer`, no solo importer.

No subir:

- datos reales de PostgreSQL
- ZIPs de Instagram
- fotos/videos
- temporales
- `.env`
- `application.properties` local si contiene configuracion privada

## Decisiones pendientes

- Cambiar `ddl-auto=update` por migraciones Flyway/Liquibase cuando el esquema se estabilice.
- Definir si `api` e `importer` duplican entidades al principio o si se extrae `core` mas adelante.
- Implementar limpieza de `storage/temp` tras import exitoso.
- Decidir estrategia ante archivos duplicados en ZIPs: sobrescribir, validar hash o saltar.
- Normalizar mojibake/textos raros de la exportacion de Meta sin perder fidelidad del dato original.

## Verificacion de estructura tras unzip

Se inspecciono la carpeta temporal real:

```text
/Users/pabloconejos/code/wrldresetServer/storage/temp/instagram-import-bf2a79c3-7fe4-49f1-9202-20a45f9cc313
```

Resultado importante:

- los ZIPs se descomprimen fusionando sus carpetas directamente dentro de una unica carpeta de trabajo
- no queda una carpeta raiz por ZIP dentro del working directory
- las rutas principales existen directamente bajo `workingDirectory`

JSONs verificados en esa estructura:

```text
personal_information/personal_information/personal_information.json
your_instagram_activity/media/stories.json
your_instagram_activity/media/posts_1.json
your_instagram_activity/media/posts.json
your_instagram_activity/media/reels.json
your_instagram_activity/media/igtv_videos.json
your_instagram_activity/media/archived_posts.json
```

Decision:

- `InstagramExportFileFinder` debe recibir el `workingDirectory` unificado y resolver esas rutas relativas desde ahi.
- no hace falta modelar `exportParts` por ahora.

## Rutas conocidas de Instagram centralizadas

Se decidio centralizar rutas esperadas de la exportacion en `InstagramExportKnownFiles`.

Motivo:

- si Instagram cambia la estructura de los ZIPs/JSONs, queremos tocar el minimo codigo posible
- los servicios de importacion no deben repetir strings de rutas por todo el proyecto

Regla:

- cualquier ruta conocida de la exportacion debe referenciarse desde `InstagramExportKnownFiles` o desde una clase equivalente de configuracion/resolucion, no hardcodearse en servicios.

## Perfil importado correctamente

El importer ya ejecuta el flujo:

```text
ZIPs -> temp -> localizar JSONs -> leer personal_information.json -> guardar InstagramProfile en PostgreSQL
```

Resultado probado:

- username: `wrldreset`
- website: `http://pabloconejos.dev`
- private account: `true`
- se hizo `insert into instagram_profiles` correctamente

Pendiente observado:

- `displayName` aparece con mojibake: `Make europe great again ð...`
- revisar normalizacion de texto antes de importar masivamente captions/comentarios si afecta al visor

## ImportJob funcionando

El importer ya crea un `ImportJob` al arrancar el flujo y lo marca como `COMPLETED` si termina bien.

Resultado probado:

- se crea `ImportJob` con status `RUNNING`
- se localizan ZIPs
- se extraen ZIPs
- se localizan JSONs importantes
- se importa perfil
- se actualiza `ImportJob` a `COMPLETED`

Pendiente de limpieza:

- `StartupConfigLogger` ya no es solo logger; esta actuando como runner de flujo completo
- extraer pronto a una clase tipo `InstagramImportWorkflow` o `InstagramImportRunner` para mantener codigo limpio

## Workflow del importer separado del runner

Se extrajo la orquestacion a `InstagramImportWorkflow` y se dejo `InstagramImportRunner` como unico `CommandLineRunner`.

Resultado:

- mismo comportamiento probado correctamente
- `StartupConfigLogger` ya no debe existir como runner activo
- el flujo queda mas legible antes de importar stories/posts

Regla:

- el runner solo dispara el workflow
- el workflow coordina pasos
- finders/extractors/importers hacen tareas concretas
