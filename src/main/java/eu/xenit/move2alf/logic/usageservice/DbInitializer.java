package eu.xenit.move2alf.logic.usageservice;

import java.util.Date;
import java.util.List;

import net.padlocksoftware.padlock.license.License;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.logic.usageservice.dto.DocumentCounter;
import eu.xenit.move2alf.logic.usageservice.dto.LicenseHistory;


@Service("dbInitializer")
@Transactional("h2txManager")
class DbInitializer implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final int MAX_INSTALLATION_DAYS = 10;
	
	enum ActionEnum {
		CREATE_TABLES, ADD_REPLENISHMENT, DO_NOTHING
	}
	
	@Autowired
	private SessionFactory h2sessionFactory;

	@Autowired
	private Loader licenseLoader;
	
	private LocalDate curDate = new LocalDate();
	
	private License license;
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//application context is completely loaded
		

		//TODO somnina h2db
		// 1) change user & password of h2dataSource (and maybe some other properties. These properties are a copy of the mysql-dataSource properties)
		// 2) db has to be encrypted
		
		//TODO somnina h2db
//		h2dataSource.setUser("move2alf1234xenit");
//		h2dataSource.setPassword("move2alf1234xenit");
		
		loadLicense();
		
		switch (getAction()) {
		case CREATE_TABLES:
			createTables();
			break;
		case ADD_REPLENISHMENT:
			addYearlyReplenishment();
			break;
		case DO_NOTHING:
		default:
			break;
		}
	}
	
	void loadLicense () {
		license = licenseLoader.getLicense();
	}

	ActionEnum getAction () {
		ActionEnum action = ActionEnum.DO_NOTHING;
		if (license != null && license.getProperty("license.initialLoadAmount") != null && license.getProperty("license.yearlyReplenishmentAmount") != null ) {
			if ( h2dbExists() ) {
		        LocalDate nextReplenishmentDate = getNextReplenishmentDate();
				if ( nextReplenishmentDate.compareTo(curDate) <= 0 ) {
					action = ActionEnum.ADD_REPLENISHMENT;
				}
			} else {
				LocalDate maxInstallationDate = getMaxInstallationDate();
				if ( curDate.compareTo(maxInstallationDate) <= 0 ) {
					action = ActionEnum.CREATE_TABLES;
				}
			}
		}
		return action;
	}

	boolean h2dbExists () {
		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		return docCounterList.size() != 0;
	}

	LocalDate getMaxInstallationDate() {
		LocalDate creationDate = LocalDate.fromDateFields(license.getCreationDate());
		return creationDate.plusDays(MAX_INSTALLATION_DAYS);
	}

	LocalDate getNextReplenishmentDate() {
		@SuppressWarnings("unchecked")
		List<LicenseHistory> licenseHistoryList = h2sessionFactory.getCurrentSession().createQuery("from LicenseHistory order by creationDateTime desc").setMaxResults(1).list();
        LicenseHistory licenseHistory = licenseHistoryList.get(0);
        LocalDate lastReplenishmentDate = new LocalDate(licenseHistory.getCreationDateTime());
        LocalDate nextReplenishmentDate = lastReplenishmentDate.plusYears(1);
        return nextReplenishmentDate;
	}

	void createTables () {
		String initialLoadAmountAsString = license.getProperty("license.initialLoadAmount");
		int initialLoadAmount = new Integer(initialLoadAmountAsString);
		createTables(initialLoadAmount, new Date());
	}

	void createTables (int initialLoadAmount, Date curDateTime) {
		Session session = h2sessionFactory.getCurrentSession();
		
        DocumentCounter documentCounter = new DocumentCounter();
        documentCounter.setCounter(initialLoadAmount);
        documentCounter.setLastModifyDateTime(curDateTime);
        session.save(documentCounter);
        
        LicenseHistory licenseHistory = new LicenseHistory();
        licenseHistory.setCreationDateTime(curDateTime);
        licenseHistory.setReplenishment(initialLoadAmount);
        licenseHistory.setNumberOfDocuments(initialLoadAmount);
        session.save(licenseHistory);
	}

	void addYearlyReplenishment () {
		Session session = h2sessionFactory.getCurrentSession();

		String yearlyReplenishmentAmountAsString = license.getProperty("license.yearlyReplenishmentAmount");
		int yearlyReplenishmentAmount = new Integer(yearlyReplenishmentAmountAsString);

		@SuppressWarnings("unchecked")
		List<DocumentCounter> docCounterList = h2sessionFactory.getCurrentSession().createQuery("from DocumentCounter").list();
		
        DocumentCounter documentCounter = docCounterList.get(0);
        int counter = documentCounter.getCounter() + yearlyReplenishmentAmount;
        documentCounter.setCounter(counter);
        documentCounter.setLastModifyDateTime(new Date());
        session.save(documentCounter);
        
        LicenseHistory licenseHistory = new LicenseHistory();
        licenseHistory.setCreationDateTime(new Date());
        licenseHistory.setReplenishment(yearlyReplenishmentAmount);
        licenseHistory.setNumberOfDocuments(counter);
        session.save(licenseHistory);
	}
	
	License getLicense () {
		return license;
	}
	
	void setCurDate (LocalDate curDate) {
		this.curDate = curDate;
	}
}