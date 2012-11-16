package eu.xenit.move2alf.logic.usageservice;

import java.io.IOException;

import net.padlocksoftware.padlock.license.ImportException;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseIO;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;




@Service("licenseLoader")
class Loader {

	private static final Logger logger = LoggerFactory.getLogger(Loader.class);

	//TODO remove public ??
    public License getLicense() {
		try {
			Resource resource = new ClassPathResource("/move2alf/move2alf.lic");
			License license = LicenseIO.importLicense(resource.getInputStream());
			logLicenseInfo(license);
			return license;
		} catch (IOException e) {
			return null;
		} catch (ImportException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

    void logLicenseInfo (License license) {
    	LocalDate creationDate = new LocalDate(license.getCreationDate());
    	LocalDate startDate = new LocalDate(license.getStartDate());
    	LocalDate expirationDate = new LocalDate(license.getExpirationDate());
		logger.debug("License loaded: creationDate = " + creationDate + "; startDate = " + startDate + "; expirationDate = " + expirationDate);
    }
}