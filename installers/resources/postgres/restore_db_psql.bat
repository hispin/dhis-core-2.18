SET PGPASSWORD=PG_PASSWORD
"c:\Program Files\PostgreSQL\9.0\bin\psql.exe" -c "CREATE USER dhis CREATEDB LOGIN PASSWORD 'dhis';" -U postgres -w postgres
"c:\Program Files\Postgresql\9.0\bin\createdb.exe" -U postgres -w -O dhis dhis2db
"c:\Program Files\Postgresql\9.0\bin\psql.exe" -U postgres -w -d dhis2db -f dhis2db.dump