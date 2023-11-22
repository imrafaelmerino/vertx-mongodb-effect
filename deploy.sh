#!/bin/bash
GPG_PASSPHRASE=json-values-to-the-mighty
mvn -X clean package deploy -Dgpg.keyname=json-values --settings settings.xml  -DskipTests=true -B -U -Prelease


