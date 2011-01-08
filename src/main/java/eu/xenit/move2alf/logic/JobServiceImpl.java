package eu.xenit.move2alf.logic;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.core.dto.Job;

@Service("jobService")
public class JobServiceImpl extends AbstractHibernateService implements
		JobService {
	
	private UserService userService;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Override
	public List<Job> getAllJobs() {
		return null;
	}

	@Override
	public Job createJob(String name, String description) {
		Date now = new Date();
		Job job = new Job();
		job.setName(name);
		job.setDescription(description);
		job.setCreationDateTime(now);
		job.setLastModifyDateTime(now);
		job.setCreator(getUserService().getCurrentUser());
		getSessionFactory().getCurrentSession().save(job);
		return job;
	}

}
