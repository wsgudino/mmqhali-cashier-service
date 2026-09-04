-- Convierte "un cobro es definitivo" y "audit_event solo se inserta" en una garantía de base,
-- no solo en una regla escrita que un script puede saltear. Evaluación de openmrs-module-billing
-- (Evaluacion-OpenMRS-Billing-para-Modulo-Caja-MMQHALI.md): ellos lo resuelven con un
-- interceptor de Hibernate; acá alcanza con un trigger, sin adoptar ese mecanismo.

CREATE FUNCTION reject_update_or_delete() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION '% on % is not allowed: rows are immutable once written', TG_OP, TG_TABLE_NAME;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER payment_immutable
    BEFORE UPDATE OR DELETE ON payment
    FOR EACH ROW EXECUTE FUNCTION reject_update_or_delete();

CREATE TRIGGER audit_event_immutable
    BEFORE UPDATE OR DELETE ON audit_event
    FOR EACH ROW EXECUTE FUNCTION reject_update_or_delete();
