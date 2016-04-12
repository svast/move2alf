package eu.xenit.move2alf.core;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Bootstrapper for the move2alf database
 */
public class ConditionalDataSourceInitializer implements InitializingBean {
	
	private boolean enabled = false;
	
	public boolean isEnabled(){
		return enabled;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

    private HibernateTransactionManager txManager;
    public void setTxManager(HibernateTransactionManager txManager){
        this.txManager = txManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!isEnabled())
            return;

        Session session = txManager.getSessionFactory().openSession();
        session.beginTransaction();
        if(isUninitializedDatabase(session)){ // Test for first load of database
            loadInitialAdminUser(session);
        }
        session.getTransaction().commit();
    }


    private void loadInitialAdminUser(Session session) {
        UserPswd admin = new UserPswd();
        admin.setUserName("admin");
        admin.setPassword("21232f297a57a5a743894a0e4a801fc3");

        Set<UserRole> roles = new HashSet<UserRole>();

        UserRole consumer = new UserRole();
        consumer.setUserName("admin");
        consumer.setRole("ROLE_CONSUMER");
        roles.add(consumer);

        UserRole scheduleAdmin = new UserRole();
        scheduleAdmin.setUserName("admin");
        scheduleAdmin.setRole("ROLE_SCHEDULE_ADMIN");
        roles.add(scheduleAdmin);

        UserRole jobAdmin = new UserRole();
        jobAdmin.setUserName("admin");
        jobAdmin.setRole("ROLE_JOB_ADMIN");
        roles.add(jobAdmin);

        UserRole systemAdmin = new UserRole();
        systemAdmin.setUserName("admin");
        systemAdmin.setRole("ROLE_SYSTEM_ADMIN");
        roles.add(systemAdmin);

        admin.setUserRoleSet(roles);

        session.save(admin);

    }

    private boolean isUninitializedDatabase(Session session) {
        List<UserPswd> users = session.createQuery("from UserPswd").list();

        return users.size() == 0;
    }
}
