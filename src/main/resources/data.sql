DROP TABLE IF EXISTS building;

CREATE TABLE public.building
(
    id            LONG PRIMARY KEY,
    building_id   VARCHAR(32)             NOT NULL,
    owner         VARCHAR(70)             NOT NULL,
    size          VARCHAR(32)             NOT NULL,
    address       VARCHAR(128)            NOT NULL,
    market_value  VARCHAR(64)             NOT NULL,
    property_type VARCHAR(64)             NOT NULL,
    created       TIMESTAMP DEFAULT NOW() NOT NULL,
    updated       TIMESTAMP DEFAULT NOW() NOT NULL

);

CREATE TABLE public.owner
(
    id         LONG IDENTITY (1,1) PRIMARY KEY,
    owner_name VARCHAR(64)             NOT NULL,
    created    TIMESTAMP DEFAULT NOW() NOT NULL,
    updated    TIMESTAMP DEFAULT NOW() NOT NULL

);
INSERT INTO public.owner(owner_name, created, updated) VALUES ( 'Owner1', NOW(), NOW());
