#!/bin/bash

mysql -u root -p < drop.sql 
mysql -u root -p < setup.sql 
mysql -u root -p move2alf < move2alf.sql 

