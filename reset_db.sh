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

PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" <<'SQL'
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