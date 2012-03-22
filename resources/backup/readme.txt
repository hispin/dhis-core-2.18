
This is a simple cron job set up for backing up PostgreSQL databases to local disk.

It consists of two files:

1) pg_backup.sh - backup script doing the actual work

- The backupdir must be created manually (mkdir pg_backups)
- The backupdir owner must be changed to postgres (chown postgres pg_backups)
- Script must be made executable (chmod 755 pg_backup.sh)
- Script must be invoked by postgres user so load crontab as postgres
- Script must be copied to /usr/local/bin/

2) pg_backup.cron - crontab file invoking the script every day at 23:00

- Crontab can be loaded with crontab command: crontab pg_backup.cron
- Active cron jobs can viewed with: crontab -l
- Cron jobs can be terminated with: crontab -r
