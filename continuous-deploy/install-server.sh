#!/bin/sh

echo ---- Linux Package Upgrades
aptitude -y upgrade
aptitude -y safe-update

echo
echo
echo ---- Timezone
# Choose yours here http://en.wikipedia.org/wiki/List_of_tz_database_time_zones
echo "America/Sao_Paulo" > /etc/timezone
dpkg-reconfigure -f noninteractive tzdata

