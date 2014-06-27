#!/bin/sh
# set -e

# install the dhis2-tools deb
dpkg -i dhis2-tools* 
apt-get -y install -f

# Uncomment below to install postgres and nginx servers on this machine
# apt-get -y install nginx postgresql 
