package eu.xenit.move2alf.logic.usageservice;

import java.io.IOException;

import net.padlocksoftware.padlock.license.ImportException;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseIO;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import eu.xenit.move2alf.logic.usageservice.Loader;



@Aspect
class LoadLicenseAspect {

	private static final Logger logger = LoggerFactory.getLogger(LoadLicenseAspect.class);
	
	@Autowired
	private LicenseFilenameService licenseFilenameService;

	@Around("execution(* eu.xenit.move2alf.logic.usageservice.Loader.getLicense(..))")
	public Object loadNonDefaultLicense(ProceedingJoinPoint pjp) throws Throwable {
		logger.debug("Executing LoadLicenseAspect.loadNonDefaultLicense");
		String licenseFilename = licenseFilenameService.getLicenseFilename();
		if ( licenseFilename == null ) {
			return pjp.proceed();
		} else {
			try {
				logger.debug("Loading license " + licenseFilenameService.getLicenseFilename());
				Resource resource = new ClassPathResource("/move2alf/" + licenseFilename);
				License license = LicenseIO.importLicense(resource.getInputStream());
				((Loader)pjp.getTarget()).logLicenseInfo(license);
				return license;
			} catch (IOException e) {
				return null;
			} catch (ImportException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}
	}

}