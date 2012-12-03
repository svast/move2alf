package eu.xenit.move2alf.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>This class, together with {@link eu.xenit.poller.repository.RepositoryAccessSession},
 * is an abstraction layer that hides the specifics of a certain repository
 * interface (e.g. webservice interface) for move2alf.</p>
 * 
 * <p>For each repository interface (e.g. webservice interface) used by move2alf this
 * class needs to be extended.</p>
 * 
 * <p>This class also provides 'retry' functionality
 * to a repository interface. This increases the
 * robustness against alfresco or network problems.</p>
 *
 */
public abstract class RepositoryAccess {
	protected static Logger logger = LoggerFactory
			.getLogger(RepositoryAccess.class);


	protected static final int defaultSleepTime = 10000; // 10 seconds
	protected static final int defaultNmbOfRetries = 3;

	/**
	 * This method must be implemented by each class that extends this
	 * class. It must return an object that implements the
	 * {@link eu.xenit.poller.repository.RepositoryAccessSession} interface.
	 * 
	 * @return {@link eu.xenit.poller.repository.RepositoryAccessSession}
	 * @throws RepositoryAccessException
	 */
	public abstract RepositoryAccessSession createSession()
	throws RepositoryAccessException;

    /**
	 * Method that tries to open a session to a repository. It will retry to open
	 * the session if the earlier attempt did not succeed (this could be caused
	 * by heavy network load, heavy alfresco load, alfresco being down).
	 * 
	 * @param nbrOfAttempts
	 *            : if null keep on trying ad infinitum
	 * @param sleepTimeInBetweenAttempts
	 *            : sleepTime between successive attempts (in millis)
     * @return : {@link eu.xenit.poller.repository.RepositoryAccessSession}
     * @throws RepositoryAccessException when session can not be created after <code>nbrOfAttempts</code>.
     */
	public RepositoryAccessSession createSessionAndRetry(Integer nbrOfAttempts,
			int sleepTimeInBetweenAttempts) throws RepositoryAccessException {
		int attemptCounter = 0;
		while (nbrOfAttempts == null || attemptCounter < nbrOfAttempts) {
			attemptCounter++;
			logger.info("Trying to open rra session, attempt {}",
					attemptCounter);
			try {
				return createSession();
			} catch (RepositoryAccessException e) {
					logger.warn("Repository is not reachable"
							, e);
				
				try {
					Thread.sleep(sleepTimeInBetweenAttempts);
				} catch (InterruptedException ie) {
					break;
				}
			}
		}
		throw new RepositoryAccessException("Could not connect after "
				+ attemptCounter + " attempts.");
	}

	/**
	 * Method tries to open a session to a repository until it succeeds.
	 * 
	 * @param sleepTimeInBetweenAttempts : Time to wait in between attempts (in millis).
	 * @return : {@link eu.xenit.poller.repository.RepositoryAccessSession}
	 */
	public RepositoryAccessSession createSessionAndRetry(
			int sleepTimeInBetweenAttempts) {
		try {
			return createSessionAndRetry(null, sleepTimeInBetweenAttempts);
		} catch (RepositoryAccessException e) {
			logger.error(
					"Should not happen in an infinite attempt to open session",
					e);
			return null;
		}
	}

	/**
	 * 
	 * Method tries to open a session to a repository until it succeeds.
	 * It uses the default sleepTime of 10 seconds.
	 * @return : {@link eu.xenit.poller.repository.RepositoryAccessSession}
	 */
	public RepositoryAccessSession createSessionAndRetry() {
		try {
			return createSessionAndRetry(defaultNmbOfRetries, defaultSleepTime);
		} catch (RepositoryAccessException e) {
			logger.error(
					"Failed to create session after default amount of attempts",
					e);
			return null;
		}
	}

	/**
	 * Checks whether the repository can be accessed.
	 * 
	 * @return <code>true</code> if accessible, <code>false</code> if not.
	 */
	public boolean checkRepositoryAccess() {
		RepositoryAccessSession ras;
		try {
			ras = createSession();
			ras.closeSession();
			return true;
		} catch (RepositoryAccessException e) {
			return false;
		}
		
	}

}
