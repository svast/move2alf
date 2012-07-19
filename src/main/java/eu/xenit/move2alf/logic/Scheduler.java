package eu.xenit.move2alf.logic;

import org.springframework.transaction.annotation.Transactional;

import eu.xenit.move2alf.core.dto.Job;

@Transactional
public interface Scheduler {

	public void reloadSchedules();

	public void immediately(Job job);

}