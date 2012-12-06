package eu.xenit.move2alf.logic.usageservice;

import java.util.Date;

public interface UsageService {
	
	public boolean isValid();
	
	public String getValidationFailureCause();
	
	public boolean isBlockedByDocumentCounter();

	public int getDocumentCounter();
	
	public void decrementDocumentCounter();

	public int getTotalNumberOfDocuments();
	
	/** Returns expiration date or null when no valid license is found */
	public Date getExpirationDate();
	
	/** Returns licensee or null when no valid license is found */
    public Licensee getLicensee();
}