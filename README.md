# Turnero Luna y Armonía

App Android nativa (Kotlin + Jetpack Compose + Room) para agendar turnos de
tratamientos faciales y corporales, con recordatorios manuales por WhatsApp,
compartir turno como evento de calendario (.ics), estados de pago y balance
por período.

## Cómo abrir el proyecto

1. Necesitás **Android Studio** (versión Koala/2024.1 o más nueva) y JDK 17
   (Android Studio ya lo trae integrado).
2. `File > Open...` y elegís esta carpeta (`turnero/`).
3. Android Studio va a pedir sincronizar Gradle la primera vez — dejalo, va
   a descargar Gradle 8.7 y las dependencias solo. Este proyecto no incluye
   el binario `gradle-wrapper.jar` (no se puede generar sin conexión desde
   donde se armó el código); si Android Studio no lo regenera solo al
   sincronizar, andá a `File > Sync Project with Gradle Files` o corré
   `gradle wrapper` una vez con cualquier Gradle instalado localmente.
4. Conectá un celular Android (modo desarrollador + depuración USB) o creá
   un emulador, y correr con el botón ▶ (mínimo Android 8.0 / API 26).

## Qué incluye hoy (funcional, corre sin configuración adicional)

- **Agenda** del día, con navegación entre días y tarjetas de turno
  coloreadas por estado (pagado / pendiente / agendado / no show / cancelado).
- **Ciclo de estados del turno**, tal como lo definimos:
  `Agendado → (pago anticipado en cualquier momento) → Pagado`, o
  `Agendado → termina el turno → la app pregunta "¿se cobró?" → Pagado / Pendiente de pago / No show`.
  La pregunta aparece sola al abrir la Agenda si hay turnos vencidos sin resolver.
- **Nuevo turno / Reagendar**, con catálogo de tratamientos que autocompleta
  precio y duración (editables), alta de paciente al vuelo (nombre, teléfono,
  **email**, notas), checkbox de pago anticipado, y aviso de WhatsApp
  tildado por default al reagendar.
- **Ficha de paciente**: turnos totales, **total tratamientos** (pagado +
  pendiente), pendiente de pago, no shows, e historial completo.
- **Tratamientos**: catálogo precargado con los 15 tratamientos habituales
  que armamos (7 faciales, 8 corporales), editable — alta y baja.
- **Balance** por período (hoy / semana / mes), separado en cobrado vs.
  pendiente, calculado al vuelo (no se guarda, así nunca queda desactualizado).
- **Compartir turno**: genera un `.ics` con título "Luna y Armonía -
  <tratamiento>" y lo manda al selector nativo de Android (WhatsApp, mail,
  etc.) — lo abre tanto Google Calendar como el Calendar de iOS.
- **Recordatorio por WhatsApp**: abre el chat de la paciente con el mensaje
  pre-cargado; el envío es manual, vos decidís cuándo tocar enviar.
- **Agregar a mi calendario** (toggle por turno, en "Nuevo turno"): además de
  guardarse en la app, crea un evento en tu Google Calendar personal usando
  el Calendar Provider nativo de Android (`app/.../util/CalendarSync.kt`) —
  no requiere login aparte ni cuenta de Firebase, solo el permiso de
  calendario que Android pide una vez cuando activás el toggle por primera
  vez. Si reagendás o cancelás un turno que tenía este toggle activado, el
  evento en tu calendario se actualiza o se borra solo.
- **Confirmación al guardar un turno**: tocar "Guardar turno" abre un
  resumen con **Cancelar**, para no guardar por error.
- **Compartir con la paciente ahora**: checkbox en "Nuevo turno" que, si
  está tildado, abre el compartir (.ics) apenas se confirma el guardado.
- **Vista mensual de la Agenda**: toggle Día/Mes arriba de la Agenda — el
  mes muestra un punto en los días con turnos y al tocar uno te lleva a esa
  fecha en vista Día.
- **Pestaña Pacientes**: lista completa y buscable de todas las pacientes,
  con alta independiente (no hace falta agendarle un turno para cargarla).
- **Tratamientos editables**: tocar una fila del catálogo abre la edición
  (antes solo se podía dar de baja).
- El campo teléfono ahora muestra un placeholder de ejemplo
  (`+54 9 11 5555-1234`) y aclara que hay que incluir código de país y de
  área, porque de ahí sale el número que se usa para el link de WhatsApp.
- El diálogo "¿Se cobró?" separa visualmente "No vino" del resto (línea
  divisoria + texto más chico) para que no compita con las otras dos
  opciones.
- **Pantalla Perfil**: elegís explícitamente qué calendario de Google (con
  su email) se usa como destino cuando activás "Agregar a mi calendario".
  Sin esta elección, sigue cayendo al calendario primario del dispositivo
  como antes — pero si el celular tiene más de una cuenta de Google, acá
  se define cuál es la correcta. La elección se guarda con DataStore
  (`app/.../util/PreferenciasCalendario.kt`), local al dispositivo.

Todo esto funciona hoy con almacenamiento **local** (Room/SQLite) — no
necesita ninguna cuenta ni configuración para probarse.

## Cómo sigue: pasar a datos en la nube (cuenta única compartida)

Definimos que va a ser una sola cuenta de centro que use todo el equipo, con
los datos sincronizados. El camino más simple es **Firebase (Firestore +
Authentication)**:

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com)
   (gratis en el plan Spark para este volumen de uso).
2. Agregar una app Android con el `applicationId`
   `com.lunayarmonia.turnero`, descargar el archivo `google-services.json`
   generado y pegarlo en `app/google-services.json` (ya está en
   `.gitignore` porque tiene credenciales del proyecto).
3. Habilitar **Firestore Database** y **Authentication** (con email/contraseña
   alcanza para una sola cuenta compartida).
4. En el código, todo pasa por una sola clase: `TurneroRepository`
   (`app/src/main/java/com/lunayarmonia/turnero/data/TurneroRepository.kt`).
   Hoy delega en Room; el cambio consiste en que cada método también lea/escriba
   en Firestore (o reemplace Room directamente), sin tocar ni los ViewModels
   ni las pantallas — ya están escritos contra esta interfaz.
5. Agregar una pantalla de login simple (usuario/contraseña de Firebase Auth)
   antes de `TurneroApp` en `MainActivity`.

## Estructura del proyecto

```
app/src/main/java/com/lunayarmonia/turnero/
  data/          Entidades (Paciente, Tratamiento, Turno), DAOs, Room, Repositorio
  ui/
    agenda/      Pantalla de Agenda
    turno/       Detalle de turno + formulario nuevo/reagendar
    paciente/    Ficha de paciente
    tratamientos/Catálogo de tratamientos
    balance/     Balance por período
    theme/       Colores y tema Material3
    AppViewModel.kt   Estado y lógica de toda la app
    Navigation.kt     Navegación entre pantallas
  util/          Generador de .ics, link de WhatsApp, formato de moneda
  MainActivity.kt
```

## Pendientes / decisiones abiertas para una siguiente vuelta

- Pantalla de login cuando se sume Firebase Auth.
- Notificación local (no solo el diálogo al abrir la app) para turnos
  vencidos sin resolver, por si un día no se abre la Agenda.
- Editar un tratamiento existente desde la UI (hoy se puede dar de alta y
  de baja; falta el editar in-place, aunque el dato ya soporta `Update`).
- Buscador de pacientes por nombre en una pantalla dedicada (hoy se busca
  dentro del formulario de nuevo turno).
