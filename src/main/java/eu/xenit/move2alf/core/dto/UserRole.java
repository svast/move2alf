package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.core.enums.ERole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserRole {
    protected static Logger logger = LoggerFactory.getLogger(UserRole.class);
    private String userName = "";
    private ERole role = null;

    public UserRole() {
    }

    /*
     * when i added this constructor, i received a RunTimExceptionError when
     * constructing the sessionFactory. then I added the default constructor
     * explicitly (see above), that helped
     */
    public UserRole(String userName, ERole role) {
        this.userName = userName;
        this.role = role;
    }

    public String getRole() {
        return role.name();
    }

    public void setRole(String roleString) {
        this.role = ERole.valueOf(roleString);
    }

    public ERole getRoleType() {
        return role;
    }

    public void setRoleType(ERole role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //
    public boolean equals(UserRole userRole) {
        logger.debug("EQUALS " + userName + userRole.userName + role.name() +
            userRole.role.name());

        if (userName.equals(userRole.userName) && (role == userRole.role)) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = (userName + role).hashCode();

        return result;
    }
}
