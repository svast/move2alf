package eu.xenit.move2alf.logic.usageservice;

import org.springframework.stereotype.Service;

@Service("licenseFilenameService")
public class LicenseFilenameService {

	private String licenseFilename;
	
	public void setLicenseFilename(String licenseFilename) {
		this.licenseFilename = licenseFilename;
	}
	
	public String getLicenseFilename() {
		return this.licenseFilename;
	}
}
