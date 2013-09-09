#!/bin/sh

# Sun JARs
mvn install:install-file -DgroupId=javax.media -Dversion=1.1.3 -Dpackaging=jar \
    -DartifactId=jai_imageio -Dfile=src/main/webapp/WEB-INF/lib/jai_imageio.jar

mvn install:install-file -DgroupId=javax.media -Dversion=1.1.3 -Dpackaging=jar \
    -DartifactId=jai_core -Dfile=src/main/webapp/WEB-INF/lib/jai_core.jar

mvn install:install-file -DgroupId=javax.media -Dversion=1.1.3 -Dpackaging=jar \
    -DartifactId=jai_codec -Dfile=src/main/webapp/WEB-INF/lib/jai_codec.jar

mvn install:install-file -DgroupId=com.sun.media -Dversion=1.1 -Dpackaging=jar \
    -DartifactId=jai_imageio -Dfile=src/main/webapp/WEB-INF/lib/jai_imageio.jar

mvn install:install-file -DgroupId=com.sun.media -Dversion=1.1 -Dpackaging=jar \
    -DartifactId=jai_core -Dfile=src/main/webapp/WEB-INF/lib/jai_core.jar

mvn install:install-file -DgroupId=com.sun.media -Dversion=1.1 -Dpackaging=jar \
    -DartifactId=jai_codec -Dfile=src/main/webapp/WEB-INF/lib/jai_codec.jar

# Padlock (http://www.javalicensemanager.com/)
mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/padlock-2.2.jar \
    -DgroupId=com.javalicensemanager -DartifactId=padlock -Dversion=2.2 -Dpackaging=jar

mvn install:install-file -Dfile=src/main/webapp/WEB-INF/lib/commons-codec-1.4.jar \
    -DgroupId=org.apache.commons.codec -DartifactId=commons-codec -Dversion=1.4 -Dpackaging=jar

# Modified jar for camel-CMIS: repository xenit/camel

mvn install:install-file -DgroupId=org.apache.camel -DartifactId=camel-cmis -Dversion=2.11.1-SNAPSHOT -Dfile=src/main/webapp/WEB-INF/lib/camel-cmis-2.11.1.jar -Dpackaging=jar
