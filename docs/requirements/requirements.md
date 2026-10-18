# Historias de usuario

## Acceso y administración

### Historia de Usuario — HU-01: Iniciar sesión

Como trabajador del hotel,<br>
quiero iniciar sesión con mi usuario y contraseña,<br>
para acceder a las funciones permitidas según mi rol.

### Criterios de Aceptación

- Escenario 1: Credenciales válidas. Al introducir un usuario y una contraseña correctos de una cuenta activa, el sistema permite el acceso.
- Escenario 2: Credenciales no válidas. Si el usuario o la contraseña no son correctos, el sistema deniega el acceso e informa del error sin revelar cuál de los datos era correcto.
- Escenario 3: Cuenta inactiva. Si la cuenta está desactivada, el sistema deniega el acceso.
- Modelo de datos: El usuario contiene nombre y apellidos, nombre de usuario único, correo electrónico, contraseña almacenada como hash seguro, rol, estado de la cuenta y fecha de creación.

### Restricciones Técnicas

- La contraseña no se almacenará ni registrará en texto claro. Se utilizará un algoritmo de hash diseñado para contraseñas; no se prescribe aquí una política de longitud o composición que no se haya acordado.

### Historia de Usuario — HU-02: Gestionar usuarios

Como administrador,<br>
quiero crear y modificar usuarios, asignarles un rol y desactivar sus cuentas,<br>
para gestionar el acceso de los trabajadores al sistema.

### Criterios de Aceptación

- Escenario 1: Alta y modificación. Administración puede crear un usuario y modificar sus datos y rol.
- Escenario 2: Desactivación. Administración puede desactivar una cuenta y esta deja de permitir el inicio de sesión.
- Escenario 3: Trazabilidad. Si el usuario tiene actividad asociada, se conserva su registro y se desactiva en vez de eliminarlo físicamente.
- Modelo de datos: El rol de cada usuario será recepción, limpieza o administración; su estado será activo o inactivo.

### Historia de Usuario — HU-03: Gestionar habitaciones

Como administrador,<br>
quiero registrar, modificar y desactivar habitaciones,<br>
para mantener actualizado el inventario del hotel.

### Criterios de Aceptación

- Escenario 1: Alta de habitación. Administración puede registrar una habitación con número, tipo y precio por noche.
- Escenario 2: Desactivación con historial. Una habitación vinculada a reservas o tareas históricas se conserva y se marca como inactiva en lugar de eliminarse físicamente.
- Escenario 3: Estancia en curso. El sistema impide desactivar una habitación mientras tenga una estancia en curso.
- Modelo de datos: La habitación contiene número único, tipo, precio por noche, estado de ocupación (vacante u ocupada), observaciones opcionales y estado de registro (activa o inactiva). La existencia de una limpieza activa se representa mediante su tarea y puede coincidir con el estado ocupada.

### Historia de Usuario — HU-04: Consultar habitaciones

Como trabajador del hotel,<br>
quiero consultar las habitaciones activas, su estado y su disponibilidad para un periodo,<br>
para saber cuáles puedo asignar o preparar.

### Criterios de Aceptación

- Escenario 1: Consulta general. El sistema muestra las habitaciones activas, si están vacantes u ocupadas y si tienen una tarea de limpieza activa. Una habitación puede aparecer ocupada y en limpieza al mismo tiempo.
- Escenario 2: Consulta por periodo. Al indicar fechas, el sistema informa de la disponibilidad considerando las reservas vigentes para ese periodo.
- Modelo de datos: La disponibilidad para fechas se deriva de las reservas y no es un estado persistido alternativo al estado operativo.

## Clientes y reservas

### Historia de Usuario — HU-05: Gestionar clientes

Como recepcionista,<br>
quiero registrar y modificar los datos de los clientes,<br>
para identificarlos y asociarlos correctamente a sus reservas.

### Criterios de Aceptación

- Escenario 1: Alta de cliente. Recepción puede registrar un cliente con nombre y apellidos.
- Escenario 2: Datos de contacto opcionales. Documento de identidad, teléfono y correo electrónico pueden registrarse cuando se proporcionen.
- Modelo de datos: El cliente contiene nombre y apellidos obligatorios; documento de identidad, teléfono y correo electrónico son opcionales.

### Historia de Usuario — HU-06: Crear una reserva

Como recepcionista,<br>
quiero crear una reserva indicando cliente, habitación y fechas,<br>
para organizar la estancia del huésped.

### Criterios de Aceptación

- Escenario 1: Datos completos. La reserva puede confirmarse cuando tiene un cliente, una habitación activa y fechas válidas.
- Escenario 2: Datos incompletos o fechas inválidas. El sistema no confirma la reserva e indica qué condición debe corregirse.
- Modelo de datos: La reserva incluye cliente, habitación, fecha de entrada, fecha de salida, estado, fecha de creación y usuario creador.

### Historia de Usuario — HU-07: Evitar reservas incompatibles

Como recepcionista,<br>
quiero que el sistema valide la disponibilidad antes de confirmar o modificar una reserva,<br>
para evitar asignar una misma habitación a huéspedes en periodos incompatibles.

### Criterios de Aceptación

- Escenario 1: Fechas disponibles. El sistema confirma la reserva si no hay otra reserva confirmada o en curso que se solape para esa habitación.
- Escenario 2: Fechas ocupadas. El sistema rechaza la operación si existe una reserva incompatible.
- Escenario 3: Reserva cancelada. Una reserva cancelada no impide reservar esas fechas.
- Escenario 4: Solicitudes simultáneas. Si dos solicitudes concurrentes intentan reservar fechas incompatibles, como máximo una puede confirmarse.

### Restricciones Técnicas

- La validación de fechas y disponibilidad se ejecutará en el servidor. La comprobación y confirmación deberán mantener la integridad ante operaciones concurrentes.

### Historia de Usuario — HU-08: Modificar una reserva

Como recepcionista,<br>
quiero modificar los datos de una reserva existente,<br>
para corregir o actualizar la estancia cuando sea necesario.

### Criterios de Aceptación

- Escenario 1: Reserva modificable. Recepción puede modificar una reserva que no esté cancelada ni finalizada.
- Escenario 2: Revalidación. El sistema vuelve a comprobar fechas y disponibilidad antes de guardar los cambios.
- Escenario 3: Historial. La modificación queda registrada con los datos cambiados, usuario responsable y fecha y hora.

### Historia de Usuario — HU-09: Cancelar una reserva

Como recepcionista,<br>
quiero cancelar una reserva confirmada,<br>
para liberar sus fechas y conservar constancia de la cancelación.

### Criterios de Aceptación

- Escenario 1: Cancelación. Una reserva confirmada puede cancelarse y deja de bloquear fechas.
- Escenario 2: Conservación. La reserva permanece almacenada con estado cancelado; no se elimina físicamente.
- Escenario 3: Registro. Se conserva la reserva afectada, el usuario que canceló, la fecha y hora y, si se indica, el motivo.

### Historia de Usuario — HU-10: Consultar reservas

Como recepcionista,<br>
quiero consultar y filtrar las reservas por cliente, habitación, estado y fechas,<br>
para encontrar rápidamente la información de una estancia.

### Criterios de Aceptación

- Escenario 1: Filtrado. La lista muestra las reservas que coinciden con los filtros seleccionados.
- Escenario 2: Información de reserva. Cada resultado permite consultar el cliente, la habitación, las fechas y el estado de la reserva.

### Historia de Usuario — HU-11: Consultar el historial de reservas

Como administrador,<br>
quiero consultar las acciones realizadas sobre las reservas y las tareas de limpieza,<br>
para disponer de trazabilidad sobre la actividad del hotel.

### Criterios de Aceptación

- Escenario 1: Eventos de reserva. El historial incluye creación, modificación, cancelación, check-in y check-out, con usuario responsable y fecha y hora.
- Escenario 2: Ocupación y limpieza. El estado de ocupación actual se obtiene de la reserva en curso y el historial de limpiezas se consulta en las tareas de limpieza.
- Escenario 3: Datos modificados. Para una modificación de reserva, el historial permite identificar los datos que cambiaron.
- Escenario 4: Integridad histórica. Los usuarios no pueden modificar los eventos históricos.
- Modelo de datos: `EventoReserva` referencia la reserva y almacena tipo de acción, usuario, fecha y hora, datos modificados cuando corresponda y motivo opcional de cancelación. `TareaLimpieza` conserva el historial de las limpiezas realizadas.

## Estancias y limpieza

### Historia de Usuario — HU-12: Registrar el check-in

Como recepcionista,<br>
quiero registrar el check-in de una reserva confirmada,<br>
para indicar el comienzo de la estancia y actualizar el estado de la habitación.

### Criterios de Aceptación

- Escenario 1: Check-in válido. La reserva pasa a estar en curso y la habitación pasa a ocupada.
- Escenario 2: Habitación no preparada. No se permite el check-in si la habitación está ocupada o tiene una tarea de limpieza posterior a un check-out pendiente o en curso. Una limpieza de servicio durante otra estancia no convierte la habitación en vacante.
- Escenario 3: Trazabilidad. El check-in se registra en el historial de la reserva.

### Historia de Usuario — HU-13: Registrar el check-out

Como recepcionista,<br>
quiero registrar el check-out de una reserva en curso,<br>
para finalizar la estancia e iniciar el proceso de limpieza.

### Criterios de Aceptación

- Escenario 1: Check-out válido. La reserva pasa a finalizada y la habitación pasa a vacante; la tarea de limpieza posterior al check-out indica que aún no está lista para una nueva estancia.
- Escenario 2: Tarea de limpieza. El sistema crea una tarea de limpieza pendiente vinculada a la habitación y al check-out.
- Escenario 3: Trazabilidad. El check-out se registra en el historial de la reserva.

### Historia de Usuario — HU-14: Solicitar limpieza durante una estancia

Como recepcionista o administrador,<br>
quiero crear una tarea de limpieza durante una estancia,<br>
para atender una solicitud o incidencia sin alterar la reserva.

### Criterios de Aceptación

- Escenario 1: Creación de tarea. La tarea queda asociada a la habitación, indica que su origen es una solicitud durante la estancia y puede asignarse a un empleado de limpieza.
- Escenario 2: Estado de habitación. Crear o completar esta tarea no cambia el estado ocupado de la habitación ni finaliza la reserva.
- Escenario 3: Tareas simultáneas. No puede haber más de una tarea de limpieza no completada por habitación.
- Modelo de datos: La tarea incluye habitación, origen, solicitante cuando corresponda, empleado asignado opcional, fechas y horas de inicio y finalización, y observaciones o incidencias. No almacena un estado separado: sin hora de inicio está pendiente; con inicio pero sin finalización está en curso; con hora de finalización está completada.

### Historia de Usuario — HU-15: Gestionar tareas de limpieza

Como personal de limpieza,<br>
quiero consultar, iniciar y completar tareas, y añadir observaciones,<br>
para organizar el trabajo y comunicar incidencias.

### Criterios de Aceptación

- Escenario 1: Consulta. El personal de limpieza puede ver las tareas pendientes y en curso.
- Escenario 2: Actualización. Puede iniciar una tarea, añadir observaciones o incidencias y registrar su finalización.
- Escenario 3: Registro. Se conservan las fechas y horas de inicio y finalización; el sistema determina a partir de ellas si la tarea está pendiente, en curso o completada. Las tareas completadas permanecen como historial.
- Escenario 4: Limpieza tras check-out. Al completar la tarea posterior a un check-out, la habitación queda lista para una nueva estancia, siempre que no tenga otra tarea de limpieza pendiente o en curso.
- Escenario 5: Limpieza durante estancia. Completar una tarea solicitada durante una estancia no cambia el estado ocupado de la habitación.

### Historia de Usuario — HU-16: Consultar el panel de control

Como administrador,<br>
quiero consultar un resumen visual del estado actual del hotel,<br>
para conocer la ocupación y la actividad de las reservas.

### Criterios de Aceptación

- Escenario 1: Estados actuales. El panel muestra habitaciones listas para alojar, habitaciones ocupadas y habitaciones con una tarea de limpieza activa. Los recuentos de ocupadas y en limpieza pueden solaparse.
- Escenario 2: Ocupación. Muestra el porcentaje actual, calculado como habitaciones ocupadas dividido entre habitaciones activas, multiplicado por 100. Una habitación ocupada durante una limpieza de servicio sigue contando como ocupada.
- Escenario 3: Reservas activas. Muestra el número de reservas confirmadas o en curso. Una reserva futura confirmada cuenta como activa, pero no como habitación ocupada en el indicador actual.
- Escenario 4: Datos actualizados. Los indicadores representan el estado en el momento de la consulta.

## Administración y uso del sistema

### Historia de Usuario — HU-17: Configurar el hotel

Como administrador,<br>
quiero mantener el nombre y la configuración básica del hotel,<br>
para adaptar el sistema a la instalación.

### Criterios de Aceptación

- Escenario 1: Datos del hotel. Administración puede consultar y modificar el nombre y la configuración básica.
- Escenario 2: Número de habitaciones. El total se obtiene de las habitaciones activas registradas y no de un valor duplicado que pueda quedar desactualizado.
- Modelo de datos: La configuración contiene el nombre del hotel y los parámetros básicos necesarios para el funcionamiento.

### Historia de Usuario — HU-18: Usar el sistema en español

Como trabajador del hotel,<br>
quiero utilizar una interfaz clara y recibir mensajes comprensibles,<br>
para realizar mis tareas sin formación técnica avanzada.

### Criterios de Aceptación

- Escenario 1: Interfaz. Las etiquetas y los estados se presentan en español y usan términos comprensibles para el personal.
- Escenario 2: Validación. Cuando una acción no se puede completar, el mensaje explica qué dato o regla debe corregirse.
- Escenario 3: Operación diaria. Las acciones principales se pueden completar desde la interfaz, sin acceso directo a la base de datos.

### Historia de Usuario — HU-19: Restringir el acceso según el rol

Como administrador,<br>
quiero que cada rol solo pueda realizar las acciones autorizadas,<br>
para proteger la información y separar las responsabilidades del personal.

### Criterios de Aceptación

- Escenario 1: Recepción. Puede gestionar clientes y reservas, registrar check-in y check-out y solicitar limpieza.
- Escenario 2: Limpieza. Puede consultar y actualizar tareas de limpieza, pero no gestionar reservas, usuarios ni datos administrativos.
- Escenario 3: Administración. Tiene acceso a la gestión de usuarios, habitaciones, reservas y configuración, y al panel de control.
- Escenario 4: Acceso no autorizado. Una operación no permitida para el rol se deniega aunque se intente invocar fuera de la interfaz.

### Restricciones Técnicas

- Las comprobaciones de permisos se aplicarán en el servidor, no únicamente ocultando opciones en la interfaz.

### Historia de Usuario — HU-20: Mantener abierta la posibilidad de ampliación

Como propietario del hotel,<br>
quiero que el sistema pueda ampliarse en el futuro para ofrecer reservas online,<br>
para incorporar nuevos canales sin rehacer el sistema completo.

### Criterios de Aceptación

- Escenario 1: Alcance actual. En esta versión, las reservas online para clientes no están incluidas; el sistema se usa internamente por el personal.
- Escenario 2: Evolución futura. La posibilidad de reservas online se considera una ampliación futura y no debe confundirse con funcionalidad implementada en esta versión.

## Reglas de negocio

### RN-01. Interpretación del periodo de reserva

La fecha de entrada está incluida y la fecha de salida excluida. La fecha de salida debe ser posterior a la fecha de entrada. Por tanto, una reserva del 10 al 12 y otra del 12 al 14 no se solapan; ambas pueden confirmarse si se cumplen las demás condiciones.

### RN-02. Solapamiento de reservas

Dos reservas de la misma habitación se solapan cuando la fecha de entrada de una es anterior a la fecha de salida de la otra y, a la vez, la fecha de entrada de la otra es anterior a la fecha de salida de la primera. Las reservas canceladas y finalizadas no bloquean disponibilidad futura.

### RN-03. Disponibilidad futura y estado operativo

La disponibilidad para reservar un periodo se determina por la ausencia de reservas confirmadas o en curso que se solapen. El estado de ocupación actual no bloquea por sí solo una reserva futura. Para hacer check-in, la habitación debe estar vacante y lista: no debe tener otra estancia en curso ni una tarea de limpieza posterior a un check-out pendiente o en curso. Una limpieza de servicio durante una estancia puede coincidir con el estado ocupada.

### RN-04. Estados de ocupación y limpieza

Una habitación activa tendrá exactamente un estado de ocupación: vacante u ocupada. Las tareas de limpieza tienen su propio estado y pueden estar activas mientras la habitación está ocupada. Por tanto, la ocupación y la limpieza no son estados mutuamente excluyentes. Una habitación está lista para alojar cuando está vacante y no tiene una tarea de limpieza posterior a un check-out pendiente o en curso. Estos estados presentes no sustituyen la comprobación de reservas para fechas futuras.

### RN-05. Transiciones por estancia

Un check-in válido cambia el estado de ocupación de vacante a ocupada. Un check-out válido cambia el estado de ocupación de ocupada a vacante y crea una tarea de limpieza posterior a la estancia. La habitación no estará lista para alojar hasta que esa tarea se complete.

### RN-06. Disponibilidad tras el check-out

Una habitación con una tarea de limpieza posterior al check-out pendiente o en curso no estará lista para un nuevo check-in. Al completar esa tarea, quedará lista si no hay otra limpieza posterior al check-out pendiente o en curso. Las tareas de servicio durante una estancia no hacen que la habitación deje de estar ocupada.

### RN-07. Limpieza durante una estancia

Una tarea de limpieza solicitada durante una estancia no finalizará ni alterará la reserva ni el estado ocupado de la habitación. Por tanto, una habitación puede estar ocupada y en limpieza al mismo tiempo. Solo podrá existir una tarea de limpieza no completada por habitación a la vez.

### RN-08. Conservación de registros

Las reservas canceladas, las tareas de limpieza completadas y los usuarios o habitaciones con referencias históricas se conservarán para mantener la trazabilidad. Las reservas conservarán el estado cancelado y los usuarios y habitaciones podrán marcarse como inactivos; la finalización de una tarea de limpieza se deducirá de sus fechas de inicio y finalización, no de un estado almacenado.

### RN-09. Cancelaciones

Toda cancelación registrará la reserva afectada, el usuario responsable y la fecha y hora. El motivo es opcional.

### RN-10. Datos obligatorios de reserva

Para confirmar una reserva deberán existir un cliente, una habitación activa, una fecha de entrada y una fecha de salida válidas.

### RN-11. Métricas del panel

El panel refleja el estado en el momento de la consulta. El porcentaje de ocupación actual se calcula como habitaciones ocupadas / habitaciones activas × 100. Las reservas activas son las que están confirmadas o en curso; una reserva futura confirmada cuenta como activa, pero no como habitación ocupada en el indicador actual.
