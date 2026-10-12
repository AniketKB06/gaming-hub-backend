CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    google_id VARCHAR(255) UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    profile_image_url VARCHAR(500),
    city VARCHAR(120),
    preferred_sports VARCHAR(255),
    skill_level VARCHAR(50),
    account_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL UNIQUE,
    slug VARCHAR(80) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS venue_owners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(200),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS venues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(180) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(80),
    latitude NUMERIC(9,6),
    longitude NUMERIC(9,6),
    images TEXT,
    amenities TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id UUID NOT NULL REFERENCES venue_owners(id) ON DELETE CASCADE,
    cancellation_policy TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS venue_sports (
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    sport_id UUID NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    PRIMARY KEY (venue_id, sport_id)
);

CREATE TABLE IF NOT EXISTS facilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    sport_id UUID NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    name VARCHAR(120) NOT NULL,
    facility_type VARCHAR(80) NOT NULL,
    base_price_per_hour NUMERIC(12,2) NOT NULL,
    capacity INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS operating_hours (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    day_of_week VARCHAR(15) NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(account_status);
CREATE INDEX IF NOT EXISTS idx_venues_city ON venues(city);
CREATE INDEX IF NOT EXISTS idx_venues_active ON venues(active);
CREATE INDEX IF NOT EXISTS idx_venues_owner ON venues(owner_id);
CREATE INDEX IF NOT EXISTS idx_facilities_venue ON facilities(venue_id);
CREATE INDEX IF NOT EXISTS idx_facilities_sport ON facilities(sport_id);
CREATE INDEX IF NOT EXISTS idx_operating_hours_venue ON operating_hours(venue_id);

INSERT INTO sports (name, slug, description, active)
VALUES
    ('Cricket', 'cricket', 'Outdoor cricket venue', TRUE),
    ('Football', 'football', 'Football turf and natural grass', TRUE),
    ('Badminton', 'badminton', 'Indoor badminton court', TRUE),
    ('Basketball', 'basketball', 'Outdoor or indoor basketball court', TRUE),
    ('Tennis', 'tennis', 'Tennis court', TRUE),
    ('Volleyball', 'volleyball', 'Volleyball court', TRUE),
    ('Table Tennis', 'table-tennis', 'Ping pong facility', TRUE),
    ('Pickleball', 'pickleball', 'Indoor/outdoor pickleball court', TRUE),
    ('Squash', 'squash', 'Squash court', TRUE),
    ('Swimming', 'swimming', 'Swimming pool access', TRUE),
    ('Box Cricket', 'box-cricket', 'Short format cricket', TRUE),
    ('Futsal', 'futsal', 'Indoor small-sided football', TRUE)
ON CONFLICT (name) DO NOTHING;
