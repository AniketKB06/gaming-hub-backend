-- V2__seed_sports.sql

INSERT INTO sports (name, slug, description, active, sort_order)
VALUES
    ('Cricket', 'cricket', 'Outdoor cricket grounds and nets.', TRUE, 1),
    ('Football', 'football', 'Football turfs and pitches.', TRUE, 2),
    ('Badminton', 'badminton', 'Indoor and outdoor badminton courts.', TRUE, 3),
    ('Basketball', 'basketball', 'Indoor basketball courts.', TRUE, 4),
    ('Tennis', 'tennis', 'Tennis courts for singles and doubles.', TRUE, 5),
    ('Volleyball', 'volleyball', 'Volleyball courts and sand courts.', TRUE, 6),
    ('Table Tennis', 'table-tennis', 'Table tennis and ping pong.', TRUE, 7),
    ('Pickleball', 'pickleball', 'Fast-paced court sport.', TRUE, 8),
    ('Squash', 'squash', 'Indoor squash courts.', TRUE, 9),
    ('Swimming', 'swimming', 'Swimming pools and lanes.', TRUE, 10),
    ('Box Cricket', 'box-cricket', 'Compact cricket experience.', TRUE, 11),
    ('Futsal', 'futsal', 'Small-sided football indoor format.', TRUE, 12),
    ('Other', 'other', 'Custom or configurable sports category.', TRUE, 99)
ON CONFLICT (slug) DO NOTHING;
