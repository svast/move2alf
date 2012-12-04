#!/bin/bash

KEY=move2alf.key
LICENSE=$1

START=`date +%Y/%m/%d`
EXPIRATION=`date -v +14d +%Y/%m/%d`

echo -n -n "Company name: "
read COMPANY_NAME
echo -n "Street: "
read STREET 
echo -n "City: "
read CITY
echo -n "Postal code: "
read POSTAL_CODE
echo -n "State: "
read STATE
echo -n "Country: "
read COUNTRY
echo -n "Contact person: "
read CONTACT_PERSON
echo -n "Email: "
read EMAIL
echo -n "Telephone: "
read TELEPHONE

set -x
java -jar licensemaker-2.2.jar -k $KEY -o $LICENSE \
    -S $START -E $EXPIRATION \
    -p "licensee.companyName=$COMPANY_NAME, licensee.street=$STREET,
        licensee.city=$CITY, licensee.postalCode=$POSTAL_CODE,
        licensee.state=$STATE, licensee.country=$COUNTRY,
        licensee.contactPerson=$CONTACT_PERSON, licensee.email=$EMAIL,
        licensee.telephone=$TELEPHONE"

