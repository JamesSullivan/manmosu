#!/bin/bash
mysql -uroot -p <<MYSQL_SCRIPT
CREATE DATABASE IF NOT EXISTS manmosu DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE USER 'manmosu_user'@'localhost' IDENTIFIED BY 'manmosuPW1!';
CREATE USER 'manmosu_user'@'%' IDENTIFIED BY 'manmosuPW1!';
GRANT ALL PRIVILEGES ON manmosu.* TO 'manmosu_user'@'localhost' IDENTIFIED BY 'manmosuPW1!';
FLUSH PRIVILEGES;
MYSQL_SCRIPT

echo "MySQL database manmosu_development created."
echo "MySQL user manmosu_user created."
echo "Username:   manmosu_user"
echo "Password:   manmosuPW1!"

mysql -umanmosu_user -p'manmosuPW1!' manmosu < conf/sql/db_structure.sql

echo "MySQL database manmosu tables created."
