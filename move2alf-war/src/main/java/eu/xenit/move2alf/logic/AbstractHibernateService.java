package eu.xenit.move2alf.logic;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AbstractHibernateService {


	protected SessionFactory sessionFactory;

	public AbstractHibernateService() {
		super();
	}

	@Autowired
	public void setSessionFactory(@Qualifier("main") SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}