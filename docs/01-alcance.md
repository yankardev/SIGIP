# Alcance empresarial

## Propósito y contexto

Centralizar la atención comercial y el seguimiento de trabajos de impresión para que cada pedido conserve especificaciones, precio acordado, diseño autorizado, materiales, avances y entrega. La imprenta concreta y sus condiciones comerciales todavía no han sido identificadas: estas reglas son una base propuesta, no un diagnóstico comprobado.

Se plantea una sola empresa y una sede inicial, moneda PEN, atención a personas y empresas, recojo en local o entrega coordinada. Multisede, contabilidad integral e integración con maquinaria quedan fuera del primer producto.

## Actores

| Actor / rol | Puede hacer | Límite relevante |
|---|---|---|
| Visitante | Consultar catálogo y preparar solicitud | No ve pedidos privados |
| CLIENTE | Cotizar, confirmar compra, subir archivos, aprobar diseños, ver seguimiento | Solo sus propios recursos |
| VENTAS | Atender solicitudes, emitir proformas, confirmar fechas y registrar pedidos | No acredita pagos por sí solo |
| CAJA | Verificar adelantos, saldos y reversos | No aprueba diseños en nombre del cliente |
| DISENO | Revisar archivos y publicar pruebas digitales | No se autoaprueba el trabajo |
| PRODUCCION | Programar trabajos, registrar avances y calidad | No inicia con requisitos pendientes |
| ALMACEN | Ingresos, reservas, consumos, devoluciones y ajustes | Todo ajuste lleva motivo |
| ADMIN | Configurar usuarios, permisos y políticas | Las excepciones quedan auditadas |

El registro público solo asigna CLIENTE. Un usuario puede tener varios roles por asignación administrativa; los roles no eliminan el control de propiedad del recurso.

## Funciones y alcance de cada módulo

| Módulo | Funciones previstas |
|---|---|
| Portal comercial | Catálogo, búsqueda, configuración de cantidad/medidas/material/color/acabados, carrito y solicitud de cotización |
| Tarifas | Combinaciones permitidas, mínimos, tramos por cantidad y recargos; los trabajos especiales pasan a revisión comercial |
| Proformas | Numeración, versión, vigencia, PDF, condiciones comerciales, aprobación/rechazo y conversión a pedido |
| Clientes | Datos de contacto y entrega; persona o empresa; identificación según el caso |
| Pedidos | Detalles congelados, fecha acordada, responsables, historial y seguimiento |
| Caja | Registro y verificación manual de adelantos/saldos, evidencia, rechazo y reverso con motivo |
| Diseño | Archivos fuente, observaciones, versiones de prueba, aprobación explícita de una versión |
| Inventario | Insumos por unidad de medida, ingresos, reservas, consumo real, merma, devolución y stock mínimo |
| Producción | Orden de trabajo, planificación, impresión, acabados, revisión de calidad y reproceso |
| Entrega | Recojo o despacho coordinado, receptor, fecha y evidencia de entrega |
| Notificaciones | Bandeja interna y correo posterior; estado de envío y reintentos |
| Reportes | Proformas aceptadas, pedidos atrasados, trabajo por etapa, cobros pendientes y materiales consumidos |

## Proforma PDF

Contendrá identidad y datos comerciales de la imprenta, cliente, número y versión, emisión y vencimiento, moneda, detalle técnico, cantidades, precios, descuentos autorizados, impuestos configurados, total, condiciones de pago, plazo estimado y modalidad de entrega. El PDF es la representación de una versión guardada; no recalcula precios con tarifas nuevas.

La proforma se limita a la oferta comercial. SIGIP no implementará emisión fiscal en esta fase. Una integración de facturación requiere un alcance y una validación propios.

## Reglas propuestas

1. Una proforma emitida conserva su contenido; una modificación genera otra versión y sustituye la oferta vigente. Solo una versión vigente puede aceptarse.
2. Una versión vencida no crea un pedido. Ventas debe emitir una nueva oferta.
3. Una aceptación se convierte en un solo pedido, incluso si el cliente repite el clic.
4. El precio final se calcula y valida en servidor. Se conserva el desglose aceptado.
5. La aceptación de la proforma confirma condiciones comerciales; la aprobación del diseño autoriza una prueba gráfica específica. Son decisiones diferentes.
6. Subir una constancia de transferencia no equivale a pago validado. CAJA verifica antes de acreditar el importe.
7. El adelanto requerido es configurable por pedido según la política aprobada. No se fija un porcentaje sin conocer el negocio.
8. Antes de liberar producción deben cumplirse: pedido confirmado, condición de pago satisfecha, diseño vigente aprobado y materiales reservados.
9. Se reserva el material necesario para la fabricación, no el número de productos vendidos sin conversión. La ficha técnica define consumo por unidad o por lote y merma prevista.
10. Una modificación posterior a la aprobación bloquea el inicio y exige nueva revisión. Si el trabajo comenzó, se tramita una orden de cambio con costo/plazo y autorización.
11. Cancelar requiere revisar el estado productivo, liberar reservas no consumidas y resolver cobros con caja. No se devuelven materiales ya consumidos automáticamente.
12. La entrega exige calidad aprobada y saldo conforme a la política de crédito. Las excepciones requieren permiso, motivo y registro.
13. No se borran transacciones históricas. El DELETE del catálogo aplica a registros sin referencias; los usados se desactivan.
14. Se registra actor, fecha, acción, versión y motivo de decisiones importantes. No se considera que un correo leído sea aprobación.

## Fuera del alcance inicial

Facturación electrónica, integración bancaria, pasarela de tarjetas, WhatsApp automatizado, compras a proveedores completas, RR. HH., contabilidad general, ruteo logístico, múltiples empresas y máquinas conectadas. Registrar pagos verificados y despachos manuales sí forma parte del flujo previsto.
