CREATE TABLE property_details (
    id BIGSERIAL PRIMARY KEY,
     granter_id BIGINT NOT NULL,
    accommodation_type VARCHAR(500) DEFAULT NULL,  -- Dropdown
    landlord_name VARCHAR(255)  DEFAULT NULL,       -- Accommodation Provider

    property_address TEXT  DEFAULT NULL,

    monthly_rent VARCHAR(255)  DEFAULT NULL,

    tenancy_start_date  VARCHAR(255)  DEFAULT NULL,
    tenancy_end_date VARCHAR(255)  DEFAULT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_property_user
        FOREIGN KEY (granter_id)
        REFERENCES granter_application(id)
        ON DELETE CASCADE
);