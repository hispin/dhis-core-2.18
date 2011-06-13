#!/bin/sh

# The backupdir must be created manually (mkdir pg_backups)
# The backupdir owner must be changed to postgres (chown postgres pg_backups)
# Script must be made executable (chmod 755 pg_backup.sh)
# Script must be invoked by postgres user

backup_dir="/var/backups/pg_backups"
datetime=`date +%F`
backupfile="$backup_dir/pg-backup-$datetime.gz"

echo "Starting backup..."

/usr/bin/pg_dump dhis2ke -U postgres | gzip > $backupfile

timeinfo=`date '+%T %x'`

echo "Backup file $backupfile complete at $timeinfo"
