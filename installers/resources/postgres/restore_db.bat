SET PGPASSWORD=PG_PASSWORD
"c:\Program Files\PostgreSQL\9.0\bin\psql.exe" -c "CREATE USER dhis2 CREATEDB LOGIN PASSWORD 'dhis2';" -U postgres -w postgres
"c:\Program Files\Postgresql\9.0\bin\createdb.exe" -U postgres -w -O dhis2 DHIS2DBNAME
"c:\Program Files\Postgresql\9.0\bin\pg_restore" -U postgres -w -d DHIS2DBNAME dhis2db.backup
