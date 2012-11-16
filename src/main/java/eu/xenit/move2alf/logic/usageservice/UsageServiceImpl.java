package eu.xenit.move2alf.logic.usageservice;

import java.util.Date;
import java.util.List;

import net.padlocksoftware.padlock.license.License;
import net.padlocksoftware.padlock.license.LicenseState;
import net.padlocksoftware.padlock.license.TestResult;
import net.padlocksoftware.padlock.validator.Validator;
import net.padlocksoftware.padlock.validator.ValidatorException;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.logic.Licensee;
import eu.xenit.move2alf.logic.usageservice.dto.DocumentCounter;
import eu.xenit.move2alf.logic.usageservice.dto.LicenseHistory;

@Service("usageService")
@Transactional("h2txManager")
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

	@Autowired
	private SessionFactory h2sessionFactory;

	@Autowired
	private Loader licenseLoader;
	
	@Override
	public boolean isValid() {
		return (getValidationFailureCause() == null);
	}

	@Override
	public String getValidationFailureCause() {
		License license = licenseLoader.getLicense();
		if (license == null) {
			logger.error("Could not read license");
			return "nolicense";
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
			return "validation";
		}
		
		if ( isBlockedByDocumentCounter() ) {
			logger.error("Document counter is 0.");
			return "blocked";
		}
		
		return null;
	}

	@Override
	public boolean isBlockedByDocumentCounter() {
		boolean isBlockedByDocumentCounter = true;
		try {
			@SuppressWarnings("unchecked")
			List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
			if ( docCounterList.size() != 0 ) {
				int counter = docCounterList.get(0).getCounter();
				if ( counter > 0 ) {
					isBlockedByDocumentCounter = false;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return isBlockedByDocumentCounter;
	}

	@Override
	public int getDocumentCounter() {
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		if ( docCounterList.size() == 0 ) {
			return 0;
		} else {
			DocumentCounter counter = docCounterList.get(0);
			return counter.getCounter();
		}
	}

	@Override
	public void decrementDocumentCounter() {
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		DocumentCounter counter = docCounterList.get(0);
		counter.setCounter(counter.getCounter() - 1);
		h2sessionFactory.getCurrentSession().save(counter);
	}

	//TODO somnina nog unit testen toevoegen
	@Override
	public int getTotalNumberOfDocuments() {
		@SuppressWarnings("unchecked")
		List<LicenseHistory> licenseHistoryList = h2sessionFactory.getCurrentSession().createQuery("from LicenseHistory order by creationDateTime desc").setMaxResults(1).list();
		if ( licenseHistoryList.size() == 0 ) {
			return 0;
		} else {
			LicenseHistory licenseHistory = licenseHistoryList.get(0);
			return licenseHistory.getNumberOfDocuments();
		}
	}

	@Override
	public Date getExpirationDate() {
		License license = licenseLoader.getLicense();
		if (license != null) {
			return license.getExpirationDate();
		} else {
			return null;
		}
	}

	@Override
	public Licensee getLicensee() {
		License license = licenseLoader.getLicense();
		if (license != null) {
			String companyName = license.getProperty("licensee.companyName");
			String street = license.getProperty("licensee.street");
			String city = license.getProperty("licensee.city");
			String postalCode = license.getProperty("licensee.postalCode");
			String state = license.getProperty("licensee.state");
			String country = license.getProperty("licensee.country");
			String contactPerson = license.getProperty("licensee.contactPerson");
			String email = license.getProperty("licensee.email");
			String telephone = license.getProperty("licensee.telephone");
			return new Licensee(companyName, street, city, postalCode, state,
					country, contactPerson, email, telephone);
		} else {
			return null;
		}
	}

}
