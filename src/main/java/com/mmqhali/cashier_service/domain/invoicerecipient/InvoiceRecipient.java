package com.mmqhali.cashier_service.domain.invoicerecipient;

import com.mmqhali.cashier_service.domain.shared.Identification;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A quién se le emite el comprobante. No siempre es el paciente (D14). Es el único agregado
 * mutable del modelo: corregir la dirección o el email de un tercero no toca los comprobantes
 * ya emitidos, porque esos guardan su propia copia.
 *
 * <p>{@link #register} es la fábrica que valida; {@link #reconstruct}, de paquete, la usa el
 * mapeador para reconstruir uno ya guardado. El rechazo de consumidor final vive en
 * {@link Identification}, así que llegar hasta acá con una identificación ya es tenerla válida.
 */
public final class InvoiceRecipient {

    private final UUID id;
    private final Identification identification;
    private String name;
    private String address;
    private String email;
    private String phone;
    private final Instant createdAt;
    private Instant updatedAt;

    private InvoiceRecipient(UUID id, Identification identification, String name, String address,
            String email, String phone, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.identification = identification;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InvoiceRecipient register(Identification identification, String name, String address,
            String email, String phone, Instant now) {
        Objects.requireNonNull(identification, "identification");
        Objects.requireNonNull(now, "now");
        if (name == null || name.isBlank()) {
            throw new InvalidInvoiceRecipient("El nombre o razón social es obligatorio");
        }
        return new InvoiceRecipient(UUID.randomUUID(), identification, name, address, email, phone, now, now);
    }

    static InvoiceRecipient reconstruct(UUID id, Identification identification, String name, String address,
            String email, String phone, Instant createdAt, Instant updatedAt) {
        return new InvoiceRecipient(id, identification, name, address, email, phone, createdAt, updatedAt);
    }

    public void updateContactInfo(String address, String email, String phone, Instant updatedAt) {
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public UUID id() {
        return id;
    }

    public Identification identification() {
        return identification;
    }

    public String name() {
        return name;
    }

    public String address() {
        return address;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
