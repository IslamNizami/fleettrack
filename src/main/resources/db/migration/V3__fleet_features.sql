-- Driver assignment + last location timestamp
ALTER TABLE vehicles
    ADD COLUMN driver_id BIGINT REFERENCES drivers(id) ON DELETE SET NULL,
    ADD COLUMN location_updated_at TIMESTAMPTZ;

CREATE INDEX idx_vehicles_driver_id ON vehicles(driver_id);
CREATE INDEX idx_vehicles_model_year ON vehicles(model_year);

-- Maintenance tracking (reminders + cleanup)
ALTER TABLE maintenance_records
    ADD COLUMN completed_at TIMESTAMPTZ,
    ADD COLUMN due_soon_notified_at TIMESTAMPTZ,
    ADD COLUMN overdue_notified_at TIMESTAMPTZ;

CREATE INDEX idx_maintenance_vehicle ON maintenance_records(vehicle_id);

-- Keep vehicles.updated_at correct on every UPDATE
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_vehicles_set_updated_at
    BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
