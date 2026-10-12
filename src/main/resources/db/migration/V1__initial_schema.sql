-- V1__initial_schema.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(512),
    full_name VARCHAR(150) NOT NULL,
    city VARCHAR(120),
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_image_url VARCHAR(1024),
    provider VARCHAR(50),
    provider_user_id VARCHAR(255),
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_provider_identity UNIQUE (provider, provider_user_id)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users (role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users (status);

CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    preferred_sports VARCHAR(500),
    skill_level VARCHAR(50),
    bio VARCHAR(1000),
    phone_number VARCHAR(20),
    state VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sports_name UNIQUE (name),
    CONSTRAINT uk_sports_slug UNIQUE (slug)
);

CREATE TABLE IF NOT EXISTS venue_owners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    business_name VARCHAR(200) NOT NULL,
    gst_number VARCHAR(50),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_venue_owners_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS venues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    address_line_1 VARCHAR(300) NOT NULL,
    address_line_2 VARCHAR(300),
    city VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    images VARCHAR(2000),
    amenities VARCHAR(1000),
    cancellation_policy VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_venues_owner FOREIGN KEY (owner_id) REFERENCES venue_owners(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_venues_city ON venues (city);
CREATE INDEX IF NOT EXISTS idx_venues_owner ON venues (owner_id);
CREATE INDEX IF NOT EXISTS idx_venues_active ON venues (active);

CREATE TABLE IF NOT EXISTS venue_sports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL,
    sport_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_venue_sports_venue_sport UNIQUE (venue_id, sport_id),
    CONSTRAINT fk_venue_sports_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    CONSTRAINT fk_venue_sports_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS facilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL,
    sport_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    facility_type VARCHAR(80),
    base_price_per_hour DECIMAL(10,2) NOT NULL,
    capacity INTEGER,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_facilities_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    CONSTRAINT fk_facilities_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_facilities_venue ON facilities (venue_id);
CREATE INDEX IF NOT EXISTS idx_facilities_sport ON facilities (sport_id);

CREATE TABLE IF NOT EXISTS operating_hours (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL,
    day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_operating_hours_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS blocked_slots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL,
    facility_id UUID,
    block_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    reason VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_blocked_slots_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    CONSTRAINT fk_blocked_slots_facility FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE
);
