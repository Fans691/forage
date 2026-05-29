#!/usr/bin/env bash
set -euo pipefail

# Script d'initialisation de la base de donnees PostgreSQL
# Assurez-vous que PostgreSQL est installe et en cours d'execution

DB_NAME="${DB_NAME:-forage}"
DB_USER="${DB_USER:-fans}"
DB_PASSWORD="${DB_PASSWORD:-123}"

if ! command -v psql >/dev/null 2>&1; then
  echo "psql not found. Install PostgreSQL client tools." >&2
  exit 1
fi

echo "Creation de la base de donnees $DB_NAME..."

sudo -u postgres psql <<SQL
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

echo ""
echo "Base '$DB_NAME' prete. Tous les privileges sont accordes a '$DB_USER'."
