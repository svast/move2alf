package eu.xenit.move2alf.logic;

import java.util.List;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.enums.ERole;

public interface UserService {
	/**
	 * Get current user.
	 * 
	 * @return The current user.
	 */
	public UserPswd getCurrentUser();

	/**
	 * Get user with given username.
	 * 
	 * @param username
	 *            The username of the user to return.
	 * @return The user with the given username. Return null if no user with the
	 *         given name exists.
	 */
	public UserPswd getUser(String username);

	/**
	 * Get all existing users.
	 * 
	 * @return A list containing all users.
	 */
	public List<UserPswd> getAllUsers();

	/**
	 * Create user with username, password and role.
	 * 
	 * @param userName
	 * @param password
	 * @param role
	 *            The highest role of the user. The new user will also have all
	 *            lower roles.
	 */
	public void createUser(String userName, String password, ERole role);

	/**
	 * Change password of the logged in user.
	 * 
	 * @param newPassword The new password.
	 */
	public void changePassword(String newPassword);

	/**
	 * Change password of a given user.
	 * 
	 * @param user The user whose password to change.
	 * @param newPassword The new password for the given user.
	 */
	public void changePassword(UserPswd user, String newPassword);

	/**
	 * Change role of an existing user.
	 * 
	 * @param user
	 *            The user to change.
	 * @param newRole
	 *            The highest role of the user. The new user will also have all
	 *            lower roles. All higher roles will be removed.
	 */
	public void changeRole(UserPswd user, ERole newRole);
}
