#!/usr/bin/env bash
set -euo pipefail

# Vide toutes les tables de la base sauf les tables de reference conservees.

DB_NAME="${DB_NAME:-forage}"
DB_USER="${DB_USER:-fans}"
DB_PASSWORD="${DB_PASSWORD:-123}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"

if ! command -v psql >/dev/null 2>&1; then
  echo "psql not found. Install PostgreSQL client tools." >&2
  exit 1
fi

if command -v sudo >/dev/null 2>&1; then
  if ! sudo -u postgres psql -p "$DB_PORT" -d "$DB_NAME" -v app_user="$DB_USER" <<'SQL'
SELECT set_config('forage.reset_user', :'app_user', false);

DO $$
DECLARE
    target_role text := current_setting('forage.reset_user');
    table_record record;
    sequence_record record;
BEGIN
    EXECUTE format('GRANT USAGE, CREATE ON SCHEMA public TO %I', target_role);

    FOR table_record IN
        SELECT schemaname, tablename
        FROM pg_tables
        WHERE schemaname = 'public'
    LOOP
        EXECUTE format('ALTER TABLE %I.%I OWNER TO %I', table_record.schemaname, table_record.tablename, target_role);
    END LOOP;

    FOR sequence_record IN
        SELECT sequence_schema, sequence_name
        FROM information_schema.sequences
        WHERE sequence_schema = 'public'
    LOOP
        EXECUTE format('ALTER SEQUENCE %I.%I OWNER TO %I', sequence_record.sequence_schema, sequence_record.sequence_name, target_role);
    END LOOP;

    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %I', target_role);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %I', target_role);
END $$;
SQL
  then
    echo "Attention: impossible de reparer automatiquement les droits avec sudo/postgres." >&2
    echo "Si une erreur permission denied apparait, lancez init_db.sh ou donnez l'ownership des tables a '$DB_USER'." >&2
  fi
else
  echo "sudo not found. Impossible de reparer automatiquement les droits PostgreSQL." >&2
  echo "Le script continue; il faut que '$DB_USER' ait deja les droits/ownership des tables." >&2
fi

PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" <<'SQL'
CREATE TABLE IF NOT EXISTS config (
    id bigserial primary key,
    id1 integer,
    id2 integer,
    dt1 integer,
    dt2 integer,
    code_couleur varchar(20)
);

ALTER TABLE IF EXISTS config ADD COLUMN IF NOT EXISTS id bigserial;
ALTER TABLE IF EXISTS config ADD COLUMN IF NOT EXISTS dt1 integer;
ALTER TABLE IF EXISTS config ADD COLUMN IF NOT EXISTS dt2 integer;
ALTER TABLE IF EXISTS demande_statut ADD COLUMN IF NOT EXISTS duree_travaille_total double precision;

DO $$
DECLARE
    constraint_to_drop text;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'config'
          AND column_name = 'dt'
    ) THEN
        EXECUTE $migrate$
            UPDATE config c
            SET dt1 = COALESCE(c.dt1, migrated.dt1),
                dt2 = COALESCE(c.dt2, migrated.dt2)
            FROM (
                SELECT ctid,
                       COALESCE(LAG(dt) OVER (PARTITION BY id1, id2 ORDER BY dt) + 1, 0) AS dt1,
                       dt AS dt2
                FROM config
                WHERE dt IS NOT NULL
            ) migrated
            WHERE c.ctid = migrated.ctid
        $migrate$;

        FOR constraint_to_drop IN
            SELECT DISTINCT tc.constraint_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_schema = kcu.constraint_schema
             AND tc.constraint_name = kcu.constraint_name
             AND tc.table_name = kcu.table_name
            WHERE tc.table_schema = 'public'
              AND tc.table_name = 'config'
              AND kcu.column_name = 'dt'
        LOOP
            EXECUTE format('ALTER TABLE config DROP CONSTRAINT IF EXISTS %I', constraint_to_drop);
        END LOOP;

        ALTER TABLE config DROP COLUMN IF EXISTS dt;
    END IF;
END $$;

DO $$
DECLARE
    truncate_sql text;
BEGIN
    SELECT 'TRUNCATE TABLE ' || string_agg(quote_ident(table_name), ', ') || ' RESTART IDENTITY CASCADE'
    INTO truncate_sql
    FROM information_schema.tables
    WHERE table_schema = 'public'
      AND table_type = 'BASE TABLE'
      AND table_name NOT IN (
          'region',
          'commune',
          'district',
          'user',
          'config',
          'statut'
      );

    IF truncate_sql IS NOT NULL THEN
        EXECUTE truncate_sql;
    END IF;
END $$;
SQL

echo "Purge terminee. Tables conservees: region, commune, district, user, config, statut."
