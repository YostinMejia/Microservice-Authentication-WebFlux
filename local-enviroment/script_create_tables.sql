
BEGIN;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS public.rol
(
    id_rol uuid NOT NULL DEFAULT gen_random_uuid(),
    nombre character varying COLLATE pg_catalog."default" NOT NULL,
    descripcion character varying COLLATE pg_catalog."default",
    CONSTRAINT rol_pkey PRIMARY KEY (id_rol),
    CONSTRAINT nombre UNIQUE (nombre)
        INCLUDE(nombre)
);

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
    id_rol uuid NOT NULL,
    document character varying COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT document UNIQUE (document)
        INCLUDE(document)
);

ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT id_rol FOREIGN KEY (id_rol)
    REFERENCES public.rol (id_rol) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

END;