SET PGPASSWORD=PG_PASSWORD
"POSTGRES_INSTALL_LOCATION\bin\psql.exe" -c "CREATE USER dhis CREATEDB LOGIN PASSWORD 'dhis';" -U postgres -w postgres
"POSTGRES_INSTALL_LOCATION\bin\createdb.exe" -U postgres -w -O dhis dhis2db
"POSTGRES_INSTALL_LOCATION\bin\psql.exe" -U postgres -w -d dhis2db -f "DATABASE_FILE"