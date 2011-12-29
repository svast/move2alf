package eu.xenit.move2alf.logic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.padlocksoftware.padlock.license.ImportException;
import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseIO;
import net.padlocksoftware.padlock.license.LicenseState;
import net.padlocksoftware.padlock.license.TestResult;
import net.padlocksoftware.padlock.validator.Validator;
import net.padlocksoftware.padlock.validator.ValidatorException;

@Service("usageService")
public class UsageServiceImpl implements UsageService {

	private static final Logger logger = LoggerFactory
			.getLogger(UsageServiceImpl.class);

	private static final String publicKey = "308201b73082012c06072a8648ce3804013082011f02818100fd7f53811d"
			+ "75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b"
			+ "556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83"
			+ "f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982"
			+ "a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159"
			+ "578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675"
			+ "916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf"
			+ "492a038184000281805cd120594ba8241c330a3744689ebf40abc3130537667b016dd10dc930f621731adc83c891"
			+ "82bd723c628f02f85cd50760ef6518de9a2793aec4b329a2ade7d439e3d3f19fc7837e60b0b6b580033c78adac2e"
			+ "bac2cb3ed6622d923509c53de117f1c915e6234170c98665d2de8cec5de10e9eece445da1f634ab8a811f80db1";

	@Override
	public boolean isValid() {
		License license = null;
		try {
			license = getLicense();
		} catch (IOException e) {
			logger.error("Could not read license");
			return false;
		} catch (ImportException e) {
			logger.error("Could not import license");
			return false;
		} catch (NullPointerException e) {
			logger.error("Could not read license");
			return false;
		}
		
		if (license == null) {
			logger.error("Could not read license");
			return false;
		}
		
		Validator validator = new Validator(license, publicKey);
		LicenseState licState = null;
		try {
			licState = validator.validate();
		} catch (ValidatorException e) {
			logger.error("License validation failed");
			licState = e.getLicenseState();
			for (TestResult test : licState.getFailedTests()) {
				logger.error(test.getResultDescription());
			}
			return false;
		}
		
		return true;
	}

    @Override
    public Licensee getLicensee() {
        // TODO: implement
        return null; 
    }

	private License getLicense() throws ImportException, IOException {
		return LicenseIO.importLicense(UsageServiceImpl.class.getClassLoader()
				.getResource("move2alf.lic").openStream());
	}

}
