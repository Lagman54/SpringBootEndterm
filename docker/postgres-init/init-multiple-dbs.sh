#!/bin/bash
set -e

for db in order_db customer_db delivery_db user_db; do
    echo "Creating database: $db"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        CREATE DATABASE $db;
EOSQL
done
