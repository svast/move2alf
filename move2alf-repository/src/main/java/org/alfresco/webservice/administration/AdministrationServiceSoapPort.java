/**
 * AdministrationServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.administration;

public interface AdministrationServiceSoapPort extends java.rmi.Remote {

    /**
     * Gets the details of the requested users.
     */
    public org.alfresco.webservice.administration.UserQueryResults queryUsers(org.alfresco.webservice.administration.UserFilter filter) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Fetch the next bathc of users from an existing user query.
     */
    public org.alfresco.webservice.administration.UserQueryResults fetchMoreUsers(java.lang.String querySession) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Get the details of a specified user.
     */
    public org.alfresco.webservice.administration.UserDetails getUser(java.lang.String userName) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Create new users with the details provided.
     */
    public org.alfresco.webservice.administration.UserDetails[] createUsers(org.alfresco.webservice.administration.NewUserDetails[] newUsers) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Updates the details of the specified users.
     */
    public org.alfresco.webservice.administration.UserDetails[] updateUsers(org.alfresco.webservice.administration.UserDetails[] users) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Changes the password of the specified user.
     */
    public void changePassword(java.lang.String userName, java.lang.String oldPassword, java.lang.String newPassword) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;

    /**
     * Delete the specified users.
     */
    public void deleteUsers(java.lang.String[] userNames) throws java.rmi.RemoteException, org.alfresco.webservice.administration.AdministrationFault;
}
