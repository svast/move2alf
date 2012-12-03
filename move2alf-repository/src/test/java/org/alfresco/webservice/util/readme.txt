This is an alfresco package of which had to change a few files (to be able to
connect to multiple alfrescos). Make sure
that the poller.jar is before the alfresco-web-service-client-3.2.0.jar in
the classpath (to have the modified classes picked up earlier by the class loader).

It is important to use the same package name, else we had to make a copy of all
files of the package that were used in the modified classes.

These files were taken from:
http://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/web-service-client/source/java/org/alfresco/webservice/util/
Revision 21730 (August 11th 2010)