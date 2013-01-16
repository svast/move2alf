package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.core.enums.ERole;

public class UserRole {
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
    public boolean equals(Object obj) {
    	if (obj == null) {
            return false;
    	}
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UserRole)) {
            return false;
        }
        
        UserRole userRole = (UserRole) obj;

        if (getUserName().equals(userRole.getUserName()) && (getRole() == userRole.getRole())) {
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
