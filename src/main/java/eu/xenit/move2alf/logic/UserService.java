package eu.xenit.move2alf.logic;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.enums.ERole;

public interface UserService {
	/**
	 * Get current user.
	 * 
	 * @return The current user.
	 */
	@PreAuthorize("isAuthenticated()")
	public UserPswd getCurrentUser();

	/**
	 * Get user with given user name.
	 * 
	 * @param username
	 *            The user name of the user to return.
	 * @return The user with the given user name. Return null if no user with the
	 *         given name exists.
	 */
	@PreAuthorize("isAuthenticated()")
	public UserPswd getUser(String username) throws IllegalArgumentException;

	/**
	 * Get all existing users.
	 * 
	 * @return A list containing all users.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UserPswd> getAllUsers();

	/**
	 * Create user with user name, password and role.
	 * 
	 * @param userName
	 * @param password
	 * @param role
	 *            The highest role of the user. The new user will also have all
	 *            lower roles.
	 */
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public UserPswd createUser(String userName, String password, ERole role);
	
	/** Delete user with given user name.
	 * 
	 * @param userName
	 */
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public void deleteUser(String userName);

	/**
	 * Change password of the logged in user.
	 * 
	 * @param newPassword The new password.
	 */
	@PreAuthorize("isAuthenticated()")
	public void changePassword(String newPassword);

	/**
	 * Change password of a given user.
	 * 
	 * @param userName The user name of the user whose password to change.
	 * @param newPassword The new password for the given user.
	 */
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public void changePassword(String userName, String newPassword);

	/**
	 * Change role of an existing user.
	 * 
	 * @param userName
	 *            The user name of the user whose role to change.
	 * @param newRole
	 *            The highest role of the user. The new user will also have all
	 *            lower roles. All higher roles will be removed.
	 */
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public void changeRole(String userName, ERole newRole);
}
