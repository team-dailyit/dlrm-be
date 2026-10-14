-- lat/lng로 자동 계산된다. ST_MakePoint는 (경도, 위도) 순서다.
ALTER TABLE places
    ADD COLUMN location geography(Point, 4326)
    GENERATED ALWAYS AS (
        ST_SetSRID(ST_MakePoint(lng, lat), 4326)::geography
    ) STORED;

CREATE INDEX idx_places_location ON places USING GIST (location);
