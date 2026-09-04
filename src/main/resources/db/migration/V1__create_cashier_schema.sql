-- Esquema del primer piloto: turno + cobro. Ver Esquema-BD-Caja.md en la vault para el porqué
-- de cada columna. invoice, invoice_line, invoice_sequence, accounting_entry y entry_detail
-- quedan diferidas junto con la emisión (D17) y no entran en esta migración.

-- El día de trabajo de un cajero.
CREATE TABLE shift (
    id             UUID PRIMARY KEY,
    cashier        VARCHAR NOT NULL,
    branch         VARCHAR NOT NULL,
    establishment  VARCHAR NOT NULL,
    issuing_point  VARCHAR NOT NULL,
    opening_float  NUMERIC(14,2) NOT NULL,
    cash_counted   NUMERIC(14,2),
    difference     NUMERIC(14,2),
    status         VARCHAR NOT NULL,
    opened_at      TIMESTAMPTZ NOT NULL,
    closed_at      TIMESTAMPTZ,
    CONSTRAINT ck_shift_status CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT ck_shift_opening_float CHECK (opening_float >= 0)
);

-- No puede haber dos turnos abiertos para el mismo cajero y sede.
CREATE UNIQUE INDEX uq_shift_open_per_cashier_branch ON shift (cashier, branch) WHERE status = 'OPEN';

-- Resumen del turno por forma de pago. Se escribe al cerrar, es una foto.
CREATE TABLE payment_method_total (
    id       UUID PRIMARY KEY,
    shift_id UUID NOT NULL REFERENCES shift (id),
    sri_code VARCHAR NOT NULL,
    total    NUMERIC(14,2) NOT NULL
);

CREATE INDEX ix_payment_method_total_shift_id ON payment_method_total (shift_id);

-- A quién se le emite el comprobante. No siempre es el paciente (D14). Reutilizable.
CREATE TABLE invoice_recipient (
    id         UUID PRIMARY KEY,
    id_type    VARCHAR NOT NULL,
    id_number  VARCHAR NOT NULL,
    name       VARCHAR NOT NULL,
    address    VARCHAR,
    email      VARCHAR,
    phone      VARCHAR,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_invoice_recipient_not_final_consumer CHECK (id_type <> '07'),
    CONSTRAINT uq_invoice_recipient_identification UNIQUE (id_type, id_number)
);

-- Qué hay que cobrarle al paciente. Resultado de la valorización.
CREATE TABLE charge_order (
    id                   UUID PRIMARY KEY,
    care_id              VARCHAR NOT NULL UNIQUE,
    patient_id_type      VARCHAR NOT NULL,
    patient_id_number    VARCHAR NOT NULL,
    patient_name         VARCHAR,
    invoice_recipient_id UUID NOT NULL REFERENCES invoice_recipient (id),
    agreement_ref        VARCHAR,
    status               VARCHAR NOT NULL,
    care_date            TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_charge_order_status
        CHECK (status IN ('PENDING_PRICING', 'PRICED', 'CHARGED', 'CANCELLED'))
);

CREATE INDEX ix_charge_order_invoice_recipient_id ON charge_order (invoice_recipient_id);

-- Una prestación ya valorizada. Acá vive RF-06.
CREATE TABLE charge_order_line (
    id                UUID PRIMARY KEY,
    charge_order_id   UUID NOT NULL REFERENCES charge_order (id),
    service_code      VARCHAR NOT NULL,
    quantity          INT NOT NULL,
    agreement_rate    NUMERIC(14,2) NOT NULL,
    recognized_amount NUMERIC(14,2) NOT NULL,
    copago            NUMERIC(14,2) NOT NULL,
    discount          NUMERIC(14,2) NOT NULL DEFAULT 0,
    vat_rate          VARCHAR NOT NULL,
    status            VARCHAR NOT NULL,
    rejection_reason  VARCHAR,
    CONSTRAINT ck_charge_order_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_charge_order_line_status CHECK (status IN ('CHARGEABLE', 'NOT_AUTHORIZED')),
    CONSTRAINT ck_charge_order_line_vat_rate CHECK (vat_rate IN ('ZERO', 'FIFTEEN')),
    CONSTRAINT ck_charge_order_line_rejection_reason CHECK (
        (status = 'NOT_AUTHORIZED' AND rejection_reason IS NOT NULL)
        OR (status = 'CHARGEABLE' AND rejection_reason IS NULL)
    )
);

CREATE INDEX ix_charge_order_line_charge_order_id ON charge_order_line (charge_order_id);

-- El dinero entrando.
CREATE TABLE payment (
    id               UUID PRIMARY KEY,
    charge_order_id  UUID NOT NULL UNIQUE REFERENCES charge_order (id),
    shift_id         UUID NOT NULL REFERENCES shift (id),
    idempotency_key  VARCHAR NOT NULL UNIQUE,
    charged_at       TIMESTAMPTZ NOT NULL
);

CREATE INDEX ix_payment_shift_id ON payment (shift_id);

-- Cada forma de pago usada. La suma debe igualar el total de la orden (se valida en dominio).
CREATE TABLE applied_payment (
    id         UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payment (id),
    sri_code   VARCHAR NOT NULL,
    amount     NUMERIC(14,2) NOT NULL,
    reference  VARCHAR,
    card_bank  VARCHAR,
    card_brand VARCHAR,
    term_days  INT NOT NULL,
    time_unit  VARCHAR NOT NULL,
    CONSTRAINT ck_applied_payment_amount CHECK (amount > 0),
    CONSTRAINT ck_applied_payment_sri_code
        CHECK (sri_code IN ('01', '15', '16', '17', '18', '19', '20', '21')),
    CONSTRAINT ck_applied_payment_term_days CHECK (term_days >= 0),
    CONSTRAINT ck_applied_payment_card_details CHECK (
        (sri_code IN ('16', '19') AND card_bank IS NOT NULL AND card_brand IS NOT NULL)
        OR (sri_code NOT IN ('16', '19') AND card_bank IS NULL AND card_brand IS NULL)
    )
);

CREATE INDEX ix_applied_payment_payment_id ON applied_payment (payment_id);

-- Salida del tramo garantizado. Un proceso programado la vacía. Sin consumidor por ahora.
CREATE TABLE outbox (
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR NOT NULL,
    aggregate_id   UUID NOT NULL,
    event_type     VARCHAR NOT NULL,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL,
    sent_at        TIMESTAMPTZ
);

CREATE INDEX ix_outbox_pending ON outbox (created_at) WHERE sent_at IS NULL;

-- Idempotencia de RegistrarCobro. La restricción de la llave es la defensa real.
CREATE TABLE idempotency_record (
    key         VARCHAR PRIMARY KEY,
    request_hash VARCHAR NOT NULL,
    result      JSONB NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL
);

-- Trazabilidad financiera. No es el log técnico. Solo se inserta (ver V2 para el trigger).
CREATE TABLE audit_event (
    id             UUID PRIMARY KEY,
    actor          VARCHAR NOT NULL,
    event_type     VARCHAR NOT NULL,
    aggregate_type VARCHAR NOT NULL,
    aggregate_id   UUID NOT NULL,
    data           JSONB NOT NULL,
    occurred_at    TIMESTAMPTZ NOT NULL
);

CREATE INDEX ix_audit_event_aggregate ON audit_event (aggregate_type, aggregate_id);
