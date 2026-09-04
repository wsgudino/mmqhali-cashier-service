# CLAUDE.md — mmqhali-cashier-service

Contexto permanente para trabajar en este repositorio. Leer completo antes de escribir código.

---

## Qué es este servicio

Microservicio de caja del ecosistema **MMQHALI**, para el grupo 4M en Ecuador (aseguradoras y
prestadores de salud: Ecuasanitas, PraxMED, Clínica Internacional, Naranja).

Cobra al paciente en el punto de atención, emite el comprobante fiscal y entrega el hecho
contable — en su diseño completo. **Es el reemplazo de la caja actual de Ecuasanitas** (Genesys +
LUCA + Hosvital), no una extensión de ella.

En el primer piloto, un cajero abre turno, recibe una atención valorizada, cobra el copago,
recibe un comprobante interno no fiscal para la demostración y al final del día cierra turno con
arqueo. La emisión fiscal vía E-Voucher queda diferida (ver alcance abajo).

---

## Stack

- **Java 25 + Spring Boot**
- **PostgreSQL**, una base física por empresa
- **Contenedores sobre Kubernetes, en Azure**
- Nombres de código en inglés (ver `Glosario-Caja.md` para la frontera de idioma)

⚠️ Verificar compatibilidad de Java 25 con la versión de Spring Boot, el driver de PostgreSQL y
la herramienta de migraciones antes de fijar dependencias.

⚠️ Si el clúster es AKS, la identidad administrada es **Workload Identity** con federación de
credenciales, no la identidad administrada clásica — confirmar con infraestructura antes de
configurarla.

⚠️ Para PostgreSQL sobre Azure, la autenticación con **Entra ID** es la que habilita conexiones
sin contraseña.

---

## Alcance del entregable actual (10 de septiembre)

**Construir:**
- Esqueleto desplegable
- Multiempresa: enrutamiento dinámico de datasource desde el atributo `codigoempresa` del token
- Dominio y persistencia: turno, orden de cobro con líneas, cobro con formas de pago
- Abrir turno, cerrar turno con arqueo, consultar turno
- Cargar orden de cobro **con monto ingresado a mano**
- Registrar cobro con todas sus validaciones e idempotencia
- Comprobante interno no fiscal (`PaymentReceipt`) para la demostración del piloto — no es factura, no reemplaza a la emisión diferida (D17)
- Registro de eventos de auditoría

**NO construir. Si parece que hace falta, está mal entendido el alcance:**
- Valorización real (consultas a Convenios y Tarifarios, Integración de Aseguradoras, Motor de Cálculo)
- Entrada desde el HIS
- Envío a SAP: solo se escribe el evento en la tabla de salida, sin consumidor
- Derivación, cuentas transitorias, reversos, notas de crédito, anulaciones
- Liquidación a la aseguradora
- Atención particular sin seguro, deducible, valor no cubierto
- **Emisión del comprobante vía E-Voucher** (armar el XML y enviarlo, registrar la respuesta). Salió del primer piloto (D17): la integración con E-Voucher es extensa y probablemente termine siendo un módulo propio. Diseño y tablas listos y diferidos, no descartados — ver `Esquema-BD-Caja.md`

---

## Reglas que no se negocian

1. **Todo importe es `BigDecimal` con escala 2 y `RoundingMode.HALF_UP`.** Nunca `double` ni `float`. Un centavo por operación es un arqueo que no cierra. Única excepción: `invoice_line.unit_price`, que replica los seis decimales del XML del SRI (ver `Esquema-BD-Caja.md`) — los totales, subtotales y montos cobrados siguen en dos decimales.
2. **Ninguna tabla lleva `codigoempresa`.** La separación entre empresas es física, una base por empresa. Repetirlo adentro invita a consultas que crucen empresas.
3. **`codigoempresa` se resuelve del token, nunca del cuerpo de la petición.** Ninguna firma de método de negocio lo recibe como parámetro.
4. **Nunca se emite a consumidor final.** Siempre con identificación del paciente. Es una regla fiscal: desde enero de 2026 una factura a consumidor final no se puede anular ni corregir con nota de crédito.
5. **Al paciente se le factura únicamente el copago.** El valor reconocido por la aseguradora no aparece en su comprobante.
6. **Una línea `NO_AUTORIZADA` no se cobra, no se factura y no se contabiliza.** Solo queda su constancia con el motivo. Se cobra el resto de la orden.
7. **Si la valorización falla, no se cobra.** Fallar cerrado.
8. **Un secuencial de comprobante no se consume dos veces.** Un reintento reusa el ya asignado.
9. **El dominio no depende de Spring, de JPA ni de HTTP.** Las reglas de negocio viven en clases planas; los adaptadores traducen. Por qué, con más detalle: D18 y D19 en `Decisiones-Modulo-Caja.md`.
10. **Las excepciones de dominio son distintas de los fallos técnicos.** Una regla violada es un 400 con mensaje entendible; un fallo técnico es un 500 con reintento.
11. **El comprobante interno (`PaymentReceipt`) nunca consume el secuencial fiscal ni reusa el agregado `Invoice`.** Es una vista de lectura sobre el cobro, no un documento fiscal — ver D20 en `Decisiones-Modulo-Caja.md`.

---

## Reglas fiscales de Ecuador que afectan el código

- **IVA:** servicios de salud van con tarifa 0%. Tarifa general 15%. La línea de IVA 0% debe aparecer igual en el comprobante; omitirla es infracción. El modelo soporta tarifas distintas por línea.
- **Formas de pago:** códigos de la Tabla 24 del SRI. Solo `01`, `15`, `16`, `17`, `18`, `19`, `20`, `21`. Si hay varias, la suma debe cuadrar con el total.
- **Bancarización:** sobre USD 500 la operación debe usar el sistema financiero. El código `01` (sin sistema financiero) queda prohibido por encima de ese monto.
- **Transmisión:** desde enero de 2026 es inmediata. La fecha de emisión debe ser la fecha corriente de la operación. No hay ventana de envío diferido.
- **Firma y trámite ante el SRI están fuera del alcance.** Los hace E-Voucher. Este servicio solo arma el XML y lo envía.

---

## Contratos externos

**E-Voucher** — se le envía el XML de la factura y él genera y autoriza el comprobante.

⚠️ **El contrato exacto no está confirmado.** No inventarlo. Antes de implementar hace falta
saber: endpoint y autenticación, si el XML va con clave de acceso y secuencial ya generados o
los asigna E-Voucher, si responde en el momento o hay que consultar después, qué devuelve ante
un rechazo del SRI, y si soporta varios RUC.

**SAP** — mecanismo de posteo sin definir. Fuera del entregable actual. Solo se escribe el
evento de turno cerrado en la tabla de salida.

---

## Cómo se escribe

- **Idioma:** código en inglés. Los términos del negocio que no tienen traducción honesta se dejan en español: `copago`, `convenio`, `arqueo`. Ver `Glosario-Caja.md` antes de nombrar algo nuevo.
- **Repositorio y artefactos:** minúsculas con guiones. Sin espacios, guiones bajos ni mayúsculas mezcladas.
- **Asincronía:** tabla de salida (outbox) con proceso programado. **No** provisionar mensajería nueva para este entregable. Se reemplaza más adelante sin tocar el dominio.
- **Idempotencia:** clave enviada por el cliente, con restricción única en base de datos. No alcanza con validarla en memoria.
- **Fechas y horas:** se pasan como parámetro a las clases de dominio, no se lee el reloj adentro. Así las pruebas pueden fijar el momento.

---

## Deuda declarada

No son olvidos. Están anotadas para que aparezcan cuando toque:

- **Un cobro se asume ingreso definitivo.** Cuando entre el flujo de derivación con cuentas transitorias, va a existir dinero cobrado que todavía no es ingreso de nadie. Este supuesto habrá que sacarlo.
- **`ANULADA` existe en los estados de la orden de cobro** aunque el flujo de reversos no se construya. Es a propósito: agregar un estado con datos productivos encima es una migración.
- **El envío a SAP queda como evento sin consumidor.** Conectarlo después es medio día.

---

## Documentación: vive en Obsidian, no en el repositorio

Toda la documentación de este proyecto está — y se sigue escribiendo — en la bóveda de Obsidian,
carpeta `MMQHALI/Modulo de Caja/`. El repositorio no acumula `.md` de diseño, decisiones ni
contexto de negocio: eso vive en la vault para que quede indexado, enlazado y disponible entre
sesiones. Acceso siempre por el MCP `obsidian` (`vault_read`, `search_simple`, `vault_patch`,
etc.), nunca leyendo el disco directo — ver el protocolo completo en el `CLAUDE.md` global.

**Antes de tocar código en este repo, leer lo pertinente de esta carpeta.** No es material de
referencia opcional: las reglas no negociables, el modelo de dominio y el esquema de BD de este
documento son un resumen: la fuente completa está allá.

- `Modulo-Caja-MMQHALI.md` — índice de todo, punto de entrada
- `Decisiones-Modulo-Caja.md` — por qué cada cosa es como es
- `Modelo-Dominio-Caja.md` — las clases, sus invariantes y sus estados
- `Esquema-BD-Caja.md` — las tablas con cada columna, su tipo y por qué está. **Es lo que hay que escribir en las migraciones**
- `Glosario-Caja.md` — frontera de idioma y términos del negocio
- `Diseno-Preliminar-Modulo-Caja-MMQHALI.md` — el diseño completo
- `Trazado-Caso-Base-Modulo-Caja-MMQHALI.md` — los 42 datos del caso base y los 16 que no existen
- `Insumos-Diseno-Modulo-Caja-MMQHALI.md` — normativa, sistema actual, evidencia de código
- `Plan-Construccion-Rebanada-Vertical-Caja.md` — plan de construcción de la rebanada vertical

**Si una decisión no está en `Decisiones-Modulo-Caja.md`, no está tomada.** No asumirla:
preguntar.

### Dónde escribir documentación nueva

Cualquier documentación que surja del trabajo en este repo — una decisión de arquitectura, un
flujo aclarado, un patrón encontrado, contexto de una sesión — se escribe en la vault, no como
archivo suelto en el repo:

- Específica del módulo de caja (dominio, BD, decisiones de este servicio) → dentro de
  `MMQHALI/Modulo de Caja/`, en la nota existente que corresponda (`vault_patch` para agregar a
  una sección) o en una nota nueva dentro de esa carpeta si no encaja en ninguna.
- Del ecosistema MMQHALI en general, no específica de caja → en `MMQHALI/`, fuera de la
  subcarpeta `Modulo de Caja`.

Antes de crear una nota nueva, buscar con `search_simple`/`search_query` si ya existe algo que
deba actualizarse en vez de duplicarse.
