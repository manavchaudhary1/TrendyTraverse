#!/bin/bash
set -e
set -x

echo "⏳ Waiting for PostgreSQL to be ready..."
until pg_isready -U keycloak; do
    sleep 5
done

echo "✅ PostgreSQL is ready. Running SQL import..."
pg_restore -U keycloak -d keycloak /sql/keycloak_backup.dump

echo "🎉 Data import completed!"
