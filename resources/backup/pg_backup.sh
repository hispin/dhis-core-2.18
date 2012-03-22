#!/bin/sh

backup_dir="/var/backups/pg_backups"
datetime=`date +%F`
backupfile="$backup_dir/pg-backup-$datetime.gz"

echo "Starting backup..."

/usr/bin/pg_dump dhis2ke -U postgres -T aggregated* | gzip > $backupfile

timeinfo=`date '+%T %x'`

echo "Backup file $backupfile complete at $timeinfo"
