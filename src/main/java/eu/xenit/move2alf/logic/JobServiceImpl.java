package eu.xenit.move2alf.logic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.IdObject;
import eu.xenit.move2alf.common.exceptions.NonexistentUserException;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSink;
import eu.xenit.move2alf.core.dto.ConfiguredSourceSinkParameter;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.Job;
import eu.xenit.move2alf.core.dto.Schedule;
import eu.xenit.move2alf.core.enums.EDestinationParameter;
import eu.xenit.move2alf.core.enums.EScheduleState;

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
	@SuppressWarnings("unchecked")
	public List<Job> getAllJobs() {
		return getSessionFactory().getCurrentSession().createQuery("from Job").list();
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
	
	@Override
	public Job editJob(int id, String name, String description) {
		Date now = new Date();
		Job job = getJob(id);
		job.setId(id);
		job.setName(name);
		job.setDescription(description);
		job.setCreationDateTime(now);
		job.setLastModifyDateTime(now);
		job.setCreator(getUserService().getCurrentUser());
		getSessionFactory().getCurrentSession().save(job);
		return job;
	}
	
	@Override
	public Job getJob(int id) {
		@SuppressWarnings("unchecked")
		List jobs = sessionFactory.getCurrentSession().createQuery(
				"from Job as j where j.id=?").setLong(0, id)
				.list();
		if (jobs.size() == 1) {
			return (Job) jobs.get(0);
		} else {
			throw new NonexistentUserException();
		}
	}
	
	@Override
	public void deleteJob(int id) {
		Job job = getJob(id);
		sessionFactory.getCurrentSession().delete(job);
	}
	
	@Override
	public List<Cycle> getCyclesForJob(int jobId) {
		return getSessionFactory().getCurrentSession().createQuery(
				"from Cycle as c where c.schedule.job.id=?").setLong(0,
				jobId).list();
	}

	@Override
	public List<Schedule> getSchedulesForJob(int jobId) {
		@SuppressWarnings("unchecked")
		List schedules = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.job.id=?").setLong(0, jobId)
				.list();
			return (List<Schedule>) schedules;
	}
	
	@Override
	public Schedule getSchedule(int scheduleId) {
		@SuppressWarnings("unchecked")
		List schedules = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.id=?").setLong(0, scheduleId)
				.list();

			return (Schedule) schedules.get(0);
	}
	
	@Override
	public int getScheduleId(int jobId, String cronJob) {
		@SuppressWarnings("unchecked")
		List schedule = sessionFactory.getCurrentSession().createQuery(
				"from Schedule as s where s.job.id=? and s.quartzScheduling=?").setLong(0, jobId).setString(1,cronJob)
				.list();

			return ((Schedule) schedule.get(0)).getId();
	}
	
	@Override
	public List<String> getCronjobsForJob(int jobId) {
		List<Schedule> schedules = getSchedulesForJob(jobId);
		List<String> cronjobs = new ArrayList();
		
		for(int i=0; i<schedules.size(); i++){
			cronjobs.add(schedules.get(i).getQuartzScheduling());
		}
			return cronjobs;
	}
	
	@Override
	public Schedule createSchedule(int jobId, String cronJob) {
		Date now = new Date();
		Schedule schedule = new Schedule();
		schedule.setJob(getJob(jobId));
		schedule.setCreator(getUserService().getCurrentUser());
		schedule.setCreationDateTime(now);
		schedule.setLastModifyDateTime(now);
		schedule.setQuartzScheduling(cronJob);
		schedule.setState("test");
		schedule.setStartDateTime(now);
		schedule.setEndDateTime(now);
		getSessionFactory().getCurrentSession().save(schedule);
		return schedule;
	}
	
	@Override
	public void deleteSchedule(int scheduleId) {
		Schedule schedule = getSchedule(scheduleId);
		sessionFactory.getCurrentSession().delete(schedule);
	}
	
	@Override
	public ConfiguredSourceSink createDestination(String destinationName, String destinationType, HashMap destinationParams){
			ConfiguredSourceSink sourceSink = new ConfiguredSourceSink();
			
			sourceSink.setSourceSinkClassName(destinationName);
		
			ConfiguredSourceSinkParameter sourceSinkName = new ConfiguredSourceSinkParameter();
			sourceSinkName.setName("name");
			sourceSinkName.setValue((String) destinationParams.get("name"));
			ConfiguredSourceSinkParameter sourceSinkURL = new ConfiguredSourceSinkParameter();
			sourceSinkURL.setName("url");
			sourceSinkURL.setValue((String) destinationParams.get("url"));
			ConfiguredSourceSinkParameter sourceSinkUser = new ConfiguredSourceSinkParameter();
			sourceSinkUser.setName("user");
			sourceSinkUser.setValue((String) destinationParams.get("user"));
			ConfiguredSourceSinkParameter sourceSinkPassword = new ConfiguredSourceSinkParameter();
			sourceSinkPassword.setName("password");
			sourceSinkPassword.setValue((String) destinationParams.get("password"));
			ConfiguredSourceSinkParameter sourceSinkThreads = new ConfiguredSourceSinkParameter();
			sourceSinkThreads.setName("threads");
			sourceSinkThreads.setValue((String) destinationParams.get("threads"));
			

			getSessionFactory().getCurrentSession().save(sourceSink);
			return sourceSink;
	}
}
