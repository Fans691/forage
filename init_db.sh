#!/usr/bin/env bash
set -euo pipefail

DB_NAME="${DB_NAME:-forage}"
DB_USER="${DB_USER:-fans}"
DB_PASSWORD="${DB_PASSWORD:-123}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
PG_SUPERUSER="${PG_SUPERUSER:-postgres}"
PG_SUPERUSER_PASSWORD="${PG_SUPERUSER_PASSWORD:-}"

if ! command -v psql >/dev/null 2>&1; then
  echo "psql not found. Install PostgreSQL client tools." >&2
  exit 1
fi

if [[ -z "$PG_SUPERUSER_PASSWORD" ]]; then
  echo "PG_SUPERUSER_PASSWORD is not set. Example: export PG_SUPERUSER_PASSWORD=your_postgres_password" >&2
  exit 1
fi

export PGPASSWORD="$PG_SUPERUSER_PASSWORD"

psql -h "$DB_HOST" -p "$DB_PORT" -U "$PG_SUPERUSER" -d postgres <<SQL
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$DB_USER') THEN
    CREATE ROLE $DB_USER LOGIN PASSWORD '$DB_PASSWORD';
  END IF;
END
\$\$;

DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB_NAME') THEN
    CREATE DATABASE $DB_NAME OWNER $DB_USER;
  END IF;
END
\$\$;

GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
SQL

echo "Database '$DB_NAME' ready. All privileges granted to '$DB_USER'."
