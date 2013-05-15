package eu.xenit.move2alf.core;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<UserPswd> users = session.createQuery("from UserPswd").list();
        if(users.size() == 0){
            UserPswd admin = new UserPswd();
            admin.setUserName("admin");
            admin.setPassword("21232f297a57a5a743894a0e4a801fc3");

            Set<UserRole> roles = new HashSet<UserRole>();

            UserRole consumer = new UserRole();
            consumer.setUserName("admin");
            consumer.setRole("CONSUMER");
            roles.add(consumer);

            UserRole scheduleAdmin = new UserRole();
            scheduleAdmin.setUserName("admin");
            scheduleAdmin.setRole("SCHEDULE_ADMIN");
            roles.add(scheduleAdmin);

            UserRole jobAdmin = new UserRole();
            jobAdmin.setUserName("admin");
            jobAdmin.setRole("JOB_ADMIN");
            roles.add(jobAdmin);

            UserRole systemAdmin = new UserRole();
            systemAdmin.setUserName("admin");
            systemAdmin.setRole("SYSTEM_ADMIN");
            roles.add(systemAdmin);

            admin.setUserRoleSet(roles);

            session.save(admin);
            session.getTransaction().commit();

            System.out.println("TEST1231111111111111111");
        }
    }
}
