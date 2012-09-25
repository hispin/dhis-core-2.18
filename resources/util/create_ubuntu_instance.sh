#Some variables. Change as you need to
USERNAME=dhis
PASSWORD=dhis
DBNAME=dhis2
#Update first
sudo apt-get -y update
#Install postgres
sudo apt-get -y install postgresql-9.1
#Set the username and password
sudo -u postgres createuser -SDRw $USERNAME
sudo -u postgres psql -c "ALTER USER $USERNAME WITH PASSWORD '$PASSWORD';"
sudo -u postgres createdb -O $USERNAME $DBNAME
#Install Java and set as the default
sudo apt-get -y install openjdk-7-jdk
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-7-openjdk-amd64/bin/java 1
sudo update-alternatives --set java /usr/lib/jvm/java-7-openjdk-amd64/bin/java
#Download and install Tomcat
wget -O /home/ubuntu/apache-tomcat-7.0.30.tar.gz http://apache.osuosl.org/tomcat/tomcat-7/v7.0.30/bin/apache-tomcat-7.0.30.tar.gz
tar zxvf /home/ubuntu/apache-tomcat-7.0.30.tar.gz
#Download and install DHIS2
wget -O /home/ubuntu/apache-tomcat-7.0.30/webapps/dhis.war http://dhis2.org/download/releases/2.9/dhis.war
#Create a hibernate.properties file
echo -e "hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
hibernate.connection.driver_class = org.postgresql.Driver
hibernate.connection.url = jdbc:postgresql://localhost/$DBNAME
hibernate.connection.username = $USERNAME
hibernate.connection.password = $PASSWORD
hibernate.hbm2ddl.auto = update" > /home/ubuntu/hibernate.properties
#Create the JAVA_OPTS
sudo echo -e "export JAVA_OPTS='-Xmx6000m -Xms3000m -XX:MaxPermSize=800m -XX:PermSize=400m'
export DHIS2_HOME='/home/ubuntu/'" > /home/ubuntu/apache-tomcat-7.0.30/bin/setenv.sh


#Make some changes to the kernel params
sudo sh -c "echo '
kernel.shmmax = 1073741824
net.core.rmem_max = 8388608
net.core.wmem_max = 8388608' >> /etc/sysctl.conf" 
sudo sysctl -p
#Backup the Postgguration file
sudo cp /etc/postgresql/9.1/main/postgresql.conf /etc/postgresql/9.1/main/postgresql.conf.bak
#Create a new postgres config and restart the server
echo -e "data_directory = '/var/lib/postgresql/9.1/main'\n         
hba_file = '/etc/postgresql/9.1/main/pg_hba.conf'\n       
ident_file = '/etc/postgresql/9.1/main/pg_ident.conf'\n  
external_pid_file = '/var/run/postgresql/9.1-main.pid'\n         
port = 5432\n                            
max_connections = 100\n                  
unix_socket_directory = '/var/run/postgresql'\n         
ssl = true\n                             
shared_buffers = 512MB\n                  
log_line_prefix = '%t '\n                 
datestyle = 'iso, mdy'\n
lc_messages = 'en_US.UTF-8'\n                     
lc_monetary = 'en_US.UTF-8'\n                     
lc_numeric = 'en_US.UTF-8'\n                     
lc_time = 'en_US.UTF-8'\n                        
default_text_search_config = 'pg_catalog.english'\n
effective_cache_size = 3500MB\n
checkpoint_segments = 32\n
checkpoint_completion_target = 0.8\n
wal_buffers = 4MB\n
synchronous_commit = off\n
wal_writer_delay = 10000ms\n"  > /home/ubuntu/postgres.conf
sudo cp /home/ubuntu/postgres.conf /etc/postgresql/9.1/main/postgresql.conf
#Restart postgres
sudo /etc/init.d/postgresql restart
#Start Tomcat
/home/ubuntu/apache-tomcat-7.0.30/bin/startup.sh
#Enjoy