package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.core.enums.ERole;

import java.util.HashSet;
import java.util.Set;


public class UserPswd extends IdObject {
    private String userName = "";
    
    private String password = "";
    
    private Set<UserRole> userRoleSet = new HashSet<UserRole>();

    // to temporarily store password in the clear (just lives in code, not persisted)
    // cfr MgmtAccountForm
    private String tempPassword = null;

    public UserPswd() {
    	
    }

    public UserPswd(String userName, String encodedPassword) {
        this.userName = userName;
        this.password = encodedPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserRole> getUserRoleSet() {
        return userRoleSet;
    }

    public void setUserRoleSet(Set<UserRole> userRoleSet) {
        this.userRoleSet = userRoleSet;
    }

    public boolean isUserInRole(ERole role) {
        for (UserRole userRole : userRoleSet) {
            if (userRole.getRoleType() == role) {
                return true;
            }
        }

        return false;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

}
