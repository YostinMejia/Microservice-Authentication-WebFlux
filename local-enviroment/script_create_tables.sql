
BEGIN;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS public.users
(
    user_id uuid NOT NULL DEFAULT gen_random_uuid(),
    last_name character varying COLLATE pg_catalog."default" NOT NULL,
    address character varying COLLATE pg_catalog."default",
    phone character varying COLLATE pg_catalog."default",
    email character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    base_salary integer NOT NULL,
    birth_date date,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);
END;